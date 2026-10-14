package org.cmdbuild.jobs;

import java.util.Map;

public interface JobRunStats {

    Map<JobRunStatus, Long> getJobRunCountByStatus();

}
