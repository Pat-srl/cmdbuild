/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.workflow.model.Flow;

public interface FlowEvent {

    Flow getFlow();

    FlowEventType getType();

    default boolean isOfType(FlowEventType eventType) {
        return equal(getType(), eventType);
    }

    enum FlowEventType {
        FE_BEFORE_ADVANCE, FE_AFTER_ADVANCE
    }

}
