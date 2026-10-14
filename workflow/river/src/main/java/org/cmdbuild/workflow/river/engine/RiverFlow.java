package org.cmdbuild.workflow.river.engine;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;

public interface RiverFlow {

    List<RiverTask> getTasks();

    RiverFlowStatus getStatus();

    RiverPlan getPlan();

    String getId();

    Map<String, Object> getData();

    default Set<String> getTaskIds() {
        return getTasks().stream().map(RiverTask::getId).collect(toSet());
    }

    default boolean isCompleted() {
        return equal(getStatus(), RiverFlowStatus.COMPLETE);
    }

    default boolean isRunning() {
        return equal(getStatus(), RiverFlowStatus.RUNNING);
    }

    default boolean isSuspended() {
        return equal(getStatus(), RiverFlowStatus.SUSPENDED);
    }

    default String getPlanId() {
        return getPlan().getId();
    }

    default RiverTask getTaskById(String taskId) {
        return getTasks().stream().filter(t -> equal(t.getId(), taskId)).collect(onlyElement("task not found for id = %s", taskId));
    }

    default List<RiverTask> getUserTasks() {
        return getTasks().stream().filter(RiverTask::isUser).collect(toList());
    }

    default List<RiverTask> getBatchTasks() {
        return getTasks().stream().filter(RiverTask::isBatch).collect(toList());
    }

    default RiverTask getUserTaskById(String taskId) {
        RiverTask task = getTaskById(taskId);
        checkArgument(task.isUser(), "task = %s is not a user task", task);
        return task;
    }

    default int getTaskCount() {
        return getTasks().size();
    }

    default RiverTask getOnlyTask() {
        return getOnlyElement(getTasks());
    }

}
