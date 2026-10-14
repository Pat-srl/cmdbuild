package org.cmdbuild.common.beans;

import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import org.cmdbuild.utils.lang.ToPrimitive;

public interface IdAndDescription {

    @Nullable
    @ToPrimitive(primary = true)
    Long getId();

    @Nullable
    String getDescription();

    @Nullable
    @ToPrimitive
    String getCode();

    @Nullable
    default String getTypeName() {
        return null;
    }

    default boolean hasId() {
        return isNotNullAndGtZero(getId());
    }

    default boolean hasCode() {
        return isNotBlank(getCode());
    }

    default boolean hasType() {
        return isNotBlank(getTypeName());
    }

    default boolean hasType(String... oneof) {
        return set(oneof).contains(getTypeName());
    }

    default boolean hasIdOrCode() {
        return hasId() || hasCode();
    }

    default boolean hasCodeAndDescription() {
        return isNotBlank(getCode()) && isNotBlank(getDescription());
    }

}
