/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.utils;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptySet;
import java.util.Queue;
import java.util.Set;
import static org.cmdbuild.utils.lang.CmCollectionUtils.queue;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTaskType;
import org.cmdbuild.workflow.river.engine.core.Step;
import org.cmdbuild.workflow.river.engine.core.StepTransition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanToDotGraphPlotter {

    public static String planToDotGraph(RiverPlan plan) {
        return planToDotGraph(plan, emptySet());
    }

    public static String planToDotGraph(RiverPlan plan, Set<String> activeTasksOrSteps) {
        return new Plotter().plot(plan, activeTasksOrSteps);
    }

    public static String planToSimplifiedDotGraph(RiverPlan plan, Set<String> activeTasksOrSteps) {
        return new SimplifiedPlotter().plotSimplifiedGraph(plan, activeTasksOrSteps);
    }

    private static class Plotter {

        private final StringBuilder stringBuilder = new StringBuilder();

        private String plot(RiverPlan plan, Set<String> activeTasksOrSteps) {
            stringBuilder.append("digraph \"")
                    .append(plan.getName())
                    .append("\" {\n\n");

            plan.getSteps().forEach((step) -> {
                boolean active = activeTasksOrSteps.contains(step.getId()) || activeTasksOrSteps.contains(step.getTask().getId());
                stringBuilder.append(format("\t\"%s\" [ shape = \"none\", label = <<table border=\"0\" cellspacing=\"0\"><tr><td border=\"1\">%s%s</td><td border=\"1\" bgcolor=\"%s\">%s</td></tr></table>>%s ];\n",
                        step.getId(),
                        step.getId(),
                        step.getTask().isUser() ? format("<br />task: %s", step.getTask().getId()) : "",
                        firstNonNull(map(RiverTaskType.SCRIPT_BATCH, "gold", RiverTaskType.SCRIPT_INLINE, "gold", RiverTaskType.USER, "palegreen").get(step.getTask().getTaskType()), "white"),
                        step.getTask().getTaskType().name(),
                        active ? ", fillcolor = \"greenyellow\", style = \"filled\"" : ""));
            });

            plan.getTransitions().forEach((transition) -> {
                stringBuilder.append("\t\"")
                        .append(transition.getSourceStepId())
                        .append("\" -> \"")
                        .append(transition.getTargetStepId())
                        .append(format("\" [ label = \"%s\", color = \"dimgray\", fontcolor = \"dimgray\" ];\n", transition.getStepTransitionId()));
            });

            stringBuilder.append("}\n");
            return stringBuilder.toString();
        }
    }

    private static class SimplifiedPlotter extends Plotter {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final StringBuilder stringBuilder = new StringBuilder();

        private String plotSimplifiedGraph(RiverPlan plan, Set<String> activeTasksOrSteps) {
            stringBuilder.append("digraph \"")
                    .append(plan.getName())
                    .append("\" {\n\n\trankdir = \"LR\";\n\n");

            plan.getSteps().stream().filter((s) -> s.getTask().isUser()).forEach((step) -> {
                boolean active = activeTasksOrSteps.contains(step.getId()) || activeTasksOrSteps.contains(step.getTask().getId());
                stringBuilder.append(format("\t\"%s\" [ shape = \"record\", label = \"%s\"%s ];\n",
                        step.getId(),
                        step.getId(),
                        active ? ", fillcolor = \"greenyellow\", style = \"filled\"" : ""));
            });

            Set<String> allEndSteps = set();

            plan.getSteps().stream().filter((s) -> s.getTask().isUser()).forEach((step) -> {

                Set<String> targetStepIds = getTargetUserSteps(plan, step);
                Set<String> endSteps = getEndSteps(plan, step);
                allEndSteps.addAll(endSteps);

                targetStepIds.forEach((targetStepId) -> {
                    stringBuilder.append("\t\"")
                            .append(step.getId())
                            .append("\" -> \"")
                            .append(targetStepId)
                            .append("\" [ color = \"dimgray\" ];\n");
                });

                endSteps.forEach(allEndSteps::add);
                endSteps.forEach((endStepId) -> {
                    stringBuilder.append("\t\"")
                            .append(step.getId())
                            .append("\" -> \"")
                            .append(endStepId)
                            .append("\" [ color = \"dimgray\" ];\n");
                });

            });

            allEndSteps.forEach((endStepId) -> {
                stringBuilder.append(format("\t\"%s\" [ label = \" \", shape = \"circle\" ];\n", endStepId));
            });

            stringBuilder.append("}\n");
            return stringBuilder.toString();
        }

        private Set<String> getTargetUserSteps(RiverPlan plan, Step step) {
            logger.trace("get target user steps from step = {}", step);
            Queue<StepTransition> transitionsToProcess = queue();
            Set<String> queuedTransitions = set();
            Set<String> targetStepIds = set();
            plan.getTransitionsBySourceStepId(step.getId()).forEach(t -> {
                logger.trace("add transition to queue = {}", t);
                transitionsToProcess.add(t);
                queuedTransitions.add(t.getStepTransitionId());
            });
            while (!transitionsToProcess.isEmpty()) {
                StepTransition thisTransition = transitionsToProcess.poll();
                Step targetStep = thisTransition.getTargetStep();
                if (targetStep.getTask().isUser()) {
                    logger.trace("found target step = {}", targetStep);
                    targetStepIds.add(targetStep.getId());
                } else {
                    plan.getTransitionsBySourceStepId(targetStep.getId()).stream()
                            .filter(t -> !queuedTransitions.contains(t.getStepTransitionId()))
                            .forEach(t -> {
                                logger.trace("add transition to queue = {}", t);
                                transitionsToProcess.add(t);
                                queuedTransitions.add(t.getStepTransitionId());
                            });
                }
            }
            logger.trace("found target steps = {} from step = {}", targetStepIds, step);
            return targetStepIds;
        }

        private Set<String> getEndSteps(RiverPlan plan, Step step) {
            logger.trace("get end steps from step = {}", step);
            Queue<StepTransition> transitionsToProcess = queue();
            Set<String> queuedTransitions = set();
            Set<String> targetStepIds = set();
            plan.getTransitionsBySourceStepId(step.getId()).forEach(t -> {
                logger.trace("add transition to queue = {}", t);
                transitionsToProcess.add(t);
                queuedTransitions.add(t.getStepTransitionId());
            });
            while (!transitionsToProcess.isEmpty()) {
                StepTransition thisTransition = transitionsToProcess.poll();
                Step targetStep = thisTransition.getTargetStep();
                if (!targetStep.getTask().isUser()) {
                    Collection<StepTransition> transitions = plan.getTransitionsBySourceStepId(targetStep.getId());
                    if (transitions.isEmpty()) {
                        logger.trace("found end step = {}", targetStep);
                        targetStepIds.add(targetStep.getId());
                    } else {
                        transitions.stream()
                                .filter(t -> !queuedTransitions.contains(t.getStepTransitionId()))
                                .forEach(t -> {
                                    logger.trace("add transition to queue = {}", t);
                                    transitionsToProcess.add(t);
                                    queuedTransitions.add(t.getStepTransitionId());
                                });
                    }

                }
            }
            logger.trace("found end steps = {} from step = {}", targetStepIds, step);
            return targetStepIds;
        }

    }

}
