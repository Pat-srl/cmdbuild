/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Map;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.multimap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.RiverTaskType;

public class TaskImpl<T> implements RiverTask<T> {

    private final String planId, taskId;
    private final RiverTaskType taskType;
    private final T extraAttr;
    private final Multimap<String, String> attributes;

    public TaskImpl(SimpleTaskBuilder<T> builder) {
        this.planId = checkNotBlank(builder.planId);
        this.taskId = checkNotBlank(builder.taskId);
        this.taskType = checkNotNull(builder.taskType);
//		this.attributes = multimap(checkNotNull(builder.attributes)).immutable(); //TODO immutable multimap
        this.attributes = multimap(checkNotNull(builder.attributes));
        this.extraAttr = builder.extraAttr;
    }

    @Override
    public String getPlanId() {
        return planId;
    }

    @Override
    public String getId() {
        return taskId;
    }

    @Override
    public RiverTaskType getTaskType() {
        return taskType;
    }

    @Override
    public T getTaskTypeData() {
        return checkNotNull(extraAttr, "extra attr not available for this task");
    }

    @Override
    public Multimap<String, String> getExtendedAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "TaskImpl{" + "planId=" + planId + ", taskId=" + taskId + ", taskType=" + taskType + '}';
    }

    public static SimpleTaskBuilder<ScriptTaskExtraAttr> batch() {
        return new SimpleTaskBuilder(RiverTaskType.SCRIPT_BATCH);
    }

    public static SimpleTaskBuilder<ScriptTaskExtraAttr> inline() {
        return new SimpleTaskBuilder(RiverTaskType.SCRIPT_INLINE);
    }

    public static SimpleTaskBuilder<Void> user() {
        return new SimpleTaskBuilder(RiverTaskType.USER);
    }

    public static SimpleTaskBuilder<Void> noop() {
        return new SimpleTaskBuilder(RiverTaskType.NOP);
    }

    public static class SimpleTaskBuilder<T> implements Builder<TaskImpl<T>, SimpleTaskBuilder<T>> {

        private String planId, taskId;
        private final RiverTaskType taskType;
        private T extraAttr;
        private Multimap<String, String> attributes = ImmutableMultimap.of();

        public SimpleTaskBuilder(RiverTaskType taskType) {
            this.taskType = taskType;
        }

        public SimpleTaskBuilder<T> withPlanId(String planId) {
            this.planId = checkNotBlank(planId);
            return this;
        }

        public SimpleTaskBuilder<T> withAttributes(Map<String, String> attributes) {
            this.attributes = multimap(checkNotNull(attributes));
            return this;
        }

        public SimpleTaskBuilder<T> withAttributes(Multimap<String, String> attributes) {
            this.attributes = checkNotNull(attributes);
            return this;
        }

        public SimpleTaskBuilder<T> withTaskId(String taskId) {
            this.taskId = checkNotBlank(taskId);
            return this;
        }

        public SimpleTaskBuilder<T> withExtraAttr(T extraAttr) {
            this.extraAttr = checkNotNull(extraAttr);
            return this;
        }

        @Override
        public TaskImpl<T> build() {
            return new TaskImpl<>(this);
        }
    }

}
