/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import static java.util.Collections.emptyMap;
import java.util.Map;

public interface JobRunner {

    String getJobRunnerName();

    default Map<String, String> runJobWithOutput(JobRunContext jobContext) {//TODO improve this
        return runJobWithOutput(jobContext.getJob(), jobContext);
    }

    default void runJob(JobData jobData, JobRunContext jobContext) {
        runJobWithOutput(jobData, jobContext);
    }

    default Map<String, String> runJobWithOutput(JobData jobData, JobRunContext jobContext) {
        runJob(jobData, jobContext);
        return emptyMap();
    }

    default void vaildateJob(JobData jobData) {

    }

}
