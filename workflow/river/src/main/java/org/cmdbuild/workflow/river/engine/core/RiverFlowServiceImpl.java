/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmConvertUtils.defaultValue;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverFlowService;
import org.cmdbuild.workflow.river.engine.RiverFlowStatus;
import static org.cmdbuild.workflow.river.engine.RiverFlowStatus.ABORTED;
import static org.cmdbuild.workflow.river.engine.RiverFlowStatus.RUNNING;
import static org.cmdbuild.workflow.river.engine.RiverFlowStatus.SUSPENDED;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;
import org.cmdbuild.workflow.river.engine.data.RiverPlanRepository;
import org.cmdbuild.workflow.river.engine.run.FlowExecutor;
import org.cmdbuild.workflow.river.engine.run.FlowExecutorImpl;
import org.cmdbuild.workflow.river.engine.task.RiverTaskService;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.ALWAYS_INITIALIZE_GLOBAL_VARIABLES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RiverFlowServiceImpl implements RiverFlowService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RiverPlanRepository planRepository;
    private final FlowExecutor flowExecutor;

    public RiverFlowServiceImpl(RiverPlanRepository planRepository, RiverTaskService taskService) {
        this.planRepository = checkNotNull(planRepository);
        flowExecutor = new FlowExecutorImpl(checkNotNull(taskService));
    }

    @Override
    public RiverPlan getPlanById(String planId) {
        return planRepository.getPlanById(planId);
    }

    @Override
    public RiverFlow createFlow(RiverPlan riverPlan) {
        logger.debug("create process for plan = {}", riverPlan);
        RiverFlow flow = RiverFlowImpl.builder()
                .withFlowId(randomId())
                .withPlan(riverPlan)
                .withTasks(emptyList())
                .withFlowStatus(RiverFlowStatus.READY)
                .withData(buildInitialData(riverPlan))
                .build();
        return flow;
    }

    @Override
    public RiverFlow startFlow(RiverFlow walk, String entryPointId) {
        logger.debug("start process for walk = {} entry point = {}", walk, entryPointId);
        return flowExecutor.startFlow(walk, entryPointId);
    }

    @Override
    public RiverFlow completedTask(RiverFlow flow, RiverTaskCompleted completedTask) {
        checkArgument(flow.isRunning(), "cannot complete task: invalid flow status = %s", flow.getStatus());
        return flowExecutor.completedTask(flow, completedTask);
    }

    @Override
    public RiverFlow executeBatchTasks(RiverFlow flow) {
        checkArgument(flow.isRunning(), "cannot complete task: invalid flow status = %s", flow.getStatus());//TODO check this
        return flowExecutor.executeBatchTasks(flow);
    }

    private Map<String, Object> buildInitialData(RiverPlan plan) {
        boolean initGlobalVars = toBooleanOrDefault(plan.getAttOrNull(ALWAYS_INITIALIZE_GLOBAL_VARIABLES), false);
        Map<String, Object> map = map();
        plan.getGlobalVariables().forEach((key, var) -> {
            Object value = null;
            if (var.getDefaultValue().isPresent()) {
                value = var.getDefaultValue().get();//TODO value conversion
            }
            if (value == null && initGlobalVars) {
                value = defaultValue(var.getJavaType());
            }
            map.put(key, value);
        });
        return map;
    }

    @Override
    public RiverFlow suspendFlow(RiverFlow flow) {
        checkArgument(flow.isRunning(), "cannot suspend flow: invalid flow status = %s", flow.getStatus());
        return RiverFlowImpl.copyOf(flow).withFlowStatus(SUSPENDED).build();
    }

    @Override
    public RiverFlow resumeFlow(RiverFlow flow) {
        checkArgument(flow.isSuspended(), "cannot resume flow: invalid flow status = %s", flow.getStatus());
        return RiverFlowImpl.copyOf(flow).withFlowStatus(RUNNING).build();
    }

    @Override
    public RiverFlow terminateFlow(RiverFlow flow) {
        return RiverFlowImpl.copyOf(flow).withFlowStatus(ABORTED).withTasks(emptyList()).build();
    }

}
