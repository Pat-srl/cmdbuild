/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Task;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class ProcessInstanceActivityWsCommand {

    private final WorkflowService workflowService;

    public ProcessInstanceActivityWsCommand(WorkflowService workflowService) {
        this.workflowService = checkNotNull(workflowService);
    }

    public List<Task> doReadMany(String classId, Long cardId) {
        return workflowService.getTaskListForCurrentUserByClassIdAndCardId(classId, cardId);
    }

    public Task doReadOne(String classId, Long cardId, String taskId) {
        Flow card = workflowService.getFlowCard(classId, cardId);
        return workflowService.getUserTask(card, taskId);
    }
}
