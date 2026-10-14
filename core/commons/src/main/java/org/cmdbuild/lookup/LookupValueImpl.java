package org.cmdbuild.lookup;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import static org.cmdbuild.lookup.DmsCategoryConfig.DMS_MODEL_CLASS;
import org.cmdbuild.lookup.LookupConfigImpl.LookupConfigImplBuilder;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class LookupValueImpl implements LookupValue {

    private final Long id;
    private final String code;
    private final String description;
    private final String notes;
    private final LookupType type;
    private final Integer number;
    private final boolean isActive;
    private final Long parentId;
    private final LookupConfig config;

    private LookupValueImpl(LookupBuilder builder) {
        this.id = ltEqZeroToNull(builder.id);
        this.code = checkNotBlank(builder.code);
        this.description = firstNotBlank(builder.description, code);
        this.notes = builder.notes;
        this.number = firstNotNull(builder.number, 0);
        this.isActive = firstNotNull(builder.isActive, true);
        this.type = checkNotNull(builder.type);
        this.parentId = ltEqZeroToNull(builder.parentId);
        this.config = new LookupConfigImpl(builder.config);
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
    public long getTypeId() {
        return type.getId();
    }

    @Override
    public String getTypeName() {
        return type.getName();
    }

    @Override
    public LookupType getType() {
        return type;
    }

    @Override
    public Integer getIndex() {
        return number;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    @Nullable
    public Long getParentId() {
        return parentId;
    }

    @Override
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
        return "LookupValueImpl{" + "id=" + id + ", code=" + code + ", type=" + type.getName() + '}';
    }

    public static LookupValue build(String type, String code) {
        return new LookupBuilder().withTypeName(type).withCode(code).build();
    }

    public static LookupValueImpl.LookupBuilder builder() {
        return new LookupBuilder();
    }

    public static LookupValueImpl.LookupBuilder copyOf(LookupValueData lookup) {
        return new LookupBuilder()
                .withCode(lookup.getCode())
                .withDescription(lookup.getDescription())
                .withId(lookup.getId())
                .withNotes(lookup.getNotes())
                .withIndex(lookup.getIndex())
                .withParentId(lookup.getParentId())
                .withActive(lookup.isActive())
                .withConfig(lookup.getConfig());
    }

    public static LookupValueImpl.LookupBuilder copyOf(LookupValue lookup) {
        return copyOf((LookupValueData) lookup)
                .withType(lookup.getType());
    }

    public static class LookupBuilder implements Builder<LookupValueImpl, LookupBuilder> {

        private LookupType type;
        private Long id;
        private String code;
        private String description;
        private String notes;
        private Integer number;
        private Boolean isActive;
        private Long parentId;
        private final Map<String, String> config = map();

        public LookupValueImpl.LookupBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public LookupValueImpl.LookupBuilder withConfig(LookupConfig config) {
            return this.withConfigAsMap(config.asMap());
        }

        public LookupValueImpl.LookupBuilder withConfig(Consumer<LookupConfigImplBuilder> builder) {
            return this.withConfig(LookupConfigImpl.builder().withConfig(config).accept(builder).build());
        }

        public LookupValueImpl.LookupBuilder withConfigAsMap(Map<String, String> config) {
            this.config.clear();
            this.config.putAll(nullToEmpty(config));
            return this;
        }

        public LookupValueImpl.LookupBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public LookupValueImpl.LookupBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public LookupBuilder withNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public LookupBuilder withType(LookupType value) {
            this.type = value;
            return this;
        }

        public LookupValueImpl.LookupBuilder withTypeName(String typeName) {
            this.type = isBlank(typeName) ? null : LookupTypeImpl.builder().withName(typeName).build();
            return this;
        }

        public LookupValueImpl.LookupBuilder withIndex(Integer value) {
            this.number = value;
            return this;
        }

        public LookupValueImpl.LookupBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public LookupValueImpl.LookupBuilder withParentId(Long parentId) {
            this.parentId = parentId;
            return this;
        }

        public LookupValueImpl.LookupBuilder withModelClass(String modelClass) {
            config.put(DMS_MODEL_CLASS, modelClass);
            return this;
        }

        public LookupValueImpl.LookupBuilder withParent(@Nullable LookupValue parent) {
            this.parentId = parent == null ? null : parent.getId();
            return this;
        }

        @Override
        public LookupValueImpl build() {
            return new LookupValueImpl(this);
        }

    }

}
