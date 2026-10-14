/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.run;

import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;

public interface FlowExecutor {

    RiverFlow startFlow(RiverFlow flow, String entryPoint);

    RiverFlow completedTask(RiverFlow flow, RiverTaskCompleted completedTask);

    RiverFlow executeBatchTasks(RiverFlow flow);

}
