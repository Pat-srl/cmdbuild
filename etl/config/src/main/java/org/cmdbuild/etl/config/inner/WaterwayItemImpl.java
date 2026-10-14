/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config.inner;

import org.cmdbuild.etl.config.WaterwayItemType;
import org.cmdbuild.etl.config.WaterwayItem;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.checkIsValidItemCode;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class WaterwayItemImpl implements WaterwayItem {

    private final String code, description, notes, configFileKey, subtype;
    private final WaterwayItemType type;
    private final Map<String, String> config;
    private final List<String> items;
    private final boolean enabled;

    private WaterwayItemImpl(WaterwayItemImplBuilder builder) {
        this.code = checkIsValidItemCode(builder.code);
        this.description = nullToEmpty(builder.description);
        this.notes = nullToEmpty(builder.notes);
        this.configFileKey = checkNotBlank(builder.configFileKey);
        this.type = checkNotNull(builder.type);
        this.subtype = emptyToNull(builder.subtype);
        this.enabled = firstNotNull(builder.enabled, true);
        this.config = map(builder.config).immutable();
        this.items = ImmutableList.copyOf(firstNotNull(builder.items, emptyList()));
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getNotes() {
        return notes;
    }

    @Override
    public String getDescriptorKey() {
        return configFileKey;
    }

    @Override
    public WaterwayItemType getType() {
        return type;
    }

    @Override
    @Nullable
    public String getSubtype() {
        return subtype;
    }

    @Override
    public Map<String, String> getConfig() {
        return config;
    }

    @Override
    public List<String> getItems() {
        return items;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "WaterwayItemImpl{" + "code=" + code + ", config=" + configFileKey + ", type=" + type + '}';
    }

    public static WaterwayItemImplBuilder builder() {
        return new WaterwayItemImplBuilder();
    }

    public static WaterwayItemImplBuilder copyOf(WaterwayItem source) {
        return new WaterwayItemImplBuilder()
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withNotes(source.getNotes())
                .withDescriptorKey(source.getDescriptorKey())
                .withType(source.getType())
                .withSubtype(source.getSubtype())
                .withConfig(source.getConfig())
                .withItems(source.getItems())
                .withEnabled(source.isEnabled());
    }

    public static class WaterwayItemImplBuilder implements Builder<WaterwayItemImpl, WaterwayItemImplBuilder> {

        private String code;
        private String description;
        private String notes, subtype;
        private String configFileKey;
        private WaterwayItemType type;
        private final Map<String, String> config = map();
        private List<String> items;
        private Boolean enabled;

        public WaterwayItemImplBuilder withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public WaterwayItemImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public WaterwayItemImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public WaterwayItemImplBuilder withNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public WaterwayItemImplBuilder withDescriptorKey(String configFileKey) {
            this.configFileKey = configFileKey;
            return this;
        }

        public WaterwayItemImplBuilder withType(WaterwayItemType type) {
            this.type = type;
            return this;
        }

        public WaterwayItemImplBuilder withSubtype(String subtype) {
            this.subtype = subtype;
            return this;
        }

        public WaterwayItemImplBuilder withConfig(Map<String, String> config) {
            this.config.clear();
            this.config.putAll(config);
            return this;
        }

        public WaterwayItemImplBuilder withItems(List<String> items) {
            this.items = items;
            return this;
        }

        @Override
        public WaterwayItemImpl build() {
            return new WaterwayItemImpl(this);
        }

    }
}
