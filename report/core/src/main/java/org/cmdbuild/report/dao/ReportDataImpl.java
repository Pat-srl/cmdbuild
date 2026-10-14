package org.cmdbuild.report.dao;

import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.report.ReportData;
import java.util.List;

import com.google.common.collect.ImmutableList;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

@CardMapping("_Report")
public class ReportDataImpl implements ReportData {

    private final Long id;
    private final String code;
    private final String description;
    private final boolean isActive;
    private final String query;
    private final List<byte[]> images, binaries;
    private final List<String> imageNames, reports;
    private final Map<String, String> config;
    private final String iconFont;

    private ReportDataImpl(ReportDataImplBuilder builder) {
        this.id = builder.id;
        this.code = checkNotBlank(builder.code, "report code cannot be null");
        this.description = firstNotBlank(builder.description, code);
        this.query = builder.query;
        this.binaries = ImmutableList.copyOf(nullToEmpty(builder.binaries));
        this.images = ImmutableList.copyOf(nullToEmpty(builder.images));
        this.imageNames = ImmutableList.copyOf(nullToEmpty(builder.imageNames));
        this.reports = ImmutableList.copyOf(nullToEmpty(builder.reports));
        this.isActive = firstNotNull(builder.isActive, true);
        this.config = map(builder.config).immutable();
        this.iconFont = builder.iconFont;

        checkArgument(imageNames.size() == images.size(), "images data and names mismatch");
        checkArgument(!binaries.isEmpty() || !reports.isEmpty(), "must have either compiled reports or text reports");
        checkArgument(binaries.isEmpty() || reports.isEmpty() || binaries.size() == reports.size());
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getCode() {
        return code;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr("Query")
    public String getQuery() {
        return query;
    }

    @Override
    @CardAttr("Sources")
    public List<String> getSourceReports() {
        return reports;
    }

    @Override
    @CardAttr("Binaries")
    public List<byte[]> getCompiledReports() {
        return binaries;
    }

    @Override
    @CardAttr("Images")
    public List<byte[]> getImages() {
        return images;
    }

    @Override
    @CardAttr("ImageNames")
    public List<String> getImageNames() {
        return imageNames;
    }

    @Override
    @CardAttr("Active")
    public boolean isActive() {
        return isActive;
    }

    @Override
    @CardAttr
    @JsonBean
    public Map<String, String> getConfig() {
        return config;
    }

    @Nullable
    @Override
    @CardAttr("IconFont")
    public String getIconFont() {
        return iconFont;
    }

    @Override
    public String toString() {
        return "ReportData{" + "id=" + id + ", code=" + code + '}';
    }

    public static ReportDataImplBuilder builder() {
        return new ReportDataImplBuilder();
    }

    public static ReportDataImplBuilder copyOf(ReportInfo source) {
        return new ReportDataImplBuilder().withInfo(source);
    }

    public static ReportDataImplBuilder copyOf(ReportData source) {
        return new ReportDataImplBuilder().withInfo(source)
                .withQuery(source.getQuery())
                .withCompiledReports(source.getCompiledReports())
                .withSourceReports(source.getSourceReports())
                .withImages(source.getImages())
                .withImageNames(source.getImageNames())
                .withConfig(source.getConfig())
                .withIconFont(source.getIconFont());
    }

    public static class ReportDataImplBuilder implements Builder<ReportDataImpl, ReportDataImplBuilder> {

        private Long id;
        private String code;
        private String description;
        private String query;
        private List<byte[]> binaries, images;
        private List<String> imageNames, reports;
        private Boolean isActive;
        private final Map<String, String> config = map();
        private String iconFont;

        public ReportDataImplBuilder withInfo(ReportInfo info) {
            return this
                    .withId(info.getId())
                    .withCode(info.getCode())
                    .withDescription(info.getDescription())
                    .withActive(info.isActive())
                    .withConfig(info.getConfig())
                    .withIconFont(info.getIconFont());
        }

        public ReportDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ReportDataImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public ReportDataImplBuilder withConfig(String key, String value) {
            config.put(key, value);
            return this;
        }

        public ReportDataImplBuilder withCustomClasspath(String classpath) {
            return this.withConfig(REPORT_CONFIG_CLASSPATH, classpath);
        }

        public ReportDataImplBuilder withConfig(Map<String, String> config) {
            this.config.putAll(config);
            return this;
        }

        public ReportDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ReportDataImplBuilder withQuery(String query) {
            this.query = query;
            return this;
        }

        public ReportDataImplBuilder withCompiledReports(List<byte[]> compiledReports) {
            this.binaries = compiledReports;
            return this;
        }

        public ReportDataImplBuilder withImages(List<byte[]> images) {
            this.images = images;
            return this;
        }

        public ReportDataImplBuilder withImageNames(List<String> imagesName) {
            this.imageNames = imagesName;
            return this;
        }

        public ReportDataImplBuilder withSourceReports(List<String> reports) {
            this.reports = reports;
            return this;
        }

        public ReportDataImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public ReportDataImplBuilder withIconFont(String iconFont) {
            this.iconFont = iconFont;
            return this;
        }

        @Override
        public ReportDataImpl build() {
            return new ReportDataImpl(this);
        }

    }
}
