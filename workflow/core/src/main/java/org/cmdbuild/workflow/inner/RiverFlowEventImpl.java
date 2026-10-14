/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.river.engine.RiverFlow;

public class RiverFlowEventImpl implements RiverFlowEvent {

    private final Flow flow;
    private final FlowEventType type;
    private final RiverFlow riverFlow;

    public RiverFlowEventImpl(Flow flow, FlowEventType type, RiverFlow riverFlow) {
        this.flow = checkNotNull(flow);
        this.type = checkNotNull(type);
        this.riverFlow = checkNotNull(riverFlow);
    }

    @Override
    public Flow getFlow() {
        return flow;
    }

    @Override
    public FlowEventType getType() {
        return type;
    }

    @Override
    public RiverFlow getRiverFlow() {
        return riverFlow;
    }

    @Override
    public String toString() {
        return "FlowEvent{" + "flow=" + flow + ", type=" + type + '}';
    }

}
