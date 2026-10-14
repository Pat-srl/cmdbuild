package org.cmdbuild.dao.entrytype.attributetype;

public class DoubleAttributeType implements CardAttributeType<Double> {

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.DOUBLE;
    }

}
