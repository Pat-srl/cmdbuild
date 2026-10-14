/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow;

import java.util.List;
import org.cmdbuild.workflow.model.AdvancedFlowStatus;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.Flow;

public interface FlowAdvanceResponse {

    Flow getFlowCard();

    List<Task> getTasklist();

    AdvancedFlowStatus getAdvancedFlowStatus();

    default String getFlowId() {
        return getFlowCard().getFlowId();
    }

    default long getCardId() {
        return getFlowCard().getCardId();
    }

}
