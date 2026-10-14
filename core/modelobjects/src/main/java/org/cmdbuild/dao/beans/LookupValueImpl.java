package org.cmdbuild.dao.beans;

import org.cmdbuild.common.beans.IdAndDescriptionImpl;
import org.cmdbuild.common.beans.LookupValue;
import com.google.common.base.MoreObjects;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

public class LookupValueImpl extends IdAndDescriptionImpl implements LookupValue {

    private final String lookupType;

    public LookupValueImpl(@Nullable Long id, @Nullable String description, @Nullable String lookupType) {
        super(id, description);
        this.lookupType = checkNotBlank(lookupType, "lookup type is null");
    }

    private LookupValueImpl(LookupValueBuilder builder) {
        super(builder.id, builder.description, builder.code);
        this.lookupType = checkNotBlank(builder.lookupType, "lookup type is null");
    }

    @Override
    @Nullable
    public String getLookupType() {
        return lookupType;
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper("LookupValue");
        if (hasId()) {
            helper.add("id", getId());
        }
        if (hasCode()) {
            helper.add("code", getCode());
        }
        if (!hasIdOrCode()) {
            helper.add("isNull", true);
        }
        return helper.toString();
    }

    public static LookupValueBuilder builder() {
        return new LookupValueBuilder();
    }

    public static LookupValueImpl fromCode(String lookupType, String code) {
        return builder().withLookupType(lookupType).withCode(checkNotBlank(code)).build();
    }

    public static LookupValueImpl fromId(String lookupType, long id) {
        return builder().withLookupType(lookupType).withId(checkNotNullAndGtZero(id)).build();
    }

    public static LookupValueBuilder copyOf(LookupValue source) {
        return new LookupValueBuilder()
                .withId(source.getId())
                .withDescription(source.getDescription())
                .withLookupType(source.getLookupType());
    }

    public static class LookupValueBuilder implements Builder<LookupValueImpl, LookupValueBuilder> {

        private Long id;
        private String description;
        private String code;
        private String lookupType;

        public LookupValueBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public LookupValueBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public LookupValueBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public LookupValueBuilder withLookupType(String lookupType) {
            this.lookupType = lookupType;
            return this;
        }

        @Override
        public LookupValueImpl build() {
            return new LookupValueImpl(this);
        }

    }
}
