package org.cmdbuild.dao.entrytype.attributetype;

public enum GeometryAttributeType implements CardAttributeType<String> {
    INSTANCE;

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "GeometryAttributeType{}";
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.GEOMETRY;
    }
}
