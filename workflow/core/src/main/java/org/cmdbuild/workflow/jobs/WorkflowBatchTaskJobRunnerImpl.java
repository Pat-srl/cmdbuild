/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.jobs;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.jobs.JobData;
import static org.cmdbuild.jobs.JobMode.JM_BATCH;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.jobs.beans.JobDataImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import static org.cmdbuild.workflow.FlowEvent.FlowEventType.FE_AFTER_ADVANCE;
import static org.cmdbuild.workflow.WorkflowCommonConst.WFBATCHTASK_JOB_TYPE;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.inner.RiverFlowEvent;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WorkflowBatchTaskJobRunnerImpl implements JobRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String WFBATCHTASK_PROCESS_ID = "wf_batch_task_processId", WFBATCHTASK_FLOW_ID = "wf_batch_task_cardId";

    private final WorkflowService workflowService;
    private final WorkflowRiverBatchExecutorService batchExecutor;
    private final JobService jobService;
    private final SessionService sessionService;

    public WorkflowBatchTaskJobRunnerImpl(WorkflowService workflowService, WorkflowRiverBatchExecutorService batchExecutor, JobService jobService, SessionService sessionService, EventBusService eventService) {
        this.workflowService = checkNotNull(workflowService);
        this.batchExecutor = checkNotNull(batchExecutor);
        this.jobService = checkNotNull(jobService);
        this.sessionService = checkNotNull(sessionService);
        eventService.getWorkflowEventBus().register(new Object() {
            @Subscribe
            public void handleFlowEvent(RiverFlowEvent event) {
                switch (event.getType()) {
                    case FE_AFTER_ADVANCE ->
                        submitBatchTasks(event.getFlow(), event.getRiverFlow());
                }
            }
        });
    }

    private void submitBatchTasks(Flow flow, RiverFlow riverFlow) {
        if (riverFlow.getBatchTasks().isEmpty()) {
            logger.debug("no batch tasks for wf = {}, skipping", flow);
        } else {
            logger.info(marker(), "submit batch tasks for wf = {} tasks = {}", flow, riverFlow.getBatchTasks());
            jobService.createJob(JobDataImpl.builder()
                    .withClusterMode(RUN_ON_SINGLE_NODE)
                    .withPersistRun(false)
                    .withSessionId(sessionService.getCurrentSessionId())
                    .withType(WFBATCHTASK_JOB_TYPE)
                    .withMode(JM_BATCH)
                    .withEnabled(true)
                    .withCode(key(WFBATCHTASK_JOB_TYPE, flow.getClassName(), flow.getCardId()))
                    .withConfig(WFBATCHTASK_PROCESS_ID, flow.getClassName())
                    .withConfig(WFBATCHTASK_FLOW_ID, toStringNotBlank(flow.getCardId()))
                    .build());
        }
    }

    @Override
    public String getJobRunnerName() {
        return WFBATCHTASK_JOB_TYPE;
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        new BatchTaskHelper(jobData).runJob(jobContext);

    }

    @Override
    public void vaildateJob(JobData jobData) {
        new BatchTaskHelper(jobData).validate();
    }

    private class BatchTaskHelper {

        private final Flow flow;

        public BatchTaskHelper(JobData jobData) {
            flow = workflowService.getFlowCard(jobData.getConfigNotBlank(WFBATCHTASK_PROCESS_ID), toLong(jobData.getConfigNotBlank(WFBATCHTASK_FLOW_ID)));
        }

        public void runJob(JobRunContext jobContext) {
            logger.info("run batch tasks for flow = {}", flow);
            batchExecutor.executeBatchTasks(flow);
        }

        public void validate() {
            //nothing to do
        }

    }
}
