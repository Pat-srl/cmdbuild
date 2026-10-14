/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import java.util.List;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Task;
import static org.cmdbuild.workflow.inner.FlowConversionMode.CM_FULL;
import static org.cmdbuild.workflow.inner.FlowConversionMode.CM_LEAN;

public interface TaskConversionService {

    Task toUserTask(Flow flow, RiverTask task, String userTaskId, String taskPerformer);

    List<Task> getTaskList(Flow flow, FlowConversionMode mode);

    Task getTask(Flow card, String userTaskId, FlowConversionMode mode);

    default List<Task> getTaskList(Flow flow) {
        return getTaskList(flow, CM_FULL);
    }

    default List<Task> getTaskListLean(Flow flow) {
        return getTaskList(flow, CM_LEAN);
    }

    default Task getTask(Flow card, String userTaskId) {
        return getTask(card, userTaskId, CM_FULL);
    }

}
