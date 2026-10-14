/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysmon;

import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

public interface SystemStatusLog {

    ZonedDateTime getBeginDate();

    int getJavaMemoryUsed();

    @Nullable
    Integer getProcessMemoryUsed();

    int getJavaMemoryTotal();

    int getJavaMemoryMax();

    int getJavaPid();

    String getHostname();

    @Nullable
    String getNodeId();

    @Nullable
    String getBuildInfo();

    @Nullable
    Integer getSystemMemoryUsed();

    @Nullable
    Integer getSystemMemoryTotal();

    double getLoadAvg();

    int getActiveSessionCount();

    @Nullable
    Integer getFilesystemMemoryUsed();

    @Nullable
    Integer getFilesystemMemoryTotal();

    @Nullable
    String getWarnings();

    default boolean hasWarnings() {
        return !isBlank(getWarnings());
    }

    @Nullable
    default Integer getFilesystemMemoryFree() {
        return getFilesystemMemoryUsed() != null && getFilesystemMemoryTotal() != null ? (getFilesystemMemoryTotal() - getFilesystemMemoryUsed()) : null;
    }

    default int getJavaMemoryFree() {
        return getJavaMemoryTotal() - getJavaMemoryUsed();
    }

    @Nullable
    default Integer getSystemMemoryFree() {
        return getSystemMemoryUsed() != null && getSystemMemoryTotal() != null ? (getSystemMemoryTotal() - getSystemMemoryUsed()) : null;
    }

}
