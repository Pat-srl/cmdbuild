/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.javascript.jscomp.jarjar.com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import net.sf.jasperreports.engine.JasperReport;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.utils.lang.Builder;

public class ReportDataExtImpl implements ReportDataExt {

    private final ReportData inner;
    private final List<JasperReport> jasperReports;

    private ReportDataExtImpl(ReportDataExtImplBuilder builder) {
        this.inner = checkNotNull(builder.inner);
        this.jasperReports = ImmutableList.copyOf(builder.jasperReports);
        checkArgument(inner.hasCompiledReports() && inner.hasSourceReports());
        checkArgument(!jasperReports.isEmpty() && jasperReports.size() == inner.getCompiledReports().size() && jasperReports.size() == inner.getSourceReports().size());
    }

    @Override
    @Nullable
    public Long getId() {
        return inner.getId();
    }

    @Override
    public String getCode() {
        return inner.getCode();
    }

    @Override
    public String getDescription() {
        return inner.getDescription();
    }

    @Override
    @Nullable
    public String getQuery() {
        return inner.getQuery();
    }

    @Override
    public List<String> getSourceReports() {
        return inner.getSourceReports();
    }

    @Override
    public List<byte[]> getCompiledReports() {
        return inner.getCompiledReports();
    }

    @Override
    public List<byte[]> getImages() {
        return inner.getImages();
    }

    @Override
    public List<String> getImageNames() {
        return inner.getImageNames();
    }

    @Override
    public boolean isActive() {
        return inner.isActive();
    }

    @Override
    public Map<String, String> getConfig() {
        return inner.getConfig();
    }

    @Override
    public List<JasperReport> getJasperReports() {
        return jasperReports;
    }

    @Override
    public String getIconFont() {
        return inner.getIconFont();
    }

    @Override
    public String toString() {
        return "ReportDataExt{" + "id=" + getId() + ", code=" + getCode() + '}';
    }

    public static ReportDataExtImplBuilder builder() {
        return new ReportDataExtImplBuilder();
    }

    public static ReportDataExtImplBuilder copyOf(ReportDataExt source) {
        return new ReportDataExtImplBuilder()
                .withInner(source)
                .withJasperReports(source.getJasperReports());
    }

    public static class ReportDataExtImplBuilder implements Builder<ReportDataExtImpl, ReportDataExtImplBuilder> {

        private ReportData inner;
        private List<JasperReport> jasperReports;

        public ReportDataExtImplBuilder withInner(ReportData inner) {
            this.inner = inner;
            return this;
        }

        public ReportDataExtImplBuilder withJasperReports(List<JasperReport> jasperReports) {
            this.jasperReports = jasperReports;
            return this;
        }

        @Override
        public ReportDataExtImpl build() {
            return new ReportDataExtImpl(this);
        }

    }
}
