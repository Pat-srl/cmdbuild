/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import static java.util.Collections.emptyList;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.widget.model.Widget;

public class TaskImpl implements Task {

    private final String taskId, flowId;
    private final String activityInstancePerformer;
    private final TaskDefinition taskDefinition;
    private final boolean isWritable;
    private final Supplier<List<Widget>> taskWidgetSupplier;
    private final Flow card;
    private final Object descriptionValue;
    private final Object activitySubsetId;

    private TaskImpl(TaskImplBuilder builder) {
        this.taskId = checkNotBlank(builder.taskId, "task id cannot be null");
        this.flowId = checkNotBlank(builder.flowId, "flow id cannot be null");
        this.activityInstancePerformer = checkNotBlank(builder.activityInstancePerformer, "task performer is blank for task %s", taskId);
        this.taskDefinition = checkNotNull(builder.taskDefinitionId);
        this.isWritable = checkNotNull(builder.isWritable);
        this.taskWidgetSupplier = checkNotNull(builder.taskWidgetSupplier);
        this.card = checkNotNull(builder.card);
        this.descriptionValue = builder.descriptionValue;
        this.activitySubsetId = builder.activitySubsetId;
    }

    public Flow getCard() {
        return card;
    }

    @Override
    public Flow getProcessInstance() {
        return card;
    }

    @Override
    public String getId() {
        return taskId;
    }

    @Override
    public String getPerformerName() {
        return activityInstancePerformer;
    }

    @Override
    public TaskDefinition getDefinition() {
        return taskDefinition;
    }

    public Supplier<List<Widget>> getTaskWidgetSupplier() {
        return taskWidgetSupplier;
    }

    @Override
    public String getFlowId() {
        return flowId;
    }

    @Override
    public List<Widget> getWidgets() {
        return getTaskWidgetSupplier().get();
    }

    @Override
    public boolean isWritable() {
        return isWritable;
    }

    @Override
    @Nullable
    public Object getDescriptionValue() {
        return descriptionValue;
    }

    @Override
    @Nullable
    public Object getActivitySubsetId() {
        return activitySubsetId;
    }

    @Override
    public String toString() {
        return "Task{" + "taskId=" + taskId + ", definitionId=" + getDefinition().getId() + ", flow=" + card + '}';
    }

    public static TaskImplBuilder builder() {
        return new TaskImplBuilder();
    }

    public static TaskImplBuilder copyOf(TaskImpl source) {
        return new TaskImplBuilder()
                .withCard(source.getCard())
                .withTaskId(source.getId())
                .withFlowId(source.getFlowId())
                .withTaskPerformer(source.getPerformerName())
                .withTaskDefinition(source.getDefinition())
                .isWritable(source.isWritable())
                .withTaskWidgetSupplier(source.getTaskWidgetSupplier())
                .withDescriptionValue(source.getDescriptionValue())
                .withActivitySubsetId(source.getActivitySubsetId());
    }

    public static class TaskImplBuilder implements Builder<TaskImpl, TaskImplBuilder> {

        private String taskId, flowId;
        private String activityInstancePerformer;
        private TaskDefinition taskDefinitionId;
        private Boolean isWritable = true;
        private Supplier<List<Widget>> taskWidgetSupplier = () -> emptyList();
        private Flow card;
        private Object descriptionValue;
        private Object activitySubsetId;

        public TaskImplBuilder withCard(Flow card) {
            this.card = card;
            return this;
        }

        public TaskImplBuilder withTaskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public TaskImplBuilder withFlowId(String flowId) {
            this.flowId = flowId;
            return this;
        }

        public TaskImplBuilder withTaskPerformer(String activityInstancePerformer) {
            this.activityInstancePerformer = activityInstancePerformer;
            return this;
        }

        public TaskImplBuilder withTaskDefinition(TaskDefinition taskDefinitionId) {
            this.taskDefinitionId = taskDefinitionId;
            return this;
        }

        public TaskImplBuilder withDescriptionValue(Object descriptionValue) {
            this.descriptionValue = descriptionValue;
            return this;
        }

        public TaskImplBuilder withActivitySubsetId(Object activitySubsetId) {
            this.activitySubsetId = activitySubsetId;
            return this;
        }

        public TaskImplBuilder isWritable(Boolean isAdvanceable) {
            this.isWritable = isAdvanceable;
            return this;
        }

        public TaskImplBuilder withTaskWidgetSupplier(Supplier<List<Widget>> taskWidgetSupplier) {
            this.taskWidgetSupplier = taskWidgetSupplier;
            return this;
        }

        @Override
        public TaskImpl build() {
            return new TaskImpl(this);
        }

    }
}
