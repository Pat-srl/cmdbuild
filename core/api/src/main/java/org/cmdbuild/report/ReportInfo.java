/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report;

import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import static org.cmdbuild.report.ReportInfo.ReportProcessingMode.RPM_REALTIME;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public interface ReportInfo extends PrivilegeSubjectWithInfo {

    final String REPORT_CONFIG_CLASSPATH = "classpath", REPORT_CONFIG_PROCESSING_MODE = "processing";

    @Nullable
    @Override
    Long getId();

    String getCode();

    @Override
    String getDescription();

    boolean isActive();

    Map<String, String> getConfig();

    @Nullable
    String getIconFont();

    @Override
    default String getName() {
        return getCode();
    }

    @Nullable
    default String getCustomClasspath() {
        return getConfig().get(REPORT_CONFIG_CLASSPATH);
    }

    default boolean hasCustomClasspath() {
        return isNotBlank(getCustomClasspath());
    }

    @Override
    public default String getPrivilegeId() {
        return privilegeId(PS_REPORT, getId());
    }

    default ReportProcessingMode getProcessingMode() {
        return parseEnumOrDefault(getConfig().get(REPORT_CONFIG_PROCESSING_MODE), RPM_REALTIME);
    }

    default boolean isBatchReport() {
        return getProcessingMode() == ReportProcessingMode.RPM_BATCH;
    }
    
    public enum ReportProcessingMode {
        RPM_REALTIME, RPM_BATCH

    }

}
