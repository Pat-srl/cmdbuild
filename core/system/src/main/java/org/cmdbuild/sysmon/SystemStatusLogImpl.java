/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysmon;

import static com.google.common.base.Strings.emptyToNull;
import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.date.CmDateUtils;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_SystemStatusLog")
public class SystemStatusLogImpl implements SystemStatusLog {

    private final int javaMemoryUsed, javaMemoryTotal, javaMemoryMax, activeSessionCount, javaPid;
    private final double loadAvg;
    private final Integer filesystemMemoryUsed, filesystemMemoryTotal, systemMemoryUsed, systemMemoryTotal, processMemoryUsed;
    private final String warnings, hostname, nodeId, buildInfo;
    private final ZonedDateTime beginDate;

    private SystemStatusLogImpl(SystemStatusRecordImplBuilder builder) {
        this.javaMemoryUsed = (builder.javaMemoryUsed);
        this.javaMemoryMax = firstNotNull(builder.javaMemoryMax, builder.javaMemoryUsed);//TODO improve this
        this.processMemoryUsed = (builder.processMemoryUsed);
        this.javaMemoryTotal = (builder.javaMemoryTotal);
        this.javaPid = (builder.javaPid);
        this.hostname = checkNotBlank(builder.hostname);
        this.nodeId = builder.nodeId;
        this.buildInfo = builder.buildInfo;
        this.systemMemoryUsed = (builder.systemMemoryUsed);
        this.systemMemoryTotal = (builder.systemMemoryTotal);
        this.activeSessionCount = (builder.activeSessionCount);
        this.loadAvg = (builder.loadAvg);
        this.filesystemMemoryUsed = builder.filesystemMemoryUsed;
        this.filesystemMemoryTotal = builder.filesystemMemoryTotal;
        this.warnings = emptyToNull(builder.warnings);
        this.beginDate = firstNotNull(builder.beginDate, CmDateUtils.now());
    }

    @Override
    @CardAttr(value = ATTR_BEGINDATE, writeToDb = false)
    @Nullable
    public ZonedDateTime getBeginDate() {
        return beginDate;
    }

    @Override
    @CardAttr
    public int getJavaMemoryUsed() {
        return javaMemoryUsed;
    }

    @Override
    @CardAttr
    public int getJavaMemoryMax() {
        return javaMemoryMax;
    }

    @Override
    @CardAttr
    @Nullable
    public Integer getProcessMemoryUsed() {
        return processMemoryUsed;
    }

    @Override
    @CardAttr
    public int getJavaMemoryTotal() {
        return javaMemoryTotal;
    }

    @Override
    @CardAttr("Pid")
    public int getJavaPid() {
        return javaPid;
    }

    @Override
    @CardAttr
    public String getHostname() {
        return hostname;
    }

    @Override
    @Nullable
    @CardAttr
    public String getNodeId() {
        return nodeId;
    }

    @Override
    @Nullable
    @CardAttr
    public String getBuildInfo() {
        return buildInfo;
    }

    @Override
    @Nullable
    @CardAttr
    public Integer getSystemMemoryUsed() {
        return systemMemoryUsed;
    }

    @Override
    @Nullable
    @CardAttr
    public Integer getSystemMemoryTotal() {
        return systemMemoryTotal;
    }

    @Override
    @CardAttr
    public int getActiveSessionCount() {
        return activeSessionCount;
    }

    @Override
    @CardAttr
    public double getLoadAvg() {
        return loadAvg;
    }

    @Override
    @Nullable
    @CardAttr
    public Integer getFilesystemMemoryUsed() {
        return filesystemMemoryUsed;
    }

    @Override
    @Nullable
    @CardAttr
    public Integer getFilesystemMemoryTotal() {
        return filesystemMemoryTotal;
    }

    @Override
    @Nullable
    @CardAttr
    public String getWarnings() {
        return warnings;
    }

    public static SystemStatusRecordImplBuilder builder() {
        return new SystemStatusRecordImplBuilder();
    }

