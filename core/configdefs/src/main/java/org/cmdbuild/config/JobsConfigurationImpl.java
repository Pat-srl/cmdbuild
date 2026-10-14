/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.jobs")
public class JobsConfigurationImpl implements JobsConfiguration {

    @ConfigValue(key = "enabled", description = "enable scheduled jobs", defaultValue = FALSE, category = CC_ENV)
    private boolean isEnabled;

    @ConfigValue(key = "realtime.delete", description = "delete job row directly from postgres table", defaultValue = TRUE, category = CC_ENV)
    private boolean isRealtimeDeleteEnabled;

    @ConfigValue(key = "run.history.maxRecordsToKeep", defaultValue = "100000")
    private Integer maxRecordsToKeep;

    @ConfigValue(key = "run.history.maxRecordAgeToKeepSeconds", defaultValue = "-1")
    private Long maxRecordAgeToKeepSeconds;

    @ConfigValue(key = "runOnceJobs.maxRecordsToKeep", defaultValue = "-1")
    private Integer maxRunOnceJobRecordsToKeep;

    @ConfigValue(key = "runOnceJobs.maxRecordAgeToKeepSeconds", defaultValue = "7776000") //90gg
    private Long maxRunOnceJobRecordAgeToKeepSeconds;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean isRealtimeDeleteEnabled() {
        return isRealtimeDeleteEnabled;
    }

    @Override
    public Integer getMaxJobRunRecordsToKeep() {
        return maxRecordsToKeep;
    }

    @Override
    public Long getMaxJobRunRecordAgeToKeepSeconds() {
        return maxRecordAgeToKeepSeconds;
    }

    @Override
    public Integer getMaxRunOnceJobRecordsToKeep() {
        return maxRunOnceJobRecordsToKeep;
    }

    @Override
    public Long getMaxRunOnceJobRecordAgeToKeepSeconds() {
        return maxRunOnceJobRecordAgeToKeepSeconds;
    }
}
