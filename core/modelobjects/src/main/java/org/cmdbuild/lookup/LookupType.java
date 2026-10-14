package org.cmdbuild.lookup;

import static com.google.common.base.Objects.equal;
import jakarta.annotation.Nullable;
import static org.cmdbuild.lookup.LookupAccessType.LT_DEFAULT;
import static org.cmdbuild.lookup.LookupAccessType.LT_PROTECTED;
import static org.cmdbuild.lookup.LookupAccessType.LT_SYSTEM;
import static org.cmdbuild.lookup.LookupSpeciality.LS_DEFAULT;
import static org.cmdbuild.lookup.LookupSpeciality.LS_DMSCATEGORY;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

public interface LookupType {

    @Nullable
    Long getId();

    String getName();

    @Nullable
    Long getParent();

    LookupSpeciality getSpeciality();

    LookupAccessType getAccessType();

    default boolean isSystem() {
        return equal(getAccessType(), LT_SYSTEM);
    }

    default boolean isProtected() {
        return equal(getAccessType(), LT_PROTECTED);
    }

    default boolean isAccessDefault() {
        return equal(getAccessType(), LT_DEFAULT);
    }

    default boolean isDefaultSpeciality() {
        return equal(getSpeciality(), LS_DEFAULT);
    }

    default boolean isDmsCategorySpeciality() {
        return equal(getSpeciality(), LS_DMSCATEGORY);
    }

    default boolean hasParent() {
        return isNotNullAndGtZero(getParent());
    }

    default long getParentNotNull() {
        return checkNotNullAndGtZero(getParent());
    }

}
