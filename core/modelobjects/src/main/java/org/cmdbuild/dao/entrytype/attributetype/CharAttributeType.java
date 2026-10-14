package org.cmdbuild.dao.entrytype.attributetype;

public class CharAttributeType implements CardAttributeType<String> {

	@Override
	public void accept(CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

	private static final CardAttributeType<?> daoType = new CharAttributeType();

	protected CardAttributeType<?> getDaoType() {
		return daoType;
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.CHAR;
	}


}
