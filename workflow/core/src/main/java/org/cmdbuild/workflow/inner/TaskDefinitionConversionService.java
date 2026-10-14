/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import java.util.List;
import java.util.Map;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.TaskPerformer;

public interface TaskDefinitionConversionService {

    TaskDefinition toTaskDefinition(RiverTask task);

    List<TaskPerformer> getProcessedTaskPerformersForTask(RiverTask task, Map<String, Object> flowData);
}
