/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import org.cmdbuild.asyncjob.AsyncRequestJob;
import org.cmdbuild.asyncjob.AsyncRequestJobService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @author schursin
 */
@Component
public class AsyncOperationWsCommand {

    private final AsyncRequestJobService asyncRequestJobService;

    public AsyncOperationWsCommand(AsyncRequestJobService asyncRequestJobService) {
        this.asyncRequestJobService = asyncRequestJobService;
    }

    public AsyncRequestJob doGetAsyncJobStatus(Long jobId) {
        return asyncRequestJobService.getJobForCurrentUserById(jobId);
    }

    public String doGetAsyncJobResult(Long jobId) {
        AsyncRequestJob job = asyncRequestJobService.getJobForCurrentUserById(jobId);
        checkArgument(job.isCompleted(), "cannot get result for job = %s: job is still running", jobId);
        asyncRequestJobService.deleteJob(job.getId());
        return new String(job.getResponseContent());//TODO status code, non--json response
    }
}
