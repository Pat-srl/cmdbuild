/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import java.util.Collection;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class WaterwayDescriptorMetaImpl implements WaterwayDescriptorMeta {

    private final boolean enabled;
    private final Set<String> disabledItems;
    private final Map<String, String> params;
    private final String code, description;

    private WaterwayDescriptorMetaImpl(WaterwayDescriptorMetaImplBuilder builder) {
        this.enabled = firstNotNull(builder.enabled, true);
        this.disabledItems = set(firstNotNull(builder.disabledItems, emptySet())).immutable();
        this.params = map(firstNotNull(builder.params, emptyMap())).immutable();
        this.code = builder.code;
        this.description = builder.description;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Set<String> getDisabledItems() {
        return disabledItems;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    @Nullable
    public String getCode() {
        return code;
    }

    @Override
    @Nullable
    public String getDescription() {
        return description;
    }

    public static WaterwayDescriptorMeta empty() {
        return builder().build();
    }

    public static WaterwayDescriptorMetaImplBuilder builder() {
        return new WaterwayDescriptorMetaImplBuilder();
    }

    public static WaterwayDescriptorMeta build(Map<String, String> params) {
        return builder().withParams(params).build();
    }

    public static WaterwayDescriptorMetaImplBuilder copyOf(WaterwayDescriptorMeta source) {
        return new WaterwayDescriptorMetaImplBuilder()
                .withEnabled(source.isEnabled())
                .withDisabledItems(source.getDisabledItems())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withParams(source.getParams());
    }

    public static class WaterwayDescriptorMetaImplBuilder implements Builder<WaterwayDescriptorMetaImpl, WaterwayDescriptorMetaImplBuilder> {

        private Boolean enabled;
        private Collection<String> disabledItems;
        private Map<String, String> params;
        private String code, description;

        public WaterwayDescriptorMetaImplBuilder withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public WaterwayDescriptorMetaImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public WaterwayDescriptorMetaImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public WaterwayDescriptorMetaImplBuilder withDisabledItems(Collection<String> disabledItems) {
            this.disabledItems = disabledItems;
            return this;
        }

        public WaterwayDescriptorMetaImplBuilder withParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        @Override
        public WaterwayDescriptorMetaImpl build() {
            return new WaterwayDescriptorMetaImpl(this);
        }

    }
}
