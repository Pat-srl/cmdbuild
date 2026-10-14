package org.cmdbuild.etl.bus;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.String.format;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.joining;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.bus.EtlBusSubscriberService.EtlBusSubscriber;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_BUS;
import org.cmdbuild.etl.gate.inner.EtlProcessingMode;
import static org.cmdbuild.etl.gate.inner.EtlProcessingMode.PM_BATCH;
import static org.cmdbuild.etl.gate.inner.EtlProcessingMode.PM_REALTIME;
import org.cmdbuild.etl.job.EtlResultProcessingConfigImpl;
import org.cmdbuild.etl.job.EtlResultProcessingService;
import org.cmdbuild.etl.job.EtlResultProcessor;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageImpl;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_PROCESSING;
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
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EtlBusServiceImpl implements EtlBusService, WaterwayMessageProcessorRepository, JobRunner {

    public final static String BUS_JOB_TYPE = "wy_bus";

    private final static String BUS_JOB_MESSAGE_REFERENCE = "message", BUS_JOB_HANDLER_CODE = "bus";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WaterwayDescriptorService configService;
    private final WaterwayService service;
    private final EtlResultProcessingService resultProcessingService;
    private final JobService jobService;
    private final FaultEventCollectorService faultService;
    private final EtlBusSubscriberService subscriberService;

    public EtlBusServiceImpl(EtlBusSubscriberService subscriberService, WaterwayDescriptorService configService, WaterwayService service, EtlResultProcessingService resultProcessingService, JobService jobService, FaultEventCollectorService faultService) {
        this.configService = checkNotNull(configService);
        this.service = checkNotNull(service);
        this.resultProcessingService = checkNotNull(resultProcessingService);
        this.jobService = checkNotNull(jobService);
        this.faultService = checkNotNull(faultService);
        this.subscriberService = checkNotNull(subscriberService);
    }

    @Override
    public Collection<WaterwayMessageProcessor> getProcessors() {
        return configService.getAllItemsEnabled(WYCIT_BUS).stream().map(WaterwayBusProcessor::new).collect(toImmutableList());
    }

    @Override
    public List<EtlBus> getAll() {
        return (List) getProcessors();
    }

    @Override
    public EtlBus getByCode(String code) {
        return new WaterwayBusProcessor(configService.getItemByCode(code));
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        new WaterwayBusProcessor(configService.getItemByCode(jobData.getConfigNotBlank(BUS_JOB_HANDLER_CODE)))
                .deliverMessage(jobContext, service.getMessage(jobData.getConfigNotBlank(BUS_JOB_MESSAGE_REFERENCE)));
    }

    @Override
    public String getJobRunnerName() {
        return BUS_JOB_TYPE;
    }

    private class WaterwayBusProcessor implements WaterwayMessageProcessor, EtlBus {

        private final WaterwayItem bus;

        private final EtlProcessingMode processingMode;

        private final EtlResultProcessor resultProcessor;

        public WaterwayBusProcessor(WaterwayItem bus) {
            checkArgument(bus.isOfType(WYCIT_BUS));
            this.bus = checkNotNull(bus);
            this.processingMode = parseEnumOrDefault(bus.getConfig("processing"), PM_REALTIME);
            resultProcessor = resultProcessingService.getResultProcessor(new EtlResultProcessingConfigImpl(bus.getConfig()));
        }

        @Override
        public String getKey() {
            return bus.getKey();
        }

        @Override
        public WaterwayMessage queueMessage(WaterwayMessage message) {
            logger.debug("received queued message = {}", message);
            JobData job = JobDataImpl.builder()
                    .withCode(format("wy_bus_%s_%s", getCode(), message.getMessageId()))
                    .withType(BUS_JOB_TYPE)
                    .withConfig((Map) map(message.getMeta()).with(
                            BUS_JOB_HANDLER_CODE, getCode(),
                            BUS_JOB_MESSAGE_REFERENCE, buildMessageReference(message.getStorageCode(), message.getMessageId()),
                            JOB_CONFIG_PERSIST_RUN, FALSE,
                            JOB_MODULE, bus.getConfig(JOB_MODULE)// normalizeId(nullToEmpty(gate.getConfig("module"))).toLowerCase()//TODO improve this
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
                    throw new EtlException("bus job failed: ", jobRun.getErrorOrWarningEvents().stream().filter(FaultEvent::isError).map(FaultEvent::getMessage).collect(joining(", ")));
                }
                return firstNotNull(service.getMessageOrNull(message.getMessageId()), message);
            } else {
                return message;
            }
        }

        public void deliverMessage(JobRunContext jobContext, WaterwayMessage message) {
            try {
                message = service.updateMessage(message).withStatus(WMS_PROCESSING).update();
                logger.debug("deliver message = {}", message);

                List<EtlBusSubscriber> subscribers = subscriberService.getSubscribersForBus(getCode());
                logger.debug("subscribers = {}", subscribers);

                WaterwayMessage baseMessage = message;
                AtomicInteger i = new AtomicInteger(1);

                subscribers.forEach(s -> {
                    WaterwayMessage toDeliver = WaterwayMessageImpl.copyOf(baseMessage).withMessageId("%s.%s".formatted(baseMessage.getMessageId(), i.getAndIncrement())).build();//TODO check this
                    try {
                        subscriberService.deliverMessage(getCode(), s.getSubscriberId(), toDeliver);//TODO handle dynamic subscriber, delayed deliver
                    } catch (Exception ex) {
                        logger.error(marker(), "error delivering message = {} to subscriber = {}", toDeliver, s);
                    }
                });

                logger.debug("delivered message = {}", message);
            } catch (Exception ex) {
                faultService.getCurrentRequestEventCollectorIfExists().ifPresent(c -> c.addError(ex));//TODO avoid duplicate error add for job logs (??)
                throw new EtlException(ex, "error processing bus = %s", bus);
            } finally {
                resultProcessor.handleProcessingResult(jobContext, message, message);
            }
        }

    }
}
