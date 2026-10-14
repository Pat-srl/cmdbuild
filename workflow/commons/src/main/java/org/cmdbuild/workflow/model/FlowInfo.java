package org.cmdbuild.workflow.model;

public interface FlowInfo extends PlanInfo {

	String getFlowId();

	FlowStatus getStatus();

}
