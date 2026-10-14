/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.jobs.JobMode.JM_OTHER;
import static org.cmdbuild.jobs.JobMode.JM_SCHEDULED;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.utils.sked.SkedJobClusterMode;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;

public interface JobData {

    final String JOB_DATA_ATTR_TYPE = "Type",
            JOB_CONFIG_CRON_EXPR = "cronExpression",
            JOB_CONFIG_RUN_ONCE_TIMESTAMP = "runOnceTimestamp",
            JOB_CONFIG_CRON_EXPR_HAS_SECONDS = "cronExpressionHasSeconds",
            JOB_CONFIG_MODE = "jobMode",
            JOB_CONFIG_SESSION_ID = "cm_job_sessionId",
            JOB_CONFIG_USE_CURRENT_SESSION = "useCurrentUser",
            JOB_CONFIG_USE_CURRENT_SESSION_OLD = "cm_job_useCurrentSession",
            JOB_CONFIG_SESSION_USER = "sessionUser",
            JOB_CONFIG_SESSION_USER_OLD = "cm_job_sessionUser",
            JOB_CONFIG_PERSIST_RUN = "persistRun",
            JOB_CONFIG_CLUSTER_MODE = "clusterMode",
            JOB_CONFIG_CLUSTER_BALANCING_STRATEGY = "clusterBalancingStrategy",
            JOB_CONFIG_WORKGROUP = "cm_job_workgroup",
            JOB_MODULE = "cm_job_module";

    @Nullable
    Long getId();

    String getCode();

    String getDescription();

    String getType();

    boolean isEnabled();

    Map<String, String> getConfig();

    @Nullable
    default String getWorkgroup() {
        return getConfig(JOB_CONFIG_WORKGROUP);
    }

    default boolean hasWorker() {
        return isNotBlank(getWorkgroup());
    }

    @Nullable
    default String getModule() {
        return toStringOrNull(getConfig(JOB_MODULE));
    }

    default boolean hasModule() {
        return isNotBlank(getModule());
    }

    default JobMode getMode() {
        String mode = toStringOrNull(getConfig(JOB_CONFIG_MODE));
        if (isNotBlank(mode)) {
            return parseEnum(mode, JobMode.class);
        } else if (isNotBlank(getCronExpression())) {
            return JM_SCHEDULED;
        } else {
            return JM_OTHER;
        }
    }

    @Nullable
    default String getCronExpression() {
        return getConfig(JOB_CONFIG_CRON_EXPR);
    }

    default boolean persistJobRun() {
        return toBooleanOrDefault(getConfig(JOB_CONFIG_PERSIST_RUN), true);
    }

    default boolean cronExpressionHasSeconds() {
        return toBooleanOrDefault(getConfig(JOB_CONFIG_CRON_EXPR_HAS_SECONDS), false);
    }

    default SkedJobClusterMode getClusterMode() {
        return parseEnumOrDefault(getConfig(JOB_CONFIG_CLUSTER_MODE), RUN_ON_SINGLE_NODE);
    }

    default boolean isOfType(String type) {
        return equal(getType(), type);
    }

    default String getConfigNotBlank(String key) {
        return checkNotBlank(getConfig(key), "config not found for key =< %s >", key);
    }

    @Nullable
    default String getConfig(String key) {
        return getConfig().get(key);
    }

    default boolean hasMode(JobMode mode) {
        return equal(getMode(), mode);
    }

    default boolean useCurrentSessionContext() {
        return toBooleanOrDefault(firstNotBlankOrNull(getConfig(JOB_CONFIG_USE_CURRENT_SESSION), getConfig(JOB_CONFIG_USE_CURRENT_SESSION_OLD)), false);
    }

    @Nullable
    default String getSessionUser() {
        return firstNotBlankOrNull(getConfig(JOB_CONFIG_SESSION_USER), getConfig(JOB_CONFIG_SESSION_USER_OLD));
    }

    @Nullable
    default String getSessionId() {
        return getConfig(JOB_CONFIG_SESSION_ID);
    }

    default boolean hasSessionUser() {
        return isNotBlank(getSessionUser());
    }

    default boolean hasSessionId() {
        return isNotBlank(getSessionId());
    }

    default boolean isRunOnce() {
        return isNotBlank(getConfig(JOB_CONFIG_RUN_ONCE_TIMESTAMP));
    }

    default ZonedDateTime getRunOnceTimestamp() {
        return checkNotNull(toDateTime(getConfig(JOB_CONFIG_RUN_ONCE_TIMESTAMP)));
    }
}
