/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import jakarta.annotation.Nullable;

public interface JobsConfiguration {

    boolean isEnabled();

    boolean isRealtimeDeleteEnabled();

    @Nullable
    Integer getMaxJobRunRecordsToKeep();

    @Nullable
    Long getMaxJobRunRecordAgeToKeepSeconds();

    @Nullable
    Integer getMaxRunOnceJobRecordsToKeep();

    @Nullable
    Long getMaxRunOnceJobRecordAgeToKeepSeconds();
}
