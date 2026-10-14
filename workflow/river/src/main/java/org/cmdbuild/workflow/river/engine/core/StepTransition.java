package org.cmdbuild.workflow.river.engine.core;

public interface StepTransition {

    String getStepTransitionId();

    Step getTargetStep();

    String getSourceStepId();

    default String getTargetStepId() {
        return getTargetStep().getId();
    }
}
