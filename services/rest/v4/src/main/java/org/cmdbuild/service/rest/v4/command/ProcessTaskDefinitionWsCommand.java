/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.formstructure.FormStructureImpl;
import org.cmdbuild.formstructure.FormStructureService;
import org.cmdbuild.service.rest.v4.model.WsTaskDefinitionData;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.workflow.utils.ClosedFlowUtils.DUMMY_TASK_FOR_CLOSED_PROCESS;
import static org.cmdbuild.workflow.utils.ClosedFlowUtils.buildTaskDefinitionForClosedTask;

/**
 * @author ldare
 */
@Component
public class ProcessTaskDefinitionWsCommand {

    private final WorkflowService workflowService;
    private final FormStructureService formStructureService;

    public ProcessTaskDefinitionWsCommand(WorkflowService workflowService, FormStructureService formStructureService) {
        this.workflowService = checkNotNull(workflowService);
        this.formStructureService = checkNotNull(formStructureService);
    }

    public Process doGetProcess(String processId) {
        return workflowService.getProcess(processId);
    }

    public TaskDefinition doGetTaskDefinition(String processId, String taskId) {
        return workflowService.getTaskDefinition(processId, taskId);
    }

    public List<TaskDefinition> doGetTaskDefinitions(String processId) {
        return workflowService.getTaskDefinitions(processId);
    }

    public TaskDefinition doUpdate(Process process, String processId, String taskId, WsTaskDefinitionData data) {
        formStructureService.setFormForTask(process, taskId, data.formStructure.isNull() ? null : new FormStructureImpl(toJson(data.formStructure)));
        TaskDefinition task;
        if (taskId.equals(DUMMY_TASK_FOR_CLOSED_PROCESS)) {
            task = buildTaskDefinitionForClosedTask();
        } else {
            task = workflowService.getTaskDefinition(processId, taskId);
        }
        return task;
    }
}
