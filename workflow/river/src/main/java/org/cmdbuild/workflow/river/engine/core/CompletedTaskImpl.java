/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverLiveTask;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;
import org.cmdbuild.workflow.river.engine.task.LiveTaskImpl;

public class CompletedTaskImpl implements RiverTaskCompleted {

    private final RiverLiveTask task;
    private final Map<String, Object> data;

    public CompletedTaskImpl(RiverLiveTask task) {
        this(task, emptyMap());
    }

    public CompletedTaskImpl(RiverLiveTask task, Map<String, Object> data) {
        this.task = checkNotNull(task);
        this.data = checkNotNull(data);
    }

    @Override
    public RiverLiveTask getTask() {
        return task;
    }

    @Override
    public Map<String, Object> getLocalVariables() {
        return data;
    }

    @Override
    public String toString() {
        return "CompletedTask{" + "task=" + task + '}';
    }

    public static CompletedTaskImpl of(RiverLiveTask task) {
        return new CompletedTaskImpl(task);
    }

    public static CompletedTaskImpl of(RiverFlow flow, RiverTask task) {
        return new CompletedTaskImpl(new LiveTaskImpl(flow, task));
    }
}
