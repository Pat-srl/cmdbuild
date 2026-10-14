package org.cmdbuild.jobs;

public interface JobTimeoutEvent {

    JobData getJob();

    JobRun getRun();

    String getThread();

    String getStackTrace();
}
