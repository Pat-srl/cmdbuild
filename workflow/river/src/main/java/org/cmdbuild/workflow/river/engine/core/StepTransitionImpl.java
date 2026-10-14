/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 *
 * @author davide
 */
public class StepTransitionImpl implements StepTransition {

    private final String transitionId, sourceStepId;
    private final Step targetStep;

    public StepTransitionImpl(String transitionId, String sourceStepId, Step targetStep) {
        this.transitionId = checkNotBlank(transitionId);
        this.sourceStepId = checkNotBlank(sourceStepId);
        this.targetStep = checkNotNull(targetStep);
    }

    @Override
    public String getStepTransitionId() {
        return transitionId;
    }

    @Override
    public Step getTargetStep() {
        return targetStep;
    }

    @Override
    public String getSourceStepId() {
        return sourceStepId;
    }

    @Override
    public String toString() {
        return "StepTransitionImpl{" + "transitionId=" + transitionId + ", sourceStepId=" + sourceStepId + ", targetStepId=" + getTargetStepId() + '}';
    }

}
