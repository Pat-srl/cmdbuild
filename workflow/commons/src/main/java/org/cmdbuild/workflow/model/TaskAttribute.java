package org.cmdbuild.workflow.model;

public interface TaskAttribute {

	String getName();

	boolean isWritable();

	boolean isMandatory();
	
	boolean isAction();

}
