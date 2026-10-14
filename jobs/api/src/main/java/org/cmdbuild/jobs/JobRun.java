/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import static com.google.common.base.Objects.equal;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.fault.FaultEventsData;
import org.cmdbuild.fault.FaultLeveOrderErrorsFirst;
import org.cmdbuild.fault.FaultLevel;
import static org.cmdbuild.fault.FaultLevel.FL_INFO;
import static org.cmdbuild.fault.FaultLevel.FL_WARNING;
import static org.cmdbuild.jobs.JobRunStatus.JRS_FAILED;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.urlToDataSource;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.url.CmUrlUtils.isDataUrl;

public interface JobRun {

    final String JOB_RUN_ATTR_HAS_ERROR = "HasError", JOB_OUTPUT = "output", JOB_RUN_RUNID = "runId";

    @Nullable
    Long getId();

    String getRunId();

    String getJobCode();

    JobRunStatus getJobStatus();

    ZonedDateTime getTimestamp();

    boolean isCompleted();

    boolean hasErrors();

    @Nullable
    Long getElapsedTime();

    @Nullable
    FaultEventsData getErrorMessageData();

    @Nullable
    String getLogs();

    @Nullable
    String getNodeId();

    Map<String, String> getMetadata();

    @Nullable
    default String getOutput() {
        return getMetadata().get(JOB_OUTPUT);
    }

    default boolean hasOutput() {
        return isNotBlank(getOutput());
    }

    default boolean isFailed() {
        return equal(getJobStatus(), JRS_FAILED);
    }

    default boolean hasWarning() {
        return hasErrors() || getMaxErrorLevel().isWorseOrEqualTo(FL_WARNING);
    }

    default List<FaultEvent> getErrorOrWarningEvents() {
        return getErrorMessageData() == null ? emptyList() : (List) getErrorMessageData().getData();
    }

    default FaultLevel getMaxErrorLevel() {
        return getErrorOrWarningEvents().stream().map(FaultEvent::getLevel).max(FaultLeveOrderErrorsFirst.INSTANCE).orElse(FL_INFO);
    }

    default boolean hasLogs() {
        return isNotBlank(getLogs());
    }

    @Nullable
    default DataSource getOutputData() {
        if (hasOutput()) {
            String output = getOutput();
            return isDataUrl(output) ? urlToDataSource(output) : newDataSource(output); //TODO improve this (allow data urls without auto decoding
        } else {
            return null;
        }
    }

}
