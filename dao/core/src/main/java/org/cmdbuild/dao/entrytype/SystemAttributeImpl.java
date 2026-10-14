package org.cmdbuild.dao.entrytype;

import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;

public class SystemAttributeImpl implements Attribute {

    private final String name;
    private final String description;
    private final boolean mandatory;
    private final EntryType entryType;
    private final CardAttributeType<?> attributeType;

    public SystemAttributeImpl(String name, EntryType entryType, CardAttributeType<?> attributeType, boolean mandatory) {
        this(name, name, entryType, attributeType, mandatory);
    }

    public SystemAttributeImpl(String name, String description, EntryType entryType, CardAttributeType<?> attributeType, boolean mandatory) {
        this.name = name;
        this.description = description;
        this.entryType = entryType;
        this.attributeType = attributeType;
        this.mandatory = mandatory;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public EntryType getOwner() {
        return entryType;
    }

    @Override
    public CardAttributeType<?> getType() {
        return attributeType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean hasNotServiceListPermission() {
        return true;
    }

    @Override
    public boolean isInherited() {
        return false;
    }

    @Override
    public boolean showInGrid() {
        return false;
    }

    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public AttributePermissionMode getMode() {
        return AttributePermissionMode.APM_WRITE;
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public String getDefaultValue() {
        return null;
    }

    @Override
    public int getClassOrder() {
        return 0;
    }

    @Override
    public AttrEditorType getEditorType() {
        return null;
    }

    @Override
    public String getFilter() {
        return "";
    }

    @Override
    public String getForeignKeyDestinationClassName() {
        return null;
    }

    @Override
    public AttributeMetadata getMetadata() {
        return new AttributeMetadataImpl(emptyMap());//TODO fix this
    }

    @Override
    public Map<PermissionScope, Set<AttributePermission>> getPermissionMap() {
        return getMetadata().getPermissionMap();
    }

}
