package org.cmdbuild.workflow.river.engine;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.river.engine.core.Step;
import org.cmdbuild.workflow.river.engine.core.StepTransition;

public interface RiverPlan {

    String getId();

    String getName();

    String toXpdl();

    List<String> getEntryPointStepIds();

    Step getStepById(String stepId);

    StepTransition getStepTransitionById(String flagId);

    Collection<StepTransition> getTransitions();

    Step getStepByTaskId(String taskId);

    Collection<StepTransition> getTransitionsByTargetStepId(String stepId);

    Map<String, RiverVariableInfo<?>> getGlobalVariables();

    Map<String, String> attributes();

    Collection<Step> getSteps();

    default Collection<StepTransition> getTransitionsBySourceStepId(String stepId) {
        checkNotBlank(stepId);
        return getTransitions().stream().filter((t) -> equal(t.getSourceStepId(), stepId)).collect(toSet());
    }

    default RiverTask getTask(String taskId) {
        return getStepByTaskId(taskId).getTask();
    }

    default String getAttr(String key) {
        return checkNotNull(getAttOrNull(key), "plan attr not found for key = %s", key);
    }

    @Nullable
    default String getAttOrNull(String key) {
        return attributes().get(key);
    }

    default List<Step> getEntryPointSteps() {
        return getEntryPointStepIds().stream().map(this::getStepById).collect(toList());
    }

    default List<RiverTask> getEntryPointTasks() {
        return getEntryPointStepIds().stream().map(this::getStepById).map(Step::getTask).collect(toList());
    }

    default Step getNextStepByTransitionId(String transitionId) {
        return getStepById(getStepTransitionById(transitionId).getTargetStepId());
    }

    default String getEntryPointIdByTaskId(String taskId) {
        Optional<String> optional = getEntryPointStepIds().stream().map(this::getStepById).filter((s) -> equal(s.getTask().getId(), taskId)).map(Step::getId).collect(toOptional());
        checkArgument(optional.isPresent(), "unable to find entry point for taskId = %s in plan = %s", taskId, this);
        return optional.get();
    }

    @Nullable
    default Object getDefaultValueOrNull(String key) {
        RiverVariableInfo variableInfo = getGlobalVariables().get(key);
        if (variableInfo != null) {
            return variableInfo.getDefaultValue().orElse(null);
        } else {
            return null;
        }
    }

    default Map<String, Object> getDefaultValues() {
        return transformValues(getGlobalVariables(), (v) -> v.getDefaultValue().orElse(null));
    }

    default boolean hasGlobalVariable(String k) {
        return getGlobalVariables().containsKey(k);
    }

}
