/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class SimpleFlowInfo extends PlanInfoImpl implements FlowInfo {

	private final String flowId;
	private final FlowStatus status;

	public SimpleFlowInfo(String flowId, FlowStatus status, String packageId, String packageVersion, String definitionId) {//TODO create builder
		super(packageId, packageVersion, definitionId);
		this.flowId = checkNotBlank(flowId);
		this.status = checkNotNull(status);
	}

	@Override
	public String getFlowId() {
		return flowId;
	}

	@Override
	public FlowStatus getStatus() {
		return status;
	}

}
