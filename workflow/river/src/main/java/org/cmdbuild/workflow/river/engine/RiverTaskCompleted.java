package org.cmdbuild.workflow.river.engine;

import java.util.Map;

public interface RiverTaskCompleted {

    RiverLiveTask getTask();

    Map<String, Object> getLocalVariables();

    default String getFlowId() {
        return getTask().getFlowId();
    }

    default String getTaskId() {
        return getTask().getTaskId();
    }

}
