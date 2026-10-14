package org.cmdbuild.dao.entrytype.attributetype;

import java.time.LocalDate;

public class DateAttributeType implements CardAttributeType<LocalDate> {

	@Override
	public void accept(CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.DATE;
	}

}
