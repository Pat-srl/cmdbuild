/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class WaterwayItemInfoImpl implements WaterwayItemInfo {

    private final String configFileKey, code, description, notes, subtype;
    private final WaterwayItemType type;
    private final boolean enabled;

    private WaterwayItemInfoImpl(WaterwayItemInfoImplBuilder builder) {
        this.configFileKey = checkNotBlank(builder.configFileKey);
        this.code = checkNotBlank(builder.code);
        this.description = nullToEmpty(builder.description);
        this.notes = nullToEmpty(builder.notes);
        this.subtype = emptyToNull(builder.subtype);
        this.type = checkNotNull(builder.type);
        this.enabled = builder.enabled;
    }

    @Override
    public String getDescriptorKey() {
        return configFileKey;
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
    @Nullable
    public String getSubtype() {
        return subtype;
    }

    @Override
    public WaterwayItemType getType() {
        return type;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "WaterwayItemInfo{" + "code=" + code + ", type=" + type + '}';
    }

    public static WaterwayItemInfoImplBuilder builder() {
        return new WaterwayItemInfoImplBuilder();
    }

    public static WaterwayItemInfoImplBuilder copyOf(WaterwayItemInfo source) {
        return new WaterwayItemInfoImplBuilder()
                .withDescriptorKey(source.getDescriptorKey())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withNotes(source.getNotes())
                .withSubtype(source.getSubtype())
                .withType(source.getType())
                .withEnabled(source.isEnabled());
    }

    public static class WaterwayItemInfoImplBuilder implements Builder<WaterwayItemInfoImpl, WaterwayItemInfoImplBuilder> {

        private String configFileKey;
        private String code;
        private String description;
        private String notes;
        private String subtype;
        private WaterwayItemType type;
        private Boolean enabled;

        public WaterwayItemInfoImplBuilder withDescriptorKey(String configFileKey) {
            this.configFileKey = configFileKey;
            return this;
        }

        public WaterwayItemInfoImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public WaterwayItemInfoImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public WaterwayItemInfoImplBuilder withNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public WaterwayItemInfoImplBuilder withSubtype(String subtype) {
            this.subtype = subtype;
            return this;
        }

        public WaterwayItemInfoImplBuilder withType(WaterwayItemType type) {
            this.type = type;
            return this;
        }

        public WaterwayItemInfoImplBuilder withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        @Override
        public WaterwayItemInfoImpl build() {
            return new WaterwayItemInfoImpl(this);
        }

    }
}
