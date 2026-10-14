/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.base.Suppliers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import static java.lang.Math.round;
import static java.lang.String.format;
import java.text.ParseException;
import java.time.LocalDate;
import static java.time.ZoneOffset.UTC;
import java.time.ZonedDateTime;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import static java.util.function.Function.identity;
import java.util.function.Supplier;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Gatherers;
import java.util.stream.Stream;
import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.apache.commons.codec.binary.StringUtils.newStringUsAscii;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.cmdbuild.auth.session.inner.CurrentSessionHolder;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.classe.access.UserCardAccess;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.cluster.NodeIdProvider;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.common.beans.CardIdAndClassNameImpl;
import static org.cmdbuild.common.beans.CardIdAndClassNameImpl.card;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.config.EtlConfiguration;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.beans.CardImpl.buildCard;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDTENANT;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ_CASE_INSENSITIVE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUPARRAY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.LookupArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.postgres.q3.RefAttrHelperService;
import org.cmdbuild.dao.postgres.utils.RelationDirectionQueryHelper;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.isLookupArrayIdsAsString;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.dao.utils.CmFilterUtils.noopFilter;
import static org.cmdbuild.dao.utils.FilterProcessor.predicateFromFilter;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.ContextFilter;
import static org.cmdbuild.data.filter.FilterType.CONTEXT;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.etl.EtlException;
import static org.cmdbuild.etl.loader.EtlFileFormat.EFF_CSV;
import static org.cmdbuild.etl.loader.EtlFileFormat.EFF_XLS;
import static org.cmdbuild.etl.loader.EtlFileFormat.EFF_XLSX;
import static org.cmdbuild.etl.loader.EtlMergeMode.EM_DELETE_MISSING;
import static org.cmdbuild.etl.loader.EtlMergeMode.EM_NO_MERGE;
import static org.cmdbuild.etl.loader.EtlMergeMode.EM_UPDATE_ATTR_ON_MISSING;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.loader.EtlProcessingResultDetails;
import org.cmdbuild.etl.loader.EtlProcessingResultDetailsImpl;
import org.cmdbuild.etl.loader.EtlProcessingResultFault;
import org.cmdbuild.etl.loader.EtlRecordInfo;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfig;
import static org.cmdbuild.etl.loader.EtlTemplateColumnConfig.RequiredAttrMode.RAM_AUTO;
import static org.cmdbuild.etl.loader.EtlTemplateColumnConfig.RequiredAttrMode.RAM_REQUIRED;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfigImpl;
import org.cmdbuild.etl.loader.EtlTemplateColumnMode;
import static org.cmdbuild.etl.loader.EtlTemplateColumnMode.ETCM_CODE;
import static org.cmdbuild.etl.loader.EtlTemplateColumnMode.ETCM_DEFAULT;
import static org.cmdbuild.etl.loader.EtlTemplateColumnMode.ETCM_ID;
import static org.cmdbuild.etl.loader.EtlTemplateColumnMode.ETCM_IGNORE;
import static org.cmdbuild.etl.loader.EtlTemplateDateTimeMode.ETDT_EXTJS;
import static org.cmdbuild.etl.loader.EtlTemplateDateTimeMode.ETDT_JAVA;
import org.cmdbuild.etl.loader.EtlTemplateFieldFormatConfig;
import org.cmdbuild.etl.loader.EtlTemplateInlineProcessorService;
import org.cmdbuild.etl.loader.EtlTemplateRepository;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_CLASS;
import org.cmdbuild.etl.loader.EtlTemplateWithData;
import static org.cmdbuild.etl.utils.EtlProcessorUtils.prepareData;
import static org.cmdbuild.etl.utils.EtlTemplateUtils.buildWorkbook;
import static org.cmdbuild.etl.utils.EtlTemplateUtils.getCsvPreference;
import org.cmdbuild.etl.utils.WorkbookInfo;
import static org.cmdbuild.etl.utils.XlsProcessingUtils.dateFormatPatternToXls;
import static org.cmdbuild.etl.utils.XlsProcessingUtils.dateTimeFormatPatternToXls;
import static org.cmdbuild.etl.utils.XlsProcessingUtils.getRecordsFromXlsFile;
import static org.cmdbuild.etl.utils.XlsProcessingUtils.lazyRecordToString;
import static org.cmdbuild.etl.waterway.message.MessageReference.MESSAGE_SKIP_STORAGE;
import static org.cmdbuild.etl.waterway.message.WaterwayMessage.MESSAGE_CONTEXT_ID;
import static org.cmdbuild.etl.waterway.message.WaterwayMessage.MESSAGE_REQUEST_ID;
import static org.cmdbuild.etl.waterway.message.WaterwayMessage.MESSAGE_SESSION_ID;
import static org.cmdbuild.etl.waterway.message.WaterwayMessage.MESSAGE_SKIP_QUEUE;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_COMPLETED;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.payloadToDataSource;
import org.cmdbuild.etl.waterway.storage.EtlMessage;
import org.cmdbuild.etl.waterway.storage.EtlMessageImpl;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.fault.FaultEventImpl;
import org.cmdbuild.fault.FaultMessage;
import static org.cmdbuild.fault.FaultUtils.errorToMessages;
import static org.cmdbuild.fault.FaultUtils.exceptionToUserMessage;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.gis.GisValue;
import org.cmdbuild.gis.model.GisValueImpl;
import static org.cmdbuild.gis.utils.GisUtils.cmGeometryToPostgisSql;
import static org.cmdbuild.gis.utils.GisUtils.parseGeometry;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupValue;
import static org.cmdbuild.requestcontext.RequestContext.REQUEST_ID;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.requestcontext.RequestContextUtils.isInterrupted;
import org.cmdbuild.script.ScriptService;
import org.cmdbuild.translation.TranslationService;
import org.cmdbuild.userconfig.DateAndFormatPreferences;
import org.cmdbuild.userconfig.DateAndFormatPreferencesImpl;
import org.cmdbuild.userconfig.UserPrefHelper;
import org.cmdbuild.userconfig.UserPrefHelperImpl;
import org.cmdbuild.userconfig.UserPreferencesService;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDate;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import static org.cmdbuild.utils.date.CmDateUtils.toTime;
import static org.cmdbuild.utils.date.ExtjsDateUtils.extjsDateTimeFormatToJavaDateTimeFormat;
import static org.cmdbuild.utils.io.CmIoUtils.countBytes;
import static org.cmdbuild.utils.io.CmIoUtils.getCharsetFromContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.setCharsetInContentType;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.buildProgressListener;
import static org.cmdbuild.utils.json.CmJsonUtils.LIST_OF_MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.extractCmPrimitiveIfAvailable;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toDouble;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.exceptionToMessage;
import static org.cmdbuild.utils.lang.CmExceptionUtils.isParamError;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.parseErrorCode;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.serializeError;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmMapUtils.multimap;
import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import org.cmdbuild.utils.lang.CmNullableUtils;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import static org.cmdbuild.utils.lang.CmObjectUtils.estimateObjectSizeBytes;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import static org.cmdbuild.utils.lang.CmRuntimeUtils.hasEnoughFreeMemoryGC;
import static org.cmdbuild.utils.lang.CmRuntimeUtils.memBytesToDisplaySize;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.mapDifferencesToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInlineLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.normalize;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewService;
import org.cmdbuild.workflow.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

