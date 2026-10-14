package org.cmdbuild.workflow.inner;

import org.cmdbuild.workflow.beans.EntryTypeAttribute;

public interface SchemaApiForWorkflowExt extends SchemaApiForWorkflow {

    AttributeInfo findAttributeFor(EntryTypeAttribute entryTypeAttribute);

}
