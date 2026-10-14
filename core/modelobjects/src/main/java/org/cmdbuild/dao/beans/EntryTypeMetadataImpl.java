/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.VIRTUAL;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwnerImpl;
import org.cmdbuild.dao.entrytype.ClassPermission;
import org.cmdbuild.dao.entrytype.ClassPermissionMode;
import org.cmdbuild.dao.entrytype.ClassPermissions;
import org.cmdbuild.dao.entrytype.ClassPermissionsImpl;
import org.cmdbuild.dao.entrytype.EntryTypeMetadata;
import static org.cmdbuild.dao.entrytype.EntryTypeMetadata.ENTRY_TYPE_MODE;
import org.cmdbuild.dao.entrytype.PermissionScope;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.getDefaultPermissions;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.parseClassPermissions;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_ALL;
import org.cmdbuild.dao.entrytype.DaoPermissionUtils;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import static org.cmdbuild.dao.utils.VirtualAttributeUtils.VIRTUAL_ATTRIBUTE_TYPE_ATTR;
import static org.cmdbuild.dao.utils.VirtualAttributeUtils.attributeTypeNameToAttributeType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMap;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public abstract class EntryTypeMetadataImpl extends AbstractMetadataImpl implements EntryTypeMetadata {

    private static final Set<String> ENTRY_TYPE_METADATA_KEYS = ImmutableSet.of(ENTRY_TYPE_MODE, PERMISSIONS);

    private final ClassPermissionMode mode;
    private final ClassPermissions permissions;
    private final List<AttributeWithoutOwner> virtualAttributes;

    protected EntryTypeMetadataImpl(Map<String, String> allAttrs, Map<String, String> customAttrs) {
        super(allAttrs, map(customAttrs).withoutKeys(ENTRY_TYPE_METADATA_KEYS).withoutKeys(k -> k.startsWith(VIRTUAL_ATTRIBUTES_PREFIX)));
        mode = Optional.ofNullable(trimToNull(allAttrs.get(ENTRY_TYPE_MODE))).map(DaoPermissionUtils::parseClassPermissionMode).orElse(CPM_ALL);
        if (isNotBlank(allAttrs.get(PERMISSIONS))) {
            permissions = ClassPermissionsImpl
                    .copyOf(getDefaultPermissions(mode))
                    .addPermissions(parseClassPermissions(allAttrs.get(PERMISSIONS)))
                    .build();
        } else {
            permissions = getDefaultPermissions(mode);
        }
        virtualAttributes = list(((Map<String, Map<String, String>>) (Map) unflattenMaps(unflattenMap(customAttrs, VIRTUAL_ATTRIBUTES_PREFIX))).entrySet()).map(e -> (AttributeWithoutOwner) AttributeWithoutOwnerImpl.builder()
                .withName(e.getKey())
                .withType(attributeTypeNameToAttributeType(parseEnum(e.getValue().get(VIRTUAL_ATTRIBUTE_TYPE_ATTR), AttributeTypeName.class)))
                .withMeta(new AttributeMetadataImpl(map(e.getValue()).withoutKey(VIRTUAL_ATTRIBUTE_TYPE_ATTR).with(VIRTUAL, TRUE)))
                .build()).sorted(AttributeWithoutOwner::getIndex).immutableCopy();
        checkArgument(virtualAttributes.size() == list(virtualAttributes).map(AttributeWithoutOwner::getName).toSet().size());
    }

    @Override
    public ClassPermissionMode getMode() {
        return mode;
    }

    @Override
    public Map<PermissionScope, Set<ClassPermission>> getPermissions() {
        return permissions.getPermissionsMap();
    }

    @Override
    public List<AttributeWithoutOwner> getVirtualAttributes() {
        return virtualAttributes;
    }

}
