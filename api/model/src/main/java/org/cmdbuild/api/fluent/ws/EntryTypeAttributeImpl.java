package org.cmdbuild.api.fluent.ws;
 
import org.cmdbuild.workflow.beans.EntryTypeAttribute;

public abstract class EntryTypeAttributeImpl implements EntryTypeAttribute {


    protected final String entryTypeName;
    private final String attributeName;

    protected EntryTypeAttributeImpl(final String entryTypeName, final String attributeName) {
        this.entryTypeName = entryTypeName;
        this.attributeName = attributeName;
    }

    public abstract void accept(AttrTypeVisitor visitor);

    @Override
    public String getAttributeName() {
        return attributeName;
    }

}
