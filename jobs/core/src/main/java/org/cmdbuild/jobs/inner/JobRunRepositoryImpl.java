/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.GT;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import org.cmdbuild.jobs.JobRun;
import static org.cmdbuild.jobs.JobRun.JOB_RUN_ATTR_HAS_ERROR;
import org.cmdbuild.jobs.JobRunRepository;
import org.cmdbuild.jobs.JobRunStats;
import org.cmdbuild.jobs.JobRunStatus;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.springframework.stereotype.Component;

@Component
public class JobRunRepositoryImpl implements JobRunRepository {

    private final DaoService dao;

    public static final Map<String, String> JOB_RUN_ATTR_MAPPING = ImmutableMap.copyOf(map(
            "timestamp", "Timestamp",
            "elapsedMillis", "ElapsedTime",
            "status", "JobStatus",
            "jobCode", "Job",
            "nodeId", "NodeId"
    ));

    public JobRunRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public JobRun create(JobRun jobRun) {
        return dao.create(jobRun);
    }

    @Override
    public JobRun update(JobRun jobRun) {
        return dao.update(jobRun);
    }

    @Override
    public JobRun getJobRun(Long runId) {
        return dao.getById(JobRun.class, runId);
    }

    @Override
    public PagedElements<JobRun> getJobRuns(String jobCode, DaoQueryOptions queryOptions) {
        checkNotBlank(jobCode);
        if (queryOptions.getSorter().isNoop()) {
            queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).orderBy("Timestamp", DESC).build();
        }
        List<JobRun> list = dao.selectAll().from(JobRun.class).where("Job", EQ, jobCode).withOptions(queryOptions.mapAttrNames(JOB_RUN_ATTR_MAPPING)).asList();
        if (queryOptions.isPaged()) {
            return paged(list, dao.selectCount().from(JobRun.class).where("Job", EQ, jobCode).where(queryOptions.getFilter().mapNames(JOB_RUN_ATTR_MAPPING)).getCount());
        } else {
            return paged(list);
        }
    }

    @Override
    public PagedElements<JobRun> getJobErrors(String jobCode, DaoQueryOptions queryOptions) {
        checkNotBlank(jobCode);
        if (queryOptions.getSorter().isNoop()) {
            queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).orderBy("Timestamp", DESC).build();
        }
        List<JobRun> list = dao.selectAll().from(JobRun.class).where("Job", EQ, jobCode).where(JOB_RUN_ATTR_HAS_ERROR, EQ, true).withOptions(queryOptions).asList();
        if (queryOptions.isPaged()) {
            return paged(list, dao.selectCount().from(JobRun.class).where("Job", EQ, jobCode).where(JOB_RUN_ATTR_HAS_ERROR, EQ, true).where(queryOptions.getFilter()).getCount());
        } else {
            return paged(list);
        }
    }

    @Override
    public PagedElements<JobRun> getJobRuns(DaoQueryOptions queryOptions) {
        if (queryOptions.getSorter().isNoop()) {
            queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).orderBy("Timestamp", DESC).build();
        }
        List<JobRun> list = dao.selectAll().from(JobRun.class).withOptions(queryOptions.mapAttrNames(JOB_RUN_ATTR_MAPPING)).asList();
        if (queryOptions.isPaged()) {
            return paged(list, dao.selectCount().from(JobRun.class).where(queryOptions.getFilter().mapNames(JOB_RUN_ATTR_MAPPING)).getCount());
        } else {
            return paged(list);
        }
    }

    @Override
    public PagedElements<JobRun> getJobErrors(DaoQueryOptions queryOptions) {
        if (queryOptions.getSorter().isNoop()) {
            queryOptions = DaoQueryOptionsImpl.copyOf(queryOptions).orderBy("Timestamp", DESC).build();
        }
        List<JobRun> list = dao.selectAll().from(JobRun.class).where(JOB_RUN_ATTR_HAS_ERROR, EQ, true).withOptions(queryOptions).asList();
        if (queryOptions.isPaged()) {
            return paged(list, dao.selectCount().from(JobRun.class).where(JOB_RUN_ATTR_HAS_ERROR, EQ, true).where(queryOptions.getFilter()).getCount());
        } else {
            return paged(list);
        }
    }

    @Override
    public List<JobRun> getJobErrors(ZonedDateTime since) {
        checkNotNull(since);
        return dao.selectAll().from(JobRun.class)
                .where(JOB_RUN_ATTR_HAS_ERROR, EQ, true)
                .where("Timestamp", GT, since)
                .orderBy("Timestamp", DESC).asList();
    }

    @Override
    public JobRunStats getStats() {
        Map<JobRunStatus, Long> stats = list(EnumSet.allOf(JobRunStatus.class)).collect(toMap(identity(), k -> 0l)).with(dao.getJdbcTemplate().queryForList("SELECT count(\"Id\") _count,\"JobStatus\" FROM \"_JobRun\" WHERE \"Status\" = 'A' GROUP BY \"JobStatus\"").stream()
                .collect(toMap(r -> parseEnum(toStringNotBlank(r.get("JobStatus")), JobRunStatus.class), r -> toLong(r.get("_count"))))).immutableCopy();
        return new JobRunStats() {
            @Override
            public Map<JobRunStatus, Long> getJobRunCountByStatus() {
                return stats;
            }
        };
    }

}
