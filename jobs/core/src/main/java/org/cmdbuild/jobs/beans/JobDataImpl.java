/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.beans;

import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyMap;
import java.util.Map;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.jobs.JobData;
import static org.cmdbuild.jobs.JobData.JOB_CONFIG_SESSION_ID;
import org.cmdbuild.jobs.JobMode;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.utils.sked.SkedJobClusterMode;

@CardMapping("_Job")
public class JobDataImpl implements JobData {

    private final Long id;
    private final String code, description, type;
    private final boolean isEnabled;
    private final Map<String, String> config;

    private JobDataImpl(JobDataImplBuilder builder) {
        this.id = builder.id;
        this.code = checkNotBlank(builder.code);
        this.description = nullToEmpty(builder.description);
        this.type = checkNotBlank(builder.type);
        this.isEnabled = firstNotNull(builder.isEnabled, true);
        this.config = map(checkNotNull(builder.config)).immutable();
    }

    @CardAttr(ATTR_ID)
    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    @CardAttr(ATTR_CODE)
    @Override
    public String getCode() {
        return code;
    }

    @CardAttr(ATTR_DESCRIPTION)
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr(JOB_DATA_ATTR_TYPE)
    public String getType() {
        return type;
    }

    @CardAttr
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @CardAttr
    @JsonBean
    @Override
    public Map<String, String> getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "JobData{" + "id=" + id + ", code=" + code + ", type=" + type + ", isEnabled=" + isEnabled + '}';
    }

    public static JobDataImplBuilder builder() {
        return new JobDataImplBuilder();
    }

    public static JobDataImplBuilder copyOf(JobData source) {
        return new JobDataImplBuilder()
                .withId(source.getId())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withType(source.getType())
                .withEnabled(source.isEnabled())
                .withConfig(source.getConfig());
    }

    public static class JobDataImplBuilder implements Builder<JobDataImpl, JobDataImplBuilder> {

        private Long id;
        private String code;
        private String description;
        private String type;
        private Boolean isEnabled;
        private Map<String, String> config = map();

        public JobDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public JobDataImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public JobDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public JobDataImplBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public JobDataImplBuilder withEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public JobDataImplBuilder withConfig(Map<String, ?> config) {
            this.config.putAll(map(firstNotNull(config, emptyMap())).mapValues(CmStringUtils::toStringOrNull));
            return this;
        }

        public JobDataImplBuilder withConfig(String key, String value) {
            this.config.put(key, value);
            return this;
        }

        public JobDataImplBuilder withConfig(String... config) {
            return this.withConfig(map(config));
        }

        public JobDataImplBuilder withRunOnce(ZonedDateTime runOnceTimestamp) {
            return withConfig(JOB_CONFIG_RUN_ONCE_TIMESTAMP, checkNotNull(toIsoDateTimeUtc(runOnceTimestamp)));
        }

        public JobDataImplBuilder withCronExpression(String value) {
            return withConfig(JOB_CONFIG_CRON_EXPR, value);
        }

        public JobDataImplBuilder withCronExpressionHasSeconds(Boolean cronExpressionHasSeconds) {
            return withConfig(JOB_CONFIG_CRON_EXPR_HAS_SECONDS, toStringOrNull(cronExpressionHasSeconds));
        }

        public JobDataImplBuilder withClusterMode(SkedJobClusterMode clusterMode) {
            return withConfig(JOB_CONFIG_CLUSTER_MODE, serializeEnum(clusterMode));
        }

        public JobDataImplBuilder withPersistRun(Boolean persistRun) {
            return withConfig(JOB_CONFIG_PERSIST_RUN, toStringOrNull(persistRun));
        }

        public JobDataImplBuilder withMode(JobMode mode) {
            this.config.put(JOB_CONFIG_MODE, serializeEnum(mode));
            return this;
        }

        public JobDataImplBuilder withUseCurrentSessionContext(boolean useCurrentSessionContext) {
            return withConfig(JOB_CONFIG_USE_CURRENT_SESSION, toStringNotBlank(useCurrentSessionContext));
        }

        public JobDataImplBuilder withSessionId(String sessionId) {
            return withConfig(JOB_CONFIG_SESSION_ID, sessionId);
        }

        @Override
        public JobDataImpl build() {
            return new JobDataImpl(this);
        }

    }
}
