package org.cmdbuild.cardfilter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static org.cmdbuild.cardfilter.CardFilterConst.FILTER;
import static org.cmdbuild.cardfilter.CardFilterConst.OWNER_NAME;
import static org.cmdbuild.cardfilter.CardFilterConst.SHARED;
import static org.cmdbuild.cardfilter.CardFilterConst.USER_ID;
import static org.cmdbuild.cardfilter.StoredFilterImpl.FILTER_CLASS_NAME;
import org.cmdbuild.cardfilter.StoredFilterImpl.StoredFilterImplBuilder;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping(FILTER_CLASS_NAME)
@JsonDeserialize(builder = StoredFilterImplBuilder.class)
public class StoredFilterImpl implements StoredFilter {

    public final static String FILTER_CLASS_NAME = "_Filter";

    private final Long id;
    private final String name;
    private final String description;
    private final String configuration;
    private final String ownerName;
    private final boolean shared;
    private final boolean active;
    private final Long userId;
    private final StoredFilterOwnerType ownerType;

    private StoredFilterImpl(StoredFilterImplBuilder builder) {
        this.id = builder.id;
        this.name = checkNotBlank(builder.name, "filter name is null");
        this.description = nullToEmpty(builder.description);
        this.configuration = checkNotBlank(builder.configuration, "filter configuration is null");
        this.ownerName = checkNotBlank(builder.ownerName, "filter class is null");
        this.shared = builder.shared;
        if (shared) {
            this.userId = null;
            this.active = builder.active;
        } else {
            this.userId = checkNotNull(builder.userId, "shared filters must have a owner user id");
            this.active = true;
        }
        ownerType = firstNotNull(builder.ownerType, StoredFilterOwnerType.SFO_CLASS);
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getName() {
        return name;
    }

    @Override
    @CardAttr("OwnerType")
    public StoredFilterOwnerType getOwnerType() {
        return ownerType;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr(FILTER)
    public String getConfiguration() {
        return configuration;
    }

    @Override
    @CardAttr(OWNER_NAME)
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    @CardAttr("Active")
    public boolean isActive() {
        return active;
    }

    @Override
    @CardAttr(SHARED)
    public boolean isShared() {
        return shared;
    }

    @Override
    @CardAttr(USER_ID)
    public Long getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "StoredFilter{" + "id=" + id + '}';
    }

    public static StoredFilterImplBuilder builder() {
        return new StoredFilterImplBuilder();
    }

    public static StoredFilterImplBuilder copyOf(StoredFilter source) {
        return new StoredFilterImplBuilder()
                .withId(source.getId())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withConfiguration(source.getConfiguration())
                .withOwnerName(source.getOwnerName())
                .withShared(source.isShared())
                .withActive(source.isActive())
                .withUserId(source.getUserId())
                .withOwnerType(source.getOwnerType());
    }

    public static class StoredFilterImplBuilder implements Builder<StoredFilterImpl, StoredFilterImplBuilder> {

        private Long id;
        private String name;
        private String description;
        private String configuration;
        private String ownerName;
        private Boolean shared;
        private Boolean active;
        private Long userId;
        private StoredFilterOwnerType ownerType;

        public StoredFilterImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public StoredFilterImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public StoredFilterImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public StoredFilterImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public StoredFilterImplBuilder withConfiguration(String configuration) {
            this.configuration = configuration;
            return this;
        }

        public StoredFilterImplBuilder withOwnerName(String ownerName) {
            this.ownerName = ownerName;
            return this;
        }

        public StoredFilterImplBuilder withOwnerType(StoredFilterOwnerType ownerType) {
            this.ownerType = ownerType;
            return this;
        }

        public StoredFilterImplBuilder withShared(Boolean shared) {
            this.shared = shared;
            return this;
        }

        public StoredFilterImplBuilder withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        @Override
        public StoredFilterImpl build() {
            return new StoredFilterImpl(this);
        }

    }
}
