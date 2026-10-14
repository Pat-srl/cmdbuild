/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.database.job;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.joining;
import org.apache.commons.dbcp2.BasicDataSource;
import org.cmdbuild.etl.EtlException;
import static org.cmdbuild.etl.database.job.DatabaseHandlerMode.DHM_AUTO;
import static org.cmdbuild.etl.database.job.DatabaseHandlerMode.DHM_IMPORT;
import static org.cmdbuild.etl.database.job.DatabaseHandlerMode.DHM_LOAD;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_DATABASE;
import org.cmdbuild.etl.job.EtlLoadHandler;
import org.cmdbuild.etl.job.EtlLoaderApi;
import static org.cmdbuild.etl.loader.EtlFileFormat.EFF_DATABASE;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfig;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfigImpl;
import org.cmdbuild.etl.loader.EtlTemplateImpl;
import org.cmdbuild.etl.loader.EtlTemplateService;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_RECORD;
import org.cmdbuild.etl.loader.EtlTemplateWithData;
import org.cmdbuild.etl.loader.EtlTemplateWithDataImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_PROCESSING_REPORT;
import org.cmdbuild.etl.waterway.message.WaterwayMessageDataImpl;
import static org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils.decryptValue;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassNameOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseEtlLoadHandler implements EtlLoadHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EtlTemplateService importService;

    public DatabaseEtlLoadHandler(EtlTemplateService importService) {
        this.importService = checkNotNull(importService);
    }

    @Override
    public String getType() {
        return ETLHT_DATABASE;
    }

    @Override
    public WaterwayMessageData load(EtlLoaderApi api) {
        return new DatabaseImportJobHelper(api).executeImportJob();
    }

    private class DatabaseImportJobConfig {

        protected final EtlLoaderApi api;
        protected final String sqlDriver, jdbcUrl, username, password, testQuery, connectionProperties;
        protected final List<EtlTemplate> templates;
        protected final DatabaseImportSourceType sourceType;
        protected final DatabaseHandlerMode mode;

        public DatabaseImportJobConfig(EtlLoaderApi api) {
            this.api = checkNotNull(api);
            sourceType = parseEnumOrDefault(api.getConfig("sourceType"), DatabaseImportSourceType.JDBC);
            sqlDriver = api.getConfigNotBlank("jdbcDriverClassName");
            jdbcUrl = api.getConfigNotBlank("jdbcUrl");
            username = api.getConfigNotBlank("jdbcUsername");
            password = decryptValue(api.getConfigNotBlank("jdbcPassword"));
            String testQueryConfig = firstNotBlank(api.getConfig("jdbcTestQuery"), sqlDriver.toLowerCase().contains("oracle") ? "SELECT 1 FROM DUAL" : "SELECT 1");
            testQuery = testQueryConfig.trim().toLowerCase().matches("false|disabled") ? null : testQueryConfig;
            connectionProperties = api.getConfig("jdbcConnectionProperties");

            templates = api.getTemplates();
            templates.forEach(t -> checkArgument(t.hasFormat(EFF_DATABASE), "invalid template format for template = %s", t));

            DatabaseHandlerMode configuredMode = parseEnumOrDefault(api.getConfig("mode"), DHM_AUTO);

            if (configuredMode == DHM_AUTO) {
                if (templates.stream().anyMatch(t -> t.getTargetType() == ET_RECORD)) {
                    configuredMode = DHM_LOAD;
                } else {
                    configuredMode = DHM_IMPORT;
                }
            }
            this.mode = configuredMode;

            try {
                Class.forName(sqlDriver);
            } catch (ClassNotFoundException ex) {
                throw new EtlException(ex, "error loading sql driver class");
            }
        }
    }

    private class DatabaseImportJobHelper extends DatabaseImportJobConfig {

        private JdbcTemplate jdbcTemplate;

        public DatabaseImportJobHelper(EtlLoaderApi api) {
            super(api);
        }

        public WaterwayMessageData executeImportJob() {
            logger.info("execute database import job");
            try (BasicDataSource dataSource = new BasicDataSource()) {
                dataSource.setDriverClassName(sqlDriver);
                dataSource.setUrl(jdbcUrl);
                dataSource.setUsername(username);
                dataSource.setPassword(password);
                if (isNotBlank(testQuery)) {
                    dataSource.setValidationQuery(testQuery);
                }
                if (isNotBlank(connectionProperties)) {
                    dataSource.setConnectionProperties(connectionProperties);
                }
                jdbcTemplate = new JdbcTemplate(dataSource);
                List<EtlTemplateWithData> data = templates.stream().map(this::loadDataForTemplate).collect(toImmutableList());
                logger.debug("loaded data from db");
                return switch (mode) {
                    case DHM_IMPORT -> {
                        logger.debug("execute card import (import mode)");
                        EtlProcessingResult result = importService.importDataWithTemplates(data);
                        logger.info(marker(), "import job completed: {}", result.getResultDescription());
                        yield WaterwayMessageDataImpl.build(WY_PROCESSING_REPORT, result, api.getMeta());
                    }
                    case DHM_LOAD -> {
                        logger.debug("attach data to return message (load mode)");
                        yield WaterwayMessageDataImpl.builder().withMeta(api.getMeta()).accept(b -> data.forEach(d -> {
                            String outputName = firstNotBlank(d.getTemplate().getTargetName(), d.getTemplate().getName());
                            b.withAttachment(outputName, d.getData(), "etl_template_code", d.getTemplate().getCode(), "etl_template_target", d.getTemplate().getTargetName());
                        })).build();
                    }
                    default ->
                        throw unsupported("unsupported mode = %s", mode);
                };
            } catch (Exception ex) {
                throw new EtlException(ex, "error executing database import job");
            } finally {
                jdbcTemplate = null;
            }
        }

        public JdbcTemplate getJdbcTemplate() {
            return checkNotNull(jdbcTemplate, "jdbc template not available");
        }

        private EtlTemplateWithData loadDataForTemplate(EtlTemplate template) {
            logger.debug("prepare data loader for template = {}", template);

            List<EtlTemplateColumnConfig> columns = template.getColumns();

            String source = trimAndCheckNotBlank(template.getSource(), "missing template source table");
            String query = isSqlQuery(source) ? source : format("SELECT %s FROM %s", columns.stream().map(EtlTemplateColumnConfig::getColumnNameOrAttributeName).collect(joining(",")), source);//TODO column name escape?? table name escape??

            Supplier<List<Map<String, ?>>> data = () -> {
                logger.info("load data for template = {}: execute sql query =< {} >", template, query);
                return getJdbcTemplate().query(query, (r, n) -> mapOf(String.class, Object.class).accept(rethrowConsumer((m) -> {
                    for (int i = 0; i < columns.size(); i++) {
                        EtlTemplateColumnConfig columnConfig = columns.get(i);
                        Object value = r.getObject(i + 1);
                        checkArgument(value == null || value instanceof String || value instanceof Number, "unsupported value type for column =< %s > record = %s, type = %s", columnConfig.getColumnNameOrAttributeName(), n, getClassNameOfNullable(value));
                        m.put(columnConfig.getAttributeName(), value);
                    }
                })));
            };

            EtlTemplate actualTemplate = EtlTemplateImpl.copyOf(template).withColumns(columns.stream().map(c -> EtlTemplateColumnConfigImpl.copyOf(c).withColumnName(c.getAttributeName()).build()).collect(toImmutableList())).build();

            return new EtlTemplateWithDataImpl(actualTemplate, data);
        }

    }

    private static boolean isSqlQuery(String source) {
        return source.matches("(?s).*\\s.*");//TODO
    }
}
