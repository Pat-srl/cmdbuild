package org.cmdbuild.dao.entrytype.attributetype;

import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;

public class StringAttributeType implements CardAttributeType<String> {

    public final int length;

    @Deprecated//use length from metadata
    public StringAttributeType(@Nullable Integer length) {
        this.length = firstNotNull(ltEqZeroToNull(length), Integer.MAX_VALUE);
    }

    public StringAttributeType() {
        this(null);
    }

    @Deprecated//use length from metadata
    public int getLength() {
        return length;
    }

    @Deprecated//use length from metadata
    public boolean hasLength() {
        return length != Integer.MAX_VALUE;
    }

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "StringAttributeType{" + "length=" + length + '}';
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.STRING;
    }
}
