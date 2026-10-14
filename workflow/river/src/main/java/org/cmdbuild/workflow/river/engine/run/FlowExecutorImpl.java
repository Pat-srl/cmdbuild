/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.run;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Queues;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverFlowStatus;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;
import static org.cmdbuild.workflow.river.engine.RiverTaskType.NOP;
import static org.cmdbuild.workflow.river.engine.RiverTaskType.SCRIPT_INLINE;
import org.cmdbuild.workflow.river.engine.core.RiverFlowImpl;
import org.cmdbuild.workflow.river.engine.core.Step;
import static org.cmdbuild.workflow.river.engine.core.Step.IncomingHandler.ACTIVATE_WHEN_ALL_INCOMING_STEPS_HAVE_COMPLETED;
import static org.cmdbuild.workflow.river.engine.core.Step.IncomingHandler.ACTIVATE_WHEN_ANY_INCOMING_STEP_HAVE_COMPLETED;
import org.cmdbuild.workflow.river.engine.core.StepTransition;
import org.cmdbuild.workflow.river.engine.task.LiveTaskImpl;
import org.cmdbuild.workflow.river.engine.task.RiverTaskService;
import static org.cmdbuild.workflow.river.engine.utils.FlowGraphUtils.getAllParallelStepIdsConvergingOnThisStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowExecutorImpl implements FlowExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RiverTaskService taskService;

    public FlowExecutorImpl(RiverTaskService taskService) {
        this.taskService = checkNotNull(taskService);
    }

    @Override
    public RiverFlow startFlow(RiverFlow flow, String entryPoint) {
        Step startingStep = flow.getPlan().getStepById(entryPoint);
        return new FlowAdvanceOperation(flow).enterStep(startingStep).processAndReturnFlow();
    }

    @Override
    public RiverFlow completedTask(RiverFlow flow, RiverTaskCompleted completedTask) {
        return new FlowAdvanceOperation(flow).completeTask(completedTask).processAndReturnFlow();
    }

    @Override
    public RiverFlow executeBatchTasks(RiverFlow flow) {
        return new FlowAdvanceOperation(flow).loadBatchSteps().processAndReturnFlow();
    }

    private class FlowAdvanceOperation {

        private boolean runBatch;
        private RiverFlow flow;
        private final Queue<Step> stepQueue = Queues.newConcurrentLinkedQueue();
        private final Queue<RiverTaskCompleted> completedTaskQueue = Queues.newConcurrentLinkedQueue();

        public FlowAdvanceOperation(RiverFlow flow) {
            this.flow = checkNotNull(flow);
            this.runBatch = false;
        }

        public FlowAdvanceOperation loadBatchSteps() {
            logger.debug("load batch steps for flow = {}", flow);
            runBatch = true;
            List<RiverTask> batchTasks = flow.getBatchTasks();
            flow = RiverFlowImpl.copyOf(flow).withTasks(list(flow.getTasks()).without(RiverTask::isBatch)).build();
            batchTasks.forEach(t -> {
                logger.debug("load batch task = {}", t);
                stepQueue.add(flow.getPlan().getStepByTaskId(t.getId()));
            });
            return this;
        }

        public RiverFlow processAndReturnFlow() {
            while (!stepQueue.isEmpty() || !completedTaskQueue.isEmpty()) {
                while (!completedTaskQueue.isEmpty()) {
                    completeTask(completedTaskQueue.poll());
                }
                if (!stepQueue.isEmpty()) {
                    enterStep(stepQueue.poll());
                }
            }
            if (flow.getTasks().isEmpty()) {
                flow = RiverFlowImpl.copyOf(flow).withFlowStatus(RiverFlowStatus.COMPLETE).build();
                logger.debug("flow completed = {}", flow);
            }
            return flow;
        }

        public FlowAdvanceOperation enterStep(Step step) {
            logger.debug("activate step = {}", step);
            RiverTask task = step.getTask();
            switch (task.getTaskType()) {
                case USER:
                    //nothing to do
                    break;
                case SCRIPT_BATCH:
                    if (runBatch) {
                        completedTaskQueue.add(taskService.executeTask(new LiveTaskImpl(flow, task)));
                    }
                    break;
                case SCRIPT_INLINE:
                case NOP:
                    completedTaskQueue.add(taskService.executeTask(new LiveTaskImpl(flow, task)));
                    break;
                default:
                    throw new IllegalArgumentException("unsupported task type " + task.getTaskType());
            }
            flow = RiverFlowImpl.copyOf(flow)
                    .withFlowStatus(RiverFlowStatus.RUNNING)
                    .withTasks(list(flow.getTasks()).with(task))
                    .build();
            return this;
        }

        public FlowAdvanceOperation completeTask(RiverTaskCompleted completedTask) {
            logger.debug("processing completed task output for flow = {} task = {}", flow.getId(), completedTask.getTaskId());
            RiverPlan plan = flow.getPlan();
            Step step = plan.getStepByTaskId(completedTask.getTask().getTaskId());

            logger.debug("merge flow data with task output");
            List<RiverTask> taskListAfterTaskCompletion = flow.getTasks()
                    .stream().filter((task) -> !equal(task.getId(), completedTask.getTaskId()))
                    .collect(toList());
            flow = RiverFlowImpl.copyOf(flow)
                    .withTasks(taskListAfterTaskCompletion)
                    .withData(mergeData(flow.getData(), completedTask.getLocalVariables()))
                    .build();

            logger.debug("calculate next steps to activate for completed task = {}", completedTask);
            List<Step> stepsThatMayActivate = step.getOutgoingStepTransitionIds(completedTask).stream().map(plan::getStepTransitionById).map(StepTransition::getTargetStepId).distinct().map(plan::getStepById).collect(toList());

            logger.debug("processing candidate steps = {}", stepsThatMayActivate);
            List<Step> stepsActivatedWithAnyIncomingHandler = stepsThatMayActivate.stream().filter(s -> s.getIncomingHandler().equals(ACTIVATE_WHEN_ANY_INCOMING_STEP_HAVE_COMPLETED)).collect(toList());
            stepsActivatedWithAnyIncomingHandler.forEach(s -> {
                logger.trace("step = {} will activate", s);
            });

            Set<String> activeSteps = (Set) set().accept((s) -> {
                flow.getTaskIds().stream().map(plan::getStepByTaskId).map(Step::getId).forEach(s::add);
                stepQueue.stream().map(Step::getId).forEach(s::add);
                stepsActivatedWithAnyIncomingHandler.stream().map(Step::getId).forEach(s::add);
            });

            List<Step> stepsActivatedWithAllIncomingHandler = stepsThatMayActivate.stream().filter(s -> s.getIncomingHandler().equals(ACTIVATE_WHEN_ALL_INCOMING_STEPS_HAVE_COMPLETED)).filter(s -> {
                logger.debug("check if all incoming steps have completed for step = {}", s);
                boolean shouldActivate = !getAllParallelStepIdsConvergingOnThisStep(plan, s.getId()).stream().anyMatch(activeSteps::contains);
                logger.debug("check if all incoming steps have completed for step = {} result = {}", s, shouldActivate);
                return shouldActivate;
            }).collect(toList());

            List<Step> stepsToActivate = list(stepsActivatedWithAnyIncomingHandler).with(stepsActivatedWithAllIncomingHandler);

            logger.debug("completed processing of task output for flow = {} task = {} ", completedTask.getTask(), flow);
            stepQueue.addAll(stepsToActivate);
            return this;
        }

        private Map<String, Object> mergeData(Map<String, Object> old, Map<String, Object> newData) {
            return map(old).with(newData);
        }

    }

}
