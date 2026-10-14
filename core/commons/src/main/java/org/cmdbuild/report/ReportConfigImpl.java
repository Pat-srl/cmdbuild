/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;

import static java.util.Collections.emptyMap;
import org.cmdbuild.report.ReportConfigImpl.ReportConfigImplBuilder;
import static org.cmdbuild.report.ReportFormat.PDF;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmInlineUtils.flattenMaps;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

@JsonDeserialize(builder = ReportConfigImplBuilder.class)
public class ReportConfigImpl implements ReportConfig {

    private final String code;
    private final ReportFormat format;
    private final Map<String, Object> params;

    private ReportConfigImpl(ReportConfigImplBuilder builder) {
        this.code = checkNotBlank(builder.code);
        this.format = firstNotNull(builder.format, PDF);
        this.params = map(firstNotNull(builder.params, emptyMap())).immutable();
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public ReportFormat getFormat() {
        return format;
    }

    @Override
    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "ReportConfig{" + "code=" + code + ", format=" + format + '}';
    }

    public static ReportConfigImplBuilder builder() {
        return new ReportConfigImplBuilder();
    }

    public static ReportConfigImplBuilder copyOf(ReportConfig source) {
        return new ReportConfigImplBuilder()
                .withCode(source.getCode())
                .withFormat(source.getFormat())
                .withParams(map(source.getParams()));
    }

    public static ReportConfigImpl fromConfig(Map<String, ?> config) {
        return builder()
                .withCode(toStringNotBlank(config.get("code")))
                .withFormat(parseEnumOrNull(toStringOrNull(config.get("format")), ReportFormat.class))
                .withParams((Map) unflattenMap(config, "params"))
                .build();
    }

    public static ReportConfigImpl fromCode(String code) {
        return builder().withCode(code).build();
    }

    public static Map<String, String> toConfig(ReportConfig config) {
        return flattenMaps(map(
                "code", config.getCode(),
                "format", serializeEnum(config.getFormat()),
                "params", config.getParams()
        ));
    }

    public static class ReportConfigImplBuilder implements Builder<ReportConfigImpl, ReportConfigImplBuilder> {

        private String code;
        private ReportFormat format;
        private Map<String, Object> params;

        public ReportConfigImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public ReportConfigImplBuilder withFormat(ReportFormat format) {
            this.format = format;
            return this;
        }

        public ReportConfigImplBuilder withParams(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        @Override
        public ReportConfigImpl build() {
            return new ReportConfigImpl(this);
        }

    }
}
