package org.cmdbuild.workflow.inner;

import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;

public interface WorkflowTypesConverter {


	/*
	 * We should not use the {@link CMAttributeType}, but the Shark type from
	 * the XPDL. It does not handle the case when the value is null and it is
	 * not a CMDBuild attribute. It should suffice for now though.
	 */
	Object toWorkflowType(CardAttributeType<?> attributeType, Object obj);

	Object fromWorkflowType(Object obj);


	interface Lookup {

		Long getId();

	}
}
