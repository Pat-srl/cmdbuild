/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.Long.parseLong;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;

public interface JobService {

    List<JobData> getAllJobs();

    JobData getJob(long id);

    JobData getJobByCode(String code);

    JobData createJob(JobData data);

    JobData updateJob(JobData data);

    void deleteJob(long id);

    void definitiveDeleteJob(long id);

    JobRun runJob(long id, Map<String, String> configOverride);

    JobRun runJobSafe(long id);

    Future<JobRun> runJobLater(long id);

    PagedElements<JobRun> getJobRuns(String jobCode, DaoQueryOptions queryOptions);

    PagedElements<JobRun> getJobRuns(long jobId, DaoQueryOptions queryOptions);

    PagedElements<JobRun> getJobErrors(long jobId, DaoQueryOptions queryOptions);

    PagedElements<JobRun> getJobRuns(DaoQueryOptions queryOptions);

    PagedElements<JobRun> getJobErrors(DaoQueryOptions queryOptions);

    JobRun getJobRun(Long runId);

    JobRunStats getJobRunStats();

    default List<JobRun> getJobRuns(String jobCode) {
        return getJobRuns(jobCode, DaoQueryOptionsImpl.emptyOptions()).elements();
    }

    default JobRun getOnlyJobRun(String jobCode) {
        return checkNotNull(getOnlyElement(getJobRuns(jobCode), null), "job run not found for code =< %s >", jobCode);
    }

    default JobRun runJob(long id) {
        return runJob(id, emptyMap());
    }

    default JobData getOneByIdOrCode(String idOrCode) {
        return NumberUtils.isCreatable(idOrCode) ? getJob(parseLong(idOrCode)) : getJobByCode(idOrCode);
    }
}
