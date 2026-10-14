/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.utils.lang.CmMapUtils;

import static org.cmdbuild.jobs.JobRunStatusImpl.serializeJobRunStatus;
import static org.cmdbuild.services.serialization.RequestSerializer.serializeErrors;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class JobSerializer {

    public static CmMapUtils.FluentMap<String, Object> serializeBasicJobRun(JobRun jobRun) {
        return map(
                "_id", jobRun.getId(),
                "jobCode", jobRun.getJobCode(),
                "status", serializeJobRunStatus(jobRun.getJobStatus()),
                "completed", jobRun.isCompleted(),
                "nodeId", jobRun.getNodeId(),
                "timestamp", toIsoDateTime(jobRun.getTimestamp()),
                "elapsedMillis", jobRun.getElapsedTime());
    }

    public static Object serializeDetailedJobRun(JobRun jobRun) {
        return serializeBasicJobRun(jobRun).with(
                "errors", serializeErrors(jobRun.getErrorOrWarningEvents()),
                "logs", jobRun.getLogs(),
                "meta", jobRun.getMetadata());
    }

    public static CmMapUtils.FluentMap<String, Object> serializeDetailedJob(JobData jobData) {
        return serializeBasicJob(jobData).with("config", unflattenMaps(jobData.getConfig()));
    }

    public static CmMapUtils.FluentMap<String, Object> serializeBasicJob(JobData jobData) {
        return map(
                "_id", jobData.getId(),
                "code", jobData.getCode(),
                "description", jobData.getDescription(),
                "type", jobData.getType(),
                "cronExpression", jobData.getConfig().get("cronExpression"),
                "enabled", jobData.isEnabled());
    }
}
