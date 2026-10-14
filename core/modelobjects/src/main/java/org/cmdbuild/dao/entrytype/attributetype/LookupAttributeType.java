package org.cmdbuild.dao.entrytype.attributetype;

import org.cmdbuild.lookup.LookupType;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class LookupAttributeType extends AbstractReferenceAttributeType {

    private final String lookupTypeName;

    public LookupAttributeType(String lookupTypeName) {
        this.lookupTypeName = checkNotBlank(lookupTypeName, "lookup type name cannot be null");
    }

    public LookupAttributeType(LookupType lookupType) {
        this(lookupType.getName());
    }

    public String getLookupTypeName() {
        return lookupTypeName;
    }

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.LOOKUP;
    }

    @Override
    public String toString() {
        return "LookupAttributeType{" + "lookupTypeName=" + lookupTypeName + '}';
    }

}
