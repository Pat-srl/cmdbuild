package org.cmdbuild.dao.entrytype.attributetype;

public class ByteaArrayAttributeType implements CardAttributeType<byte[][]> {

	@Override
	public void accept(final CMAttributeTypeVisitor visitor) {
//		visitor.visit(this);TODO
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.BYTEAARRAY;
	}

}
