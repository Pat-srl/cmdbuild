package org.cmdbuild.lookup;

import java.util.Map;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NOTES;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.lookup.DmsCategoryConfig.DMS_MODEL_CLASS;
import org.cmdbuild.lookup.LookupConfigImpl.LookupConfigImplBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.cmdbuild.utils.json.JsonBean;

@CardMapping("_LookupValue")
public class LookupValueDataImpl implements LookupValueData {

    private final Long id;
    private final long type;
    private final String code;
    private final String description;
    private final String notes;
    private final Integer number;
    private final boolean isActive;
    private final Long parentId;
    private final LookupConfig config;

    private LookupValueDataImpl(LookupBuilder builder) {
        this.id = ltEqZeroToNull(builder.id);
        this.code = checkNotBlank(builder.code);
        this.description = firstNotBlank(builder.description, code);
        this.notes = builder.notes;
        this.number = firstNotNull(builder.number, 0);
        this.isActive = firstNotNull(builder.isActive, true);
        this.type = builder.type;
        this.parentId = ltEqZeroToNull(builder.parentId);
        this.config = new LookupConfigImpl(builder.config);
    }

    @Override
    public String getLookupType() {
        throw new UnsupportedOperationException();//TODO improve this!!
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
    @CardAttr(ATTR_NOTES)
    public String getNotes() {
        return notes;
    }

    @CardAttr("Type")
    @Override
    public long getTypeId() {
        return type;
    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    @CardAttr("Index")
    public Integer getIndex() {
        return number;
    }

    @Override
    @CardAttr("Active")
    public boolean isActive() {
        return isActive;
    }

    @Override
    @CardAttr("ParentValue")
    @Nullable
    public Long getParentId() {
        return parentId;
    }

    @Override
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    public LookupConfig getConfig() {
        return config;
    }

    @JsonBean
    @CardAttr("Config")
    public Map<String, String> getConfigAsMap() {
        return config.asMap();
    }

    @Override
    public String toString() {
        return "LookupValueData{" + "id=" + id + ", code=" + code + ", type=" + type + '}';
    }

    public static LookupValueDataImpl.LookupBuilder builder() {
        return new LookupBuilder();
    }

    public static LookupValueDataImpl.LookupBuilder copyOf(LookupValueData lookup) {
        return new LookupBuilder()
                .withCode(lookup.getCode())
                .withDescription(lookup.getDescription())
                .withId(lookup.getId())
                .withNotes(lookup.getNotes())
                .withIndex(lookup.getIndex())
                .withParentId(lookup.getParentId())
                .withTypeId(lookup.getTypeId())
                .withActive(lookup.isActive())
                .withConfig(lookup.getConfig());
    }

    public static class LookupBuilder implements Builder<LookupValueDataImpl, LookupBuilder> {

        private Long id, type;
        private String code;
        private String description;
        private String notes;
        private Integer number;
        private Boolean isActive;
        private Long parentId;
        private final Map<String, String> config = map();

        public LookupValueDataImpl.LookupBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public LookupValueDataImpl.LookupBuilder withConfig(LookupConfig config) {
            return this.withConfigAsMap(config.asMap());
        }

        public LookupValueDataImpl.LookupBuilder withConfig(Consumer<LookupConfigImplBuilder> builder) {
            return this.withConfig(LookupConfigImpl.builder().withConfig(config).accept(builder).build());
        }

        public LookupValueDataImpl.LookupBuilder withConfigAsMap(Map<String, String> config) {
            this.config.clear();
            this.config.putAll(nullToEmpty(config));
            return this;
        }

        public LookupValueDataImpl.LookupBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public LookupValueDataImpl.LookupBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public LookupBuilder withNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public LookupBuilder withTypeId(Long value) {
            this.type = value;
            return this;
        }

        public LookupBuilder withType(LookupType type) {
            return this.withTypeId(type.getId());
        }

        public LookupValueDataImpl.LookupBuilder withIndex(Integer value) {
            this.number = value;
            return this;
        }

        public LookupValueDataImpl.LookupBuilder withActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public LookupValueDataImpl.LookupBuilder withParentId(Long parentId) {
            this.parentId = parentId;
            return this;
        }

        public LookupValueDataImpl.LookupBuilder withModelClass(String modelClass) {
            config.put(DMS_MODEL_CLASS, modelClass);
            return this;
        }

        public LookupValueDataImpl.LookupBuilder withParent(@Nullable LookupValue parent) {
            this.parentId = parent == null ? null : parent.getId();
            return this;
        }

        @Override
        public LookupValueDataImpl build() {
            return new LookupValueDataImpl(this);
        }

    }

}
