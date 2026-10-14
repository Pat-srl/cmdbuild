/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobRunStatus;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsJobData;
import org.cmdbuild.service.rest.v4.model.WsJobRunTriggerData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;

/**
 * @author ldare
 */
@Component
public class JobsWsCommand {

    private final JobService jobService;

    public JobsWsCommand(JobService jobService) {
        this.jobService = checkNotNull(jobService);
    }

    public List<JobData> doReadMany() {
        return jobService.getAllJobs();
    }

    public JobData doReadOne(String jobId) {
        return jobService.getOneByIdOrCode(jobId);
    }

    public JobData doCreate(WsJobData wsJobData) {
        return jobService.createJob(wsJobData.toJobData().build());
    }

    public JobData doUpdate(String jobId, WsJobData wsJobData) {
        return jobService.updateJob(wsJobData.toJobData().withId(jobService.getOneByIdOrCode(jobId).getId()).build());
    }

    public void doDelete(String jobId) {
        jobService.deleteJob(jobService.getOneByIdOrCode(jobId).getId());
    }

    public JobRun doRunJobNow(String jobId, WsJobRunTriggerData triggerData) {
        return jobService.runJob(jobService.getOneByIdOrCode(jobId).getId(), (Map) firstNonNull(triggerData == null ? null : triggerData.getConfig(), emptyMap()));
    }

    public PagedElements<JobRun> doGetJobRunsForJob(String jobId, WsQueryOptions wsQueryOptions) {
        return jobService.getJobRuns(jobService.getOneByIdOrCode(jobId).getId(), wsQueryOptions.getQuery());
    }

    public PagedElements<JobRun> doGetJobRunErrorsForJob(String jobId, Long offset, Long limit) {
        return jobService.getJobErrors(jobService.getOneByIdOrCode(jobId).getId(), DaoQueryOptionsImpl.builder().withPaging(offset, limit).build());
    }

    public PagedElements<JobRun> doGetJobRuns(WsQueryOptions wsQueryOptions) {
        return jobService.getJobRuns(wsQueryOptions.getQuery());
    }

    public PagedElements<JobRun> doGetJobRunErrors(Long offset, Long limit) {
        return jobService.getJobErrors(DaoQueryOptionsImpl.builder().withPaging(offset, limit).build());
    }

    public JobRun doGetJobRun(Long runId) {
        return jobService.getJobRun(runId);
    }

    public Map<JobRunStatus, Long> doReadJobRunStats() {
        return jobService.getJobRunStats().getJobRunCountByStatus();
    }
}
