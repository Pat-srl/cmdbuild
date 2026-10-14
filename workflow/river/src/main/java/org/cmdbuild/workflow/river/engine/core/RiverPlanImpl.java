/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Multimap;
import static com.google.common.collect.Multimaps.index;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;

public class RiverPlanImpl implements RiverPlan {

    private final String planId, planName, xpdl;
    private final List<String> entryPointIds;
    private final Map<String, Step> stepsById, stepsByTaskId;
    private final Map<String, StepTransition> transitionsById;
    private final Multimap<String, StepTransition> transitionsByTargetStepId;
    private final Map<String, RiverVariableInfo<?>> globalVariablesAndDefaults;
    private final Map<String, String> attributes;

    private RiverPlanImpl(PlanImplBuilder builder) {
        this.xpdl = checkNotBlank(builder.xpdl, "xpdl is null");
        this.planId = checkNotBlank(builder.planId, "plan id is null");
        this.planName = checkNotBlank(builder.planName, "plan name is null");
        this.entryPointIds = ImmutableList.copyOf(set(checkNotNull(builder.entryPointIds, "plan entry points not set")));
        checkArgument(!entryPointIds.isEmpty(), "must have at leas one plan entry point");
        checkNotNull(builder.steps, "steps not set");
        stepsById = uniqueIndex(builder.steps, Step::getId);
//		stepsByTaskId = uniqueIndex(builder.steps.stream().filter((step) -> !step.getTask().isInline()).collect(toList()), (step) -> step.getTask().getId());
        stepsByTaskId = uniqueIndex(builder.steps, (step) -> step.getTask().getId());
        checkArgument(!stepsById.isEmpty(), "must have at least one step");
        checkNotNull(builder.transitions, "transitions not set");
        transitionsById = uniqueIndex(builder.transitions, StepTransition::getStepTransitionId);
        transitionsByTargetStepId = index(builder.transitions, StepTransition::getTargetStepId);
        globalVariablesAndDefaults = map(checkNotNull(builder.globalVariablesAndDefaults)).immutable();
        attributes = map(checkNotNull(builder.attributes)).immutable();
    }

    @Override
    public String toXpdl() {
        return xpdl;
    }

    @Override
    public String getName() {
        return planName;
    }

    @Override
    public String getId() {
        return planId;
    }

    @Override
    public List<String> getEntryPointStepIds() {
        return entryPointIds;
    }

    @Override
    public Step getStepById(String stepId) {
        return checkNotNull(stepsById.get(stepId), "step not found for id = %s", stepId);
    }

    @Override
    public StepTransition getStepTransitionById(String flagId) {
        return checkNotNull(transitionsById.get(flagId), "flag not found for id = %s", flagId);
    }

    @Override
    public Collection<StepTransition> getTransitions() {
        return transitionsById.values();
    }

    @Override
    public Step getStepByTaskId(String taskId) {
        return checkNotNull(stepsByTaskId.get(taskId), "step not found for task id = %s", taskId);
    }

    @Override
    public Collection<StepTransition> getTransitionsByTargetStepId(String stepId) {
        return transitionsByTargetStepId.get(stepId);
    }

    @Override
    public Map<String, RiverVariableInfo<?>> getGlobalVariables() {
        return globalVariablesAndDefaults;
    }

    @Override
    public Map<String, String> attributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "PlanImpl{" + "planId=" + planId + ", planName=" + planName + '}';
    }

    @Override
    public Collection<Step> getSteps() {
        return stepsById.values();
    }

    public static PlanImplBuilder builder() {
        return new PlanImplBuilder();
    }

    public static PlanImplBuilder copyOf(RiverPlan riverPlan) {
        return builder()
                .withAttributes(riverPlan.attributes())
                .withEntryPoints(riverPlan.getEntryPointStepIds())
                .withGlobals(riverPlan.getGlobalVariables())
                .withPlanId(riverPlan.getId())
                .withPlanName(riverPlan.getName())
                .withStepsAndFlags(riverPlan.getSteps(), riverPlan.getTransitions())
                .withXpdlContent(riverPlan.toXpdl());
    }

    public static class PlanImplBuilder implements Builder<RiverPlanImpl, PlanImplBuilder> {

        private String planId, planName, xpdl;
        private Collection<String> entryPointIds;
        private Collection<Step> steps;
        private Collection<StepTransition> transitions;
        private Map<String, RiverVariableInfo<?>> globalVariablesAndDefaults;
        private Map<String, String> attributes = emptyMap();

        public PlanImplBuilder withXpdlContent(String xpdl) {
            this.xpdl = xpdl;
            return this;
        }

        public PlanImplBuilder withPlanId(String planId) {
            this.planId = planId;
            return this;
        }

        public PlanImplBuilder withPlanName(String planName) {
            this.planName = planName;
            return this;
        }

        public PlanImplBuilder withEntryPoints(Collection<String> entryPointIds) {
            this.entryPointIds = entryPointIds;
            return this;
        }

        public PlanImplBuilder withAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public PlanImplBuilder withGlobals(Map<String, RiverVariableInfo<?>> globalVariablesAndDefaults) {
            this.globalVariablesAndDefaults = globalVariablesAndDefaults;
            return this;
        }

        public PlanImplBuilder withStepsAndFlags(Collection<Step> steps, Collection<StepTransition> flags) {
            this.steps = steps;
            this.transitions = flags;
            return this;
        }

        @Override
        public RiverPlanImpl build() {
            return new RiverPlanImpl(this);
        }

    }
}
