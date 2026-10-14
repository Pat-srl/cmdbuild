package org.cmdbuild.workflow.model;

public enum FlowStatus {
    OPEN, SUSPENDED, COMPLETED, ABORTED;

    public boolean isCompleted() {
        switch (this) {
            case OPEN:
            case SUSPENDED:
                return false;
            default:
                return true;
        }
    }
}
