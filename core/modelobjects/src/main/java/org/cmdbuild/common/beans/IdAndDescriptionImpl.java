package org.cmdbuild.common.beans;

import jakarta.annotation.Nullable;

public class IdAndDescriptionImpl implements IdAndDescription {

    private final Long id;
    private final String description, code, typeName;

    public IdAndDescriptionImpl(@Nullable Long id, @Nullable String description) {
        this(id, description, null);
    }

    public IdAndDescriptionImpl(@Nullable Long id, @Nullable String description, @Nullable String code) {
        this(null, id, description, code);
    }

    public IdAndDescriptionImpl(@Nullable String typeName, @Nullable Long id, @Nullable String description, @Nullable String code) {
        this.id = id;
        this.description = description;
        this.code = code;
        this.typeName = typeName;
    }

    @Nullable
    @Override
    public String getTypeName() {
        return typeName;
    }

    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    @Nullable
    @Override
    public String getDescription() {
        return description;
    }

    @Nullable
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "IdAndDescription{" + "id=" + id + ", description=" + description + '}';
    }

    public static IdAndDescription copyOf(IdAndDescription source) {
        return new IdAndDescriptionImpl(source.getId(), source.getDescription(), source.getCode());
    }
}
