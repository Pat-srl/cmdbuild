/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.workflow.xpdl;

import static org.cmdbuild.workflow.model.WorkflowConstants.XPDL_LOOKUP_ARRAY_DECLARED_TYPE;
import static org.cmdbuild.workflow.model.WorkflowConstants.XPDL_LOOKUP_DECLARED_TYPE;
import static org.cmdbuild.workflow.model.WorkflowConstants.XPDL_REFERENCE_ARRAY_DECLARED_TYPE;
import static org.cmdbuild.workflow.model.WorkflowConstants.XPDL_REFERENCE_DECLARED_TYPE;

/**
 *
 * @author ataboga
 */
public enum StandardAndCustomTypes {
    BOOLEAN,
    DATETIME,
    FLOAT,
    INTEGER,
    STRING,
    REFERENCE(XPDL_REFERENCE_DECLARED_TYPE),
    REFERENCEARRAY(XPDL_REFERENCE_ARRAY_DECLARED_TYPE),
    LOOKUP(XPDL_LOOKUP_DECLARED_TYPE),
    LOOKUPARRAY(XPDL_LOOKUP_ARRAY_DECLARED_TYPE);

    private final ElementType element;
    private final String type;

    private StandardAndCustomTypes() {
        this.element = ElementType.BASICTYPE;
        this.type = null;
    }

    private StandardAndCustomTypes(String type) {
        this.element = ElementType.DECLAREDTYPE;
        this.type = type;
    }

    public ElementType getElement() {
        return this.element;
    }

    public String getType() {
        return this.type;
    }
    
    public enum ElementType {
    BASICTYPE, DECLAREDTYPE;
    }
}
