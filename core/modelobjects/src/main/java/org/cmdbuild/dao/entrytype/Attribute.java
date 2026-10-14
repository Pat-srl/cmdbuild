package org.cmdbuild.dao.entrytype;

import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public interface Attribute extends AttributeWithoutOwner {

    EntryType getOwner();

    default String getOwnerName() {
        return getOwner().getName();
    }

    default boolean isOfType(AttributeTypeName... types) {
        return set(types).contains(getType().getName());
    }

    default Classe getOwnerClass() {
        return (Classe) getOwner();
    }

    default AttributeGroupInfo getGroup() {
        return getOwner().getAttributeGroup(getGroupName());
    }

    @Nullable
    default String getGroupDescriptionOrNull() {
        return hasGroup() ? getGroup().getDescription() : null;
    }

}
