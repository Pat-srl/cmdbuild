package org.cmdbuild.workflow.model;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;

import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface Process extends Classe {

    public static final String ADMIN_PERFORMER_AS_GROUP = "__ADMIN__";
    public static final String UNKNOWN_PERFORMER_AS_GROUP = "__UNKNOWN__";

    @Nullable
    String getPlanIdOrNull();

    Map<String, TaskDefinition> getTasksById();

    @Nullable
    default String getProviderOrNull() {
        return getMetadata().getFlowProviderOrNull();
    }

    default String getProviderOrDefault(String defaultProvider) {
        return firstNonNull(getProviderOrNull(), defaultProvider);
    }

    default String getPlanId() {
        return checkNotBlank(getPlanIdOrNull(), "no planId available for classe = %s", this);
    }

    default boolean hasPlan() {
        return getPlanIdOrNull() != null;
    }

    default boolean isRunnable() {
        return isActive() && hasPlan();
    }

    default TaskDefinition getTaskById(String taskId) {
        return checkNotNull(getTasksById().get(taskId), "task definition not found for taskId =< %s > within process = %s", taskId, this);
    }

    Map<String, TaskDefinition> getEntryTasksByGroup();

    default Collection<TaskDefinition> getAllEntryTasks() {
        return getEntryTasksByGroup().values().stream().map(TaskDefinition::getId).distinct().map(this::getTaskById).collect(toList());
    }

    @Nullable
    default TaskDefinition getEntryTaskByGroupOrNull(String group) {
        return getEntryTasksByGroup().get(group);
    }

    default TaskDefinition getEntryTaskByGroup(String group) {
        return checkNotNull(getEntryTaskByGroupOrNull(group), "entry task not found for group =< %s >", group);
    }

    @Override
    default boolean isWfUserStoppable() {
        return getMetadata().isWfUserStoppable();
    }

    @Nullable
    default String getFlowStatusLookup() {
        return getMetadata().getFlowStatusAttr();
    }

    @Nullable
    default Boolean isFlowSaveButtonEnabled() {
        return getMetadata().isFlowSaveButtonEnabled();
    }

    default boolean isUserAttribute(String key) {
        return hasAttribute(key) && getAttribute(key).isActive() && !getAttribute(key).hasNotServiceListPermission();
    }
}
