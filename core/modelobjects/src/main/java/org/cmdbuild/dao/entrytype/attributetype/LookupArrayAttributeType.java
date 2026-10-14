package org.cmdbuild.dao.entrytype.attributetype;

import org.cmdbuild.lookup.LookupType;

public class LookupArrayAttributeType extends LookupAttributeType {

    public LookupArrayAttributeType(String lookupTypeName) {
        super(lookupTypeName);
    }

    public LookupArrayAttributeType(LookupType lookupType) {
        super(lookupType);
    }

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.LOOKUPARRAY;
    }

    @Override
    public String toString() {
        return "LookupArrayAttributeType{" + "lookupTypeName=" + getLookupTypeName() + '}';
    }

}
