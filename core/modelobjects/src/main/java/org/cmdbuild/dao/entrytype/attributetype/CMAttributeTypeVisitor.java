package org.cmdbuild.dao.entrytype.attributetype;

import static java.lang.String.format;

public interface CMAttributeTypeVisitor {

    default void visit(GeometryAttributeType attributeType) {
//        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
//TODO
    }

    default void visit(BooleanAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(CharAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(DateAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(DateTimeAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(DecimalAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(DoubleAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(FloatAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(RegclassAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(ForeignKeyAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(IntegerAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(LongAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(IpAddressAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(LookupAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(ReferenceAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(StringArrayAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(JsonAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(StringAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(TextAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(TimeAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(ByteArrayAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(ReferenceArrayAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(IntervalAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(LongArrayAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(LookupArrayAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(FileAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(FormulaAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }

    default void visit(LinkAttributeType attributeType) {
        throw new UnsupportedOperationException(format("unsupported attribute type = %s", attributeType.getClass()));
    }
}
