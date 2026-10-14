/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Task;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class ProcessTaskWsCommand {

    private final WorkflowService workflowService;

    public ProcessTaskWsCommand(WorkflowService workflowService) {
        this.workflowService = checkNotNull(workflowService);
    }

    public PagedElements<Task> doGetAllActivities(String processId, DaoQueryOptions queryOptions) {
        return workflowService.getTaskListForCurrentUserByClassIdSkipFlowData(processId, queryOptions);
    }
}
