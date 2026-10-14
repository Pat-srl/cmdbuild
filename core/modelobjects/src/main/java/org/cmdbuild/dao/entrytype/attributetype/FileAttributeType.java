package org.cmdbuild.dao.entrytype.attributetype;

public class FileAttributeType extends AbstractReferenceAttributeType {

    public static FileAttributeType INSTANCE = new FileAttributeType();

    private FileAttributeType() {
    }

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.FILE;
    }

}
