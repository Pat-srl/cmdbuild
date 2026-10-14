/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config.inner;

import static com.google.common.base.Strings.nullToEmpty;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NOTES;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromYaml;
import static org.cmdbuild.utils.json.CmJsonUtils.toPrettyJson;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;

@CardMapping("_EtlConfig")
public class WaterwayDescriptorRecordImpl implements WaterwayDescriptorRecord {

    private final Long id;
    private final String code, description, notes, data, tag, config;
    private final int version;
    private final boolean enabled, valid;
    private final Set<String> disabledItems;
    private final Map<String, String> params;

    private WaterwayDescriptorRecordImpl(WaterwayDescriptorRecordImplBuilder builder) {
        this.id = builder.id;
        this.version = checkNotNullAndGtZero(firstNotNull(builder.version, 1));
        this.code = checkNotBlank(builder.code);
        this.description = nullToEmpty(builder.description);
        this.notes = nullToEmpty(builder.notes);
        this.disabledItems = set(firstNotNull(builder.disabledItems, emptySet())).immutable();
        this.data = checkNotBlank(builder.data);
        Map<String, Object> map = fromYaml(data, MAP_OF_OBJECTS);
        this.config = toPrettyJson(map);
        this.tag = toStringOrEmpty(map.get("tag"));//TODO improve this
        this.enabled = toBooleanOrDefault(builder.enabled, true);
        this.valid = toBooleanOrDefault(builder.valid, false);
        this.params = map(firstNotNull(builder.params, emptyMap())).immutable();
    }

    @CardAttr(ATTR_ID)
    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    @CardAttr(ATTR_CODE)
    @Override
    public String getCode() {
        return code;
    }

    @CardAttr(ATTR_DESCRIPTION)
    @Override
    public String getDescription() {
        return description;
    }

    @CardAttr(ATTR_NOTES)
    @Override
    public String getNotes() {
        return notes;
    }

    @CardAttr
    @Override
    public String getData() {
        return data;
    }

    @CardAttr(readFromDb = false)
    public String getConfig() {
        return config;
    }

    @CardAttr
    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    @CardAttr
    public boolean isEnabled() {
        return enabled;
    }

    @CardAttr("Disabled")
    @Override
    public Set<String> getDisabledItems() {
        return disabledItems;
    }

    @CardAttr
    @JsonBean
    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        return "WaterwayDescriptorRecord{" + "id=" + id + ", code=" + code + '}';
    }

    public static WaterwayDescriptorRecordImplBuilder builder() {
        return new WaterwayDescriptorRecordImplBuilder();
    }

    public static WaterwayDescriptorRecordImplBuilder copyOf(WaterwayDescriptorRecord source) {
        return new WaterwayDescriptorRecordImplBuilder()
                .withId(source.getId())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withNotes(source.getNotes())
                .withVersion(source.getVersion())
                .withEnabled(source.isEnabled())
                .withValid(source.isValid())
                .withData(source.getData())
                .withDisabledItems(source.getDisabledItems())
                .withParams(source.getParams());
    }

    public static class WaterwayDescriptorRecordImplBuilder implements Builder<WaterwayDescriptorRecordImpl, WaterwayDescriptorRecordImplBuilder> {

        private Long id;
        private String code;
        private String description;
        private String notes;
        private String data;
        private Integer version;
        private Boolean enabled, valid;
        private Collection<String> disabledItems;
        private Map<String, String> params;

        public WaterwayDescriptorRecordImplBuilder withParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public WaterwayDescriptorRecordImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public WaterwayDescriptorRecordImplBuilder withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public WaterwayDescriptorRecordImplBuilder withValid(Boolean valid) {
            this.valid = valid;
            return this;
        }

        public WaterwayDescriptorRecordImplBuilder withDisabledItems(Collection<String> disabledItems) {
            this.disabledItems = disabledItems;
            return this;
        }

        public WaterwayDescriptorRecordImplBuilder withVersion(Integer version) {
            this.version = version;
            return this;
        }

        public WaterwayDescriptorRecordImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public WaterwayDescriptorRecordImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public WaterwayDescriptorRecordImplBuilder withNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public WaterwayDescriptorRecordImplBuilder withData(String data) {
            this.data = data;
            return this;
        }

        @Override
        public WaterwayDescriptorRecordImpl build() {
            return new WaterwayDescriptorRecordImpl(this);
        }

    }
}
