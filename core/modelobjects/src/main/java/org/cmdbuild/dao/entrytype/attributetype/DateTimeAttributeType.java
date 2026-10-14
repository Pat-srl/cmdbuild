package org.cmdbuild.dao.entrytype.attributetype;

import java.time.ZonedDateTime;

public class DateTimeAttributeType implements CardAttributeType<ZonedDateTime> {

	@Override
	public void accept(CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.TIMESTAMP;
	}

}
