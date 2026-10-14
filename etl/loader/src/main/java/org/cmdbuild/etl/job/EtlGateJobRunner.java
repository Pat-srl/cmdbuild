package org.cmdbuild.etl.job;

import com.google.common.annotations.VisibleForTesting;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Suppliers;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import org.cmdbuild.api.CmApiService;
import org.cmdbuild.customclassloader.CustomClassloaderService;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.gate.EtlGateService;
import org.cmdbuild.etl.gate.inner.EtlGate;
import static org.cmdbuild.etl.gate.inner.EtlGate.ETL_GATE_KEY;
import org.cmdbuild.etl.gate.inner.EtlGateHandler;
import static org.cmdbuild.etl.gate.inner.EtlGateHandler.ETL_HANDLER_CONFIG_INPUT_ATTACHMENT_NAME;
import static org.cmdbuild.etl.gate.inner.EtlGateHandler.ETL_HANDLER_CONFIG_OUTPUT_ATTACHMENT_NAME;
import static org.cmdbuild.etl.gate.inner.EtlGateHandler.ETL_HANDLER_CONFIG_SKIP_NEXT_ON_NO_DATA;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_GATE;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_NOOP;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_SCRIPT;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_TEMPLATE;
import org.cmdbuild.etl.gate.inner.EtlGateImpl;
import static org.cmdbuild.etl.gate.inner.EtlJobUtils.ETLJOB_ATTR_GATE;
import static org.cmdbuild.etl.gate.inner.EtlJobUtils.ETLJOB_ATTR_MESSAGE_ID;
import static org.cmdbuild.etl.gate.inner.EtlJobUtils.ETLJOB_ATTR_MESSAGE_STORAGE;
import static org.cmdbuild.etl.gate.inner.EtlJobUtils.ETLJOB_OUTPUT_MESSAGE_REFERENCE;
import static org.cmdbuild.etl.gate.inner.EtlJobUtils.ETLJOB_TYPE;
import static org.cmdbuild.etl.handler.FileReaderHelperService.FAIL_ON_MISSING_SOURCE_DATA_CONFIG;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateReference;
import org.cmdbuild.etl.loader.EtlTemplateService;
import org.cmdbuild.etl.loader.EtlTemplateWithData;
import org.cmdbuild.etl.loader.EtlTemplateWithDataImpl;
import static org.cmdbuild.etl.utils.EtlUtils.getMessageAndPayloadMeta;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentImpl.WaterwayMessageAttachmentImplBuilder;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_JOB_RUN;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_MESSAGE_DESCRIPTION;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_PROCESSING_REPORT;
import org.cmdbuild.etl.waterway.message.WaterwayMessageDataImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageStatus;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_ERROR;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_FAILED;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_PROCESSING;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.appendDataForAttachments;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.buildMessageReference;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.isValidPayload;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.mergeDataForAttachments;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.splitDataForAttachments;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.toDataSource;
import org.cmdbuild.etl.waterway.storage.EtlMessageImpl;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.fault.FaultEventImpl;
import static org.cmdbuild.fault.FaultUtils.errorsToMessage;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobFaultTolerantException;
import static org.cmdbuild.jobs.JobRun.JOB_OUTPUT;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.cmdbuild.script.ScriptService;
import org.cmdbuild.script.ScriptService.ScriptServiceHelper;
import static org.cmdbuild.script.ScriptType.ST_GROOVY;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPacked;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.script.CmScriptUtils.SCRIPT_OUTPUT_VAR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EtlGateJobRunner implements JobRunner {

    private final Logger logger;

    private final WaterwayService waterwayService;
    private final EtlTemplateService importService;
    private final EtlGateService gateService;
    private final ScriptService scriptService;
    private final CmApiService apiService;
    private final EtlLoadHandlerRepository handlerRepository;
    private final CustomClassloaderService customClassloaderService;
    private final EtlResultProcessingService resultProcessingService;
    private final FaultEventCollectorService faultService;

    /**
     * Handles <i>plugin proxy</i> for plugin manager 4.x
     */
    public EtlGateJobRunner(WaterwayService waterwayService, EtlTemplateService importService, EtlGateService gateService, ScriptService scriptService, CmApiService apiService, EtlLoadHandlerRepository handlerRepository, CustomClassloaderService customClassloaderService, EtlResultProcessingService resultProcessingService, FaultEventCollectorService faultService) { // See https://dzone.com/articles/optional-dependency-injection-with-spring
        this.waterwayService = checkNotNull(waterwayService);
        this.importService = checkNotNull(importService);
        this.gateService = checkNotNull(gateService);
        this.scriptService = checkNotNull(scriptService);
        this.apiService = checkNotNull(apiService);
        this.handlerRepository = checkNotNull(handlerRepository);
        this.customClassloaderService = checkNotNull(customClassloaderService);
        this.resultProcessingService = checkNotNull(resultProcessingService);
        this.faultService = checkNotNull(faultService);

        this.logger = buildLogger();
    }

    @Override
    public String getJobRunnerName() {
        return ETLJOB_TYPE;
    }

    @Override
    public void vaildateJob(JobData jobData) {
        new EtlLoaderBase(jobData);
    }

    @Override
    public Map<String, String> runJobWithOutput(JobData job, JobRunContext jobContext) {
        try {
            logger.debug("run job = {}", job);
            WaterwayMessage message = new EtlLoaderHelper(jobContext).runJob();
            if (message.getFaultTolerantErrors().isEmpty()) {
                checkArgument(!message.hasStatus(WMS_FAILED, WMS_ERROR), "job processing error: %s", errorsToMessage(message.getErrors()));
            }
            return map(message.getMeta()).mapValues(CmStringUtils::toStringOrNullSafe).with(ETLJOB_OUTPUT_MESSAGE_REFERENCE, buildMessageReference(message.getStorageCode(), message.getMessageKey()));
        } catch (Exception ex) {
            throw new EtlException(ex, "error processing etl job = %s", job);
        }
    }

    /**
     * Handle for tests: logger used in this runner.
     *
     * @return
     */
    @VisibleForTesting
    protected Logger buildLogger() {
        return LoggerFactory.getLogger(getClass());
    }

    /**
     * Handle for tests: logger used inside Groovy scripts.
     *
     * @param gateCode
     * @return
     */
    @VisibleForTesting
    protected Logger buildLoggerInScript(String gateCode) {
        return LoggerFactory.getLogger(format("%s.JOB.%s", getClass().getName(), gateCode));
    }

    private EtlGate getGateForJob(JobData job) {
        String code = job.getConfigNotBlank(ETLJOB_ATTR_GATE);
        return switch (code) {
            case "INLINE" ->
                EtlGateImpl.builder()
                .withCode("INLINE_JOB_%s".formatted(job.getCode()))
                .withConfig(ETL_GATE_KEY, "JOB_%s_v1#INLINE_JOB_%s".formatted(job.getId(), job.getCode()))
                .withConfig(unflattenMap(job.getConfig(), "gateconfig")).build();
            default ->
                gateService.getByCode(code);
        };
    }

    private class EtlLoaderBase {

        protected final EtlGate gate;
        protected WaterwayMessage message;
        protected final EtlResultProcessor resultProcessor;

        public EtlLoaderBase(JobData job) {
            gate = getGateForJob(job);
            message = isBlank(job.getConfig(ETLJOB_ATTR_MESSAGE_ID))
                    ? waterwayService.newRequest().withTarget(gate.getKey()).withMeta(WY_MESSAGE_DESCRIPTION, "dummy message for gate job \"%s\"".formatted(job)).createQueued()
                    : waterwayService.getMessage(job.getConfigNotBlank(ETLJOB_ATTR_MESSAGE_STORAGE), job.getConfigNotBlank(ETLJOB_ATTR_MESSAGE_ID));
            logger.debug("loaded job message = {}", message);
            message.checkHasQueueCode(gate.getCode());
            resultProcessor = resultProcessingService.getResultProcessor(new EtlResultProcessingConfigImpl(gate.getConfig()));
        }
    }

    private class EtlLoaderHelper extends EtlLoaderBase {

        private final JobRunContext jobContext;

        public EtlLoaderHelper(JobRunContext job) {
            super(job.getJob());
            this.jobContext = checkNotNull(job);
        }

        public WaterwayMessage runJob() {
            WaterwayMessageData data = message;
            try {
                message = waterwayService.updateMessage(message).withStatus(WMS_PROCESSING).withMeta(WY_JOB_RUN, toStringOrNull(jobContext.getJobRunId())).update();
                data = doRunJob();
            } finally {
                message = resultProcessor.handleProcessingResult(jobContext, message, data);
            }
            return message;
        }

        private WaterwayMessageData doRunJob() {
            try {
                List<WaterwayMessageData> list = list(checkNotNull(message));//TODO load attachments (?)
                for (EtlHandlerHelper helper : gate.getHandlers().stream().map(h -> new EtlHandlerHelper(gate, h)).collect(toImmutableList())) {
                    list = list(list).flatMap(in -> {
                        logger.debug("execute etl handler = {} with config = \n\n{}\n ", helper.handler, mapToLoggableStringLazy(helper.handler.getConfig()));
                        logger.debug("processing data = {} with attachments = {}", in, in.getAttachmentMap().values());

                        WaterwayMessageData out;
                        try {
                            out = helper.handleDataImport(in);
                        } catch (JobFaultTolerantException exFaultTolerant) {
                            // #7942 fault tolerant error handling
                            out = createFaultTolerantErrorMessage(helper.handler.getType(), exFaultTolerant);
                        }

                        logger.debug("processed data = {} with attachments = {}", out, out.getAttachmentMap().values());
                        return switch (helper.handler.getOutputMode()) {
                            case EOM_REPLACE ->
                                singletonList(out);
                            case EOM_SPLIT ->
                                splitDataForAttachments(out);
                            case EOM_ATTACH ->
                                singletonList(mergeDataForAttachments(list(in, out)));
                            case EOM_APPEND ->
                                singletonList(appendDataForAttachments(list(in, out)));
                        };
                    });
                    if (list.stream().allMatch(not(WaterwayMessageData::hasAttachments)) && toBooleanOrDefault(helper.handler.getConfig(ETL_HANDLER_CONFIG_SKIP_NEXT_ON_NO_DATA), false) == true) {
                        logger.debug("no data returned from handler = {}, and skip next = true: skip next handlers", helper.handler);
                        break;
                    }
                }
                return list.size() == 1 ? getOnlyElement(list) : mergeDataForAttachments(list);
            } catch (Exception ex) {
                faultService.getCurrentRequestEventCollectorIfExists().ifPresent(c -> c.addError(ex));//TODO avoid duplicate error add for job logs (??)
                throw new EtlException(ex, "error processing etl gate = %s", gate);
            }
        }

        private WaterwayMessageData createFaultTolerantErrorMessage(String handlerType, JobFaultTolerantException exFaultTolerant) {
            final String warnMsg = "etl handler with fault tolerant error: %s -".formatted(handlerType);
            final Throwable causeEx = exFaultTolerant.getCause();
            logger.warn(warnMsg, causeEx);

            return EtlMessageImpl.builder().withStatus(WaterwayMessageStatus.WMS_FAULT_TOLERANT_ERROR).withLogs(warnMsg)
                    .withTimestamp(java.time.ZonedDateTime.now())
                    .withMessageId("-1").withTransactionId(-1)
                    .withStorage("fake_storage").withQueue("fake_queue").withNodeId("fake_nodeId")
                    .withFaultTolerantErrors(list(FaultEventImpl.warningWithExc(warnMsg, causeEx)))
                    .build();
        }

    }

    private class EtlHandlerHelper {

        private final EtlGate gate;
        private final EtlGateHandler handler;
        private final Function<WaterwayMessageData, WaterwayMessageData> handlerProcessor;
        private final List<EtlTemplateReference> templates;

        private final Map<String, WaterwayMessageAttachmentImplBuilder> attachmentsFromApi = map();

        public EtlHandlerHelper(EtlGate gate, EtlGateHandler handler) {
            this.gate = checkNotNull(gate);
            this.handler = checkNotNull(handler);
            handlerProcessor = switch (handler.getType()) {
                case ETLHT_TEMPLATE ->
                    this::applyTemplates;
                case ETLHT_SCRIPT ->
                    this::executeScript;
                case ETLHT_GATE -> {
                    EtlGate nextGate = gateService.getByCode(handler.getConfigNotBlank("target"));
                    Map<String, String> configOverride = unflattenMap(handler.getConfig(), "config");//TODO check copy config override !!!
                    logger.debug("found next gate = {} with config override = \n\n{}\n", nextGate, mapToLoggableStringLazy(configOverride));
                    if (!configOverride.isEmpty()) {
                        nextGate = EtlGateImpl.copyOf(nextGate).withConfig(configOverride).build();
                    }
                    yield new EtlNextHandlerProcessor(nextGate)::process; //TODO check copy config override !!!
                }
                case ETLHT_NOOP ->
                    identity();
                default -> {
                    EtlLoadHandler item = handlerRepository.getHandler(handler.getType());
                    yield (c) -> item.load(new EtlLoaderApiImpl(c));
                }
            };
            templates = handler.getTemplates().stream().map(importService::getByName).collect(toImmutableList());
        }

        public WaterwayMessageData handleDataImport(WaterwayMessageData context) {
            return handlerProcessor.apply(context);
        }

        private WaterwayMessageData applyTemplates(WaterwayMessageData data) {
            logger.debug("apply etl import template[s]");
            if (data.hasAttachments()) {
                List<EtlTemplateWithData> list = list(templates).map(t -> {
                    Object payload;
                    EtlTemplate template = prepareTemplate(t, data);//TODO handle multiple attachments (??)
                    if (handler.hasConfigNotBlank(ETL_HANDLER_CONFIG_INPUT_ATTACHMENT_NAME)) {
                        payload = data.getAttachment(handler.getConfigNotBlank(ETL_HANDLER_CONFIG_INPUT_ATTACHMENT_NAME)).getObject();
                    } else if (isNotBlank(template.getSource())) {
                        payload = data.getAttachment(template.getSource()).getObject();
                    } else if (data.hasAttachment(JOB_OUTPUT)) {
                        payload = data.getAttachment(JOB_OUTPUT).getObject();
                    } else {
                        payload = data.getPayload().getObject();
                    }
                    return new EtlTemplateWithDataImpl(template, Suppliers.ofInstance(payload));
                });
                return WaterwayMessageDataImpl.builder().withAttachment(WY_PROCESSING_REPORT, importService.importDataWithTemplates(list)).withMeta(data.getMeta()).build();
//                    return new EtlHandlerContextImpl(importService.importDataWithTemplate(data.getData(), getOnlyElement(templates)), data.getMeta());//TODO handle multi table import (order, relations, etc
            } else {
                checkArgument(!toBooleanOrDefault(handler.getConfig(FAIL_ON_MISSING_SOURCE_DATA_CONFIG), true), "cannot apply templates, missing input data");
                logger.debug("no input data received, skip template processing");
                return WaterwayMessageDataImpl.copyOf(data).clearAttachments().build();
            }
        }

        private WaterwayMessageData executeScript(WaterwayMessageData data) {
            attachmentsFromApi.clear();//TODO improve this, remove mutable object
            logger.debug("execute custom script");
            String script = unpackIfPacked(handler.getScript());
            Map<String, Object> params = getContextForScript(data);
            logger.debug("execute gate script = \n\n{}\n", script);
            ClassLoader classLoader = customClassloaderService.getCustomClassLoaderOrNull(handler.getConfig().get("classpath"));
            ScriptServiceHelper scriptServiceHelper = scriptService.helper().withClassLoader(classLoader).withScript(script).withLanguage(CmConvertUtils.parseEnumOrDefault(handler.getConfig().get("language"), ST_GROOVY)).withData(params);
            if (params.containsKey("logger")) {
                scriptServiceHelper.withLogger((Logger) params.get("logger"));
            }
            Map<String, Object> res = scriptServiceHelper.execute();
            Object out = res.get(SCRIPT_OUTPUT_VAR);
            logger.debug("gate script output = {}", out);
            return out instanceof WaterwayMessageData waterwayMessageData
                    ? WaterwayMessageDataImpl.copyOf(waterwayMessageData).withMeta((Map<String, String>) firstNotNull(res.get("meta"), emptyMap())).build() // override data with WaterwayMessageData from script
                    : WaterwayMessageDataImpl.copyOf(data).withMeta((Map<String, String>) firstNotNull(res.get("meta"), emptyMap())).accept(b -> {
                        attachmentsFromApi.values().stream().map(WaterwayMessageAttachmentImplBuilder::build).forEach(b::withAttachments);
                        if (out != null && isValidPayload(out)) {
                            b.withAttachment(firstNotBlank(handler.getConfig(ETL_HANDLER_CONFIG_OUTPUT_ATTACHMENT_NAME), JOB_OUTPUT), out);//TODO improve this ?
                        }
                    }).build();
        }

        private Map<String, Object> getContextForScript(WaterwayMessageData data) {
            EtlLoaderApi api = new EtlLoaderApiImpl(data);
            return map((Map) api.getConfig()).with(getMessageAndPayloadMeta(data))
                    .with(
                            "gate", api,
                            "cmdb", apiService.getCmApi(),
                            "logger", buildLoggerInScript(gate.getCode()),
                            "meta", map(data.getMeta()),
                            "config", api.getConfig()
                    );
        }

        private EtlTemplate prepareTemplate(EtlTemplateReference templateReference, WaterwayMessageData data) {
            return prepareTemplate(templateReference, data, data.hasPayload() ? data.getPayload() : null);
        }

        private EtlTemplate prepareTemplate(EtlTemplateReference templateReference, WaterwayMessageData data, @Nullable WaterwayMessageAttachment attachment) {
            return importService.prepareTemplate(templateReference, map(getContextForScript(data)).accept(m -> {
                if (attachment != null) {
                    m.putAll(map(attachment.getMeta()).with(m));
                    m.put("attachment", map(attachment.getMeta()));
                }
            }));
        }

        private class EtlNextHandlerProcessor {

            private final EtlGate nextGate;

            public EtlNextHandlerProcessor(EtlGate nextGate) {
                this.nextGate = checkNotNull(nextGate);
            }

            public WaterwayMessageData process(WaterwayMessageData c) {
                logger.debug("forward data to next gate = {}", nextGate);
//                    gateService.receive(nextGate, toDataSource(c), c.getMeta());
//                    waterwayService.forwardMessage(WaterwayMessageImpl.copyOf(message).addHistory(h->h.withQueue(nextGate)).w) //TODO check this
//                    gateService.receive(nextGate, toDataSource(c), c.getMeta());

                WaterwayMessage response = waterwayService
                        .newRequest(nextGate.getCode())//TODO copy config override !!!
                        .withMeta(c.getMeta())
                        .accept(b -> c.getAttachmentMap().forEach((k, a) -> b.withPayload(k, toDataSource(a), a.getMeta())))
                        .submit();//TODO check this
                return response; //WaterwayMessageDataImpl.copyOf(c).withAttachments(emptyList()).build();//new EtlHandlerContextImpl(null, c.getMeta());//TODO handle etl gate response (if any)
            }

        }

        private class EtlLoaderApiImpl implements EtlLoaderApi {

            private final String ETL_CONFIG_OVERRIDE_PREFIX = "var_";   // configs starting with this prefix will be overridden with meta values

            private final WaterwayMessageData context;
            private final Map<String, String> config;
            private final Map<String, EtlLoaderApiAttachment> attachments;

            public EtlLoaderApiImpl(WaterwayMessageData context) {
                this.context = checkNotNull(context);
                config = map(gate.getConfig())
                        .with(map(handler.getConfig())
                                .withoutKeys(key -> key.startsWith(ETL_CONFIG_OVERRIDE_PREFIX))
                                .with(map(handler.getConfig()).filterMapKeys(ETL_CONFIG_OVERRIDE_PREFIX).mapValues(getConfigFromMeta(context.getMeta()))))
                        .mapValues(Cm3EasyCryptoUtils::decryptValue);
                attachments = map(context.getAttachmentMap()).mapValues(EtlLoaderApiAttachmentImpl::new).mapValues(EtlLoaderApiAttachment.class::cast).immutable();
                logger.trace("etl loader handler api ready with config, meta = \n\n{}\n\n{}\n", mapToLoggableStringLazy(config), mapToLoggableStringLazy(context.getMeta()));
            }

            @Override
            public WaterwayMessageData getContext() {
                return context;
            }

            @Override
            public String getGateCode() {
                return gate.getCode();
            }

            @Override
            public Map<String, String> getConfig() {
                return config;
            }

            @Override
            public List<EtlTemplate> getTemplates() {
                return list(templates).map(t -> prepareTemplate(t, context));
            }

            @Override
            public EtlLoaderApiAttachmentHelper newAttachment(String name) {
                return new EtlLoaderApiAttachmentHelperImpl(name);
            }

            @Override
            public Map<String, EtlLoaderApiAttachment> getAttachmentsByCode() {
                return attachments;
            }

            private Function<String, String> getConfigFromMeta(Map<String, String> meta) {
                return v -> {
                    if (!meta.containsKey(v)) {
                        throw new EtlException("variable not found in meta, expected: meta.%s", v);
                    }
                    return meta.get(v);
                };
            }
        }

        private class EtlLoaderApiAttachmentImpl implements EtlLoaderApiAttachment {

            private final WaterwayMessageAttachment attachment;

            public EtlLoaderApiAttachmentImpl(WaterwayMessageAttachment attachment) {
                this.attachment = checkNotNull(attachment);
                //TODO lazy load attachments (??)
            }

            @Override
            public String getCode() {
                return attachment.getName();
            }

            @Override
            public String getDataAsString() {
                return readToString(getData());
            }

            @Override
            public DataSource getData() {
                return toDataSource(attachment);
            }

            @Override
            public Object getObject() {
                return attachment.getObject();
            }

            @Override
            public Map<String, String> getMeta() {
                return attachment.getMeta();
            }

            @Override
            public String toString() {
                return "EtlLoaderApiAttachment{" + "code=" + getCode() + '}';
            }

        }

        private class EtlLoaderApiAttachmentHelperImpl implements EtlLoaderApiAttachmentHelper {

            private final WaterwayMessageAttachmentImplBuilder builder;

            public EtlLoaderApiAttachmentHelperImpl(String name) {
                checkNotBlank(name);
                this.builder = WaterwayMessageAttachmentImpl.builder().withName(name).fromObject("");
                attachmentsFromApi.put(name, builder);
            }

            @Override
            public EtlLoaderApiAttachmentHelper withData(Object data) {
                builder.fromObject(data);
                return this;
            }

            @Override
            public EtlLoaderApiAttachmentHelper withMeta(String key, String value) {
                builder.withMeta(key, value);
                return this;
            }

        }
    }

}
