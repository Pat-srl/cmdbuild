/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import static org.cmdbuild.gis.stylerules.GisStyleRulesetAccessType.AT_PUBLIC;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class GisStyleRulesetParamsImpl implements GisStyleRulesetParams {

    private final GisStyleRulesetAnalysisType analysisType;
    private final Integer segments;
    private final String classAttribute, userOwner;
    private final GisStyleRulesetAccessType accessType;

    private GisStyleRulesetParamsImpl(GisStyleRulesetParamsImplBuilder builder) {
        this.analysisType = builder.analysisType;
        this.segments = builder.segments;
        this.classAttribute = builder.classAttribute;
        this.userOwner = builder.userOwner;
        this.accessType = firstNotNull(builder.accessType, AT_PUBLIC);
    }

    @JsonCreator
    public GisStyleRulesetParamsImpl(@Nullable @JsonProperty("analysisType") String analysisType, @Nullable @JsonProperty("classAttribute") String classAttribute, @Nullable @JsonProperty("segments") Integer segments, @Nullable @JsonProperty("userOwner") String userOwner, @Nullable @JsonProperty("accessType") String accessType) {
        this.analysisType = parseEnumOrNull(analysisType, GisStyleRulesetAnalysisType.class);
        this.segments = toIntegerOrNull(segments);
        this.classAttribute = classAttribute;
        this.userOwner = userOwner;
        this.accessType = parseEnumOrDefault(accessType, AT_PUBLIC);
    }

    @Nullable
    @Override
    public GisStyleRulesetAnalysisType getAnalysisType() {
        return analysisType;
    }

    @Nullable
    @Override
    public Integer getSegments() {
        return segments;
    }

    @Nullable
    @Override
    public String getClassAttribute() {
        return classAttribute;
    }

    @Nullable
    @Override
    public String getUserOwner() {
        return userOwner;
    }

    @Override
    public GisStyleRulesetAccessType getAccessType() {
        return accessType;
    }

    public static GisStyleRulesetParamsImplBuilder builder() {
        return new GisStyleRulesetParamsImplBuilder();
    }

    public static GisStyleRulesetParamsImplBuilder copyOf(GisStyleRulesetParams source) {
        return new GisStyleRulesetParamsImplBuilder()
                .withAnalysisType(source.getAnalysisType())
                .withSegments(source.getSegments())
                .withClassAttribute(source.getClassAttribute())
                .withUserOwner(source.getUserOwner())
                .withAccessType(source.getAccessType());
    }

    public static class GisStyleRulesetParamsImplBuilder implements Builder<GisStyleRulesetParamsImpl, GisStyleRulesetParamsImplBuilder> {

        private GisStyleRulesetAnalysisType analysisType;
        private Integer segments;
        private String classAttribute, userOwner;
        private GisStyleRulesetAccessType accessType;

        public GisStyleRulesetParamsImplBuilder withAnalysisType(GisStyleRulesetAnalysisType analysisType) {
            this.analysisType = analysisType;
            return this;
        }

        public GisStyleRulesetParamsImplBuilder withSegments(Integer segments) {
            this.segments = segments;
            return this;
        }

        public GisStyleRulesetParamsImplBuilder withClassAttribute(String classAttribute) {
            this.classAttribute = classAttribute;
            return this;
        }

        public GisStyleRulesetParamsImplBuilder withUserOwner(String userOwner) {
            this.userOwner = userOwner;
            return this;
        }

        public GisStyleRulesetParamsImplBuilder withAccessType(GisStyleRulesetAccessType accessType) {
            this.accessType = accessType;
            return this;
        }

        @Override
        public GisStyleRulesetParamsImpl build() {
            return new GisStyleRulesetParamsImpl(this);
        }

    }
}
