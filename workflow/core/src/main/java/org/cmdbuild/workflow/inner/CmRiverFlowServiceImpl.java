/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.workflow.WorkflowService.CONTEXT_SCOPE_WORKFLOW;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;
import org.cmdbuild.workflow.river.engine.core.RiverFlowServiceImpl;
import org.cmdbuild.workflow.river.engine.data.RiverPlanRepository;
import org.springframework.stereotype.Component;
import org.cmdbuild.workflow.river.engine.task.RiverTaskService;

@Component
public class CmRiverFlowServiceImpl extends RiverFlowServiceImpl {

    private final RequestContextService contextService;

    public CmRiverFlowServiceImpl(RiverPlanRepository planRepository, RiverTaskService taskService, RequestContextService contextService) {
        super(planRepository, taskService);
        this.contextService = checkNotNull(contextService);
    }

    @Override
    public RiverFlow completedTask(RiverFlow flow, RiverTaskCompleted completedTask) {
        contextService.getRequestContext().pushContextScope(CONTEXT_SCOPE_WORKFLOW);//TODO improve this
        try {
            return super.completedTask(flow, completedTask);
        } finally {
            contextService.getRequestContext().popContextScope();
        }
    }

    @Override
    public RiverFlow startFlow(RiverFlow flow, String entryPointId) {
        contextService.getRequestContext().pushContextScope(CONTEXT_SCOPE_WORKFLOW);//TODO improve this
        try {
            return super.startFlow(flow, entryPointId);
        } finally {
            contextService.getRequestContext().popContextScope();
        }
    }

    @Override
    public RiverFlow executeBatchTasks(RiverFlow flow) {
        contextService.getRequestContext().pushContextScope(CONTEXT_SCOPE_WORKFLOW);//TODO improve this
        try {
            return super.executeBatchTasks(flow);
        } finally {
            contextService.getRequestContext().popContextScope();
        }
    }

}
