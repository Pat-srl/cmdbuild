/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverLiveTask;
import org.cmdbuild.workflow.river.engine.RiverTask;

public class LiveTaskImpl implements RiverLiveTask {

    private final RiverTask task;
    private final RiverFlow flow;

    public LiveTaskImpl(RiverFlow flow, RiverTask task) {
        this.task = checkNotNull(task);
        this.flow = checkNotNull(flow);
    }

    @Override
    public RiverFlow getFlow() {
        return flow;
    }

    @Override
    public RiverTask getTask() {
        return task;
    }

    @Override
    public String toString() {
        return "LiveTaskImpl{" + "task=" + task + ", flow=" + flow + '}';
    }

}
