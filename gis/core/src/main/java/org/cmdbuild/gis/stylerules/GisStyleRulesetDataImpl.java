/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.stylerules;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_GisStyleRules")
public class GisStyleRulesetDataImpl implements GisStyleRulesetData {

    private final Long id;
    private final String description, code, function, rules, owner;
    private final long gisAttribute;
    private final GisStyleRulesetParams params;

    private GisStyleRulesetDataImpl(GisStyleRulesetDataImplBuilder builder) {
        this.id = builder.id;
        this.description = builder.description;
        this.code = checkNotBlank(builder.code);
        this.function = builder.function;
        this.rules = checkNotBlank(builder.rules);
        this.owner = checkNotBlank(builder.owner);
        this.gisAttribute = builder.gisAttribute;
        this.params = checkNotNull(builder.params);
    }

    @Nullable
    @Override
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Nullable
    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getCode() {
        return code;
    }

    @Override
    @CardAttr("Owner")
    public String getOwner() {
        return owner;
    }

    @Nullable
    @Override
    @CardAttr("Function")
    public String getFunction() {
        return function;
    }

    @Override
    @CardAttr("Rules")
    public String getRules() {
        return rules;
    }

    @Override
    @CardAttr("Attribute")
    public long getGisAttribute() {
        return gisAttribute;
    }

    @Override
    @CardAttr("Params")
    public GisStyleRulesetParams getParams() {
        return params;
    }

    public static GisStyleRulesetDataImplBuilder builder() {
        return new GisStyleRulesetDataImplBuilder();
    }

    public static GisStyleRulesetDataImplBuilder copyOf(GisStyleRulesetData source) {
        return new GisStyleRulesetDataImplBuilder()
                .withId(source.getId())
                .withDescription(source.getDescription())
                .withCode(source.getCode())
                .withFunction(source.getFunction())
                .withRules(source.getRules())
                .withOwner(source.getOwner())
                .withGisAttribute(source.getGisAttribute())
                .withParams(source.getParams());
    }

    public static class GisStyleRulesetDataImplBuilder implements Builder<GisStyleRulesetDataImpl, GisStyleRulesetDataImplBuilder> {

        private Long id;
        private String description;
        private String code;
        private String function;
        private String rules, owner;
        private long gisAttribute;
        private GisStyleRulesetParams params;

        public GisStyleRulesetDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public GisStyleRulesetDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public GisStyleRulesetDataImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public GisStyleRulesetDataImplBuilder withFunction(String function) {
            this.function = function;
            return this;
        }

        public GisStyleRulesetDataImplBuilder withRules(String rules) {
            this.rules = rules;
            return this;
        }

        public GisStyleRulesetDataImplBuilder withOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public GisStyleRulesetDataImplBuilder withGisAttribute(long gisAttribute) {
            this.gisAttribute = gisAttribute;
            return this;
        }

        public GisStyleRulesetDataImplBuilder withParams(GisStyleRulesetParams params) {
            this.params = params;
            return this;
        }

        @Override
        public GisStyleRulesetDataImpl build() {
            return new GisStyleRulesetDataImpl(this);
        }

    }
}
