/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.river.engine.RiverTask;

public class StepImpl implements Step {

    private final String stepId;
    private final IncomingHandler incomingHandler;
    private final RiverTask task;
    private final OutgoingHandler outgoingHandler;

    public StepImpl(String stepId, IncomingHandler incomingHandler, RiverTask task, OutgoingHandler outgoingHandler) {
        this.stepId = checkNotBlank(stepId);
        this.incomingHandler = checkNotNull(incomingHandler);
        this.task = checkNotNull(task);
        this.outgoingHandler = checkNotNull(outgoingHandler);
    }

    @Override
    public String getId() {
        return stepId;
    }

    @Override
    public IncomingHandler getIncomingHandler() {
        return incomingHandler;
    }

    @Override
    public RiverTask getTask() {
        return task;
    }

    @Override
    public OutgoingHandler getOutgoingHandler() {
        return outgoingHandler;
    }

    @Override
    public String toString() {
        return "StepImpl{" + "stepId=" + stepId + ", task=" + task + '}';
    }

    @Override
    public int hashCode() {
        return stepId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Step)) {
            return false;
        }
        Step other = (Step) obj;
        return getId().equals(other.getId());
    }

}
