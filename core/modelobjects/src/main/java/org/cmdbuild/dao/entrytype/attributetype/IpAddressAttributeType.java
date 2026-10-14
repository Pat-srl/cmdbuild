package org.cmdbuild.dao.entrytype.attributetype;

public class IpAddressAttributeType implements CardAttributeType<String> {

    private final IpType type;

    public IpAddressAttributeType(IpType type) {
        this.type = type;
    }

    public IpType getType() {
        return type;
    }

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.INET;
    }

    @Override
    public String toString() {
        return "IpAddressAttributeType{" + "type=" + type + '}';
    }

}