    public static SystemStatusRecordImplBuilder copyOf(SystemStatusLog source) {
        return new SystemStatusRecordImplBuilder()
                .withBeginDate(source.getBeginDate())
                .withJavaMemoryUsed(source.getJavaMemoryUsed())
                .withProcessMemoryUsed(source.getProcessMemoryUsed())
                .withJavaMemoryTotal(source.getJavaMemoryTotal())
                .withJavaMemoryMax(source.getJavaMemoryMax())
                .withJavaPid(source.getJavaPid())
                .withHostname(source.getHostname())
                .withNodeId(source.getNodeId())
                .withBuildInfo(source.getBuildInfo())
                .withSystemMemoryUsed(source.getSystemMemoryUsed())
                .withSystemMemoryTotal(source.getSystemMemoryTotal())
                .withActiveSessionCount(source.getActiveSessionCount())
                .withLoadAvg(source.getLoadAvg())
                .withFilesystemMemoryUsed(source.getFilesystemMemoryUsed())
                .withFilesystemMemoryTotal(source.getFilesystemMemoryTotal())
                .withWarnings(source.getWarnings());
    }

    public static class SystemStatusRecordImplBuilder implements Builder<SystemStatusLogImpl, SystemStatusRecordImplBuilder> {

        private Integer javaMemoryUsed, processMemoryUsed;
        private Integer javaMemoryTotal, javaMemoryMax, javaPid;
        private Integer systemMemoryUsed;
        private Integer systemMemoryTotal;
        private Integer activeSessionCount;
        private Double loadAvg;
        private Integer filesystemMemoryUsed;
        private Integer filesystemMemoryTotal;
        private String warnings, hostname, nodeId, buildInfo;
        private ZonedDateTime beginDate;

        public SystemStatusRecordImplBuilder withBeginDate(ZonedDateTime date) {
            this.beginDate = date;
            return this;
        }

        public SystemStatusRecordImplBuilder withJavaMemoryUsed(Integer javaMemoryUsed) {
            this.javaMemoryUsed = javaMemoryUsed;
            return this;
        }

        public SystemStatusRecordImplBuilder withProcessMemoryUsed(Integer processMemoryUsed) {
            this.processMemoryUsed = processMemoryUsed;
            return this;
        }

        public SystemStatusRecordImplBuilder withJavaMemoryTotal(Integer javaMemoryTotal) {
            this.javaMemoryTotal = javaMemoryTotal;
            return this;
        }

        public SystemStatusRecordImplBuilder withJavaMemoryMax(Integer javaMemoryMax) {
            this.javaMemoryMax = javaMemoryMax;
            return this;
        }

        public SystemStatusRecordImplBuilder withJavaPid(Integer javaPid) {
            this.javaPid = javaPid;
            return this;
        }

        public SystemStatusRecordImplBuilder withHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public SystemStatusRecordImplBuilder withNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public SystemStatusRecordImplBuilder withBuildInfo(String buildInfo) {
            this.buildInfo = buildInfo;
            return this;
        }

        public SystemStatusRecordImplBuilder withSystemMemoryUsed(Integer systemMemoryUsed) {
            this.systemMemoryUsed = systemMemoryUsed;
            return this;
        }

        public SystemStatusRecordImplBuilder withSystemMemoryTotal(Integer systemMemoryTotal) {
            this.systemMemoryTotal = systemMemoryTotal;
            return this;
        }

        public SystemStatusRecordImplBuilder withActiveSessionCount(Integer activeSessionCount) {
            this.activeSessionCount = activeSessionCount;
            return this;
        }

        public SystemStatusRecordImplBuilder withLoadAvg(Double loadAvg) {
            this.loadAvg = loadAvg;
            return this;
        }

        public SystemStatusRecordImplBuilder withFilesystemMemoryUsed(Integer filesystemMemoryUsed) {
            this.filesystemMemoryUsed = filesystemMemoryUsed;
            return this;
        }

        public SystemStatusRecordImplBuilder withFilesystemMemoryTotal(Integer filesystemMemoryTotal) {
            this.filesystemMemoryTotal = filesystemMemoryTotal;
            return this;
        }

        public SystemStatusRecordImplBuilder withWarnings(String warnings) {
            this.warnings = warnings;
            return this;
        }

        @Override
        public SystemStatusLogImpl build() {
            return new SystemStatusLogImpl(this);
        }

    }
}
