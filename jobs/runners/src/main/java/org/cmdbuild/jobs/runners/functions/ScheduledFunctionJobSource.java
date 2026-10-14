/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.runners.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.Collection;
import java.util.Map;
import org.cmdbuild.dao.function.StoredFunctionService;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.cmdbuild.jobs.beans.JobDataImpl;
import org.cmdbuild.scheduler.JobSource;
import org.cmdbuild.scheduler.SchedulerException;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.springframework.stereotype.Component;

@Component
public class ScheduledFunctionJobSource implements JobSource, JobRunner {

    private static final String SCHEDULED_FUNCTION_JOB_TYPE = "cm_function", FUNCTION_PARAM = "cm_sql_function";

    private final StoredFunctionService functionService;

    public ScheduledFunctionJobSource(StoredFunctionService functionService) {
        this.functionService = checkNotNull(functionService);
    }

    @Override
    public String getJobSourceName() {
        return "function";
    }

    @Override
    public Collection<JobData> getJobs() {
        return functionService.getAllFunctions().stream().filter(f -> f.isScheduled()).map(f -> {
            try {
                return JobDataImpl.builder()
                        .withType(SCHEDULED_FUNCTION_JOB_TYPE)
                        .withPersistRun(true)
                        .withEnabled(true)
                        .withCronExpressionHasSeconds(false)
                        .withCode(f.getName())
                        .withClusterMode(RUN_ON_SINGLE_NODE)
                        .withCronExpression(checkNotBlank(f.getMetadata().getScheduledJobExpr()))
                        .withConfig(FUNCTION_PARAM, f.getName())
                        .build();
            } catch (Exception ex) {
                throw new SchedulerException(ex, "error loading scheduled function job for function =< %s >", f);
            }
        }).collect(toImmutableList());
    }

    @Override
    public Map<String, String> runJobWithOutput(JobData jobData, JobRunContext jobContext) {
        return map(functionService.callFunction(jobData.getConfigNotBlank(FUNCTION_PARAM))).mapValues(CmStringUtils::toStringOrNull);
    }

    @Override
    public void vaildateJob(JobData jobData) {
        jobData.getConfigNotBlank(FUNCTION_PARAM);
    }

    @Override
    public String getJobRunnerName() {
        return SCHEDULED_FUNCTION_JOB_TYPE;
    }

}
