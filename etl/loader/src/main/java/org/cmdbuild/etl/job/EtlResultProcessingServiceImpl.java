/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.job;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;
import java.time.Duration;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.joining;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.etl.EtlException;
import static org.cmdbuild.etl.jobs.WaterwayDelayedMessageJobRunner.WY_DELAYED_MESSAGES_JOB_ATTR_MESSAGE_ID;
import static org.cmdbuild.etl.jobs.WaterwayDelayedMessageJobRunner.WY_DELAYED_MESSAGES_JOB_ATTR_MESSAGE_STORAGE;
import static org.cmdbuild.etl.jobs.WaterwayDelayedMessageJobRunner.WY_DELAYED_MESSAGES_JOB_TYPE;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.utils.EtlResultUtils;
import static org.cmdbuild.etl.utils.EtlResultUtils.serializeEtlProcessingResult;
import static org.cmdbuild.etl.utils.EtlUtils.getEtlProcessingResultOrNull;
import static org.cmdbuild.etl.utils.EtlUtils.getMessageAndPayloadMeta;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentImpl;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentStorage.WMAS_EMBEDDED;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_JSON;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_OBJECT;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_FAULT_EVENT_META_JOB_RUN;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_MESSAGE_RETRY_DELAY;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_MESSAGE_RETRY_TIMESTAMP;
import org.cmdbuild.etl.waterway.message.WaterwayMessageDataImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageImpl;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_COMPLETED;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_ERROR;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_FAILED;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_PROCESSED;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_PROCESSING;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_STANDBY;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.toDataSource;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.fault.FaultEventCollector;
import org.cmdbuild.fault.FaultEventCollectorService;
import static org.cmdbuild.jobs.JobMode.JM_SCHEDULED;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.jobs.beans.JobDataImpl;
import org.cmdbuild.notification.NotificationService;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDuration;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EtlResultProcessingServiceImpl implements EtlResultProcessingService {

    private final static String ETL_RETRY_COUNT = "etl_retry_count";

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final EmailTemplateService emailTemplateService;
    private final NotificationService notificationService;
    private final WaterwayService waterwayService;
    private final FaultEventCollectorService faultService;
    private final WaterwayService messageService;
    private final JobService jobService;
    private final EventBus eventBus;

    public EtlResultProcessingServiceImpl(EventBusService eventBusService, EmailTemplateService emailTemplateService, NotificationService notificationService, WaterwayService waterwayService, FaultEventCollectorService faultService, WaterwayService messageService, JobService jobService) {
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.notificationService = checkNotNull(notificationService);
        this.waterwayService = checkNotNull(waterwayService);
        this.faultService = checkNotNull(faultService);
        this.messageService = checkNotNull(messageService);
        this.jobService = checkNotNull(jobService);
        this.eventBus = eventBusService.getWaterwayEventBus();
    }

    @Override
    public void overrideLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public EtlResultProcessor getResultProcessor(EtlResultProcessingConfig config) {
        return new EtlResultProcessorImpl(config);
    }

    private class EtlResultProcessorImpl implements EtlResultProcessor {

        private final EtlResultProcessingConfig config;
        private final EmailTemplate errorTemplate, successTemplate, warningTemplate, failTemplate;

        public EtlResultProcessorImpl(EtlResultProcessingConfig config) {
            this.config = checkNotNull(config);
            failTemplate = Optional.ofNullable(trimToNull(config.onFailNotify())).map(emailTemplateService::getByNameOrId).orElse(null);
            errorTemplate = Optional.ofNullable(trimToNull(config.onErrorNotify())).map(emailTemplateService::getByNameOrId).orElse(null);
            warningTemplate = Optional.ofNullable(trimToNull(config.onWarningNotify())).map(emailTemplateService::getByNameOrId).orElse(null);
            successTemplate = Optional.ofNullable(trimToNull(config.onSuccessNotify())).map(emailTemplateService::getByNameOrId).orElse(null);
        }

        @Override
        public WaterwayMessage handleProcessingResult(JobRunContext jobContext, WaterwayMessage message, WaterwayMessageData result) {
            try {
                result = WaterwayMessageDataImpl.copyOf(result)
                        .withErrors(list(result.getErrors()).with(list(faultService.getCurrentRequestEventCollectorIfExists().map(FaultEventCollector::getCollectedEvents).orElse(emptyList()))).map(e -> e.withMeta(WY_FAULT_EVENT_META_JOB_RUN, jobContext.getRunId())))
                        .withFaultTolerantErrors(result.getFaultTolerantErrors())
                        .withLogs(list(result.getLogs(), faultService.getCurrentRequestEventCollectorIfExists().map(FaultEventCollector::getLogs).orElse(null)).filter(StringUtils::isNotBlank).collect(joining("\n\n")))
                        .build();
                return new EtlResultProcessorHelper(jobContext, message, result).handleProcessingResult();
            } catch (Exception ex) {
                throw new EtlException(ex, "error processing result for job = %s with result = %s", jobContext, result);
            }
        }

        private class EtlResultProcessorHelper {

            private final JobRunContext jobContext;
            private final WaterwayMessageData result;
            private final List<FaultEvent> faults;
            private final List<FaultEvent> faultTolerantEvents; // will be used in getDataForStorage()
            private final boolean handleRegularMessageLifecycle;
            private WaterwayMessage message;

            public EtlResultProcessorHelper(JobRunContext jobContext, WaterwayMessage message, WaterwayMessageData result) {
                this.jobContext = checkNotNull(jobContext);
                this.message = checkNotNull(message);
                this.result = checkNotNull(result);
                faults = list(result.getErrors()).filter(e -> equal(e.getMeta(WY_FAULT_EVENT_META_JOB_RUN), jobContext.getRunId())).immutableCopy();
                faultTolerantEvents = list(result.getFaultTolerantErrors());
                handleRegularMessageLifecycle = message.hasStatus(WMS_PROCESSING);
            }

            public WaterwayMessage handleProcessingResult() {
                logger.debug("processing result has {} fault events", faults.size());
                faults.forEach(f -> logger.debug("fault event = {}", f));
                faultTolerantEvents.forEach(f -> logger.debug("fault tolerant event = {}", f));

                boolean hasWarning = faults.stream().anyMatch(FaultEvent::isWarning) && !faults.stream().anyMatch(FaultEvent::isError),
                        hasError = faults.stream().anyMatch(FaultEvent::isError) || (hasWarning && config.onWarningThrowError()),
                        isFailed = hasError && !config.onFailThrowSuccess();

                logger.debug("handle processing result for message = {} : warning = {} error = {} failed = {}", message, hasWarning, hasError, isFailed);

                if (hasError) {
                    message = messageService.updateMessage(message).withStatus(WMS_ERROR).withData(getDataForStorage()).update();
                } else if (handleRegularMessageLifecycle) {
                    message = messageService.updateMessage(message).withStatus(WMS_PROCESSED).withData(getDataForStorage()).update();
                }

                if (hasWarning) {
                    logger.debug("processing warning result");
                    handleNotification(warningTemplate, false);
                    handleForward(config.onWarningForward());
                }

                if (hasError) {
                    logger.debug("processing error result");
                    handleNotification(errorTemplate, true);
                    handleForward(config.onErrorForwad());
                }

                if (!message.getFaultTolerantErrors().isEmpty()) {
                    logger.warn("[%d] fault tolerant errors found:".formatted(message.getFaultTolerantErrors().size()));
                    message.getFaultTolerantErrors().forEach(fte -> {
                        final String warnMsg = "etl with fault tolerant error: %s".formatted(getFirstLine(fte.getStacktrace()));
                        logger.warn(warnMsg);
                    });
                }

                if (isFailed) {

                    boolean hasRetry = config.onErrorRetry(), retryFailed = hasRetry && toIntegerOrDefault(result.getMeta(ETL_RETRY_COUNT), 0) + 1 >= config.getRetryCount(), retryActive = hasRetry && !retryFailed;

                    if (retryFailed) {
                        logger.warn("exceeded retry count, processing will fail");
                        message = WaterwayMessageImpl.copyOf(message).withMeta(ETL_RETRY_COUNT, 0).build();//TODO check retry count reset
                    }

                    if (retryActive) {
                        logger.debug("processing retry");
                        int retryCount = toIntegerOrDefault(result.getMeta(ETL_RETRY_COUNT), 0) + 1;
                        Duration delay = Duration.ofSeconds(config.getRetryDelay().getSeconds() * retryCount);
                        ZonedDateTime retryTimestamp = now().plus(delay);
                        logger.debug("will retry this job after delay = {} timestamp = {}", toIsoDuration(delay), toIsoDateTimeLocal(retryTimestamp));
                        messageService.updateMessage(message).withStatus(WMS_STANDBY).withMeta(ETL_RETRY_COUNT, retryCount, WY_MESSAGE_RETRY_DELAY, toIsoDuration(delay), WY_MESSAGE_RETRY_TIMESTAMP, toIsoDateTimeUtc(retryTimestamp)).update();
                        jobService.createJob(JobDataImpl.builder()
                                .withCode("wy_delayed_message_%s_%s".formatted(message.getQueue(), message.getMessageId()))
                                .withType(WY_DELAYED_MESSAGES_JOB_TYPE)
                                .withConfig(WY_DELAYED_MESSAGES_JOB_ATTR_MESSAGE_STORAGE, checkNotBlank(message.getStorageCode()), WY_DELAYED_MESSAGES_JOB_ATTR_MESSAGE_ID, message.getMessageId())
                                .withClusterMode(RUN_ON_SINGLE_NODE).withMode(JM_SCHEDULED).withRunOnce(retryTimestamp)
                                .build());
                    } else {
                        logger.debug("processing fail result");
                        message = messageService.updateMessage(message).withStatus(WMS_FAILED).update();
                        handleNotification(failTemplate, true);
                        handleForward(config.onFailForward());
                    }
                } else {
                    logger.debug("processing success result");
                    handleNotification(successTemplate, false);
                    if (isNotBlank(config.onSuccessForward())) {
                        logger.debug("forward success message = {} to target =< {} >", message, config.onSuccessForward());
                        message = messageService.updateMessage(message).forwardTo(config.onSuccessForward()).update();//TODO check this
                    } else if (handleRegularMessageLifecycle) {
                        logger.debug("completed processing for message = {}", message);
                        message = messageService.updateMessage(message).withStatus(WMS_COMPLETED).update();
                    }
                }

                logger.debug("completed result processing for message = {}", message);

                eventBus.post(new EtlAfterProcessingEventImpl(message));

                return message;
            }

            private FaultEvent getFaultEvent() {//TODO improve fault event management, handle multiple faults etc
                return checkNotNull(faults.stream().sorted(Ordering.natural().onResultOf(e -> e.getLevel().getIndex())).findFirst().orElse(null), "missing fault event for failed operation result = %s", result);
            }

            private void handleNotification(@Nullable EmailTemplate template, boolean addFaultEvent) {
                try {
                    if (template != null) {
                        logger.debug("send notification with template = {}", template);
                        notificationService.sendNotificationFromTemplate(template, prepareEmailData(addFaultEvent ? getFaultEvent() : null));
                    }
                } catch (Exception ex) {
                    logger.error(marker(), "error processing notification with data = {} template = {}", result, template, ex);
                }
            }

            private void handleForward(@Nullable String target) {
                try {
                    if (isNotBlank(target)) {
                        logger.debug("forward message to target =< {} >", target);
                        waterwayService.newRequest(target)
                                .withMeta(result.getMeta())
                                .accept(b -> result.getAttachmentMap().forEach((k, a) -> b.withPayload(k, toDataSource(a), a.getMeta())))
                                .submit();//TODO check this; fix forward id etc (if data is message, which it is
                    }
                } catch (Exception ex) {
                    logger.error(marker(), "error processing forward with data = {} target =< {} >", result, target, ex);
                }
            }

            private Map<String, Object> prepareEmailData(@Nullable FaultEvent fault) {
                Map<String, Object> meta = map();
                if (result != null) {
                    meta.putAll(getMessageAndPayloadMeta(result));
                }
                return map(meta).with("meta", meta, "cm_etl_meta", meta).with(EtlResultUtils.prepareEmailData(jobContext, fault, getEtlProcessingResultOrNull(result)));//TODO improve fault event management, handle multiple faults etc
            }

            private WaterwayMessageData getDataForStorage() {//TODO improve this
                try {
                    return WaterwayMessageDataImpl.copyOf(result).withFaultTolerantErrors(faultTolerantEvents).clearAttachments().withAttachments(list(result.getAttachmentMap().values()).map(a -> {
                        if (a.isOfType(WMAT_OBJECT) && a.hasStorage(WMAS_EMBEDDED)) {
                            Object object = a.getObject();
                            if (object instanceof EtlProcessingResult r) {
                                a = WaterwayMessageAttachmentImpl.copyOf(a).withType(WMAT_JSON).withObject(serializeEtlProcessingResult(r)).build();
                            } else if (object instanceof DataSource dataSource) {
                                a = WaterwayMessageAttachmentImpl.copyOf(a).fromObject(dataSource).build();
                            } else {
                                a = WaterwayMessageAttachmentImpl.copyOf(a).withType(WMAT_JSON).withObject(toJson(a.getObject())).build();//TODO improve this
                            }
                            checkArgument(!a.isOfType(WMAT_OBJECT));
                        }
                        return a;
                    })).build();
                } catch (Exception ex) {
                    throw new EtlException(ex, "error preparing data for storage");
                }
            }

            private String getFirstLine(String stacktrace) {
                return stacktrace.split(System.lineSeparator())[0];
            }
        }

    }
}
