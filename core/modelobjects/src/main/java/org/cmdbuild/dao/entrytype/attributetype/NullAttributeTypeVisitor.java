package org.cmdbuild.dao.entrytype.attributetype;

public class NullAttributeTypeVisitor implements CMAttributeTypeVisitor {

    private static final NullAttributeTypeVisitor INSTANCE = new NullAttributeTypeVisitor();

    public static NullAttributeTypeVisitor getInstance() {
        return INSTANCE;
    }

    protected NullAttributeTypeVisitor() {
        // use factory method
    }

    @Override
    public void visit(BooleanAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(CharAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(DateAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(DateTimeAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(DecimalAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(DoubleAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(FloatAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(RegclassAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(ForeignKeyAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(IntegerAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(LongAttributeType attributeType) {
        // nothing to do		
    }

    @Override
    public void visit(IpAddressAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(LookupAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(ReferenceAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(StringArrayAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(StringAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(TextAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(TimeAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(ByteArrayAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(ReferenceArrayAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(JsonAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(IntervalAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(LongArrayAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(FileAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(LookupArrayAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(FormulaAttributeType attributeType) {
        // nothing to do
    }

    @Override
    public void visit(LinkAttributeType attributeType) {
        // nothing to do
    }

}
