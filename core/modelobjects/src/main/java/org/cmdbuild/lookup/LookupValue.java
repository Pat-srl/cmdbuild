package org.cmdbuild.lookup;

import jakarta.annotation.Nullable;
import org.cmdbuild.lookup.LookupSpeciality;
import org.cmdbuild.lookup.LookupType;

public interface LookupValue extends LookupValueData {

    LookupType getType();

    default LookupSpeciality getSpeciality() {
        return getType().getSpeciality();
    }

    @Override
    default String getLookupType() {
        return getType().getName();
    }

    @Nullable
    default Long getParentTypeOrNull() {
        return getType().getParent();
    }
}
