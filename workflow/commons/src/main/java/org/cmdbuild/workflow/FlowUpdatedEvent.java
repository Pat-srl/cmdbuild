/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow;

import org.cmdbuild.workflow.model.Flow;

public interface FlowUpdatedEvent {

	boolean isAdvanced();

	FlowAdvanceResponse getAdvanceResponse();

	default Flow getFlow() {
		return getAdvanceResponse().getFlowCard();
	}
}
