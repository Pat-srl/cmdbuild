/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.utils;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import java.util.Collection;
import java.util.Set;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentSet;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.core.Step;
import org.cmdbuild.workflow.river.engine.core.StepTransition;

public class FlowGraphUtils {

    public static Set<String> getAllParallelStepIdsConvergingOnThisStep(RiverPlan plan, String stepId) {
        return getAllParallelStepsConvergingOnThisStep(plan, plan.getStepById(stepId)).stream().map(Step::getId).collect(toImmutableSet());
    }

    public static Collection<Step> getAllParallelStepsConvergingOnThisStep(RiverPlan plan, Step convergingStep) {
        //TODO this algo must be tested
        //TODO: replace with better algorithm (explore parent nodes until we find the lowest common ancestor for all immediate ancestors of 'convergingStep'
//        List<Step> steps = list();
//        Queue<Step> stepsToProcess = queue(plan.getEntryPointSteps());
//        Set<Step> processedSteps = set();
//        while (!stepsToProcess.isEmpty()) {
//            Step stepToProcess = stepsToProcess.poll();
//            processedSteps.add(stepToProcess);
//            if (!equal(convergingStep, stepToProcess)) {
//                steps.add(stepToProcess);
//                stepToProcess.getOutgoingHandler().getAllOutgoingStepTransitionIds().stream().map(plan::getStepTransitionById).map(StepTransition::getTargetStep)
//                        .filter(not(stepsToProcess::contains))
//                        .filter(not(processedSteps::contains))
//                        .forEach(stepsToProcess::add);
//            }
//        }
//        return steps;
        return plan.getSteps().stream().filter(s -> getAllStepsReacheableFromThisStep(plan, s.getId()).contains(convergingStep.getId())).collect(toImmutableList());
    }

    public static Set<String> getAllStepsReacheableFromThisStep(RiverPlan plan, String stepId) {
        FluentSet<String> reached = set(), queue = set();
        plan.getTransitionsBySourceStepId(stepId).stream().map(StepTransition::getTargetStepId).forEach(queue::add);
        while (!queue.isEmpty()) {
            String nextStepId = queue.iterator().next();
            reached.add(nextStepId);
            queue.remove(nextStepId);
            plan.getTransitionsBySourceStepId(nextStepId).stream().map(StepTransition::getTargetStepId).filter(not(reached::contains)).forEach(queue::add);
        }
        return reached.without(stepId);
    }
}
