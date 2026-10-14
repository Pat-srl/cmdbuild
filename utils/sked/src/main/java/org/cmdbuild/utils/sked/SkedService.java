/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sked;

import java.time.ZonedDateTime;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Objects;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface SkedService extends AutoCloseable {

    SkedService addJobs(Iterable<SkedJob> job);

    void removeJob(String jobCode);

    List<SkedJob> getJobs();

    @Override
    void close();

    SkedService pause();

    SkedService start();

    /**
     *
     * @param from start timestamp (exclusive)
     * @param to end timestamp (exclusive)
     */
    void runJobs(ZonedDateTime from, ZonedDateTime to);

    boolean isRunning();

    List<SkedJobInfo> getJobInfos();

    boolean isRunning(String jobCode);

    void runJobNow(String code);

    default SkedJobInfo getJobInfo(String code) {
        checkNotBlank(code);
        return getJobInfos().stream().filter(j -> Objects.equals(j.getCode(), code)).collect(onlyElement("job info not found for job code =< %s >", code));
    }

    default void stop() {
        close();
    }

    default SkedService addJobs(SkedJob... jobs) {
        return addJobs(asList(jobs));
    }

    default SkedService withJobs(SkedJob... jobs) {
        return addJobs(asList(jobs));
    }

    default SkedService withJobs(Iterable<SkedJob> jobs) {
        return addJobs(jobs);
    }

    default void removeJob(SkedJob job) {
        removeJob(job.getCode());
    }

    default void runJobsInclusive(ZonedDateTime from, ZonedDateTime to) {
        runJobs(from.minusSeconds(1), to.plusSeconds(1));
    }

    default boolean isShutdown() {
        return !isRunning();
    }

}
