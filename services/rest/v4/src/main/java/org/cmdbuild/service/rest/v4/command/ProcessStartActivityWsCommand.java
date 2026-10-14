/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class ProcessStartActivityWsCommand {
    private final WorkflowService workflowService;

    public ProcessStartActivityWsCommand(WorkflowService workflowService) {
        this.workflowService = checkNotNull(workflowService);
    }

    public Process doReadProcess(String processId) {
        return workflowService.getProcess(processId);
    }

    public TaskDefinition doReadTaskDefinition(String processId) {
        return workflowService.getEntryTaskForCurrentUser(processId);
    }
}
