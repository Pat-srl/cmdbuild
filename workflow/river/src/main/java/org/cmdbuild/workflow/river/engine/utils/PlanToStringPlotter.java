/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.utils;

import com.google.common.collect.Ordering;
import static java.lang.String.format;
import static org.cmdbuild.utils.lang.CmStringUtils.multilineWithOffset;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.core.Step;
import org.cmdbuild.workflow.river.engine.task.ScriptTaskExtraAttr;

public class PlanToStringPlotter {

    public static String planToString(RiverPlan plan) {
        return new Plotter().plot(plan);
    }

    private static class Plotter {

        private final StringBuilder stringBuilder = new StringBuilder();

        private String plot(RiverPlan plan) {
            stringBuilder.append(format("PLAN = %s\n\n", plan.getId()));

            plan.getSteps().stream().sorted(Ordering.natural().onResultOf(Step::getId)).forEach((step) -> {
                stringBuilder.append(format("%s\ntype = %s\nactivate = %s\n",
                        step.getId(),
                        step.getTask().getTaskType().name().toLowerCase(),
                        step.getIncomingHandler().name().toLowerCase()
                ));

                if (step.getTask().isBatch() || step.getTask().isInline()) {
                    ScriptTaskExtraAttr scriptAttr = (ScriptTaskExtraAttr) step.getTask().getTaskTypeData();
                    stringBuilder.append(format(" --- --- ---\n%s\n --- --- ---\n", multilineWithOffset(scriptAttr.getScript(), 8)));
                }

                plan.getTransitionsBySourceStepId(step.getId()).forEach(t -> {
                    stringBuilder.append(format("transition -> %s\n", t.getTargetStepId()));
                });

                stringBuilder.append("\n\n");
            });

            return stringBuilder.toString();
        }
    }

}
