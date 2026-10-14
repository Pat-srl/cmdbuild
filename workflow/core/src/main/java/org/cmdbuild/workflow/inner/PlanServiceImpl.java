/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.eventbus.EventBus;
import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.core.FixedOutgoingHandler;
import org.cmdbuild.workflow.river.engine.core.Step;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.springframework.stereotype.Component;
import org.cmdbuild.workflow.dao.ExtendedRiverPlanRepository;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.workflow.dao.RiverPlanRepositoryImpl.ATTR_BIND_TO_CLASS;
import org.cmdbuild.workflow.inner.PlanService;

@Component
public class PlanServiceImpl implements PlanService {

    private final ExtendedRiverPlanRepository planRepository;
    private final TaskDefinitionConversionService taskToTaskDataService;

    public PlanServiceImpl(ExtendedRiverPlanRepository planRepository, TaskDefinitionConversionService taskToTaskDataService) {
        this.planRepository = checkNotNull(planRepository);
        this.taskToTaskDataService = checkNotNull(taskToTaskDataService);
    }

    @Override
    public EventBus getEventBus() {
        return planRepository.getEventBus();
    }

    @Override
    public boolean hasPlanId(String planId) {
        return planRepository.getPlanByIdOrNull(planId) != null;
    }

    @Override
    @Nullable
    public String getPlanIdOrNull(Classe classe) {
        RiverPlan riverPlan = planRepository.getPlanByClassIdOrNull(classe.getName());
        return riverPlan == null ? null : riverPlan.getId();
    }

    @Override
    @Nullable
    public String getClassNameOrNull(String planId) {
        RiverPlan riverPlan = planRepository.getPlanByIdOrNull(planId);
        if (riverPlan == null) {
            return null;
        } else {
            return riverPlan.getAttr(ATTR_BIND_TO_CLASS);
        }
    }

    @Override
    public List<TaskDefinition> getEntryTasks(String planId) {
        RiverPlan riverPlan = planRepository.getPlanById(planId);
        List<Step> steps = riverPlan.getEntryPointSteps();
        List<RiverTask> entryPointTasks = getNextTasksForEntrySteps(riverPlan, steps);
        return entryPointTasks.stream().map(taskToTaskDataService::toTaskDefinition).collect(toList());
    }

    @Override
    public List<TaskDefinition> getAllTasks(String planId) {
        RiverPlan riverPlan = planRepository.getPlanById(planId);
        Collection<Step> steps = riverPlan.getSteps();
        return steps.stream().map(Step::getTask).map(taskToTaskDataService::toTaskDefinition).collect(toList());
    }

    private List<RiverTask> getNextTasksForEntrySteps(RiverPlan plan, List<Step> steps) {
        return steps.stream().map((step) -> {
            return findUserTask(plan, step);
        }).collect(toList());
    }

    private RiverTask findUserTask(RiverPlan plan, Step step) { //TODO move this into river plan parsing
        RiverTask task = step.getTask();
        if (task.isUser()) {
            return task;
        } else {
            Step.OutgoingHandler outgoingHandler = step.getOutgoingHandler();
            checkArgument(outgoingHandler instanceof FixedOutgoingHandler, "invalid outgoing handler = %s for step = %s", outgoingHandler, step);
            List<String> outgoingFlags = ((FixedOutgoingHandler) outgoingHandler).getOutgoingStepTransitionIdsForTask(null);
            checkArgument(outgoingFlags.size() == 1);
            Step nextStep = plan.getNextStepByTransitionId(getOnlyElement(outgoingFlags));
            return findUserTask(plan, nextStep);
        }
    }

}
