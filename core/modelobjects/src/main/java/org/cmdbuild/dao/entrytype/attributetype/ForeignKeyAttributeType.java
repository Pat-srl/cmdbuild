package org.cmdbuild.dao.entrytype.attributetype;

import org.cmdbuild.dao.entrytype.CascadeAction;
import static org.cmdbuild.dao.entrytype.CascadeAction.CA_RESTRICT;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ForeignKeyAttributeType extends AbstractReferenceAttributeType {

    private final String targetClassName;
    private final CascadeAction cascadeAction;

    public ForeignKeyAttributeType(String destinationClassName) {
        this(destinationClassName, null);
    }

    public ForeignKeyAttributeType(String destinationClassName, CascadeAction cascadeAction) {
        this.targetClassName = checkNotBlank(destinationClassName, "missing fk target class name");
        this.cascadeAction = firstNotNull(cascadeAction, CA_RESTRICT);
    }

    public ForeignKeyAttributeType(EntryType entryType) {
        this(entryType.getName(), null);
    }

    public ForeignKeyAttributeType(EntryType entryType, CascadeAction cascadeAction) {
        this(entryType.getName(), cascadeAction);
    }

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    public String getForeignKeyDestinationClassName() {
        return targetClassName;
    }

    public CascadeAction getForeignKeyCascadeAction() {
        return cascadeAction;
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.FOREIGNKEY;
    }

    @Override
    public String toString() {
        return "ForeignKeyAttributeType{" + "targetClassName=" + targetClassName + ", cascadeAction=" + cascadeAction + '}';
    }
}
