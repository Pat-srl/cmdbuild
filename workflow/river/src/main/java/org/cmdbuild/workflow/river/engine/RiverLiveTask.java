/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine;

public interface RiverLiveTask {

    RiverFlow getFlow();

    RiverTask getTask();

    default String getFlowId() {
        return getFlow().getId();
    }

    default String getTaskId() {
        return getTask().getId();
    }

    default RiverTaskType getTaskType() {
        return getTask().getTaskType();
    }

    default String getPlanId() {
        return getTask().getPlanId();
    }

}
