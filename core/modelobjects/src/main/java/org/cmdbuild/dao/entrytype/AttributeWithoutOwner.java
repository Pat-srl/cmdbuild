package org.cmdbuild.dao.entrytype;

import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.utils.VirtualAttributeUtils;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public interface AttributeWithoutOwner extends AttributePermissions {

    CardAttributeType<?> getType();

    String getName();

    AttributeMetadata getMetadata();

    default String getDescription() {
        return firstNotBlank(getMetadata().getDescription(), getName());
    }

    default boolean isInherited() {
        return getMetadata().isInherited();
    }

    default boolean isActive() {
        return getMetadata().isActive();
    }

    default boolean showInGrid() {
        return getMetadata().showInGrid();
    }

    default boolean showInReducedGrid() {
        return getMetadata().showInReducedGrid();
    }

    default boolean isMandatory() {
        return getMetadata().isMandatory();
    }

    default boolean isUnique() {
        return getMetadata().isUnique();
    }

    default AttributePermissionMode getMode() {
        return getMetadata().getMode();
    }

    default int getIndex() {
        return getMetadata().getIndex();
    }

    default String getDefaultValue() {
        return getMetadata().getDefaultValue();
    }

    default boolean hasGroup() {
        return isNotBlank(getGroupNameOrNull());
    }

    default String getGroupName() {
        return checkNotBlank(getGroupNameOrNull());
    }

    default boolean isSortable() {
        return getMetadata().isSortable();
    }

    default boolean isHiddenInFilter() {
        return getMetadata().hideInFilter();
    }

    default boolean isHiddenInGrid() {
        return getMetadata().hideInGrid();
    }

    default boolean isVirtual() {
        return VirtualAttributeUtils.isVirtual(getType()) || getMetadata().isVirtual();
    }

    @Nullable
    default String getGroupNameOrNull() {
        return getMetadata().getGroup();
    }

    default int getClassOrder() {
        return getMetadata().getClassOrder();
    }

    @Nullable
    default AttrEditorType getEditorType() {
        return getMetadata().getEditorType();
    }

    default boolean isDomainKey() {
        return getMetadata().isDomainKey();
    }

    /**
     *
     * @return the string containing the cql
     */
    default String getFilter() {
        return getMetadata().getFilter();
    }

    default boolean hasFilter() {
        return !isBlank(getFilter());
    }

    default String getForeignKeyDestinationClassName() {
        return getMetadata().getForeignKeyDestinationClassName();
    }

    @Nullable
    default Integer getMaxLength() {
        return getMetadata().getMaxLength();
    }

    default boolean hasMaxLenght() {
        return isNotNullAndGtZero(getMaxLength());
    }

    default boolean isMultiline() {
        return getMetadata().isMultiline();
    }

    default boolean isHtmlSafe() {
        return switch (getMetadata().getTextContentSecurity()) {
            case TCS_HTML_ALL, TCS_HTML_SAFE ->
                true;
            default ->
                false;
        };
    }

    default boolean isTrimEnabled() {
        return getMetadata().isTrimEnabled();
    }

    default boolean isAnonymizable() {
        return getMetadata().isAnonymizable();
    }

    default boolean isShowOnlyIconIfPresent() {
        return getMetadata().isShowOnlyIconIfPresent();
    }

}
