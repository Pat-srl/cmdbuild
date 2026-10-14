/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.fault.FaultEventImpl;
import org.cmdbuild.fault.FaultEventsData;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobRunStatus;
import static org.cmdbuild.jobs.JobRunStatus.JRS_RUNNING;
import static org.cmdbuild.jobs.JobRunStatusImpl.serializeJobRunStatus;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.fault.FaultEvent;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

@CardMapping("_JobRun")
public class JobRunImpl implements JobRun {

    private final Long id, elapsedTime;
    private final String jobCode, logs, nodeId, runId;
    private final JobRunStatus jobStatus;
    private final ZonedDateTime timestamp;
    private final boolean completed, hasError;
    private final FaultEventsData errorMessageData;
    private final Map<String, String> metadata;

    private JobRunImpl(JobRunImplBuilder builder) {
        this.id = builder.id;
        this.jobCode = checkNotBlank(builder.jobCode);
        this.timestamp = checkNotNull(builder.timestamp);
        this.completed = builder.completed;
        if (completed) {
            this.elapsedTime = checkNotNull(builder.elapsedTime);
            this.errorMessageData = checkNotNull(builder.errorMessageData);
            this.jobStatus = checkNotNull(builder.jobStatus);
            switch (jobStatus) {
                case JRS_FAILED:
                    hasError = true;
                    break;
                case JRS_COMPLETED:
                    this.hasError = builder.errorMessageData.getData().stream().anyMatch(FaultEvent::isError);
                    break;
                case JRS_RUNNING:
                default:
                    throw runtime("invalid job status = %s", jobStatus);
            }
        } else {
            this.jobStatus = JRS_RUNNING;
            this.elapsedTime = null;
            this.errorMessageData = null;
            this.hasError = false;
        }
        this.logs = builder.logs;
        this.nodeId = builder.nodeId;
        this.metadata = map(firstNotNull(builder.metadata, emptyMap())).accept(m -> {
            if (isNullOrLtEqZero(id) && isBlank(m.get(JOB_RUN_RUNID))) {
                m.put(JOB_RUN_RUNID, randomId());
            }
        }).immutable();
        runId = isNullOrLtEqZero(id) ? checkNotBlank(metadata.get(JOB_RUN_RUNID)) : toStringNotBlank(id);
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @Nullable
    @CardAttr
    public Long getElapsedTime() {
        return elapsedTime;
    }

    @Override
    @CardAttr("Job")
    public String getJobCode() {
        return jobCode;
    }

    @Override
    public JobRunStatus getJobStatus() {
        return jobStatus;
    }

    @CardAttr("JobStatus")
    public String getJobStatusAsString() {
        return serializeJobRunStatus(jobStatus);
    }

    @Override
    @CardAttr
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    @CardAttr
    public boolean isCompleted() {
        return completed;
    }

    @Override
    @Nullable
    @CardAttr("Errors")
    public FaultEventsData getErrorMessageData() {
        return errorMessageData;
    }

    @Override
    @JsonBean
    @CardAttr
    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    @Nullable
    @CardAttr(value = JOB_RUN_ATTR_HAS_ERROR, readFromDb = false)
    public boolean hasErrors() {
        return hasError;
    }

    @Override
    @Nullable
    @CardAttr
    public String getLogs() {
        return logs;
    }

    @Override
    @Nullable
    @CardAttr
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public String getRunId() {
        return runId;
    }

    @Override
    public String toString() {
        return "JobRun{job=" + jobCode + ", run=" + runId + '}';
    }

    public static JobRunImplBuilder builder() {
        return new JobRunImplBuilder();
    }

    public static JobRunImplBuilder copyOf(JobRun source) {
        return new JobRunImplBuilder()
                .withId(source.getId())
                .withElapsedTime(source.getElapsedTime())
                .withJobCode(source.getJobCode())
                .withJobStatus(source.getJobStatus())
                .withTimestamp(source.getTimestamp())
                .withCompleted(source.isCompleted())
                .withNodeId(source.getNodeId())
                .withLogs(source.getLogs())
                .withErrorMessageData(source.getErrorMessageData())
                .withMetadata(source.getMetadata());
    }

    public static class JobRunImplBuilder implements Builder<JobRunImpl, JobRunImplBuilder> {

        private Long id;
        private Long elapsedTime;
        private String jobCode, nodeId, logs;
        private JobRunStatus jobStatus;
        private ZonedDateTime timestamp;
        private Boolean completed;
        private FaultEventsData errorMessageData;
        private Map<String, String> metadata = map();

        public JobRunImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public JobRunImplBuilder withElapsedTime(Long elapsedTime) {
            this.elapsedTime = elapsedTime;
            return this;
        }

        public JobRunImplBuilder withJobCode(String jobCode) {
            this.jobCode = jobCode;
            return this;
        }

        public JobRunImplBuilder withNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public JobRunImplBuilder withLogs(String logs) {
            this.logs = logs;
            return this;
        }

        @CardAttr("JobStatus")
        public JobRunImplBuilder withJobStatus(JobRunStatus jobStatus) {
            this.jobStatus = jobStatus;
            return this;
        }

        public JobRunImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public JobRunImplBuilder withCompleted(Boolean completed) {
            this.completed = completed;
            return this;
        }

        public JobRunImplBuilder withErrorMessageData(FaultEventsData errorMessageData) {
            this.errorMessageData = errorMessageData;
            return this;
        }

        public JobRunImplBuilder withErrorMessages(List<FaultEvent> data) {
            return this.withErrorMessageData(new FaultEventsData((List) data));
        }

        public JobRunImplBuilder withMetadata(Map<String, String> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        @Override
        public JobRunImpl build() {
            return new JobRunImpl(this);
        }

    }
}
