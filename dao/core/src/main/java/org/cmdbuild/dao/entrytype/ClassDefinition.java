package org.cmdbuild.dao.entrytype;

import jakarta.annotation.Nullable;

public interface ClassDefinition {

    @Nullable
    Long getOid();

    String getName();

    @Nullable
    String getParentOrNull();

    ClassMetadata getMetadata();

    default boolean hasParent() {
        return getParentOrNull() != null;
    }
}
