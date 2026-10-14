/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task.scriptexecutors;

import java.util.Map;
import org.cmdbuild.workflow.river.engine.RiverLiveTask;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;

public interface TaskScriptExecutorService {

    RiverTaskCompleted executeTask(RiverLiveTask liveTask, Map<String, Object> data);

}
