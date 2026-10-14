package org.cmdbuild.dao.entrytype.attributetype;

public enum LinkAttributeType implements CardAttributeType<String> {

    INSTANCE;

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.LINK;
    }
}
