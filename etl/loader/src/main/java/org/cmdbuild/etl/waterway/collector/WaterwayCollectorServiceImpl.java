package org.cmdbuild.etl.waterway.collector;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicates;
import static com.google.common.base.Predicates.compose;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.joining;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl.eq;
import static org.cmdbuild.data.filter.beans.AttributeFilterImpl.and;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_COLLECTOR;
import org.cmdbuild.etl.gate.inner.EtlProcessingMode;
import static org.cmdbuild.etl.gate.inner.EtlProcessingMode.PM_BATCH;
import static org.cmdbuild.etl.gate.inner.EtlProcessingMode.PM_REALTIME;
import org.cmdbuild.etl.job.EtlResultProcessingConfigImpl;
import org.cmdbuild.etl.job.EtlResultProcessingService;
import org.cmdbuild.etl.job.EtlResultProcessor;
import org.cmdbuild.etl.waterway.WaterwayService;
import static org.cmdbuild.etl.waterway.collector.WaterwayCollectorServiceImpl.CollectorRule.CR_DEFAULT;
import static org.cmdbuild.etl.waterway.collector.WaterwayCollectorServiceImpl.CollectorRule.CR_EXPR;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_COMPLETED;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_PROCESSED;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_PROCESSING;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_QUEUED;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.buildMessageReference;
import org.cmdbuild.etl.waterway.service.WaterwayMessageProcessor;
import org.cmdbuild.etl.waterway.service.WaterwayMessageProcessorRepository;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.jobs.JobData;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_PERSIST_RUN;
import static org.cmdbuild.jobs.JobData.JOB_MODULE;
import static org.cmdbuild.jobs.JobMode.JM_BATCH;
import static org.cmdbuild.jobs.JobMode.JM_REALTIME;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.jobs.beans.JobDataImpl;
import org.cmdbuild.lock.AutoCloseableItemLock;
import static org.cmdbuild.lock.LockScope.LS_REQUEST;
import org.cmdbuild.lock.LockService;
import org.cmdbuild.script.ScriptService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WaterwayCollectorServiceImpl implements WaterwayMessageProcessorRepository, JobRunner {

    public final static String COLLECTOR_JOB_TYPE = "wy_collect";

    private final static String COLLECTOR_JOB_MESSAGE_REFERENCE = "message", COLLECTOR_JOB_HANDLER_CODE = "collector";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WaterwayDescriptorService configService;
    private final WaterwayService service;
    private final ScriptService scriptService;
    private final LockService lockService;
    private final EtlResultProcessingService resultProcessingService;
    private final JobService jobService;
    private final FaultEventCollectorService faultService;

    public WaterwayCollectorServiceImpl(WaterwayDescriptorService configService, WaterwayService service, ScriptService scriptService, LockService lockService, EtlResultProcessingService resultProcessingService, JobService jobService, FaultEventCollectorService faultService) {
        this.configService = checkNotNull(configService);
        this.service = checkNotNull(service);
        this.scriptService = checkNotNull(scriptService);
        this.lockService = checkNotNull(lockService);
        this.resultProcessingService = checkNotNull(resultProcessingService);
        this.jobService = checkNotNull(jobService);
        this.faultService = checkNotNull(faultService);
    }

    @Override
    public Collection<WaterwayMessageProcessor> getProcessors() {
        return configService.getAllItemsEnabled(WYCIT_COLLECTOR).stream().map(WaterwayCollectorProcessor::new).collect(toImmutableList());
    }

    @Override
    public String getJobRunnerName() {
        return COLLECTOR_JOB_TYPE;
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        new WaterwayCollectorProcessor(configService.getItemByCode(jobData.getConfigNotBlank(COLLECTOR_JOB_HANDLER_CODE)))
                .processMessage(jobContext, service.getMessage(jobData.getConfigNotBlank(COLLECTOR_JOB_MESSAGE_REFERENCE)));
    }

    enum CollectorRule {
        CR_DEFAULT, CR_EXPR
    }

    private class WaterwayCollectorProcessor implements WaterwayMessageProcessor {

        private final WaterwayItem collector;

        private final CollectorRule rule;
        private final String aggregationKeyParam, aggregationKeyExpr, aggregationCountParam, aggregationCountExpr, expr, target;
        private final Integer aggregationCount;
        private final EtlProcessingMode processingMode;

        private final EtlResultProcessor resultProcessor;

        public WaterwayCollectorProcessor(WaterwayItem collector) {
            checkArgument(collector.isOfType(WYCIT_COLLECTOR));
            this.collector = checkNotNull(collector);
            this.aggregationKeyParam = collector.getConfig("aggregationKeyParam");
            this.aggregationKeyExpr = collector.getConfig("aggregationKeyExpr");
            this.aggregationCountParam = collector.getConfig("aggregationCountParam");
            this.aggregationCountExpr = collector.getConfig("aggregationCountExpr");
            this.expr = collector.getConfig("expr");
            this.target = checkNotBlank(collector.getConfig("target"));
            this.aggregationCount = toIntegerOrNull(collector.getConfig("aggregationCount"));
            this.rule = parseEnumOrDefault(collector.getConfig("rule"), isBlank(expr) ? CR_DEFAULT : CR_EXPR);
            this.processingMode = parseEnumOrDefault(collector.getConfig("processing"), PM_REALTIME);
            switch (rule) {
                case CR_EXPR -> {
                    checkNotBlank(expr);
                }
            }
            resultProcessor = resultProcessingService.getResultProcessor(new EtlResultProcessingConfigImpl(collector.getConfig()));
        }

        @Override
        public String getKey() {
            return collector.getKey();
        }

        @Override
        public WaterwayMessage queueMessage(WaterwayMessage message) {
            logger.debug("received queued message = {}", message);
            JobData job = JobDataImpl.builder()
                    .withCode(format("wy_collector_%s_%s", getCode(), message.getMessageId()))
                    .withType(COLLECTOR_JOB_TYPE)
                    .withConfig((Map) map(message.getMeta()).with(
                            COLLECTOR_JOB_HANDLER_CODE, getCode(),
                            COLLECTOR_JOB_MESSAGE_REFERENCE, buildMessageReference(message.getStorageCode(), message.getMessageId()),
                            JOB_CONFIG_PERSIST_RUN, FALSE,
                            JOB_MODULE, collector.getConfig(JOB_MODULE)// normalizeId(nullToEmpty(gate.getConfig("module"))).toLowerCase()//TODO improve this
                    ))//.with(map(collector.getConfig()).withKeys("softTimeout", "hardTimeout")))//TODO improve this
                    .withEnabled(true)
                    .accept(b -> {
                        switch (processingMode) {
                            case PM_REALTIME ->
                                b.withMode(JM_REALTIME);
                            case PM_BATCH ->
                                b.withMode(JM_BATCH);
                            default ->
                                throw new IllegalArgumentException();
                        }
                    })
                    .build();
            job = jobService.createJob(job);
            if (job.hasMode(JM_REALTIME)) {
                JobRun jobRun = jobService.getOnlyJobRun(job.getCode());//TODO improve duplicate code, see etl gate service
                jobRun.getErrorOrWarningEvents().forEach(faultService.getCurrentRequestEventCollector()::addEvent);
                if (jobRun.isFailed()) {
                    throw new EtlException("collector job failed: ", jobRun.getErrorOrWarningEvents().stream().filter(FaultEvent::isError).map(FaultEvent::getMessage).collect(joining(", ")));
                }
                return firstNotNull(service.getMessageOrNull(message.getMessageId()), message);
            } else {
                return message;
            }
        }

        public void processMessage(JobRunContext jobContext, WaterwayMessage message) {
            new MessageProcessorHelper(jobContext, message).processMessage();
        }

        private class MessageProcessorHelper {

            private final JobRunContext jobContext;
            private WaterwayMessage message, aggregate;

            public MessageProcessorHelper(JobRunContext jobContext, WaterwayMessage message) {
                this.message = checkNotNull(message);
                this.jobContext = checkNotNull(jobContext);
            }

            public void processMessage() {
                try {
                    message = service.updateMessage(message).withStatus(WMS_PROCESSING).update();
                    aggregateMessagesAndSendIfTriggered();
                } catch (Exception ex) {
                    faultService.getCurrentRequestEventCollectorIfExists().ifPresent(c -> c.addError(ex));//TODO avoid duplicate error add for job logs (??)
                    throw new EtlException(ex, "error processing collector = %s", collector);
                } finally {
                    resultProcessor.handleProcessingResult(jobContext, message, firstNotNull(aggregate, message));//TODO improve this, proper processing of whole message set, and forward of aggregate message (?)
                }
            }

            private void aggregateMessagesAndSendIfTriggered() {
                logger.debug("processing message = {}", message);
                message.checkHasQueueCode(getCode());
                String aggregationKey = getAggregationKey(message);
                Integer count = aggregationCount;

                if (isNullOrLtEqZero(count)) {
                    logger.debug("extract aggregation count with expr =< {} >", abbreviate(aggregationCountExpr));
                    if (isNotBlank(aggregationCountExpr)) {
                        count = toIntegerOrNull(scriptService.helper(getClass(), aggregationCountExpr).executeForOutput("meta", message.getMeta()));
                    } else {
                        count = toIntegerOrNull(message.getMeta(firstNotBlank(aggregationCountParam, "aggregationCount")));
                    }
                }
                logger.debug("aggregation key =< {} > count =< {} > for message = {}", aggregationKey, count, message);

                AutoCloseableItemLock lock = isBlank(aggregationKey) ? null : lockService.aquireLockOrWaitOrFail(aggregationKey, LS_REQUEST);

                try {
                    switch (rule) {
                        case CR_DEFAULT -> {
                            checkNotBlank(aggregationKey, "missing aggregation key for message =< %s >", message);
                            checkArgument(isNotNullAndGtZero(count), "missing aggregation count for message =< %s >", message);
                            List<WaterwayMessage> messages = list(message).with(getOtherQueuedMessages()).stream().filter(compose(Predicates.equalTo(aggregationKey), this::getAggregationKey))
                                    .sorted(Ordering.natural().onResultOf(WaterwayMessage::getTimestamp))
                                    .limit(count)
                                    .collect(toImmutableList());
                            if (messages.size() == count) {
                                checkArgument(messages.contains(message), "aggregation mismatch: selected messages does not include current message = %s", message);//TODO handle this better
                                aggregateMessagesAndSend(messages);
                            } else {
                                returnMessageToQueue();
                            }
                        }
                        case CR_EXPR -> {
                            logger.debug("execute custom aggregation script =< {} >", abbreviate(expr));
                            List messages = (List) scriptService.helper(getClass(), expr).executeForOutput("meta", message.getMeta());
                            if (messages != null && !messages.isEmpty()) {
                                messages = list(messages).map(m -> m instanceof String s ? service.getMessage(s) : (WaterwayMessage) m);
                                aggregateMessagesAndSend(messages);
                            } else {
                                returnMessageToQueue();
                            }
                        }
                        default ->
                            throw new IllegalArgumentException();
                    }
                } finally {
                    if (lock != null) {
                        lock.close();
                    }
                }
            }

            private void returnMessageToQueue() {
                logger.debug("no aggregation triggered, leave message in collector queue");
                message = service.updateMessage(message).withStatus(WMS_PROCESSED).update();
                message = service.updateMessage(message).withStatus(WMS_QUEUED).update();
            }

            private void aggregateMessagesAndSend(List<WaterwayMessage> messages) {
                logger.debug("aggregate messages = {}", messages);
                messages.forEach(m -> service.updateMessage(m).withStatus(WMS_PROCESSING).update());//TODO handle processing, exceptions (??)
                aggregate = service.newRequest()
                        .withTarget(target)
                        .accept(b -> {
                            Multimap<String, WaterwayMessageAttachment> attachments = LinkedHashMultimap.create();
                            messages.forEach(m -> {
                                b.withMeta(m.getMeta());
                                m.getAttachmentMap().forEach(attachments::put);
                            });
                            //TODO load attachments data ??
                            attachments.asMap().forEach((k, v) -> {
                                if (v.size() == 1) {
                                    b.withPayload(k, getOnlyElement(v).getObject(), getOnlyElement(v).getMeta());
                                } else {
                                    AtomicInteger i = new AtomicInteger(1);
                                    v.forEach(a -> b.withPayload("%s_%s".formatted(k, i.getAndIncrement()), a.getObject(), a.getMeta()));
                                }
                            });
                        }).submit();//TODO force batch processing (also ignore processing errors)
                logger.debug("mark messages as completed");
                messages.forEach(m -> service.updateMessage(m).withStatus(WMS_PROCESSED).update());//TODO handle processing, exceptions (??)
                messages.forEach(m -> service.updateMessage(m).withStatus(WMS_COMPLETED).update());//TODO trace aggregated message (like forward etc)
            }

            @Nullable
            private String getAggregationKey(WaterwayMessage message) {
                if (isNotBlank(aggregationKeyExpr)) {
                    logger.debug("extract aggregation key with expr =< {} >", abbreviate(aggregationKeyExpr));
                    return toStringOrNull(scriptService.helper(getClass(), aggregationKeyExpr).executeForOutput("meta", message.getMeta()));
                } else {
                    return message.getMeta(firstNotBlank(aggregationKeyParam, "aggregationId"));
                }
            }

            private List<WaterwayMessage> getOtherQueuedMessages() {
                return service.getMessages(DaoQueryOptionsImpl.build(and(eq("queue", getCode()).toAttributeFilter(), eq("status", serializeEnum(WMS_QUEUED)).toAttributeFilter()).toCmdbFilters())).elements();//TODO improve attribute filter !!
            }
        }

    }
}
