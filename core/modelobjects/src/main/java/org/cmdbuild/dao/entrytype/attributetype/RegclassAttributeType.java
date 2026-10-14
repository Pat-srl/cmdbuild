package org.cmdbuild.dao.entrytype.attributetype;

public enum RegclassAttributeType implements CardAttributeType<Long> {
	INSTANCE;

	@Override
	public void accept(CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.REGCLASS;
	}

}
