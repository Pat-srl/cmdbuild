/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.AttributeGroupData;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.EntryTypeType;

public interface AttributeGroupRepository {

    List<AttributeGroupData> getAll();

    List<AttributeGroupData> getAttributeGroupsForEntryType(String ownerName, EntryTypeType ownerType);

    @Nullable
    AttributeGroupData getOrNull(String ownerName, EntryTypeType ownerType, String groupId);

    AttributeGroupData create(AttributeGroupData group);

    AttributeGroupData update(AttributeGroupData group);

    void delete(AttributeGroupData group);

    default AttributeGroupData createOrUpdate(AttributeGroupData group) {
        if (exists(group.getOwnerName(), group.getOwnerType(), group.getName())) {
            return update(group);
        } else {
            return create(group);
        }
    }

    default AttributeGroupData get(String ownerName, EntryTypeType ownerType, String groupId) {
        return checkNotNull(getOrNull(ownerName, ownerType, groupId), "attribute group not found for owner = %s code =< %s >", ownerName, groupId);
    }

    default AttributeGroupData get(EntryType owner, String groupId) {
        return checkNotNull(getOrNull(owner, groupId), "attribute group not found for owner = %s code =< %s >", owner, groupId);
    }

    @Nullable
    default AttributeGroupData getOrNull(EntryType owner, String groupId) {
        return getOrNull(owner.getName(), owner.getEtType(), groupId);
    }

    default List<AttributeGroupData> getAttributeGroupsForEntryType(EntryType owner) {
        return AttributeGroupRepository.this.getAttributeGroupsForEntryType(owner.getName(), owner.getEtType());
    }

    default boolean exists(EntryType owner, String groupId) {
        return getOrNull(owner, groupId) != null;
    }

    default boolean exists(String ownerName, EntryTypeType ownerType, String groupId) {
        return getOrNull(ownerName, ownerType, groupId) != null;
    }

    default int getNextIndexForOwner(EntryType owner) {
        return getAttributeGroupsForEntryType(owner).stream().mapToInt(AttributeGroupData::getIndex).max().orElse(0) + 1;
    }
}
