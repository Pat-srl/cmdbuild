package org.cmdbuild.dao.entrytype.attributetype;

import java.time.LocalTime;

public class TimeAttributeType implements CardAttributeType<LocalTime> {

    @Override
    public void accept(final CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.TIME;
    }
}
