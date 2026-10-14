/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.gate.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.lang.String.format;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import static java.util.stream.Collectors.joining;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ETLGATE_ACCESS;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ETLGATE_ALL;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.auth.utils.AuthUtils.checkAuthorized;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.gate.EtlGateService;
import static org.cmdbuild.etl.gate.inner.EtlGate.ETL_GATE_KEY;
import static org.cmdbuild.etl.gate.inner.EtlJobUtils.ETLJOB_ATTR_GATE;
import static org.cmdbuild.etl.gate.inner.EtlJobUtils.ETLJOB_ATTR_MESSAGE_STORAGE;
import static org.cmdbuild.etl.gate.inner.EtlJobUtils.ETLJOB_ATTR_MESSAGE_ID;
import static org.cmdbuild.etl.gate.inner.EtlJobUtils.ETLJOB_TYPE;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_QUEUED;
import org.cmdbuild.etl.waterway.service.WaterwayMessageProcessor;
import org.cmdbuild.etl.waterway.service.WaterwayMessageProcessorRepository;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.jobs.JobData;
import static org.cmdbuild.jobs.JobData.JOB_MODULE;
import org.cmdbuild.jobs.JobService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.jobs.JobMode.JM_REALTIME;
import static org.cmdbuild.jobs.JobMode.JM_BATCH;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.beans.JobDataImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.etl.waterway.WaterwayService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.etl.gate.inner.EtlJobUtils.ETLJOB_OUTPUT_MESSAGE_REFERENCE;
import static org.cmdbuild.etl.gate.inner.EtlProcessingMode.PM_BATCH;
import static org.cmdbuild.etl.gate.inner.EtlProcessingMode.PM_GRID;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_SESSION_USER;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_SESSION_USER_OLD;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_USE_CURRENT_SESSION;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_USE_CURRENT_SESSION_OLD;

