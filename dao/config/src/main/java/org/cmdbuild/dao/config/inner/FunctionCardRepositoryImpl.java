/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.dao.DaoException;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.dao.sql.utils.SqlFunction;
import org.cmdbuild.dao.sql.utils.SqlFunctionImpl;
import org.cmdbuild.debuginfo.BuildInfoService;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FunctionCardRepositoryImpl implements FunctionCardRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BuildInfoService buildInfoService;
    private final JdbcTemplate jdbcTemplate;

    public FunctionCardRepositoryImpl(@Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate, BuildInfoService buildInfoService) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        this.buildInfoService = checkNotNull(buildInfoService);
    }

    @Override
    public Map<String, SqlFunction> getFunctions(String category) {
        return doGetFunctions(checkNotBlank(category));
    }

    private Map<String, SqlFunction> doGetFunctions(@Nullable String category) {//note: must handle null category here to allow upgrade; remove this later
        return jdbcTemplate.query(isBlank(category) ? "SELECT * FROM \"_Function\"" : format("SELECT * FROM \"_Function\" WHERE \"Category\" = %s", systemToSqlExpr(category)), (r, i) -> {
            try {
                Map<String, String> meta = fromJson(r.getString("Meta"), MAP_OF_STRINGS);
                return SqlFunctionImpl.builder()
                        .withHash(r.getString("Hash"))
                        .withFunctionDefinition(r.getString("Content"))
                        .withRequiredPatchVersion("unknown")
                        .withSignature(r.getString("Code"))
                        .withComment(meta.get("COMMENT"))
                        .build();
            } catch (Exception ex) {
                throw new DaoException(ex, "error reading `_Function` record %s", r.getLong(ATTR_ID));
            }
        }).stream().collect(toMap(SqlFunction::getSignature, identity()));
    }

    @Override
    public void update(String category, SqlFunction function) {
        String meta = toJson(mapOf(String.class, String.class).skipNullValues().with("COMMENT", trimToNull(function.getComment())));
        if (doGetFunctions(null).containsKey(function.getSignature())) {
            checkArgument(1 == jdbcTemplate.update("UPDATE \"_Function\" SET \"Category\" = ?, \"Hash\" = ?, \"Revision\" = ?, \"Content\" = ?, \"Meta\" = ?::jsonb, \"BeginDate\" = now() WHERE \"Code\" = ?",
                    checkNotBlank(category), function.getHash(), buildInfoService.getCommitInfo(), function.getFunctionDefinition(), meta, function.getSignature()), "error updating function registry: no record modified");
        } else {
            jdbcTemplate.update("INSERT INTO \"_Function\" (\"Category\",\"Code\",\"Hash\",\"Revision\",\"Content\",\"Meta\") VALUES (?,?,?,?,?,?::jsonb)", checkNotBlank(category), function.getSignature(), function.getHash(), buildInfoService.getCommitInfo(), function.getFunctionDefinition(), meta);
        }
    }

}
