package org.cmdbuild.dao.entrytype.attributetype;

public class ByteArrayAttributeType implements CardAttributeType<byte[]> {

	public ByteArrayAttributeType() {
	}

	@Override
	public void accept(CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "ByteArrayAttributeType{}";
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.BYTEARRAY;
	}

}
