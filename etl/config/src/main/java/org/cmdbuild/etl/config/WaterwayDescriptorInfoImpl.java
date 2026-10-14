/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import static com.google.common.base.Strings.nullToEmpty;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

public class WaterwayDescriptorInfoImpl implements WaterwayDescriptorInfoExt {

    private final String code, description, notes, tag;
    private final int version;
    private final boolean enabled, valid;
    private final Set<String> disabledItems;
    private final Map<String, String> params;

    private WaterwayDescriptorInfoImpl(WaterwayConfigFileInfoImplBuilder builder) {
        this.code = checkNotBlank(builder.code);
        this.description = nullToEmpty(builder.description);
        this.notes = nullToEmpty(builder.notes);
        this.tag = nullToEmpty(builder.tag);
        this.version = checkNotNullAndGtZero(builder.version);
        this.enabled = firstNotNull(builder.enabled, true);
        this.valid = toBooleanOrDefault(builder.valid, false);
        this.disabledItems = set(firstNotNull(builder.disabledItems, emptySet())).immutable();
        this.params = map(firstNotNull(builder.params, emptyMap())).immutable();
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
    public int getVersion() {
        return version;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isValid() {
        return valid;
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
    public String toString() {
        return "WaterwayConfigFileInfo{" + "code=" + code + ", version=" + version + '}';
    }

    public static WaterwayConfigFileInfoImplBuilder builder() {
        return new WaterwayConfigFileInfoImplBuilder();
    }

    public static WaterwayConfigFileInfoImplBuilder copyOf(WaterwayDescriptorInfoExt source) {
        return new WaterwayConfigFileInfoImplBuilder()
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withNotes(source.getNotes())
                .withTag(source.getTag())
                .withEnabled(source.isEnabled())
                .withValid(source.isValid())
                .withVersion(source.getVersion())
                .withDisabledItems(source.getDisabledItems())
                .withParams(source.getParams());
    }

    public static class WaterwayConfigFileInfoImplBuilder implements Builder<WaterwayDescriptorInfoImpl, WaterwayConfigFileInfoImplBuilder> {

        private String code;
        private String description;
        private String notes;
        private String tag;
        private Integer version;
        private Boolean enabled, valid;
        private Collection<String> disabledItems;
        private Map<String, String> params;

        public WaterwayConfigFileInfoImplBuilder withParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public WaterwayConfigFileInfoImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public WaterwayConfigFileInfoImplBuilder withTag(String tag) {
            this.tag = tag;
            return this;
        }

        public WaterwayConfigFileInfoImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public WaterwayConfigFileInfoImplBuilder withNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public WaterwayConfigFileInfoImplBuilder withVersion(Integer version) {
            this.version = version;
            return this;
        }

        public WaterwayConfigFileInfoImplBuilder withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public WaterwayConfigFileInfoImplBuilder withValid(Boolean valid) {
            this.valid = valid;
            return this;
        }

        public WaterwayConfigFileInfoImplBuilder withDisabledItems(Collection<String> disabledItems) {
            this.disabledItems = disabledItems;
            return this;
        }

        @Override
        public WaterwayDescriptorInfoImpl build() {
            return new WaterwayDescriptorInfoImpl(this);
        }

    }
}
