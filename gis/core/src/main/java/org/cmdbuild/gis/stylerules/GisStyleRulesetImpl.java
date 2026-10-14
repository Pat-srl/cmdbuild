/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class GisStyleRulesetImpl implements GisStyleRuleset {

    private final Long id;
    private final String code, description, function;
    private final GisAttribute gisAttribute;
    private final List<Pair<CmdbFilter, Map<String, Object>>> rules;
    private final GisStyleRulesetParams params;

    private GisStyleRulesetImpl(GisStyleRulesetImplBuilder builder) {
        this.id = builder.id;
        this.code = checkNotBlank(builder.code);
        this.description = builder.description;
        this.function = builder.function;
        this.gisAttribute = checkNotNull(builder.gisAttribute);
        this.rules = ImmutableList.copyOf(builder.rules);
        this.params = firstNotNull(builder.params, GisStyleRulesetParamsImpl.builder().build());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    @Nullable
    public String getDescription() {
        return description;
    }

    @Override
    @Nullable
    public String getFunction() {
        return function;
    }

    @Override
    public GisAttribute getGisAttribute() {
        return gisAttribute;
    }

    @Override
    public List<Pair<CmdbFilter, Map<String, Object>>> getRules() {
        return rules;
    }

    @Override
    public GisStyleRulesetParams getParams() {
        return params;
    }

    public static GisStyleRulesetImplBuilder builder() {
        return new GisStyleRulesetImplBuilder();
    }

    public static GisStyleRulesetImplBuilder copyOf(GisStyleRuleset source) {
        return new GisStyleRulesetImplBuilder()
                .withId(source.getId())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withFunction(source.getFunction())
                .withGisAttribute(source.getGisAttribute())
                .withRules(source.getRules())
                .withParams(source.getParams());
    }

    public static class GisStyleRulesetImplBuilder implements Builder<GisStyleRulesetImpl, GisStyleRulesetImplBuilder> {

        private Long id;
        private String code;
        private String description;
        private String function;
        private GisAttribute gisAttribute;
        private List<Pair<CmdbFilter, Map<String, Object>>> rules;
        private GisStyleRulesetParams params;

        public GisStyleRulesetImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public GisStyleRulesetImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public GisStyleRulesetImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public GisStyleRulesetImplBuilder withFunction(String function) {
            this.function = function;
            return this;
        }

        public GisStyleRulesetImplBuilder withGisAttribute(GisAttribute gisAttribute) {
            this.gisAttribute = gisAttribute;
            return this;
        }

        public GisStyleRulesetImplBuilder withRules(List<Pair<CmdbFilter, Map<String, Object>>> rules) {
            this.rules = rules;
            return this;
        }

        public GisStyleRulesetImplBuilder withParams(GisStyleRulesetParams params) {
            this.params = params;
            return this;
        }

        public GisStyleRulesetImplBuilder withParams(Consumer<GisStyleRulesetParamsImpl.GisStyleRulesetParamsImplBuilder> visitor) {
            this.params = (params == null ? GisStyleRulesetParamsImpl.builder() : GisStyleRulesetParamsImpl.copyOf(params)).accept(visitor).build();
            return this;
        }

        @Override
        public GisStyleRulesetImpl build() {
            return new GisStyleRulesetImpl(this);
        }

    }
}