@Component
public class EtlGateServiceImpl implements EtlGateService, WaterwayMessageProcessorRepository, EtlGateGridMessageProcessingHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EtlGateRepository gateRepository;
    private final JobService jobService;
    private final FaultEventCollectorService faultService;
    private final OperationUserSupplier operationUser;
    private final WaterwayService messageService;
    private final EtlGateGridMessageProcessor gridProcessor;

    public EtlGateServiceImpl(EtlGateRepository gateRepository, JobService jobService, FaultEventCollectorService faultService, OperationUserSupplier operationUser, WaterwayService messageService, EtlGateGridMessageProcessor gridProcessor) {
        this.gateRepository = checkNotNull(gateRepository);
        this.jobService = checkNotNull(jobService);
        this.faultService = checkNotNull(faultService);
        this.operationUser = checkNotNull(operationUser);
        this.messageService = checkNotNull(messageService);
        this.gridProcessor = checkNotNull(gridProcessor);
    }

    @Override
    public List<EtlGate> getAllForCurrentUser() {
        return list(getAll()).filter(this::currentUserCanAccess);
    }

    @Override
    public EtlGate getByCodeForCurrentUser(String gateId) {
        EtlGate gate = getByCode(gateId);
        checkAuthorized(currentUserCanAccess(gate), "access denied for gate =< %s >", gate.getCode());
        return gate;
    }

    @Override
    public boolean currentUserCanAccess(EtlGate gate) {
        return gate.getAllowPublicAccess() || operationUser.hasPrivileges(p -> p.hasPrivileges(RP_ETLGATE_ALL) || (p.hasPrivileges(RP_ETLGATE_ACCESS) && p.hasReadAccess(gate)));
    }

    @Override
    public Collection<WaterwayMessageProcessor> getProcessors() {
        return list(getAll()).map(EtlGateProcessor::new);
    }

    @Override
    public EtlGate create(EtlGate gate) {
        return gateRepository.create(gate);
    }

    @Override
    public EtlGate update(EtlGate gate) {
        return gateRepository.update(gate);
    }

    @Override
    public void delete(String code) {
        gateRepository.delete(code);
    }

    @Override
    public List<EtlGate> getAll() {
        return gateRepository.getAll();
    }

    @Override
    public EtlGate getByCode(String gateId) {
        return gateRepository.getByCode(gateId);
    }

    @Override
    public EtlGate getByCodeOrNull(String gate) {
        return gateRepository.getByCodeOrNull(gate);
    }

    @Override
    public WaterwayMessage queueGridMessageForLocalProcessing(String messageReference) {
        logger.debug("received for local processing grid message reference = {}", messageReference);
        WaterwayMessage message = messageService.getMessage(messageReference);
        logger.debug("received for local processing grid message = {}", message);
        EtlGateProcessor processor = (EtlGateProcessor) uniqueIndex(getProcessors(), WaterwayMessageProcessor::getCode).get(message.getQueueCode());//TODO improve this
        message = processor.queueGridMessageForLocalProcessing(message);
        logger.debug("queued grid message, output = {}", message);
        return message;
    }

    private class EtlGateProcessor implements WaterwayMessageProcessor {

        private final EtlGate gate;

        public EtlGateProcessor(EtlGate gate) {
            this.gate = checkNotNull(gate);
        }

        @Override
        public String getKey() {
            return checkNotBlank(gate.getConfig(ETL_GATE_KEY));
        }

        @Override
        public WaterwayMessage queueMessage(WaterwayMessage message) {
            message.checkHasStatus(WMS_QUEUED);
            message.checkHasQueueKey(getKey());

            switch (gate.getProcessingMode()) {
                case PM_GRID -> {
                    gridProcessor.queueMessageForGridProcessing(message);
                    return message;
                }
                default -> {
                    return doQueueMessageForLocalProcessing(message, gate.getProcessingMode());
                }
            }
        }

        public WaterwayMessage queueGridMessageForLocalProcessing(WaterwayMessage message) {
            message.checkHasStatus(WMS_QUEUED);
            message.checkHasQueueKey(getKey());

            return doQueueMessageForLocalProcessing(message, PM_BATCH);
        }

        private WaterwayMessage doQueueMessageForLocalProcessing(WaterwayMessage message, EtlProcessingMode processingMode) {
            JobData job = JobDataImpl.builder()
                    .withCode(format("etl_%s_%s", gate.getCode(), message.getMessageId()))
                    .withType(ETLJOB_TYPE)
                    .withConfig((Map) map(message.getMeta()).with(
                            ETLJOB_ATTR_GATE, gate.getCode(),
                            ETLJOB_ATTR_MESSAGE_STORAGE, message.getStorageCode(),
                            ETLJOB_ATTR_MESSAGE_ID, message.getMessageId()
                    //ETLJOB_ATTR_TIMESTAMP, toIsoDateTime(job.getTimestamp())
                    //JOB_MODULE, gate.getConfig(JOB_MODULE)
                    // normalizeId(nullToEmpty(gate.getConfig("module"))).toLowerCase()//TODO improve this
                    ).with(map(gate.getConfig()).withKeys("softTimeout", "hardTimeout", JOB_MODULE, JOB_CONFIG_SESSION_USER, JOB_CONFIG_USE_CURRENT_SESSION_OLD, JOB_CONFIG_USE_CURRENT_SESSION, JOB_CONFIG_SESSION_USER_OLD)))//TODO improve this
                    .withEnabled(true)
                    .accept(b -> {
                        switch (processingMode) {
                            case PM_REALTIME ->
                                b.withMode(JM_REALTIME);
                            case PM_BATCH ->
                                b.withMode(JM_BATCH);
                            case PM_NOOP ->
                                b.withEnabled(false);
                            default ->
                                throw new IllegalArgumentException();
                        }
                    })
                    .build();

            logger.debug("create job = {} from message = {}", job, message);
            job = jobService.createJob(job); //TODO jobrun-less processing (process job without writing records on db)

            if (job.hasMode(JM_REALTIME)) {
//                message = messageService.getMessage(gate.getCode(), message.getMessageId());

//                message.getErrorOrWarningEvents().stream().map(ErrorMessageData::toFaultEvent).forEach(faultService.getCurrentRequestEventCollector()::addEvent);
//                if (jobRun.isFailed()) {
//                    throw new EtlException("etl job failed: ", jobRun.getErrorOrWarningEvents().stream().filter(ErrorMessageData::isError).map(ErrorMessageData::getMessage).collect(joining(", ")));
//                }
                JobRun jobRun = jobService.getOnlyJobRun(job.getCode());//TODO improve error message management, for wy messages
                String messageReference = jobRun.getMetadata().get(ETLJOB_OUTPUT_MESSAGE_REFERENCE);
                message = messageService.getMessage(messageReference);
                jobRun.getErrorOrWarningEvents().forEach(faultService.getCurrentRequestEventCollector()::addEvent);
                if (jobRun.isFailed()) {                    
                    throw new EtlException("etl job failed: ", fetchMessages(jobRun.getErrorOrWarningEvents(), FaultEvent::isError));
                } 
            }
            return message;
        }

        @Override
        public String toString() {
            return "EtlGateProcessor{" + "gate=" + gate + '}';
        }
        
        private String fetchMessages(final List<FaultEvent> events, final Predicate<FaultEvent> eventMatcher) {
            return events.stream().filter(eventMatcher).map(FaultEvent::getMessage).collect(joining(", "));
        }

    }

}
