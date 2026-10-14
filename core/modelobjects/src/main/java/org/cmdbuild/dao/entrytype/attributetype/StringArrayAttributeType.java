package org.cmdbuild.dao.entrytype.attributetype;

public class StringArrayAttributeType implements CardAttributeType<String[]> {

	@Override
	public void accept(final CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.STRINGARRAY;
	}

}
