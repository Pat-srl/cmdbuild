package org.cmdbuild.dao.entrytype.attributetype;

public class BooleanAttributeType implements CardAttributeType<Boolean> {

	@Override
	public void accept(CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.BOOLEAN;
	}

}