@Component
@Primary
public class EtlTemplateProcessorServiceImpl implements EtlTemplateInlineProcessorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String IMPORT_RECORD_LINE_NUMBER = "CM_IMPORT_RECORD_LINE_NUMBER", CM_IMPORT_RECORD_ID = "CM_IMPORT_RECORD_ID";

    private final ViewService viewService;
    private final CoreConfiguration config;
    private final LookupService lookupService;
    private final UserCardService cardService;
    private final UserDomainService domainService;
    private final UserPreferencesService userPreferencesService;
    private final DaoService dao;
    private final GisService gis;
    private final RequestContextService requestContextService;
    private final CurrentSessionHolder currentSession;
    private final OperationUserSupplier operationUser;
    private final RefAttrHelperService refAttrHelperService;
    private final EtlConfiguration etlConfiguration;
    private final WorkflowService workflowService;
    private final ScriptService scriptService;
    private final EtlTemplateRepository templateRepository;
    private final NodeIdProvider nodeIdProvider;
    private final TranslationService translationService;

    public EtlTemplateProcessorServiceImpl(EtlTemplateRepository templateRepository, ViewService viewService, CoreConfiguration config, LookupService lookupService, UserCardService cardService, UserDomainService domainService, UserPreferencesService userPreferencesService, DaoService dao, GisService gis, RequestContextService requestContextService, CurrentSessionHolder currentSession, OperationUserSupplier operationUser, RefAttrHelperService refAttrHelperService, EtlConfiguration etlConfiguration, WorkflowService workflowService, ScriptService scriptService, NodeIdProvider nodeIdProvider, TranslationService translationService) {
        this.viewService = checkNotNull(viewService);
        this.config = checkNotNull(config);
        this.lookupService = checkNotNull(lookupService);
        this.cardService = checkNotNull(cardService);
        this.domainService = checkNotNull(domainService);
        this.userPreferencesService = checkNotNull(userPreferencesService);
        this.dao = checkNotNull(dao);
        this.gis = checkNotNull(gis);
        this.requestContextService = checkNotNull(requestContextService);
        this.currentSession = checkNotNull(currentSession);
        this.operationUser = checkNotNull(operationUser);
        this.refAttrHelperService = checkNotNull(refAttrHelperService);
        this.etlConfiguration = checkNotNull(etlConfiguration);
        this.workflowService = checkNotNull(workflowService);
        this.scriptService = checkNotNull(scriptService);
        this.templateRepository = checkNotNull(templateRepository);
        this.nodeIdProvider = checkNotNull(nodeIdProvider);
        this.translationService = checkNotNull(translationService);
    }

    @Override
    public EtlProcessingResult importDataWithTemplates(List<EtlTemplateWithData> templatesWithData) {
        try {
            logger.info("execute multi import operation with templates = {}", list(templatesWithData).map(t -> t.getTemplate().getCode()));
            ImportRegister register = new ImportRegister();
            List<ImportProcessor> helpers = list(templatesWithData).map(rethrowFunction(td -> buildImportProcessor(new ImportContext(td.getTemplate(), register, prepareData(td.getData()), td.getCallback()))));
            logger.info("prepare data");
            helpers.forEach(rethrowConsumer(ImportProcessor::getRawRecords));
            logger.info("create/update records");
            helpers.forEach(rethrowConsumer(ImportProcessor::createUpdateRecords));
            logger.info("handle missing/deleted records");
            list(helpers).reverse().forEach(rethrowConsumer(ImportProcessor::handleMissingRecords));
            EtlProcessingResult result = register.getAggregateResult();
            writeProcessingResult(result, list(templatesWithData).map(EtlTemplateWithData::getTemplate).toArray(EtlTemplate[]::new));
            return result;
        } catch (Exception ex) {
            throw new EtlException(ex, "import error");
        }
    }

    @Override
    public DataSource exportDataWithTemplate(EtlTemplate template) {
        try {
            logger.info("start data export for template = {}", template);
            checkNotNull(template, "invalid template: template is null");
            DataSource dataSource = buildExportProcessor(template).exportData();
            logger.info("completed export with template = {} output file = {} ({} {})", template, dataSource.getName(), byteCountToDisplaySize(countBytes(dataSource)), dataSource.getContentType());
            return dataSource;
        } catch (Exception ex) {
            throw new EtlException(ex, "export error with template = %s", template);
        }
    }

    @Override
    public EtlProcessingResult importDataWithTemplate(Object data, EtlTemplate template) {
        try {
            checkNotNull(template, "invalid template: template is null");
            checkNotNull(data, "invalid data: data is null");
            EtlProcessingResult result = buildImportProcessor(new ImportContext(template, new ImportRegister(), prepareData(data))).importData();
            writeProcessingResult(result, template);
            return result;
        } catch (Exception ex) {
            throw new EtlException(ex, "import error with template = %s", template);
        }
    }

    @Override
    public DataSource exportDataInline(List<? extends DatabaseRecord> data, EtlTemplate template) {
        try {
            return buildExportProcessor(template).exportData(data);
        } catch (Exception ex) {
            throw new EtlException(ex, "inline export error");
        }
    }

    @Override
    public <T extends DatabaseRecord> List<T> importDataInline(Object data, EntryType type, EtlTemplate template) {
        try {
            ImportRegister importRegister = new ImportRegister();
            ImportProcessor importProcessor = buildImportProcessor(new ImportContext(template, importRegister, prepareData(data)));
            List<? extends DatabaseRecord> importData = importProcessor.importDataInline((Classe) type);
            writeProcessingResult(importRegister.getAggregateResult(), template);
            return (List) importData;
        } catch (Exception ex) {
            throw new EtlException(ex, "inline import error");
        }
    }

    private void writeProcessingResult(EtlProcessingResult result, EtlTemplate... templates) {
        FluentList<EtlTemplate> templatesList = list(templates);
        logger.info("completed import with templates = {} result: {}", templatesList, result.getResultDescription());
        EtlMessage etlMessage = EtlMessageImpl.builder()
                .withMessageId(randomId())
                .withTimestamp(now())
                .withTransactionId(0)
                .withStorage(MESSAGE_SKIP_STORAGE)
                .withQueue(MESSAGE_SKIP_QUEUE)
                .withNodeId(nodeIdProvider.getNodeId())
                .withStatus(WMS_COMPLETED)
                .withMeta(
                        mapOf(String.class, String.class)
                                .with("templates", templatesList.map(EtlTemplate::getCode).collect(joining(",")))
                                .with(
                                        // TODO flattenMaps?
                                        map("result", map("processed", toStringNotBlank(result.getProcessedRecordCount()),
                                                "created", toStringNotBlank(result.getCreatedRecordCount()),
                                                "modified", toStringNotBlank(result.getModifiedRecordCount()),
                                                "deleted", toStringNotBlank(result.getDeletedRecordCount()),
                                                "unmodified", toStringNotBlank(result.getUnmodifiedRecordCount()),
                                                "errors", toStringNotBlank(result.getErrors().size())
                                        )))
                                .with(
                                        MESSAGE_SESSION_ID, currentSession.getOrNull(),
                                        MESSAGE_CONTEXT_ID, requestContextService.getRequestContextIdOrNull(),
                                        MESSAGE_REQUEST_ID, (String) requestContextService.get(REQUEST_ID)
                                )
                )
                .withLogs(result.getResultDescription())
                .build();
        dao.createOnly(etlMessage);
    }

    private class ImportContext {

        private final EtlTemplate template;
        private final ImportRegister register;
        private final Object data;
        private final Optional<Object> eventListener;

        public ImportContext(EtlTemplate template, ImportRegister register, Object data) {
            this(template, register, data, null);
        }

        public ImportContext(EtlTemplate template, ImportRegister register, Object data, @Nullable Object callback) {
            this.template = checkNotNull(template);
            this.register = checkNotNull(register);
            this.data = checkNotNull(data);
            eventListener = Optional.ofNullable(callback);
        }

        public EtlTemplate getTemplate() {
            return template;
        }

        public ImportRegister getRegister() {
            return register;
        }

        public Object getData() {
            return data;
        }

        public Optional<Object> getEventListener() {
            return eventListener;
        }

    }

    private ExportProcessor buildExportProcessor(EtlTemplate template) throws Exception {
        return switch (template.getFileFormat()) {
            case EFF_CSV ->
                new CsvExportProcessor(template);
            case EFF_XLS, EFF_XLSX ->
                new XlsExportProcessor(template);
            default ->
                throw new EtlException("unsupported template file format = %s", template.getFileFormat());
        };
    }

    private ImportProcessor buildImportProcessor(ImportContext context) throws Exception {
        return switch (context.getTemplate().getFileFormat()) {
            case EFF_CSV ->
                new CsvImportProcessor(context);
            case EFF_XLS, EFF_XLSX ->
                new XlsImportProcessor(context);
            case EFF_DATABASE, EFF_IFC, EFF_CAD ->
                new BeanImportProcessor(context, false);
            case EFF_OTHER ->
                new BeanImportProcessor(context, true);
            default ->
                throw new EtlException("unsupported template file format = %s", context.getTemplate().getFileFormat());
        };
    }

    private class CsvImportProcessor extends ImportProcessor {

        public CsvImportProcessor(ImportContext context) {
            super(context);
            checkArgument(equal(template.getFileFormat(), EFF_CSV));
        }

        @Override
        protected List prepareDataInner() throws Exception {
            List records = getRecords((DataSource) data);
            hasLineNumber = true;
            return records;
        }

        private List<Map<String, Object>> getRecords(DataSource data) throws Exception {
            List<Map<String, Object>> list = list();
            String charset = firstNotBlank(template.getCharset(), getCharsetFromContentType(data.getContentType()), userPreferencesService.getUserPreferences().getPreferredFileCharset());
            logger.debug("using charset =< {} >", charset);
            try (CsvListReader csvReader = new CsvListReader(new InputStreamReader(new BOMInputStream.Builder().setInputStream(data.getInputStream()).get(), charset), getCsvPreference(template))) {
                List<String> line;
                int lineNumber = -1;
                while ((line = csvReader.read()) != null) {
                    lineNumber++;
                    if (lineNumber == 0 && template.getUseHeader()) {
                        checkHeader(line);
                    } else {
                        try {
                            list.add(parseLine(line).with(IMPORT_RECORD_LINE_NUMBER, lineNumber));
                        } catch (Exception ex) {
                            throw new EtlException(ex, "error while parsing line = %s", lineNumber);
                        }
                    }
                }
            }
            logger.debug("loaded {} records from file", list.size());
            return list;
        }

        private FluentMap<String, Object> parseLine(List<String> line) {
            checkArgument(line.size() >= importColumns.size(), "invalid line size = %s (expected size = %s)", line.size(), importColumns.size());
            Iterator<String> iterator = line.iterator();
            return mapOf(String.class, Object.class).accept(m -> importColumns.stream().forEach((c) -> {
                Object value = iterator.next();
                if (isNotBlank(c.getAttributeName())) {
                    m.put(c.getAttributeName(), value);
                }
            }));
        }

    }

    private class BeanImportProcessor extends ImportProcessor {

        private final boolean mapRecordData;

        public BeanImportProcessor(ImportContext context, boolean mapRecordData) {
            super(context);
            this.mapRecordData = mapRecordData;
        }

        @Override
        protected List prepareDataInner() throws Exception {
            List<Map<String, Object>> list = data instanceof List dataList ? dataList : fromJson(payloadToDataSource(data), LIST_OF_MAP_OF_OBJECTS);
            if (mapRecordData) {
                list = list(list).map(r -> map(r).accept(m -> template.getColumns().stream().flatMap(c -> {
                    if (c.hasMode(ETCM_DEFAULT) && c.hasReferenceTemplate()) {
                        EtlTemplate t = templateRepository.getTemplateByName(c.getReferenceTemplate());
                        return list(t.getImportKeyAttributes()).map(t::getColumnByAttrName).map(tc -> Pair.of(tc.getColumnName(), format("%s_%s", c.getAttributeName(), tc.getAttributeName()))).stream();
                    } else {
                        return singletonList(Pair.of(c.getColumnName(), c.getAttributeName())).stream();
                    }
                }).filter((Pair<String, String> c) -> isNotBlank(c.getLeft()) && !equal(c.getLeft(), c.getRight())).forEach(c -> m.put(c.getRight(), r.get(c.getLeft())))));
            }
            return list;
        }

    }

    private class XlsImportProcessor extends ImportProcessor {

        private final int headerRow, dataRow, columnOffset;

        public XlsImportProcessor(ImportContext context) {
            super(context);
            checkArgument(set(EFF_XLS, EFF_XLSX).contains(template.getFileFormat()));
            headerRow = isNullOrLtEqZero(template.getHeaderRow()) ? 0 : template.getHeaderRow() - 1;
            dataRow = isNullOrLtEqZero(template.getDataRow()) ? (template.getUseHeader() ? 1 : 0) : template.getDataRow() - 1;
            columnOffset = isNullOrLtEqZero(template.getFirstCol()) ? 0 : template.getFirstCol() - 1;
            logger.debug("start import with header row = {} data row = {} column offset = {}", headerRow, dataRow, columnOffset);
        }

        @Override
        protected List prepareDataInner() throws Exception {
            List records = getRecords((DataSource) data);
            hasLineNumber = true;
            return records;
        }

        private List<Map<String, Object>> getRecords(DataSource data) throws Exception {
            List<List<Object>> rawRecords = getRecordsFromXlsFile(data, template.getFileFormat(), template.getSkipUnknownColumns() ? null : importColumns.size(), columnOffset);
            List<Map<String, Object>> list = list();
            for (int rowIndex = 0; rowIndex < rawRecords.size(); rowIndex++) {
                List<Object> rawRecord = rawRecords.get(rowIndex);
                if (rowIndex == headerRow && template.getUseHeader()) {
                    logger.debug("check header row = {}", rowIndex);
                    checkHeader(rawRecord);
                } else if (rowIndex >= dataRow) {
                    try {
                        logger.trace("parse data row = {}", rowIndex);
                        list.add(parseRow(rawRecord).with(IMPORT_RECORD_LINE_NUMBER, rowIndex + 1));
                    } catch (Exception ex) {
                        throw new EtlException(ex, "error while parsing line = %s", rowIndex);
                    }
                } else {
                    logger.trace("skipping row = {}", rowIndex);
                }
            }
            logger.debug("loaded {} records from file", list.size());
            return list;
        }

        private FluentMap<String, Object> parseRow(List<Object> row) {
            return mapOf(String.class, Object.class).accept(m -> {
                for (int i = 0; i < importColumns.size(); i++) {
                    Object value = i < row.size() ? row.get(i) : null;
                    if (isNotBlank(importColumns.get(i).getAttributeName())) {
                        m.put(importColumns.get(i).getAttributeName(), value);
                    }
                }
            });
        }

    }

    private class ImportRegister {

        private final ZonedDateTime begin = now();
        private final Map<String, ImportRegisterClassInfo> infoByClass = map();
        private final Map<String, CardIdAndClassName> processedRecordsByRecordId = map();

        public ImportRegisterClassInfo getInfoByClass(String classId) {
            ImportRegisterClassInfo info = infoByClass.get(classId);
            if (info == null) {
                infoByClass.put(classId, info = new ImportRegisterClassInfo(classId) {

                    @Override
                    public void addUnmodifiedRecord(Map<String, Object> record, long id) {
                        super.addUnmodifiedRecord(record, id);
                        addProcessedRecord(record, classId, id);
                    }

                    @Override
                    public void addModifiedRecord(Map<String, Object> record, long id) {
                        super.addModifiedRecord(record, id);
                        addProcessedRecord(record, classId, id);
                    }

                    @Override
                    public void addCreatedRecord(Map<String, Object> record, long id) {
                        super.addCreatedRecord(record, id);
                        addProcessedRecord(record, classId, id);
                    }

                });
            }
            return info;
        }

        public EtlProcessingResult getAggregateResult() {
            int created = 0, modified = 0, unmodified = 0, deleted = 0, processed = 0;
            List<Long> createdRecords = list(), modifiedRecords = list(), deletedRecords = list();
            List<EtlProcessingResultFault> errors = list();
            for (ImportRegisterClassInfo info : infoByClass.values()) {
                created += info.getCreatedCount();
                modified += info.getModifiedCount();
                unmodified += info.getUnmodifiedCount();
                deleted += info.getDeletedCount();
                processed += info.getProcessedCount();
                info.getCreatedRecords().forEach(createdRecords::add);
                info.getModifiedRecords().forEach(modifiedRecords::add);
                info.getDeletedRecords().forEach(deletedRecords::add);
                info.getFaultsOrdered().flatMap(e -> {
                    FaultEvent faultEvent = FaultEventImpl.error(e.getTechErrorMessage());
                    List<FaultMessage> faultMessages = errorToMessages(faultEvent);
                    return faultMessages.stream().map(f -> new EtlProcessingResultErrorImpl(e.getRecordIndex(), e.getRecordLineNumber(), e.getRawRecord(), formatMessage(f.getMessage(), f.getCode()), e.getTechErrorMessage()));
                }).collect(toList()).forEach(errors::add);
            }
            return new EtlProcessingResultImpl(created, modified, unmodified, deleted, processed, errors, new EtlProcessingResultDetailsImpl(createdRecords, modifiedRecords, deletedRecords)).withTime(begin, now());
        }

        private void addProcessedRecord(Map<String, Object> record, String classId, long id) {
            if (isNotBlank(record.get(CM_IMPORT_RECORD_ID))) {
                processedRecordsByRecordId.put(toStringNotBlank(record.get(CM_IMPORT_RECORD_ID)), card(classId, id));
            }
        }
    }

    private String formatMessage(String message, @Nullable String code) {
        try {
            Pair<String, List<String>> error = Pair.of(message, emptyList());
            if (isParamError(message)) {
                error = serializeError(message);
                message = error.getKey();
            }
            if (isNotBlank(code)) {
                String defaultValue = message;
                message = safe(() -> translationService.translateByCode(code, defaultValue), defaultValue);
            }
            if (error.getValue().isEmpty()) {
                return message;
            }
            return escapeHtml4(format(message, error.getValue().toArray()));
        } catch (Exception ex) {
            logger.error("error on translating message", ex);
        }
        return message;
    }

    private class ImportRegisterClassInfo {

        protected final Set<Long> createdRecords = set(), modifiedRecords = set(), deletedRecords = set(), unmodifiedRecords = set(), processedRecords = set();
        protected final List<EtlProcessingResultFault> faults = list();

        protected final String classId;

        public ImportRegisterClassInfo(String classId) {
            this.classId = checkNotBlank(classId);
        }

        public void addCreatedRecord(Map<String, Object> record, long id) {
            createdRecords.add(id);
            processedRecords.add(id);
        }

        public void addModifiedRecord(Map<String, Object> record, long id) {
            modifiedRecords.add(id);
            processedRecords.add(id);
        }

        public void addUnmodifiedRecord(Map<String, Object> record, long id) {
            unmodifiedRecords.add(id);
            processedRecords.add(id);
        }

        public void addDeletedRecord(long id) {
            deletedRecords.add(id);
        }

        public void addProcessingFault(EtlProcessingResultFault fault) {
            faults.add(fault);
        }

        public Collection<Long> getCreatedRecords() {
            return createdRecords;
        }

        public Collection<Long> getModifiedRecords() {
            return modifiedRecords;
        }

        public Collection<Long> getUnmodifiedRecords() {
            return unmodifiedRecords;
        }

        public Collection<Long> getDeletedRecords() {
            return deletedRecords;
        }

        public List<EtlProcessingResultFault> getFaults() {
            return faults;
        }

        public Stream<EtlProcessingResultFault> getFaultsOrdered() {
            return getFaults().stream().sorted(Ordering.natural().onResultOf(EtlProcessingResultFault::getRecordIndex));
        }

        public int getCreatedCount() {
            return createdRecords.size();
        }

        public int getModifiedCount() {
            return modifiedRecords.size();
        }

        public int getUnmodifiedCount() {
            return unmodifiedRecords.size();
        }

        public int getDeletedCount() {
            return deletedRecords.size();
        }

        public boolean hasProcessedRecord(long id) {
            return processedRecords.contains(id);
        }

        public boolean hasErrors() {
            return !faults.isEmpty();
        }

        public int getProcessedCount() {
            return processedRecords.size();
        }

        public EtlProcessingResult toProcessingResult(boolean includeDetails) {
            EtlProcessingResultDetails details = includeDetails ? new EtlProcessingResultDetailsImpl(getCreatedRecords(), getModifiedRecords(), getDeletedRecords()) : null;
            return new EtlProcessingResultImpl(getCreatedCount(), getModifiedCount(), getUnmodifiedCount(), getDeletedCount(), getProcessedCount(), getFaultsOrdered().collect(toImmutableList()), details);
        }

    }

    private abstract class BaseProcessor {

        protected final EventBus eventBus = new EventBus(logExceptions(logger));
        protected final DateAndFormatPreferences userPreferences = userPreferencesService.getUserPreferences(), templatePreferences;
        protected final BiMap<String, Long> tenantLoader = HashBiMap.create();
        protected final EtlTemplate template;

        protected BaseProcessor(EtlTemplate template) {
            this.template = checkNotNull(template);
            templatePreferences = buildPreferences(userPreferences, template);
            if (template.hasColumnWithAttrName(ATTR_IDTENANT)) {
                operationUser.getUser().getUserTenantContext().getActiveTenantIds().stream().map(dao::getCard).forEach(t -> {
                    tenantLoader.put(switch (template.getColumnByAttrName(ATTR_IDTENANT).getMode()) {
                        case ETCM_CODE ->
                            t.getCode();
                        case ETCM_DESCRIPTION ->
                            t.getDescription();
                        default ->
                            toStringNotBlank(t.getId());
                    }, t.getId());
                });
            }
        }

        protected UserPrefHelper getHelper(EtlTemplateColumnConfig column) {//TODO cache this
            return new UserPrefHelperImpl(buildPreferences(templatePreferences, column));
        }

        protected String getValueArraySeparator() {
            return checkNotBlank(etlConfiguration.getValueArraySeparator());
        }

    }

    private DateAndFormatPreferences buildPreferences(DateAndFormatPreferences inner, EtlTemplateFieldFormatConfig config) {
        return DateAndFormatPreferencesImpl.copyOf(inner).accept(c -> {
            if (config.hasDecimalSeparator()) {
                c.withDecimalSeparator(config.getDecimalSeparator());
            }
            if (config.hasThousandsSeparator()) {
                c.withNumberGroupingSeparator(config.getThousandsSeparator());
            }
            if (etlConfiguration.getThousandsSeparator() != null) {
                c.withNumberGroupingSeparator(etlConfiguration.getThousandsSeparator());
            }
            Function<String, String> convertDateTimeFormat = format -> {
                return switch (config.getDateTimeMode()) {
                    case ETDT_EXTJS ->
                        extjsDateTimeFormatToJavaDateTimeFormat(format);
                    case ETDT_JAVA ->
                        format;
                };
            };
            if (config.hasDateFormat()) {
                c.withDateFormat(convertDateTimeFormat.apply(config.getDateFormat()));
            }
            if (config.hasTimeFormat()) {
                c.withTimeFormat(convertDateTimeFormat.apply(config.getTimeFormat()));
            }
            if (config.hasDateTimeFormat()) {
                c.withDateTimeFormat(convertDateTimeFormat.apply(config.getDateTimeFormat()));
            }
        }).build();
    }

    private abstract class ImportProcessor extends BaseProcessor {

        protected final ImportRegisterClassInfo info;
        protected final ImportRegister register;
        protected final List<EtlTemplateColumnConfig> importColumns;
        protected EntryTypeHelper entryTypeHelper;
        protected Object data;
        protected boolean hasLineNumber = false;
        protected final boolean handleMissingRecordsOnError;
        private final Cache<String, Optional<Object>> referenceHelperCache = CacheBuilder.newBuilder().maximumSize(etlConfiguration.getReferenceCacheMaxSize()).build();
        protected final Map<String, Object> defaults;
        protected final ZonedDateTime begin = now();
        private final Supplier<List<Map<String, Object>>> preparedData = Suppliers.memoize(this::prepareData);

        public ImportProcessor(ImportContext context) {
            super(context.getTemplate());
            checkArgument(template.isImportTemplate(), "invalid template: this is not an import template");
            importColumns = listOf(EtlTemplateColumnConfig.class).accept(l -> template.getColumns().forEach(c -> {
                if (c.hasMode(ETCM_DEFAULT) && c.hasReferenceTemplate()) {
                    templateRepository.getTemplateByName(c.getReferenceTemplate()).getImportKeyAttributes().stream()
                            .map(a -> EtlTemplateColumnConfigImpl.builder().withMode(ETCM_IGNORE).withColumnName(templateRepository.getTemplateByName(c.getReferenceTemplate()).getColumnByAttrName(a).getColumnName()).withAttributeName(format("%s_%s", c.getAttributeName(), a)).build())
                            //                            .map(a -> EtlTemplateColumnConfigImpl.builder().withMode(ETCM_IGNORE).withColumnName(format("%s_%s", c.getColumnNameOrAttributeName(), a)).withAttributeName(format("%s_%s", c.getAttributeName(), a)).build())
                            .forEach(l::add);
//                            .map(a -> EtlTemplateColumnConfigImpl.builder().withColumnName(c.getColumnName()).withAttributeName(format("%s_%s", c.getAttributeName(), a)).build()).forEach(l::add);
                } else {
                    l.add(c);
                }
            }));
            register = context.getRegister();
            handleMissingRecordsOnError = template.getHandleMissingRecordsOnError();
            info = context.getRegister().getInfoByClass(template.getTargetName());
            data = context.getData();
            defaults = template.getColumns().stream().filter(EtlTemplateColumnConfig::hasDefault).collect(toImmutableMap(EtlTemplateColumnConfig::getAttributeName, EtlTemplateColumnConfig::getDefault));
            context.getEventListener().ifPresent(eventBus::register);
        }

        protected abstract List<Map<String, Object>> prepareDataInner() throws Exception;

        private CmdbFilter getFilter() {
            return mapFilterForProcessedRecords(template.getFilter());
        }

        private CmdbFilter getReferenceFilter() {
            return mapFilterForProcessedRecords(template.getReferenceFilter());
        }

        private CmdbFilter mapFilterForProcessedRecords(CmdbFilter filter) {
            return filter.mapValues(map(v -> register.processedRecordsByRecordId.containsKey(v) ? register.processedRecordsByRecordId.get(v).getId().toString() : v));
        }

        public List<? extends DatabaseRecord> importDataInline(Classe entryType) throws Exception {
            checkArgument(equal(template.getMergeMode(), EM_NO_MERGE), "invalid templat merge mode for inline import, required no_merge");
            entryTypeHelper = new ClassHelper(entryType);
            return list(getRawRecords()).mapWithIndex((i, r) -> entryTypeHelper.prepareRecord(toRecordInfo(r, i), true, false).getRecord());
        }

        public EtlProcessingResult importData() throws Exception {
            createUpdateRecords(true, true);
            handleMissingRecords();
            return getResult().withTime(begin, now());
        }

        public void createNewRecords() throws Exception {
            createUpdateRecords(true, false);
        }

        public void updateExistingRecords() throws Exception {
            createUpdateRecords(false, true);
        }

        public void createUpdateRecords() throws Exception {
            createUpdateRecords(true, true);
        }

        private void createUpdateRecords(boolean create, boolean update) throws Exception {
            List<Map<String, Object>> rawRecords = getRawRecords();
            prepareHelper();
            int size = rawRecords.size();
            logger.info("load {} records (create = {}, update = {})", size, create, update);
            Consumer<Long> processProgressListener = buildProgressListener(size, (e) -> {
                if (size > 100) {
                    logger.info("import progress: {}", e.getProgressDescriptionDetailed());
                }
            });

            Set<String> keys = set();

            List<Pair<EtlRecordInfo, RecordReadyForImport>> queues = list();

            AtomicInteger recordNumber = new AtomicInteger(0);
            for (Map<String, Object> rawRecord : rawRecords) {
                EtlRecordInfo recordInfo = toRecordInfo(rawRecord, recordNumber.getAndIncrement());
                Pair<EtlRecordInfo, RecordReadyForImport> res;
                try {
                    RecordReadyForImport recordReadyForImport = entryTypeHelper.prepareRecord(recordInfo, create, update);
                    res = Pair.of(recordInfo, recordReadyForImport);
                } catch (Exception ex) {
                    handleRecordError(ex, recordInfo);
                    res = Pair.of(recordInfo, null);
                }
                queues.add(res);
            }
            if (hasReferenceLoop() && size > 0) {
                queues = sortReferenceLoopRecords(queues);
            }

            AtomicLong progress = new AtomicLong(0);
            for (Pair<EtlRecordInfo, RecordReadyForImport> item : queues) {
                try {
                    if (item.getRight() != null) {
                        try {
                            if (isMergeEnabled()) {
                                checkArgument(keys.add(nullToEmpty(item.getRight().getKey())), "invalid record: duplicate key =< %s >", item.getRight().getKey());
                            }
                        } catch (Exception ex) {
                            handleRecordError(ex, item.getLeft());
                            continue;
                        }
                        if (item.getRight().isCreate()) {
                            processCreate(item.getRight(), item.getLeft());
                        } else if (item.getRight().isUpdate()) {
                            try {
                                item.getRight().doUpdate();
                            } catch (Exception ex) {
                                handleRecordError(ex, item.getLeft());
                            }
                        }
                        item.getRight().executePostScript();
                    }
                } finally {
                    processProgressListener.accept(progress.getAndIncrement());
                }
            }
            logger.debug("record processing complete");
        }

        private EtlRecordInfo toRecordInfo(Map<String, Object> rawRecord, int recordNumber) {
            int recordLineNumber;
            if (hasLineNumber) {
                recordLineNumber = toInt(rawRecord.get(IMPORT_RECORD_LINE_NUMBER));
                rawRecord = map(rawRecord).withoutKey(IMPORT_RECORD_LINE_NUMBER);
            } else {
                recordLineNumber = recordNumber;
            }
            return new EtlRecordInfoImpl(recordNumber, recordLineNumber, rawRecord);
        }

        private void prepareHelper() {
            if (entryTypeHelper == null) {
                entryTypeHelper = getTarget(template);
            }
        }

        private boolean isMergeEnabled() {
            return switch (template.getMergeMode()) {
                case EM_NO_MERGE ->
                    false;
                default ->
                    true;
            };
        }

        private void handleRecordError(Exception ex, EtlRecordInfo recordInfo) {
            info.addProcessingFault(new EtlProcessingResultErrorImpl(recordInfo, exceptionToUserMessage(ex), exceptionToMessage(ex)));
            switch (template.getOnRecordError()) {
                case ETE_FAIL ->
                    throw new EtlException(ex, "error processing record = %s at line = %s", recordInfo.getRecordIndex(), recordInfo.getRecordLineNumber());
                case ETE_LOG_ERROR ->
                    logger.error(marker(), "error loading record = {} with template =< {} >", recordInfo, template.getCode(), ex);
                case ETE_LOG_WARNING ->
                    logger.warn(marker(), "error loading record = {} with template =< {} >", recordInfo, template.getCode(), ex);
            }
        }

        private void processCreate(RecordReadyForImport record, EtlRecordInfo recordInfo) {
            try {
                record.postCreate(dao.createOnly(record.getRecord()), record.getRecord(), recordInfo.getRawRecord());
            } catch (Exception ex) {
                handleRecordError(ex, recordInfo);
            }
        }

        public void handleMissingRecords() throws Exception {
            if (isInterrupted()) {
                logger.warn(marker(), "process was interrupted, skipping missing records processing");
            } else {
                logger.debug("prepare missing records processing");
                getRawRecords();
                prepareHelper();
                if (template.hasMergeMode(EM_DELETE_MISSING, EM_UPDATE_ATTR_ON_MISSING)) {
                    if (info.hasErrors() && !handleMissingRecordsOnError) {
                        logger.warn(marker(), "import has errors, skip processing of missing records");
                    } else if (info.getProcessedCount() == 0 && !handleMissingRecordsOnError) {
                        logger.warn(marker(), "import has processed 0 records, skip processing of missing records");
                    } else {
                        logger.debug("handle missing records");
                        entryTypeHelper.handleMissingRecords();
                    }
                }
            }
        }

        public EtlProcessingResult getResult() {
            return info.toProcessingResult(true);
        }

        private List<Map<String, Object>> getRawRecords() {
            return preparedData.get();
        }

        private List<Map<String, Object>> prepareData() {
            try {
                logger.info("read source records");
                return prepareDataInner().stream()
                        .filter(r -> !getAllColumns(template.getColumns())
                        .filter(EtlTemplateColumnConfig::doNotIgnoreColumn)
                        .map(EtlTemplateColumnConfig::getAttributeName)
                        .map(r::get)
                        .allMatch(CmNullableUtils::isNullOrEmpty))
                        .filter(predicateFromFilter(template.getImportFilter()))
                        .map(r -> isBlank(r.get(CM_IMPORT_RECORD_ID)) ? map(r).with(CM_IMPORT_RECORD_ID, randomId()) : r)
                        .collect(toImmutableList());
            } catch (Exception ex) {
                throw new EtlException(ex);
            }
        }

        private Stream<EtlTemplateColumnConfig> getAllColumns(List<EtlTemplateColumnConfig> columns) {
            return columns.stream().flatMap(this::getReferenceColumns);
        }

        private Stream<EtlTemplateColumnConfig> getReferenceColumns(EtlTemplateColumnConfig column) {
            if (column.hasReferenceTemplate()) {
                return list(column).with(getAllColumns(templateRepository.getTemplateByName(column.getReferenceTemplate()).getColumns())).stream(); // recursive column search
            } else {
                return list(column).stream();
            }
        }

        protected Stream<Map<String, Object>> processRecords(Stream<Map<String, Object>> rawRecords) {
            return rawRecords.filter(r -> !template.getColumns().stream().filter(EtlTemplateColumnConfig::doNotIgnoreColumn).map(EtlTemplateColumnConfig::getAttributeName).map(r::get).allMatch(CmNullableUtils::isNullOrEmpty));
        }

        protected void checkHeader(List<? extends Object> row) {
            logger.trace("check header row data = {}", lazyRecordToString(row));//TODO improve row debug dump
            List<String> rawLine = list(transform(row, s -> toStringOrEmpty(s).trim())),
                    expected = list(transform(importColumns, EtlTemplateColumnConfig::getColumnName)),
                    lineToCheck = rawLine;
            if (template.getSkipUnknownColumns()) {
                lineToCheck = list(lineToCheck).without(not(expected::contains));
            }
            if (lineToCheck.size() > importColumns.size()) {
                lineToCheck = lineToCheck.subList(0, importColumns.size());
            }
            if (template.getIgnoreColumnOrder()) {
                checkArgument(lineToCheck.size() == importColumns.size() && equal(set(lineToCheck), set(expected)), "invalid header row: expected (in any order) = %s but found = %s", new TreeSet(expected), rawLine);
                reorderColumnConfigs(rawLine);
                logger.debug("actual column order = {}", list(transform(importColumns, EtlTemplateColumnConfig::getColumnName)));
            } else {
                checkArgument(equal(lineToCheck, expected), "invalid header row: expected (in this order) = %s but found = %s", expected, rawLine);
            }
            logger.debug("header is ok");
        }

        protected void reorderColumnConfigs(List<String> colHeaders) {
            Map<String, EtlTemplateColumnConfig> configsByColumnHeader = uniqueIndex(template.getColumns(), EtlTemplateColumnConfig::getColumnName);
            importColumns.clear();
            colHeaders.stream().map(c -> {
                EtlTemplateColumnConfig col = configsByColumnHeader.get(c);
                if (template.getSkipUnknownColumns() && col == null) {
                    col = EtlTemplateColumnConfigImpl.builder().withMode(ETCM_IGNORE).build();
                }
                return checkNotNull(col, "config not found for column header =< %s >", c);
            }).forEach(importColumns::add);
        }

        protected Consumer<FluentMap<String, Object>> handleMultiValues(Map<String, Object> rawData) {
            return m -> {
                template.getColumns().stream().filter(c -> c.hasReferenceTemplate()).forEach(c -> {
                    EtlTemplate refTemplate = templateRepository.getTemplateByName(c.getReferenceTemplate());
                    Object value = refTemplate.getImportKeyAttributes().stream().map(k -> rawData.get(format("%s_%s", c.getAttributeName(), refTemplate.getColumnByAttrName(k).getAttributeName()))).collect(toList());
                    m.put(c.getAttributeName(), convertValueToSystem(c.getAttributeName(), value));
                });
            };
        }

        protected Classe getTargetClassFromTemplateColumnOrNull(EtlTemplateColumnConfig column) {
            if (isNotBlank(column.hasReferenceTemplate())) {
                EtlTemplate refTemplate = templateRepository.getTemplateByName(column.getReferenceTemplate());
                return dao.getClasseOrNull(refTemplate.getTargetName());
            }
            return null;
        }

        protected Map<String, List<String>> getMultiKeysFromTemplateColumn() {
            Map<String, List<String>> multiKeys = map();
            template.getColumns().stream().filter(c -> c.hasReferenceTemplate()).forEach(c -> {
                EtlTemplate refTemplate = templateRepository.getTemplateByName(c.getReferenceTemplate());
                multiKeys.put(c.getAttributeName(), refTemplate.getImportKeyAttributes().stream().map(k -> format("%s_%s", c.getAttributeName(), refTemplate.getColumnByAttrName(k).getAttributeName())).collect(toList()));
            });
            return multiKeys;
        }

        protected Map<String, List<String>> getMultiColumnFromTemplateColumn() {
            Map<String, List<String>> multiColumnName = map();
            template.getColumns().stream().filter(c -> c.hasReferenceTemplate()).forEach(c -> {
                EtlTemplate refTemplate = templateRepository.getTemplateByName(c.getReferenceTemplate());
                multiColumnName.put(c.getAttributeName(), refTemplate.getImportKeyAttributes().stream().map(k -> refTemplate.getColumnByAttrName(k).getColumnName()).collect(toList()));
            });
            return multiColumnName;
        }

        protected List<EtlTemplateColumnConfig> getColumnsFromTemplateReference() {
            List<EtlTemplateColumnConfig> columns = list();
            template.getColumns().stream().filter(EtlTemplateColumnConfig::hasReferenceTemplate).forEach(c -> {
                EtlTemplate refTemplate = templateRepository.getTemplateByName(c.getReferenceTemplate());
                refTemplate.getImportKeyAttributes().forEach(k -> columns.add(templateRepository.getTemplateByName(c.getReferenceTemplate()).getColumnByAttrName(k)));
            });
            return columns;
        }

        @Nullable
        private <T> T convertValueToSystem(String attributeName, @Nullable Object value) {
            if (template.hasNonExprColumnWithAttrName(attributeName)) {
                String columnName = "<undefined>";
                try {
                    EtlTemplateColumnConfig columnConfig = template.getColumnByAttrName(attributeName);
                    if (!columnConfig.ignoreColumn()) {
                        return switch (columnConfig.getAttributeType()) {
                            case AT_DEFAULT -> {
                                Attribute attribute = entryTypeHelper.getEntryType().getAttribute(attributeName);
                                yield convertValueToSystem(attribute, columnConfig, value);
                            }
                            case AT_RELATION -> {
                                Domain domain = entryTypeHelper.getDomain(attributeName).getThisDomainWithDirection(columnConfig.getRelationDirection());
                                checkArgument(domain.isDomainForSourceClasse(entryTypeHelper.getEntryType().asClasse()));
                                Classe target = domain.getTargetClass();
                                yield (T) processRefValue(target, columnConfig, value);
                            }
                        };
                    }
                } catch (Exception ex) {
                    throw new EtlException(ex, "error importing value for attribute =< %s > column =< %s > value =< %s >", attributeName, columnName, value);
                }
            }
            return (T) value;
        }

        @Nullable
        private <T> T convertValueToSystem(Attribute attribute, EtlTemplateColumnConfig columnConfig, @Nullable Object value) {
            try {
                if (attribute.getOwner().isDomain() && set(ATTR_IDOBJ1, ATTR_IDOBJ2).contains(attribute.getName())) {
                    Classe target = switch (attribute.getName()) {
                        case ATTR_IDOBJ1 ->
                            ((Domain) attribute.getOwner()).getSourceClass();
                        case ATTR_IDOBJ2 ->
                            ((Domain) attribute.getOwner()).getTargetClass();
                        default ->
                            throw new IllegalArgumentException("unsupported attribute = " + attribute);
                    };
                    value = processRefValue(target, columnConfig, value);
                }
                if (attribute.getName().equals(ATTR_IDTENANT)) {
                    value = checkNotNull(tenantLoader.get(toStringOrNull(value)), "tenant id not found");
                } else {
                    value = switch (attribute.getType().getName()) {
                        case REFERENCE, FOREIGNKEY ->
                            processRefValue(refAttrHelperService.getTargetClassForAttribute(attribute), columnConfig, value);
                        case LOOKUP ->
                            isNullOrBlank(value) ? null : new LookupConverterHelper(attribute.getType().as(LookupAttributeType.class).getLookupTypeName(), columnConfig).convertValueToSystem(value);
                        case LOOKUPARRAY ->
                            isNullOrBlank(value) ? emptyList() : Splitter.on(getValueArraySeparator()).splitToList(toStringNotBlank(value)).stream().map(new LookupConverterHelper(attribute.getType().as(LookupArrayAttributeType.class).getLookupTypeName(), columnConfig)::convertValueToSystem).collect(toImmutableList());
                        case TIMESTAMP ->
                            getHelper(columnConfig).parseDateTime(value);
                        case DATE ->
                            getHelper(columnConfig).parseDate(value);
                        case TIME ->
                            getHelper(columnConfig).parseTime(value);
                        case DECIMAL, DOUBLE, FLOAT, INTEGER, LONG -> {
                            if (value instanceof String string) {
                                yield getHelper(columnConfig).parseNumber(string);
                            } else {
                                yield value;
                            }
                        }
                        default ->
                            value;
                    };
                }
//                value = rawToSystem(attribute, value);
                value = attribute.isOfType(REFERENCE, FOREIGNKEY) && value instanceof String ? value : rawToSystem(attribute, value); //TODO improve this
            } catch (Exception ex) {
                value = null;
                switch (columnConfig.getOnErrorAction()) {
                    case CEA_FAIL ->
                        throw ex;
                    case CEA_LOG ->
                        logger.warn(marker(), "error importing value for attribute =< {} > column =< {} > value =< {} >", attribute.getName(), columnConfig.getColumnName(), value, ex);
                    case CEA_IGNORE -> {
                    }
                }
            }
            if (columnConfig.hasRequiredAttrMode(RAM_REQUIRED) || (columnConfig.hasRequiredAttrMode(RAM_AUTO) && attribute.isMandatory())) {
                checkArgument(isNotBlank(value), parseErrorCode("850", "missing value for required attr =< %s >", attribute.getName()));
            }
            return (T) value;
        }

        private class LookupConverterHelper {

            private final String lookupType;
            private final EtlTemplateColumnConfig columnConfig;

            public LookupConverterHelper(String lookupType, EtlTemplateColumnConfig columnConfig) {
                this.lookupType = checkNotBlank(lookupType);
                this.columnConfig = checkNotNull(columnConfig);
            }

            public LookupValue convertValueToSystem(Object value) {
                return convertValueToSystem(columnConfig.getMode(), value);
            }

            private LookupValue convertValueToSystem(EtlTemplateColumnMode mode, Object value) {
                return switch (mode) {
                    case ETCM_CODE ->
                        handleOnMissingRefAction(lookupService.getLookupByTypeAndCodeOrNull(lookupType, toStringNotBlank(value)), toStringNotBlank(value), toStringNotBlank(value));
                    case ETCM_DESCRIPTION ->
                        handleOnMissingRefAction(lookupService.getLookupByTypeAndDescriptionOrNull(lookupType, toStringNotBlank(value)), StringUtils.deleteWhitespace(toStringNotBlank(value)), toStringNotBlank(value));
                    case ETCM_DESCRIPTION_TRANSLATED ->
                        handleOnMissingRefAction(lookupService.getActiveTranslatedLookupOrNull(lookupType, toStringNotBlank(value)), StringUtils.deleteWhitespace(toStringNotBlank(value)), toStringNotBlank(value));
                    case ETCM_ID ->
                        checkNotNull(lookupService.getLookupOrNull(toLong(value)), parseErrorCode("851", "lookup not found for id =< %s >", value.toString()));
                    case ETCM_DEFAULT ->
                        NumberUtils.isCreatable(toStringOrNull(value)) ? convertValueToSystem(ETCM_ID, value) : convertValueToSystem(ETCM_CODE, value);
                    default ->
                        throw new EtlException("invalid column mode = %s for lookup attr", columnConfig.getMode());
                };
            }

            private LookupValue handleOnMissingRefAction(@Nullable LookupValue value, String code, String description) {
                return switch (columnConfig.getOnMissingRefAction()) {
                    case CMA_CREATE ->
                        Optional.ofNullable(value).orElseGet(() -> lookupService.createLookupValue(lookupType, code, description));
                    case CMA_IGNORE ->
                        value;
                    case CMA_ERROR ->
                        checkNotNull(value, parseErrorCode("852", "lookup not found for value =< %s >", code));
                };
            }

        }

        public boolean hasReferenceLoop() {
            return template.getColumns().stream().anyMatch(this::isAttributeReferenceLoop);
        }

        private boolean isAttributeReferenceLoop(EtlTemplateColumnConfig column) {
            return column.doNotIgnoreColumn() && !column.hasExpr() && !column.isRelation() && entryTypeHelper.getEntryType().hasAttribute(column.getAttributeName()) && entryTypeHelper.getEntryType().getAttribute(column.getAttributeName()).isOfType(REFERENCE, FOREIGNKEY) && isReferenceLoop(refAttrHelperService.getTargetClassForAttribute(entryTypeHelper.getEntryType().getAttribute(column.getAttributeName())));
        }

        private boolean isReferenceLoop(Classe target) {
            return equal(template.getTargetName(), target.getName()) || (entryTypeHelper.getEntryType().isClasse() && entryTypeHelper.getEntryType().asClasse().equalToOrDescendantOf(target));
        }

        private Map<String, Object> handleReferenceLoopRecord(Map<String, Object> card) {
            return map(card).accept(c -> {
                list(entryTypeHelper.getEntryType().getAllAttributes()).filter(a -> template.hasColumnWithAttrName(a.getName()) && isAttributeReferenceLoop(template.getColumnByAttrName(a.getName()))).forEach(a -> {
                    Object value = c.get(a.getName());
                    if (isNotBlank(value) && value instanceof String) {
                        logger.debug("changing reference loop {} : {} with value =< {} >", a.getName(), value, register.processedRecordsByRecordId.get(toStringOrNull(value)));
                        c.put(a.getName(), handleOnMissingRefAction(entryTypeHelper.getEntryType().asClasse(), template.getColumnByAttrName(a.getName()), value, register.processedRecordsByRecordId.get(toStringNotBlank(value))));
                    }
                });
            });
        }

        private List<Pair<EtlRecordInfo, RecordReadyForImport>> sortReferenceLoopRecords(Collection<Pair<EtlRecordInfo, RecordReadyForImport>> records) {
            List<Pair<EtlRecordInfo, RecordReadyForImport>> list = list(records);

            logger.debug("sorting records = {}", list);

            template.getColumns().stream().filter(this::isAttributeReferenceLoop).forEach(c -> {
                Multimap<String, Pair<EtlRecordInfo, RecordReadyForImport>> recordsByParent = multimap();
                list(list).filter(r -> r.getRight() != null && r.getRight().hasRecord()).forEach(r -> {
                    Object value = r.getRight().getRecordDataBeforeLastFix().get(c.getAttributeName());
                    if (isNotBlank(value) && value instanceof String) {
                        logger.debug("sorting records, extract element = {}", r);
                        recordsByParent.put(toStringNotBlank(value), r);
                        checkArgument(list.remove(r));
                    }
                });
                int stopLoop = 0;
                while (!recordsByParent.isEmpty() && stopLoop != recordsByParent.size()) {
                    FluentList<Pair<EtlRecordInfo, RecordReadyForImport>> thisList = list(list);
                    stopLoop = recordsByParent.size();
                    recordsByParent.forEach((k, v) -> {
                        thisList.stream().forEach(r -> {
                            if (k.equals(r.getLeft().getRawRecord().get(CM_IMPORT_RECORD_ID))) {
                                logger.debug("sorting records, append element = {}", v);
                                list.add(v);
                                recordsByParent.remove(k, v);
                            }
                        });
                    });
                }
                recordsByParent.values().forEach(list::add);
            });

            logger.debug("sorted records = {}", list);
            checkArgument(list.size() == records.size());

            return list;
        }

        @Nullable
        private Object processRefValue(Classe target, EtlTemplateColumnConfig columnConfig, @Nullable Object value) {
            if (isBlank(value)) {
                return null;
            } else {
                Object res;
                if (isReferenceLoop(target)) {
                    res = doProcessRefValue(target, columnConfig, value);
                } else {
                    try {
                        res = referenceHelperCache.get(key(target.getName(), columnConfig.getMode(), toStringOrEmpty(value)), () -> Optional.ofNullable(doProcessRefValue(target, columnConfig, value)).map(c -> c instanceof CardIdAndClassName ca ? CardIdAndClassNameImpl.copyOf(ca) : c)).orElse(null);
                    } catch (ExecutionException ex) {
                        throw runtime(ex);
                    }
                }
                return handleOnMissingRefAction(target, columnConfig, value, res);
            }
        }

        @Nullable
        private Object handleOnMissingRefAction(Classe target, EtlTemplateColumnConfig columnConfig, Object value, Object ref) {
            if (isBlank(ref)) {
                return switch (columnConfig.getOnMissingRefAction()) {
                    case CMA_CREATE -> {
                        Object cardOrRecordId;
                        if (columnConfig.hasReferenceTemplate()) {
                            EtlTemplate refTemplate = templateRepository.getTemplateByName(columnConfig.getReferenceTemplate());
                            Classe subTarget = getTargetClassFromTemplateColumnOrNull(columnConfig);
                            Map<String, Object> attributes = map();
                            refTemplate.getColumns().stream().filter(c -> c.hasDefault() || refTemplate.getImportKeyAttributes().contains(c.getAttributeName())).forEach(attributeColumnConfig -> {
                                logger.debug("creating card with attribute {} =< {} >", attributeColumnConfig.getAttributeName(), firstNotNull(toStringOrNull(attributeColumnConfig.getDefault()), toStringNotBlank(value)));
                                attributes.put(attributeColumnConfig.getAttributeName(), convertValueToSystem(subTarget.getAttribute(attributeColumnConfig.getAttributeName()), attributeColumnConfig, firstNotNull(toStringOrNull(attributeColumnConfig.getDefault()), toStringNotBlank(value))));
                            });
                            cardOrRecordId = dao.create(buildCard(subTarget, attributes));
                        } else {
                            cardOrRecordId = dao.create(buildCard(target, ATTR_CODE, toStringNotBlank(value), ATTR_DESCRIPTION, toStringNotBlank(value)));
                        }
                        referenceHelperCache.invalidate(key(target.getName(), columnConfig.getMode(), toStringOrEmpty(value)));
                        yield cardOrRecordId;
                    }
                    case CMA_IGNORE ->
                        null;
                    case CMA_ERROR ->
                        checkNotNull(ref, parseErrorCode("853", "card not found for type = %s,  %s =< %s >", target.getName(), serializeEnum(columnConfig.getMode()), value.toString()));
                };
            } else {
                return ref;
            }
        }

        @Nullable
        private Object doProcessRefValue(Classe target, EtlTemplateColumnConfig columnConfig, Object value) {
            CmdbFilter filter = noopFilter();
            if (template.hasReferenceFilter()) {
                template.getReferenceFilter().checkHasOnlySupportedFilterTypes(CONTEXT);
                ContextFilter masterCard = getReferenceFilter().getContextFilter();
                if (target.equalToOrAncestorOf(dao.getClasse(masterCard.getClassName()))) {
                    logger.debug("shortcut reference filter for template = {}, return master card = {}", template, masterCard);
                    return card(masterCard.getClassName(), masterCard.getId());//TODO check this
                }
                Attribute attribute = refAttrHelperService.getAttrForMasterCardFilterOrNull(target, dao.getClasse(masterCard.getClassName()));
                if (attribute != null) {
                    logger.debug("add reference filter from master card = {} to template = {} using reference attr = {}", masterCard, template, attribute);
                    filter = AttributeFilterConditionImpl.eq(attribute.getName(), masterCard.getId()).toAttributeFilter().toCmdbFilters();
                } else {
                    logger.debug("skip master card filter for template = {} (no unique reference attr found)", template);
                }
            }
            EtlTemplateColumnMode mode = switch (columnConfig.getMode()) {
                case ETCM_DEFAULT ->
                    columnConfig.hasReferenceTemplate() ? ETCM_DEFAULT : NumberUtils.isCreatable(toStringOrEmpty(value)) ? ETCM_ID : ETCM_CODE;
                default ->
                    columnConfig.getMode();
            };
            List<String> keys = switch (mode) {
                case ETCM_CODE, ETCM_CODE_CASEINSENSITIVE ->
                    singletonList(ATTR_CODE);
                case ETCM_DESCRIPTION, ETCM_DESCRIPTION_CASEINSENSITIVE ->
                    singletonList(ATTR_DESCRIPTION);
                case ETCM_DEFAULT ->
                    columnConfig.hasReferenceTemplate() ? checkNotEmpty(templateRepository.getTemplateByName(columnConfig.getReferenceTemplate()).getImportKeyAttributes()) : singletonList("*");
                default ->
                    singletonList("*");
            };
            List<Object> values = (value instanceof Iterable || keys.size() > 1) ? convert(value, List.class) : singletonList(value);
            checkArgument(keys.size() == values.size());
            Map<String, Object> valuesByKey = map(keys, identity(), k -> values.get(keys.indexOf(k)));

            Object cardOrRecordId = switch (mode) {
                case ETCM_DEFAULT ->
                    dao.select(ATTR_ID).from(target).accept(q -> valuesByKey.forEach((k, v) -> q.where(k, EQ, v))).where(filter).getCardOrNull();
                case ETCM_CODE, ETCM_DESCRIPTION ->
                    dao.select(ATTR_ID).from(target).accept(q -> valuesByKey.forEach((k, v) -> q.where(k, EQ, toStringNotBlank(v)))).where(filter).getCardOrNull();
                case ETCM_CODE_CASEINSENSITIVE, ETCM_DESCRIPTION_CASEINSENSITIVE ->
                    dao.select(ATTR_ID).from(target).accept(q -> valuesByKey.forEach((k, v) -> q.where(k, EQ_CASE_INSENSITIVE, toStringNotBlank(v)))).where(filter).getCardOrNull();
                case ETCM_ID ->
                    dao.select(ATTR_ID).from(target).accept(q -> q.where(ATTR_ID, EQ, toLong(value))).getCardOrNull(); //TODO filter ??
                case ETCM_RECORDID ->
                    register.processedRecordsByRecordId.get(toStringNotBlank(value));
                default ->
                    throw new EtlException("invalid column mode = %s", columnConfig.getMode());
            };

            if (cardOrRecordId == null && isReferenceLoop(target)) {
                cardOrRecordId = switch (mode) {
                    case ETCM_CODE, ETCM_DESCRIPTION, ETCM_DEFAULT ->
                        keys.stream().allMatch(template::hasColumnWithAttrName) ? getRawRecords().stream().filter(r -> isNotBlank(r.get(CM_IMPORT_RECORD_ID)) && keys.stream().allMatch(k -> equal(convertValueToSystem(k, r.get(k)), toStringNotBlank(valuesByKey.get(k))))).collect(toOptional()).map(r -> r.get(CM_IMPORT_RECORD_ID)).orElse(null) : null;
                    case ETCM_RECORDID ->
                        getRawRecords().stream().anyMatch(r -> isNotBlank(r.get(CM_IMPORT_RECORD_ID)) && Objects.equals(r.get(CM_IMPORT_RECORD_ID), value)) ? value : null;
                    default ->
                        null;
                };
            }

            return cardOrRecordId;
        }

        private EntryTypeHelper getTarget(EtlTemplate template) {
            return switch (template.getTargetType()) {
                case ET_CLASS, ET_PROCESS ->
                    new ClassHelper();
                case ET_DOMAIN ->
                    new DomainHelper();
                case ET_VIEW ->
                    new ViewHelper();
                case ET_RECORD ->
                    throw unsupported("unable to execute import/export template with target type = %s", template.getTargetType());
            };
        }

        private abstract class EntryTypeHelper {

            public abstract EntryType getEntryType();

            public abstract RecordReadyForImport prepareRecord(EtlRecordInfo record, boolean create, boolean update);

            public abstract void handleMissingRecords();

            protected void handleMissingRecordSafe(long cardId) {
                try {
                    handleMissingRecord(cardId);
                } catch (Exception ex) {
                    logger.warn(marker(), "error processing missing record = {}", cardId, ex);
                    info.addProcessingFault(new EtlProcessingResultErrorImpl(0l, 0l, map(ATTR_ID, cardId), exceptionToUserMessage(ex), exceptionToMessage(ex)));                    //TODO check record info (??)
                }
            }

            protected void handleMissingRecord(long cardId) {
                switch (template.getMergeMode()) {
                    case EM_DELETE_MISSING -> {
                        logger.debug("delete missing record = {}", cardId);
                        dao.delete(getEntryType(), cardId);
                        info.addDeletedRecord(cardId);
                    }
                    case EM_UPDATE_ATTR_ON_MISSING -> {
                        DatabaseRecord currentRecord = switch (template.getUpdateAttrOnMissingType()) {
                            case UAOMT_ATTRIBUTE ->
                                dao.select(ATTR_ID, checkNotBlank(template.getAttributeNameForUpdateAttrOnMissing())).from(getEntryType().asClasse()).where(ATTR_ID, EQ, cardId).getCard();
                            case UAOMT_SCRIPT ->
                                Optional.ofNullable(template.getAvailableAttrsForScriptOnMissing())
                                .map(attrs -> dao.select(list(ATTR_ID).with(attrs)).from(getEntryType().asClasse()).where(ATTR_ID, EQ, cardId).getCard())
                                .orElse(dao.getCard(getEntryType().asClasse(), cardId));
                            default ->
                                throw unsupported("unable to handle missing record, attribute or script not defined");
                        };

                        DatabaseRecord newRecord = switch (template.getUpdateAttrOnMissingType()) {
                            case UAOMT_ATTRIBUTE ->
                                CardImpl.copyOf(currentRecord).accept(b -> {
                                    String attributeName = checkNotBlank(template.getAttributeNameForUpdateAttrOnMissing());
                                    Attribute attribute = getEntryType().getAttribute(attributeName);
                                    EtlTemplateColumnConfig colConfig = EtlTemplateColumnConfigImpl.builder().withAttributeName(attributeName).withColumnName("DUMMY").accept(c -> {
                                        if (attribute.isOfType(REFERENCE, FOREIGNKEY, LOOKUP)) {
                                            if (NumberUtils.isCreatable(template.getAttributeValueForUpdateAttrOnMissing()) && equal(parseEnumOrDefault(template.getAttributeModeForUpdateAttrOnMissing(), ETCM_ID), ETCM_ID)) {
                                                c.withMode(ETCM_ID);
                                            } else {
                                                c.withMode(ETCM_CODE);
                                            }
                                        } else if (attribute.isOfType(LOOKUPARRAY)) {
                                            if (isLookupArrayIdsAsString(template.getAttributeValueForUpdateAttrOnMissing(), getValueArraySeparator())) {
                                                c.withMode(ETCM_ID);
                                            } else {
                                                c.withMode(ETCM_CODE);
                                            }
                                        }
                                    }).build();
                                    Object value = convertValueToSystem(attribute, colConfig, template.getAttributeValueForUpdateAttrOnMissing());
                                    b.withAttribute(attributeName, value);
                                }).build();
                            case UAOMT_SCRIPT ->
                                CardImpl.copyOf(currentRecord).accept(b -> {
                                    scriptService.helper(EtlTemplateProcessorServiceImpl.this.getClass(), template.getScriptForUpdateAttrOnMissing())
                                            .execute(map(currentRecord.toMap()).with("card", map(currentRecord.toMap()), "_markDirty", false))//TODO convert values for script
                                            .forEach(b::withAttribute);//TODO convert values for script
                                }).build();
                            default ->
                                throw unsupported("unable to handle missing record, attribute or script not defined");
                        };

                        Boolean recordChanged = !newRecord.getAttrsChangedFrom(currentRecord).isEmpty();
                        if (recordChanged || toBooleanOrDefault(newRecord.get("_markDirty"), false)) {
                            logger.debug("update missing record = {}", newRecord);
                            if (recordChanged) {
                                dao.update((Card) newRecord);//TODO
                            }
                            info.addDeletedRecord(currentRecord.getId());//TODO mark deleted only if not already deleted (add filter on query for missing records)
                        }
                    }
                    case EM_LEAVE_MISSING -> {
                        //do nothing
                    }
                    default ->
                        throw new EtlException("unsupported merge mode for missing record processing = %s", template.getMergeMode());
                }
            }

            protected boolean isNewRecordDifferent(DatabaseRecord dbRecord, String attributeName, Object value) {
                return !equal(extractCmPrimitiveIfAvailable(dbRecord.get(attributeName)), extractCmPrimitiveIfAvailable(value));
            }

            protected abstract void updateRecord(DatabaseRecord dbRecord, String attributeName, Object value);

            protected Consumer<FluentMap<String, Object>> handleScriptValues(Map<String, Object> rawData) {
                return m -> template.getColumns().stream().filter(EtlTemplateColumnConfig::hasExpr).forEach(c -> {
                    Attribute attribute = entryTypeHelper.getEntryType().getAttribute(c.getAttributeName());
                    String script = c.getExpr();
                    logger.trace("execute script expression =< {} > for col = {} record = {}", abbreviate(script), c, mapToLoggableStringInlineLazy(rawData));
                    Object value = scriptService.helper(getClass(), script).withClassLoader(firstNotBlankOrNull(c.getClasspath(), template.getClasspath()))
                            .executeForOutput(map(rawData).with("record", unmodifiableMap(rawData)).with("processed", unmodifiableMap(m)));//TODO check params & names !!
                    logger.trace("processed script expression for col = {}, output =< {} > ( {} )", c, value, getClassOfNullable(value).getName());
                    m.put(attribute.getName(), convertValueToSystem(attribute, c, value));
                });
            }

            protected Map<String, Object> handleRecordScript(Map<String, Object> record, Map<String, Object> attrs, @Nullable Card currentCard) {
                if (isNotBlank(template.getCallback())) {
                    String script = template.getCallback();
                    logger.trace("execute callback script expression =< {} > for record = {}", abbreviate(script), mapToLoggableStringInlineLazy(record));
                    Map<String, Object> res = map(attrs);
                    scriptService.helper(getClass(), script).withClassLoader(template.getClasspath())
                            .execute(map(record).with("data", res, "record", unmodifiableMap(record), "processed", unmodifiableMap(attrs), "current", unmodifiableMap(Optional.ofNullable(currentCard).map(Card::getAllValuesAsMap).orElse(emptyMap()))));//TODO translate attrs for wf
                    return res;
                } else {
                    return attrs;
                }
            }

            private Domain getDomain(String domainName) {
                Domain domain = dao.getDomain(domainName);
                checkArgument(domain.isDomainForClasse(getEntryType().asClasse()), "invalid domain = %s for entry type = %s", domain, getEntryType());
                return domain;
            }
        }

        private class ClassHelper extends EntryTypeHelper {

            private final Classe classe;
            private final Map<String, GisAttribute> geoAttributes;
            private final Function<List<Object>, Card> cardLoaderFunction;
            private final Supplier<Stream<Long>> cardIdsLoaderFunction;

            public ClassHelper() {
                this(dao.getClasse(template.getTargetName()));
            }

            public ClassHelper(Classe classe) {
                this.classe = checkNotNull(classe);
                logger.debug("import to class = {}", classe);

                CmdbFilter filter;

                if (classe.hasId()) {
                    geoAttributes = gis.isGisEnabled() ? gis.getGisAttributesByOwnerClassIncludeInherited(classe.getName())
                            .stream().filter(a -> template.hasColumnWithAttrName(a.getLayerName()))
                            .collect(toImmutableMap(GisAttribute::getLayerName, identity())) : emptyMap();
                    UserCardAccess cardAccess = cardService.getUserCardAccess(classe.getName());
                    filter = getFilter().and(cardAccess.getWholeClassFilter());//TODO handle attr permission, etc
                } else {
                    geoAttributes = emptyMap();
                    filter = getFilter();
                }

                boolean preloadCards = isMergeEnabled();//TODO preload cards eurystic/etc

                /*

                idea: per partizionare/parallelizzare in caso di dataset da db troppo grandi per essere caricati in memoria:
                    1. query da db, ordinata sul campo chiave, paginata;
                    2. per ogni pagina, identifichiamo i valori chiave massimi/minimi e li usiamo per definire la partizione su cui lavorare
                    3. preleviamo dai dati sorgente i record appartenenti alla partizione considerata (in base al range di valori chiave che abbiamo identificato)
                    4. elaboriamo i dati; gli update possono essere eseguiti subito; gli insert devono essere raccolti ed eseguiti in seguito (per non interferire con la paginazione);
                        in alternativa la query su db deve includere un filtro su id o begindate per escludere gli insert eseguiti nel corso dell'elaborazione;
                    5. al termine, eseguiamo insert e delete;

                 */
                long cardsOnDb, estimateCardSizeBytes, estimateMemoryUsageBytes;
                if (preloadCards) {
                    cardsOnDb = dao.selectCount().from(classe).where(filter).getCount();
                    estimateCardSizeBytes = round(dao.select(template.getColumns().stream()
                            .filter(a -> a.doNotIgnoreColumn() && !a.isRelation() && classe.hasAttributeActive(a.getAttributeName()))
                            .map(EtlTemplateColumnConfig::getAttributeName).collect(toSet())).from(classe).where(filter).limit(100).getCards().stream().mapToLong(c -> estimateObjectSizeBytes(c.getAllValuesAsMap())).average().orElse(0));
                    estimateMemoryUsageBytes = estimateCardSizeBytes * cardsOnDb;
                    if (hasEnoughFreeMemoryGC(estimateMemoryUsageBytes)) {
                        logger.info("preload {} cards from db for import merge, key attr[s] = {}, estimate memory usage = {}", cardsOnDb, template.getImportKeyAttributes(), memBytesToDisplaySize(estimateMemoryUsageBytes));
                    } else {
                        logger.info("skip card preload, estimate memory usage too big ( {} )", memBytesToDisplaySize(estimateMemoryUsageBytes));
                        preloadCards = false;
                    }
                }
                if (preloadCards) {
                    Map<String, Card> currentCardsByKeyAttributes = map();
                    dao.select(template.getColumns().stream()
                            .filter(a -> a.doNotIgnoreColumn() && !a.isRelation() && classe.hasAttributeActive(a.getAttributeName()))
                            .map(EtlTemplateColumnConfig::getAttributeName).collect(toSet())).from(classe).where(filter).getCards().forEach(card -> currentCardsByKeyAttributes.put(key(list(template.getImportKeyAttributes()).map(card::get).map(CmConvertUtils::toStringForKeyOrEmpty)), addRelationsToCurrentCard(card)));
                    logger.debug("loaded {} cards from db", currentCardsByKeyAttributes.size());
                    cardLoaderFunction = k -> currentCardsByKeyAttributes.get(key(list(k).map(CmConvertUtils::toStringForKeyOrEmpty)));
                    cardIdsLoaderFunction = () -> currentCardsByKeyAttributes.values().stream().map(Card::getId);
                } else {
                    cardLoaderFunction = k -> addRelationsToCurrentCard(dao.select(template.getColumns().stream().filter(a -> a.doNotIgnoreColumn() && !a.isRelation() && classe.hasAttributeActive(a.getAttributeName())).map(EtlTemplateColumnConfig::getAttributeName).collect(toSet())).from(classe).where(filter).accept(q -> {
                        for (int i = 0; i < template.getImportKeyAttributes().size(); i++) {
                            q.where(template.getImportKeyAttributes().get(i), EQ, k.get(i));
                        }
                    }).getCardOrNull());
                    cardIdsLoaderFunction = () -> dao.select(ATTR_ID).from(classe).where(filter).getCards().stream().map(Card::getId);
                }
            }

            @Nullable
            private Card addRelationsToCurrentCard(@Nullable Card card) {//TODO improve performance of this
                return card == null ? null : CardImpl.copyOf(card).accept(b -> {
                    template.getColumns().stream().filter(EtlTemplateColumnConfig::isRelation).forEach(c -> {
                        //TODO rename target attr (?)
                        b.withAttribute(c.getAttributeName(), Optional.ofNullable(dao.selectAll().fromDomain(c.getAttributeName())
                                .where(RelationDirectionQueryHelper.forDirection(c.getRelationDirection()).getSourceCardIdExpr(), EQ, card.getId()).getRelationOrNull())
                                .map(r -> r.getRelationWithDirection(c.getRelationDirection()).getTargetCard()).orElse(null));
                    });
                }).build();
            }

            @Override
            public EntryType getEntryType() {
                return classe;
            }

            @Override
            public RecordReadyForImport prepareRecord(EtlRecordInfo recordInfo, boolean create, boolean update) {
                try {
                    Map<String, Object> record = recordInfo.getRawRecord();
                    logger.trace("import class record for template = {}, raw data = \n\n{}\n", template, mapToLoggableStringLazy(record));
                    Map<String, Object> importRawAttrs = map(record).filterKeys(not(geoAttributes::containsKey))
                            .mapValues((k, v) -> firstNotBlankOrNull(v, defaults.get(k)))
                            .mapValues(ImportProcessor.this::convertValueToSystem)
                            .accept(handleMultiValues(record))
                            .accept(handleScriptValues(record));
                    Map<String, String> newGeoAttrs = map(record).filterKeys(geoAttributes::containsKey).mapValues(String.class::cast);
                    logger.trace("import class record for template = {}, processed data = \n\n{}\n", template, mapToLoggableStringLazy(importRawAttrs));
                    Card currentCard;
                    Map<String, String> currentGeoAttrs;
                    String key;
                    if (isMergeEnabled()) {
                        List<Object> keyValues = list(template.getImportKeyAttributes()).map(importRawAttrs::get);
                        key = key(list(keyValues).map(CmConvertUtils::toStringForKeyOrEmpty));
                        currentCard = cardLoaderFunction.apply(keyValues);
                        currentGeoAttrs = (geoAttributes.isEmpty() || currentCard == null) ? emptyMap() : gis.getGisValues(classe.getName(), currentCard.getId()).stream()
                                .filter(g -> geoAttributes.containsKey(g.getLayerName())).collect(toMap(GisValue::getLayerName, a -> cmGeometryToPostgisSql(a.getGeometry())));
                    } else {
                        currentCard = null;
                        currentGeoAttrs = emptyMap();
                        key = null;//not required
                    }
                    Map<String, Object> importAttrs = handleRecordScript(record, importRawAttrs, currentCard);
                    if (currentCard == null) {
                        if (create) {
                            switch (template.getEnableCreate()) {
                                case EC_FALSE ->
                                    throw runtime("error: current card not found for key =< %s > and insert is disabled", key);
                                case EC_SKIP -> {
                                    return new RecordReadyForImport(recordInfo) {
                                        @Override
                                        public String getKey() {
                                            return key;
                                        }
                                    };
                                }
                            }
                            Map<String, Object> importAttrsAndSysAttrs = map(importAttrs).accept(c -> {
                                if (classe.hasMultitenantEnabled()) {
                                    Long idTenant = ltEqZeroToNull(toLongOrNull(importAttrs.get(ATTR_IDTENANT)));
                                    if (classe.hasMultitenantModeAlways() && idTenant == null) {
                                        idTenant = operationUser.getUser().getUserTenantContext().getDefaultTenantId();
                                    }
                                    checkArgument(!classe.hasMultitenantModeAlways() || isNotNullAndGtZero(idTenant), "missing tenant id");
                                    checkArgument(idTenant == null || operationUser.getUser().getUserTenantContext().canAccessTenant(idTenant), "invalid tenant id = %s: access denied", idTenant);
                                    c.put(ATTR_IDTENANT, idTenant);
                                }
                            });
//                            Card newCard = CardImpl.builder().withType(classe).withAttributes(actualAttrs).build();
//                            logger.debug("prepared card for create = {}", newCard);
//                            logger.trace("new card (for create) attributes =\n\n{}\n", mapToLoggableStringLazy(newCard.getAllValuesAsMap()));
                            logger.trace("new card (for create) attributes =\n\n{}\n", mapToLoggableStringLazy(importAttrsAndSysAttrs));
                            return new RecordReadyForImport(recordInfo) {
                                @Override
                                public boolean isCreate() {
                                    return true;
                                }

                                @Override
                                public DatabaseRecord getRecord() {
                                    return CardImpl.builder().withType(classe).withAttributes(handleReferenceLoopRecord(importAttrsAndSysAttrs)).build(); //TODO improve this (?)
                                }

                                @Override
                                public boolean hasRecord() {
                                    return true;
                                }

                                @Override
                                public Map<String, Object> getRecordDataBeforeLastFix() {
                                    return importAttrsAndSysAttrs;
                                }

                                @Override
                                public String getKey() {
                                    return key;
                                }

                                @Override
                                public void postCreate(long id, DatabaseRecord card, Map<String, Object> record) {
                                    cardId = id;
                                    updateGeoAttributes(card(classe.getName(), id), newGeoAttrs);
                                    updateRelations(card(classe.getName(), id), handleReferenceLoopRecord(importAttrs), recordInfo);
                                    info.addCreatedRecord(record, id);
                                    if (card.getType().isClasse()) {
                                        eventBus.post(new CardCreatedEventImpl(CardImpl.copyOf(card).withId(id).build(), record));
                                    }
                                }

                                @Override
                                public void executePostScript() {
                                    if (isNullOrEmpty(template.getScriptOnCreated())) {
                                        return;
                                    }

                                    // execute script with known attributes and record data
                                    scriptService.helper(EtlTemplateProcessorServiceImpl.this.getClass(), template.getScriptOnCreated())
                                            .execute(mapOf(String.class, Object.class).with("Id", cardId, "IdClass", classe.getName()).with("record", getRecordInfo().getRawRecord()).immutable());
                                }
                            };
                        }
                    } else {
                        logger.trace("current card attributes =\n\n{}\n", mapToLoggableStringLazy(currentCard.getAllValuesAsMap()));
                        if (update) {
                            Set<String> attrsChanged = currentCard.getAttrsChangedFrom(importAttrs).stream().filter(template::hasColumnWithAttrName).collect(toSet()),
                                    relationsChanged = template.getColumns().stream().filter(EtlTemplateColumnConfig::isRelation).filter(c -> !equal(currentCard.getLong(c.getAttributeName()), toLong(importAttrs.get(c.getAttributeName())))).map(EtlTemplateColumnConfig::getAttributeName).collect(toSet()),
                                    geoAttrsChanged = set(currentGeoAttrs.keySet()).with(newGeoAttrs.keySet()).stream().filter(k -> !equal(currentGeoAttrs.get(k), newGeoAttrs.get(k))).collect(toSet());
                            boolean updateCard = !attrsChanged.isEmpty() || !geoAttrsChanged.isEmpty();
                            if (updateCard || !relationsChanged.isEmpty()) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("detected changes in these attributes = \n\n{}\n", mapDifferencesToLoggableString(map(currentCard.toMap()).withKeys(set(attrsChanged).with(relationsChanged)::contains).with(map(currentGeoAttrs).withKeys(geoAttrsChanged::contains)),
                                            map(importAttrs).withKeys(set(attrsChanged).with(relationsChanged)::contains).with(map(newGeoAttrs).withKeys(geoAttrsChanged::contains))));
                                }
                                if (updateCard) {
                                    logger.debug("prepared card for update = {}", importAttrs);
                                    logger.trace("new card (for update) attributes =\n\n{}\n", mapToLoggableStringLazy(importAttrs));
                                }
                                return new RecordReadyForImport(recordInfo) {
                                    @Override
                                    public boolean isUpdate() {
                                        return true;
                                    }

                                    @Override
                                    public DatabaseRecord getRecord() {
                                        return currentCard;
                                    }

                                    @Override
                                    public boolean hasRecord() {
                                        return true;
                                    }

                                    @Override
                                    public Map<String, Object> getRecordDataBeforeLastFix() {
                                        return importAttrs;
                                    }

                                    @Override
                                    public String getKey() {
                                        return key;
                                    }

                                    @Override
                                    public void doUpdate() {
                                        Card newCard = CardImpl.builder().withType(classe).withAttributes(handleReferenceLoopRecord(importAttrs)).withId(getRecord().getId()).build();
                                        if (updateCard) {
                                            logger.debug("do update card = {}", newCard);
                                            cardId = getRecord().getId();
                                            dao.updateOnly(newCard);
                                            updateGeoAttributes(newCard, newGeoAttrs);
                                            info.addModifiedRecord(record, newCard.getId());
                                            eventBus.post(new CardUpdatedEventImpl(newCard, record));
                                        } else {
                                            info.addUnmodifiedRecord(record, currentCard.getId());
                                            eventBus.post(new CardUnmodifiedEventImpl(currentCard, record));
                                        }
                                        updateRelations(newCard, importAttrs, recordInfo);
                                    }

                                    @Override
                                    public void executePostScript() {
                                        if (isNullOrEmpty(template.getScriptOnModified())) {
                                            return;
                                        }

                                        // execute script with known attributes and record data
                                        scriptService.helper(EtlTemplateProcessorServiceImpl.this.getClass(), template.getScriptOnModified())
                                                .execute(mapOf(String.class, Object.class).with("Id", cardId, "IdClass", classe.getName()).with("previous", getRecord().toMap()).with("record", getRecordInfo().getRawRecord()).immutable());
                                    }
                                };
                            } else {
                                logger.trace("skipping unmodified card = {}", currentCard);
                                info.addUnmodifiedRecord(record, currentCard.getId());
                                eventBus.post(new CardUnmodifiedEventImpl(currentCard, record));
                            }
                        }
                    }
                    return new RecordReadyForImport(recordInfo) {
                        @Override
                        public String getKey() {
                            return key;
                        }

                        @Override
                        public void executePostScript() {
                            if (isNullOrEmpty(template.getScriptOnUnmodified())) {
                                return;
                            }

                            cardId = currentCard.getId();

                            // execute script with known attributes and record data
                            scriptService.helper(EtlTemplateProcessorServiceImpl.this.getClass(), template.getScriptOnUnmodified())
                                    .execute(mapOf(String.class, Object.class).with("Id", cardId, "IdClass", classe.getName()).with("record", getRecordInfo().getRawRecord()).immutable());
                        }
                    };
                } catch (Exception ex) {
                    throw new EtlException(ex, "error processing record for template = %s record = %s", template, mapToLoggableStringInline(recordInfo.getRawRecord()));
                }
            }

            private void updateGeoAttributes(CardIdAndClassName card, Map<String, String> geoAttrs) {
                geoAttrs.forEach((k, v) -> {
                    gis.setGisValue(GisValueImpl.builder()
                            .withOwnerClassId(card.getClassName())
                            .withOwnerCardId(card.getId())
                            .withLayerName(k)
                            .withGeometry(parseGeometry(v)).build());
                });
            }

            private void updateRelations(CardIdAndClassName card, Map<String, Object> attrs, EtlRecordInfo recordInfo) {
                template.getColumns().stream().filter(EtlTemplateColumnConfig::isRelation).forEach(c -> {
                    CardIdAndClassName target = (CardIdAndClassName) attrs.get(c.getAttributeName());
                    RelationDirectionQueryHelper helper = RelationDirectionQueryHelper.forDirection(c.getRelationDirection());
                    Domain domain = entryTypeHelper.getDomain(c.getAttributeName());
                    logger.debug("processing relation import for card = {} domain = {} {} target = {}", card, domain, helper.toRelationDirection(), target);
                    try {
                        CMRelation current = Optional.ofNullable(dao.selectAll().from(domain).where(helper.getSourceCardIdExpr(), EQ, card.getId()).getRelationOrNull()) //TODO handle multiple relations
                                .map(r -> r.getRelationWithDirection(helper.toRelationDirection())).orElse(null);
                        if (target != null && current == null) {
                            CMRelation relation = RelationImpl.builder().withType(domain).withSourceCard(card).withTargetCard(target).withDirection(helper.toRelationDirection()).build();
                            logger.debug("create relation = {}", relation);
                            relation = dao.create(relation);
                            info.addCreatedRecord(relation.getAllValuesAsMap(), relation.getId());
                        } else if (target == null && current != null) {
                            logger.debug("delete relation = {}", current);
                            dao.delete(current);
                            info.addDeletedRecord(current.getId());
                        } else if (target == null && current == null) {
                            //nothing to do
                        } else if (target.getId() == (long) current.getTargetId()) {
                            logger.debug("relation unchanged = {}", current);
                            info.addUnmodifiedRecord(current.getAllValuesAsMap(), current.getId());
                        } else {
                            logger.debug("update relation = {} with taget = {}", current, target);
                            current = dao.update(RelationImpl.copyOf(current).withTargetCard(target).build());
                            info.addModifiedRecord(current.getAllValuesAsMap(), current.getId());
                        }
                    } catch (Exception ex) {
                        info.addProcessingFault(new EtlProcessingResultErrorImpl(recordInfo, exceptionToUserMessage(ex), exceptionToMessage(ex)));
                        switch (template.getOnRecordError()) {
                            case ETE_FAIL, ETE_LOG_ERROR ->//TODO improve error processing (fail?)
                                logger.error(marker(), "error synchronizing relation for card = {} domain = {} target = {}", card, domain, target, ex);
                            case ETE_LOG_WARNING ->
                                logger.warn(marker(), "error synchronizing relation for card = {} domain = {} target = {}", card, domain, target, ex);
                        }
                    }
                });
            }

            @Override
            public void handleMissingRecords() {
                cardIdsLoaderFunction.get().filter(not(info::hasProcessedRecord)).forEach(this::handleMissingRecordSafe);
            }

            @Override
            protected void updateRecord(DatabaseRecord dbRecord, String attributeName, Object value) {
                dao.update(CardImpl.copyOf((Card) dbRecord).withAttribute(attributeName, value).build());
            }

        }

        private class DomainHelper extends EntryTypeHelper {

            private final Domain domain;
            private final FluentList<String> keys;
            private final Function<List<Object>, CMRelation> cardLoaderFunction;

            public DomainHelper() {
                domain = dao.getDomain(template.getTargetName());
                logger.debug("import to domain = {}", domain);

                UserCardAccess sourceAccess = cardService.getUserCardAccess(domain.getSourceClassName());
                UserCardAccess targetAccess = cardService.getUserCardAccess(domain.getTargetClassName());
                CmdbFilter filter = getFilter().and(sourceAccess.getWholeClassFilter()).and(targetAccess.getWholeClassFilter());

                boolean preloadCards = isMergeEnabled();//TODO preload cards eurystic/etc

                /*

                idea: per partizionare/parallelizzare in caso di dataset da db troppo grandi per essere caricati in memoria:
                    1. query da db, ordinata sul campo chiave, paginata;
                    2. per ogni pagina, identifichiamo i valori chiave massimi/minimi e li usiamo per definire la partizione su cui lavorare
                    3. preleviamo dai dati sorgente i record appartenenti alla partizione considerata (in base al range di valori chiave che abbiamo identificato)
                    4. elaboriamo i dati; gli update possono essere eseguiti subito; gli insert devono essere raccolti ed eseguiti in seguito (per non interferire con la paginazione);
                        in alternativa la query su db deve includere un filtro su id o begindate per escludere gli insert eseguiti nel corso dell'elaborazione;
                    5. al termine, eseguiamo insert e delete;

                 */
                keys = list(ATTR_IDOBJ1, ATTR_IDOBJ2).with(list(domain.getActiveServiceAttributes()).filter(Attribute::isDomainKey).map(Attribute::getName));
                long cardsOnDb, estimateCardSizeBytes, estimateMemoryUsageBytes;
                if (preloadCards) {
                    cardsOnDb = dao.selectCount().from(domain).where(filter).getCount();
                    estimateCardSizeBytes = round(dao.select(keys).from(domain).where(filter).limit(100).getRelations().stream().mapToLong(c -> estimateObjectSizeBytes(c.getAllValuesAsMap())).average().orElse(0));
                    estimateMemoryUsageBytes = estimateCardSizeBytes * cardsOnDb;
                    if (hasEnoughFreeMemoryGC(estimateMemoryUsageBytes)) {
                        logger.info("preload {} cards from db for import merge, key attr[s] = {}, estimate memory usage = {}", cardsOnDb, template.getImportKeyAttributes(), memBytesToDisplaySize(estimateMemoryUsageBytes));
                    } else {
                        logger.info("skip card preload, estimate memory usage too big ( {} )", memBytesToDisplaySize(estimateMemoryUsageBytes));
                        preloadCards = false;
                    }
                }

                if (preloadCards) {
                    Map<String, CMRelation> currentCardsByKeyAttributes = map();
                    dao.selectAll().from(domain).where(filter).getRelations().forEach(card -> currentCardsByKeyAttributes.put(key(keys.map(card::get).map(CmConvertUtils::toStringForKeyOrEmpty)), card));
                    logger.debug("loaded {} cards from db", currentCardsByKeyAttributes.size());
                    cardLoaderFunction = k -> currentCardsByKeyAttributes.get(key(list(k).map(CmConvertUtils::toStringForKeyOrEmpty)));
                } else {
                    cardLoaderFunction = k -> dao.selectAll().from(domain).where(filter).accept(q -> {
                        for (int i = 0; i < keys.size(); i++) {
                            q.where(keys.get(i), EQ, k.get(i));
                        }
                    }).getRelationOrNull();
                }
            }

            @Override
            public EntryType getEntryType() {
                return domain;
            }

            @Override
            public RecordReadyForImport prepareRecord(EtlRecordInfo recordInfo, boolean create, boolean update) {
                Map<String, Object> record = recordInfo.getRawRecord();
                Map<String, Object> attrs = map(record)
                        .mapValues((k, v) -> firstNotBlankOrNull(v, defaults.get(k)))
                        .mapValues(ImportProcessor.this::convertValueToSystem)
                        .accept(handleMultiValues(record))
                        .accept(handleScriptValues(record));

                long sourceId = toLong(attrs.get(ATTR_IDOBJ1)),
                        targetId = toLong(attrs.get(ATTR_IDOBJ2));

                List<Object> valuesOfKeys = keys.map(attrs::get);

                CMRelation relation;
                String key;
                if (isMergeEnabled()) {
                    key = key(valuesOfKeys);
                    relation = cardLoaderFunction.apply(list(valuesOfKeys));
                } else {
                    relation = null;
                    key = null;//not required
                }

                if (relation == null) {
                    if (create) {
                        Card sourceCard = dao.getCard(domain.getSourceClass(), sourceId),
                                targetCard = dao.getCard(domain.getTargetClass(), targetId);
                        CMRelation toCreate = RelationImpl.builder()
                                .withType(domain)
                                .withSourceCard(sourceCard)
                                .withTargetCard(targetCard)
                                .withAttributes(attrs).build();
                        logger.debug("create new relation = {}", toCreate);
                        return new RecordReadyForImport(recordInfo) {
                            @Override
                            public String getKey() {
                                return key;
                            }

                            @Override
                            public boolean isCreate() {
                                return true;
                            }

                            @Override
                            public DatabaseRecord getRecord() {
                                return toCreate;
                            }

                            @Override
                            public boolean hasRecord() {
                                return true;
                            }

                            @Override
                            public void postCreate(long id, DatabaseRecord card, Map<String, Object> record) {
                                info.addCreatedRecord(record, id);
                                if (card.getType().isClasse()) {
                                    eventBus.post(new CardCreatedEventImpl(CardImpl.copyOf(card).withId(id).build(), record));
                                }
                            }

                        };
                    }
                } else {
                    if (update) {
                        CMRelation newRelation = RelationImpl.copyOf(relation).addAttributes(attrs).build();
                        if (!relation.allValuesEqualTo(newRelation)) {
                            logger.debug("update relation = {}", newRelation);
                            return new RecordReadyForImport(recordInfo) {
                                @Override
                                public boolean isUpdate() {
                                    return true;
                                }

                                @Override
                                public String getKey() {
                                    return key;
                                }

                                @Override
                                public void doUpdate() {
                                    CMRelation updated = dao.update(newRelation);
                                    info.addModifiedRecord(record, updated.getId());
                                }
                            };
                        } else {
                            logger.debug("skipping unmodified relation = {}", relation);
                            info.addUnmodifiedRecord(record, relation.getId());
                            eventBus.post(new CardUnmodifiedEventImpl(relation, record));
                        }
                    }
                }
                return new RecordReadyForImport(recordInfo) {
                    @Override
                    public String getKey() {
                        return key;
                    }
                };
            }

            @Override
            public void handleMissingRecords() {
                dao.selectAll().from(domain).where(getFilter()).getRelations() //TODO access control
                        .stream().filter(c -> !info.hasProcessedRecord(c.getId())).map(CMRelation::getId).forEach(this::handleMissingRecordSafe);
            }

            @Override
            protected void updateRecord(DatabaseRecord dbRecord, String attributeName, Object value) {
                dao.update(RelationImpl.copyOf((CMRelation) dbRecord).withAttribute(attributeName, value).build());
            }

        }

        private class ViewHelper extends EntryTypeHelper {

            private final View view;
            private final EntryType entryType;

            public ViewHelper() {
                view = viewService.getSharedByName(template.getTargetName());
                entryType = viewService.getEntryTypeForView(view);
                logger.debug("import to view = {}", view);
            }

            @Override
            public EntryType getEntryType() {
                return entryType;
            }

            @Override
            public RecordReadyForImport prepareRecord(EtlRecordInfo recordInfo, boolean create, boolean update) {
                throw new UnsupportedOperationException("view import is not supported");
            }

            @Override
            public void handleMissingRecords() {
                throw new UnsupportedOperationException("view import is not supported");
            }

            @Override
            protected void updateRecord(DatabaseRecord dbRecord, String attributeName, Object value) {
                throw new UnsupportedOperationException("view import is not supported");
            }

        }

    }

    private class XlsExportProcessor extends ExportProcessor {

        private final WorkbookInfo workbookInfo;
        private final Workbook workbook;
        private final Sheet sheet;
        private final CellStyle dateCellStyle, dateTimeCellStyle, timeCellStyle;
        private int rowIndex;
        private final int headerRow, dataRow, columnOffset;

        public XlsExportProcessor(EtlTemplate template) {
            super(template);
            checkArgument(set(EFF_XLS, EFF_XLSX).contains(template.getFileFormat()));

            workbookInfo = buildWorkbook(template, "export");
            workbook = workbookInfo.getWorkbook();
            sheet = workbookInfo.getSheet();
            String dateFormat = dateFormatPatternToXls(templatePreferences.getDateFormatPattern()),
                    dateTimeFormat = dateTimeFormatPatternToXls(templatePreferences.getDateTimeFormatPattern()),
                    timeFormat = dateTimeFormatPatternToXls(templatePreferences.getTimeFormatPattern());
            logger.debug("use date format pattern =< {} >, datetime format pattern =< {} >", dateFormat, dateTimeFormat);
            dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(dateFormat));
            dateTimeCellStyle = workbook.createCellStyle();
            dateTimeCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(dateTimeFormat));
            timeCellStyle = workbook.createCellStyle();
            timeCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(timeFormat));

            headerRow = isNullOrLtEqZero(template.getHeaderRow()) ? 0 : template.getHeaderRow() - 1;
            dataRow = isNullOrLtEqZero(template.getDataRow()) ? (template.getUseHeader() ? 1 : 0) : template.getDataRow() - 1;
            columnOffset = isNullOrLtEqZero(template.getFirstCol()) ? 0 : template.getFirstCol() - 1;

            if (template.getUseHeader()) {
                Row row = sheet.createRow(headerRow);
                for (int i = 0; i < template.getColumns().size(); i++) {
                    Cell cell = row.createCell(i + columnOffset, STRING);
                    cell.setCellValue(template.getColumns().get(i).getColumnName());
                }
            }

            rowIndex = dataRow - 1;
        }

        @Override
        protected void addRecordToResponse(DatabaseRecord record) throws Exception {
            Row row = sheet.createRow(++rowIndex);
            for (int i = 0; i < template.getColumns().size(); i++) {
                EtlTemplateColumnConfig config = template.getColumns().get(i);
                Pair<Attribute, Object> pair = getAttributeAndValue(record, config);
                serializeAttributeValue(row, i + columnOffset, config, pair.getKey(), pair.getValue());
            }
        }

        @Override
        protected DataSource doExportData() throws Exception {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] data = out.toByteArray();
            logger.trace("export {} data = \n\n{}\n", workbookInfo.getFileExt(), lazyString(() -> newStringUsAscii(encodeBase64(data, true))));
            return newDataSource(data, workbookInfo.getContentType(), format("export_%s_%s.%s", normalize(template.getCode()), CmDateUtils.dateTimeFileSuffix(), workbookInfo.getFileExt()));
        }

        private void serializeAttributeValue(Row row, int columnIndex, EtlTemplateColumnConfig columnConfig, @Nullable Attribute attribute, @Nullable Object value) throws ParseException {
            if (attribute == null || isNullOrBlank(value)) {
                row.createCell(columnIndex, BLANK);
            } else if (attribute.getName().equals(ATTR_IDTENANT)) {
                row.createCell(columnIndex, STRING).setCellValue(checkNotNull(toStringOrNull(tenantLoader.inverse().get(toLong(value))), "tenant id not found"));
            } else {
                switch (attribute.getType().getName()) {
                    case BOOLEAN ->
                        row.createCell(columnIndex, BOOLEAN).setCellValue(toBoolean(value));
                    case CHAR, INET, JSON, REGCLASS, STRING, GEOMETRY, TEXT, LINK ->
                        row.createCell(columnIndex, STRING).setCellValue(convert(value, String.class));
                    case DECIMAL, DOUBLE, FLOAT ->
                        row.createCell(columnIndex, NUMERIC).setCellValue(toDouble(value));
                    case INTEGER, LONG ->
                        row.createCell(columnIndex, NUMERIC).setCellValue(toLong(value));
                    case DATE -> {
                        Cell dateCell = row.createCell(columnIndex, NUMERIC);
                        dateCell.setCellValue(toJavaDate(value));
                        dateCell.setCellStyle(dateCellStyle);
                    }
                    case TIMESTAMP -> {
                        Cell dateTimeCell = row.createCell(columnIndex, NUMERIC);
//                        dateTimeCell.setCellValue(helper.zonedDateTimeToUserLocalJavaDate(toDateTime(value)));
                        dateTimeCell.setCellValue(toJavaDate(value));
                        dateTimeCell.setCellStyle(dateTimeCellStyle);
                    }
                    case TIME -> {
                        Cell timeCell = row.createCell(columnIndex, NUMERIC);
                        timeCell.setCellStyle(timeCellStyle);
                        timeCell.setCellValue(toJavaDate(LocalDate.parse("1970-01-01").atTime(toTime(value)).atZone(UTC)));
                    }
                    case REFERENCE, FOREIGNKEY, LOOKUP -> {
                        IdAndDescription idAndDescription = (IdAndDescription) value;
                        if (isNullOrLtEqZero(idAndDescription.getId())) {
                            row.createCell(columnIndex, BLANK);
                        } else {
                            switch (columnConfig.getMode()) {
                                case ETCM_CODE ->
                                    row.createCell(columnIndex, STRING).setCellValue(checkNotBlank(idAndDescription.getCode(), parseErrorCode("854", "invalid code export for value = %s : code is null", idAndDescription.getId().toString())));
                                case ETCM_DESCRIPTION ->
                                    row.createCell(columnIndex, STRING).setCellValue(checkNotBlank(idAndDescription.getDescription(), parseErrorCode("855", "invalid description export for value = %s : description is null", idAndDescription.getId().toString())));
                                case ETCM_DESCRIPTION_TRANSLATED ->
                                    row.createCell(columnIndex, STRING).setCellValue(checkNotBlank(lookupService.getTranslatedLookup(idAndDescription.getId()).getDescription(), parseErrorCode("856", "invalid description export for value = %s : description is null", idAndDescription.getId().toString())));
                                case ETCM_ID ->
                                    row.createCell(columnIndex, NUMERIC).setCellValue(idAndDescription.getId());
                                default ->
                                    throw new EtlException("unsupported column export mode = %s for attribute = %s", columnConfig.getMode(), attribute);
                            }
                        }
                    }
                    case LOOKUPARRAY -> {
                        if (value != null) {
                            List<IdAndDescription> lookupList = (List<IdAndDescription>) value;
                            if (!lookupList.isEmpty()) {
                                switch (columnConfig.getMode()) {
                                    case ETCM_CODE ->
                                        row.createCell(columnIndex, STRING).setCellValue(lookupList.stream().map(IdAndDescription::getCode).collect(joining(getValueArraySeparator())));
                                    case ETCM_DESCRIPTION ->
                                        row.createCell(columnIndex, STRING).setCellValue(lookupList.stream().map(IdAndDescription::getDescription).collect(joining(getValueArraySeparator())));
                                    case ETCM_DESCRIPTION_TRANSLATED ->
                                        row.createCell(columnIndex, STRING).setCellValue(lookupList.stream().map(l -> lookupService.getTranslatedLookup(l.getId()).getDescription()).collect(joining(getValueArraySeparator())));
                                    case ETCM_ID ->
                                        row.createCell(columnIndex, NUMERIC).setCellValue(lookupList.stream().map(l -> l.getId().toString()).collect(joining(getValueArraySeparator())));
                                    default ->
                                        throw new EtlException("unsupported column export mode = %s for attribute = %s", columnConfig.getMode(), attribute);
                                }
                            }
                        }
                    }
                    default ->
                        throw new EtlException("unable to export attribute = %s: unsupported attribute type", attribute);
                }
            }
            logger.trace("export column = {} with value = {} ({}) to cell = {}", columnConfig.getColumnName(), value, getClassOfNullable(value).getName(), row.getCell(rowIndex));
        }

    }

    private class CsvExportProcessor extends ExportProcessor {

        private final StringWriter writer;
        private final CsvListWriter csv;

        public CsvExportProcessor(EtlTemplate template) throws Exception {
            super(template);
            checkArgument(equal(template.getFileFormat(), EFF_CSV));

            CsvPreference csvPreference = getCsvPreference(template);

            writer = new StringWriter();
            csv = new CsvListWriter(writer, csvPreference);
            if (template.getUseHeader()) {
                csv.write(list(transform(template.getColumns(), EtlTemplateColumnConfig::getColumnName)));
            }
        }

        @Override
        protected void addRecordToResponse(DatabaseRecord record) throws Exception {
            logger.debug("export row = {}", record);
            csv.write(list(template.getColumns()).map(c -> {
                Pair<Attribute, Object> pair = getAttributeAndValue(record, c);
                return nullToEmpty(serializeAttributeValue(c, pair.getKey(), pair.getValue()));
            }));
        }

        @Override
        protected DataSource doExportData() throws Exception {
            csv.close();
            String csvString = writer.toString();
            logger.trace("export csv data = \n\n{}\n", csvString);
            String charset = firstNotBlank(template.getCharset(), userPreferencesService.getUserPreferences().getPreferredFileCharset());
            logger.debug("export csv with charset =< {} >", charset);
            return newDataSource(csvString.getBytes(charset), setCharsetInContentType("text/csv", charset), format("export_%s_%s.csv", normalize(template.getCode()), CmDateUtils.dateTimeFileSuffix()));
        }

        private String serializeAttributeValue(EtlTemplateColumnConfig columnConfig, @Nullable Attribute attribute, @Nullable Object value) {
            if (attribute == null || isNullOrBlank(value)) {
                return "";
            } else if (attribute.getName().equals(ATTR_IDTENANT)) {
                return checkNotNull(toStringOrNull(tenantLoader.inverse().get(toLong(value))), "tenant id not found");
            } else {
                return switch (attribute.getType().getName()) {
                    case DECIMAL, DOUBLE, FLOAT, INTEGER, LONG ->
                        getHelper(columnConfig).serializeNumber(convert(value, Number.class));
                    case BOOLEAN, CHAR, INET, JSON, REGCLASS, STRING, GEOMETRY, TEXT, LINK ->
                        convert(value, String.class);
                    case DATE ->
                        getHelper(columnConfig).serializeDate(toDate(value));
                    case TIME ->
                        getHelper(columnConfig).serializeTime(toTime(value));
                    case TIMESTAMP ->
                        getHelper(columnConfig).serializeDateTime(toDateTime(value));
                    case REFERENCE, FOREIGNKEY, LOOKUP -> {
                        IdAndDescription idAndDescription = (IdAndDescription) value;
                        if (isNullOrLtEqZero(idAndDescription.getId())) {
                            yield "";
                        } else {
                            yield switch (columnConfig.getMode()) {
                                case ETCM_CODE ->
                                    checkNotBlank(idAndDescription.getCode(), parseErrorCode("854", "invalid code export for value = %s : code is null", idAndDescription.getId().toString()));
                                case ETCM_DESCRIPTION ->
                                    checkNotBlank(idAndDescription.getDescription(), parseErrorCode("855", "invalid description export for value = %s : description is null", idAndDescription.getId().toString()));
                                case ETCM_DESCRIPTION_TRANSLATED ->
                                    checkNotBlank(lookupService.getTranslatedLookup(idAndDescription.getId()).getDescription(), parseErrorCode("856", "invalid description export for value = %s : description is null", idAndDescription.getId().toString()));
                                case ETCM_ID ->
                                    idAndDescription.getId().toString();
                                default ->
                                    throw new EtlException("unsupported column export mode = %s for attribute = %s", columnConfig.getMode(), attribute);
                            };
                        }
                    }
                    case LOOKUPARRAY -> {
                        List<IdAndDescription> lookupList = (List<IdAndDescription>) value;
                        if (!lookupList.isEmpty()) {
                            yield switch (columnConfig.getMode()) {
                                case ETCM_CODE ->
                                    String.join(getValueArraySeparator(), lookupList.stream().map(IdAndDescription::getCode).collect(toList()));
                                case ETCM_DESCRIPTION ->
                                    String.join(getValueArraySeparator(), lookupList.stream().map(IdAndDescription::getDescription).collect(toList()));
                                case ETCM_DESCRIPTION_TRANSLATED ->
                                    String.join(getValueArraySeparator(), lookupList.stream().map(l -> lookupService.getTranslatedLookup(l.getId()).getDescription()).collect(toList()));
                                case ETCM_ID ->
                                    String.join(getValueArraySeparator(), lookupList.stream().map(l -> l.getId().toString()).collect(toList()));
                                default ->
                                    throw new EtlException("unsupported column export mode = %s for attribute = %s", columnConfig.getMode(), attribute);
                            };
                        } else {
                            yield "";
                        }
                    }
                    default ->
                        throw new EtlException("unable to export attribute = %s: unsupported attribute type", attribute);
                };
            }
        }

    }

    private abstract class ExportProcessor extends BaseProcessor {

        public ExportProcessor(EtlTemplate template) {
            super(template);
            checkArgument(template.isExportTemplate(), "invalid template: this is not an export template");
        }

        public DataSource exportData() throws Exception {
            CmdbFilter filter = template.getExportFilter().and(template.getFilter());
            return switch (template.getTargetType()) {
                case ET_CLASS, ET_PROCESS -> {
                    Classe classe = dao.getClasse(template.getTargetName());
                    Set<String> activeAttributes = template.getColumns().stream().filter(EtlTemplateColumnConfig::doNotIgnoreColumn).map(EtlTemplateColumnConfig::getAttributeName).collect(toSet());
                    activeAttributes.forEach(a -> checkArgument(classe.hasAttributeActive(a), "invalid template: attribute not found in class = %s for name = %s", classe, a));
                    List<? extends Card> cards;
                    if (classe.isProcess()) {
                        cards = workflowService.getUserFlowCardsByClasseIdAndQueryOptions(template.getTargetName(), DaoQueryOptionsImpl.build(filter)).elements();
                        logger.info("building export with {} flow rows", cards.size());
                        cards.forEach(rethrowConsumer(this::addRecordToResponse));
                    } else {
                        UserCardAccess cardAccess = cardService.getUserCardAccess(template.getTargetName());
                        CmdbFilter cardAccessFilter = cardAccess.getWholeClassFilter();
                        filter = filter.and(cardAccessFilter);
                        cards = dao.select(activeAttributes)//TODO fix this, use user access service
                                .from(template.getTargetName())
                                //                .orderBy(sorter)TODO check order
                                .where(filter)
                                .orderBy(classe.getDefaultOrder())
                                .accept(cardAccess.addSubsetFilterMarkersToQueryVisitor()::accept)
                                .getCards();
                        logger.info("building export with {} card rows", cards.size());
                        Consumer<Long> processProgressListener = buildProgressListener(cards.size(), (e) -> {
                            if (cards.size() > 1000) {
                                logger.info("export progress: {}", e.getProgressDescriptionDetailed());
                            }
                        });
                        AtomicLong progress = new AtomicLong(0);
                        cards.stream()
                                .gather(Gatherers.mapConcurrent(Runtime.getRuntime().availableProcessors() / 2, cardAccess::addCardAccessPermissionsFromSubfilterMark))
                                .forEach(rethrowConsumer(c -> {
                                    addRecordToResponse(c);
                                    processProgressListener.accept(progress.getAndIncrement());
                                }));
                    }
                    yield doExportData();
                }
                case ET_DOMAIN -> {
                    Domain domain = domainService.getUserDomain(template.getTargetName());
                    template.getColumns().stream().filter(EtlTemplateColumnConfig::doNotIgnoreColumn).forEach(c -> checkArgument(domain.hasAttributeActive(c.getAttributeName()), "invalid template: attribute not found in domain = %s for name = %s", domain, c.getAttributeName()));
                    List<CMRelation> relations = dao.selectAll()
                            .from(domain)
                            .where(filter)
                            .orderBy(domain.getDefaultOrder())
                            .getRelations(); //TODO access control
                    logger.info("building export with {} relation rows", relations.size());

                    relations.removeIf(r -> !r.canReadSource() || !r.canReadTarget());

                    relations.forEach(rethrowConsumer(this::addRecordToResponse));
                    yield doExportData();
                }
                case ET_VIEW -> {
                    View view = viewService.getSharedByName(template.getTargetName());
                    EntryType entryType = viewService.getEntryTypeForView(view);
                    template.getColumns().stream().filter(EtlTemplateColumnConfig::doNotIgnoreColumn).forEach(c -> checkArgument(entryType.hasAttributeActive(c.getAttributeName()), "invalid template: attribute not found in view = %s for name = %s", view, c.getAttributeName()));
                    List<Card> records = viewService.getCards(view, DaoQueryOptionsImpl.build(filter)).elements();
                    logger.info("building export with {} view records", records.size());
                    records.forEach(rethrowConsumer(this::addRecordToResponse));
                    yield doExportData();
                }
                default ->
                    throw new EtlException("unsupported target type = %s", template.getTargetType());
            };
        }

        public DataSource exportData(List<? extends DatabaseRecord> data) throws Exception {
            data.forEach(rethrowConsumer(this::addRecordToResponse));
            return doExportData();
        }

        protected Pair<Attribute, Object> getAttributeAndValue(DatabaseRecord record, EtlTemplateColumnConfig c) {
            Attribute attribute;
            Object value;
            if (c.ignoreColumn()) {
                attribute = null;
                value = null;
            } else {
                if (record.getType().isDomain() && set(ATTR_IDOBJ1, ATTR_IDOBJ2).contains(c.getAttributeName())) {
                    attribute = record.getType().asDomain().getIdObjAttrAsFkAttr(c.getAttributeName());
                    value = ((CMRelation) record).getIdObjAttrValueAsFkAttrValue(c.getAttributeName());
                } else {
                    attribute = record.getType().getAttributeOrNull(c.getAttributeName());
                    value = record.get(c.getAttributeName());
                }
            }
            logger.trace("export column = {} with value = {} ({})", c, value, getClassOfNullable(value).getName());
            return Pair.of(attribute, value);
        }

        protected abstract void addRecordToResponse(DatabaseRecord record) throws Exception;

        protected abstract DataSource doExportData() throws Exception;

    }

    private abstract class RecordReadyForImport {

        private final EtlRecordInfo recordInfo;
        protected long cardId;

        public RecordReadyForImport(EtlRecordInfo recordInfo) {
            this.recordInfo = checkNotNull(recordInfo);
        }

        public abstract String getKey();

        public EtlRecordInfo getRecordInfo() {
            return recordInfo;
        }

        public boolean isCreate() {
            return false;
        }

        public boolean isUpdate() {
            return false;
        }

        public DatabaseRecord getRecord() {
            throw new UnsupportedOperationException();
        }

        public boolean hasRecord() {
            return false;
        }

        public Map<String, Object> getRecordDataBeforeLastFix() {
            return getRecord().getAllValuesAsMap();
        }

        public void postCreate(long id, DatabaseRecord card, Map<String, Object> record) {
        }

        public void doUpdate() {
            throw new UnsupportedOperationException();
        }

        public void executePostScript() {
            // do nothing
        }

        @Override
        public String toString() {
            return "RecordReadyForImport{" + "recordInfo=" + recordInfo + ", isCreate=" + isCreate() + ", isUpdate=" + isUpdate() + ", hasRecord=" + hasRecord() + '}';
        }

    }

    private abstract class CardEventImpl implements CardEvent {

        final DatabaseRecord card;
        final Map<String, Object> record;

        public CardEventImpl(DatabaseRecord card, Map<String, Object> record) {
            this.card = checkNotNull(card);
            this.record = checkNotNull(record);
        }

        @Override
        public DatabaseRecord getCard() {
            return card;
        }

        @Override
        public Map<String, Object> getRecord() {
            return record;
        }

    }

    private class CardCreatedEventImpl extends CardEventImpl implements CardCreatedEvent {

        public CardCreatedEventImpl(DatabaseRecord card, Map<String, Object> record) {
            super(card, record);
        }

    }

    private class CardUpdatedEventImpl extends CardEventImpl implements CardUpdatedEvent {

        public CardUpdatedEventImpl(DatabaseRecord card, Map<String, Object> record) {
            super(card, record);
        }

    }

    private class CardUnmodifiedEventImpl extends CardEventImpl implements CardUnmodifiedEvent {

        public CardUnmodifiedEventImpl(DatabaseRecord card, Map<String, Object> record) {
            super(card, record);
        }

    }
}
