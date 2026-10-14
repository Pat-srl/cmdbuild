package org.cmdbuild.scheduler;

import java.time.ZonedDateTime;
import java.util.List;

public interface SchedulerService {

    List<ScheduledJobInfo> getConfiguredJobs();

    void runJob(String code);

    void runJobsForTimeRange(ZonedDateTime startInclusive, ZonedDateTime endInclusive);

}
