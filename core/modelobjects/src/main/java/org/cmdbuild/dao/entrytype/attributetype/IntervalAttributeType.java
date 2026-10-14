package org.cmdbuild.dao.entrytype.attributetype;

import org.cmdbuild.utils.date.Interval;

public enum IntervalAttributeType implements CardAttributeType<Interval> {
    INSTANCE;

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.INTERVAL;
    }
}
