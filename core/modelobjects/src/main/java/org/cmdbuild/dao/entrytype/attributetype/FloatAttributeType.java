package org.cmdbuild.dao.entrytype.attributetype;

public enum FloatAttributeType implements CardAttributeType<Float> {

    INSTANCE;

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.FLOAT;
    }

}
