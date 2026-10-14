/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Objects;
import org.cmdbuild.utils.lang.Builder;
import org.cmdbuild.widget.model.WidgetData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class TaskDefinitionImpl implements TaskDefinition {

    private final String id, description, instructions;
    private final List<TaskPerformer> performers;
    private final TaskPerformer firstNonAdminPerformer;
    private final List<TaskAttribute> variables;
    private final Iterable<TaskMetadata> metadata;
    private final List<WidgetData> widgets;

    private TaskDefinitionImpl(TaskDefinitionImplBuilder builder) {
        this.id = checkNotBlank(builder.id);
        this.description = nullToEmpty(builder.description);
        this.instructions = nullToEmpty(builder.instructions);
        this.performers = ImmutableList.copyOf(builder.performers);
        this.firstNonAdminPerformer = (builder.firstNonAdminPerformer);
        this.variables = ImmutableList.copyOf(builder.variables);
        this.metadata = checkNotNull(builder.metadata);
        this.widgets = ImmutableList.copyOf(builder.widgets);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getInstructions() {
        return instructions;
    }

    @Override
    public List<TaskPerformer> getPerformers() {
        return performers;
    }

    @Override
    public TaskPerformer getFirstNonAdminPerformer() {
        return firstNonAdminPerformer;
    }

    @Override
    public List<TaskAttribute> getVariables() {
        return variables;
    }

    @Override
    public Iterable<TaskMetadata> getMetadata() {
        return metadata;
    }

    @Override
    public List<WidgetData> getWidgets() {
        return widgets;
    }

    @Override
    public String toString() {
        return "TaskDefinitionImpl{" + "id=" + id + ", description=" + description + '}';
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && (obj instanceof TaskDefinition) && Objects.equals(this.id, ((TaskDefinition) obj).getId()));
    }

    public static TaskDefinitionImplBuilder builder() {
        return new TaskDefinitionImplBuilder();
    }

    public static TaskDefinitionImplBuilder copyOf(TaskDefinition source) {
        return new TaskDefinitionImplBuilder()
                .withId(source.getId())
                .withDescription(source.getDescription())
                .withInstructions(source.getInstructions())
                .withPerformers(source.getPerformers())
                .withFirstNonAdminPerformer(source.getFirstNonAdminPerformer())
                .withVariables(source.getVariables())
                .withMetadata(source.getMetadata())
                .withWidgets(source.getWidgets());
    }

    public static class TaskDefinitionImplBuilder implements Builder<TaskDefinitionImpl, TaskDefinitionImplBuilder> {

        private String id;
        private String description;
        private String instructions;
        private Collection<TaskPerformer> performers = emptyList();
        private TaskPerformer firstNonAdminPerformer;
        private final List<TaskAttribute> variables = list();
        private Iterable<TaskMetadata> metadata = emptyList();
        private List<WidgetData> widgets = emptyList();

        public TaskDefinitionImplBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public TaskDefinitionImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public TaskDefinitionImplBuilder withInstructions(String instructions) {
            this.instructions = instructions;
            return this;
        }

        public TaskDefinitionImplBuilder withPerformers(Collection<TaskPerformer> performers) {
            this.performers = performers;
            return this;
        }

        public TaskDefinitionImplBuilder withFirstNonAdminPerformer(TaskPerformer firstNonAdminPerformer) {
            this.firstNonAdminPerformer = firstNonAdminPerformer;
            return this;
        }

        public TaskDefinitionImplBuilder withVariables(List<TaskAttribute> variables) {
            this.variables.clear();
            this.variables.addAll(variables);
            return this;
        }

        public TaskDefinitionImplBuilder addAttribute(TaskAttribute attr) {
            this.variables.add(attr);
            return this;
        }

        public TaskDefinitionImplBuilder withMetadata(Iterable<TaskMetadata> metadata) {
            this.metadata = metadata;
            return this;
        }

        public TaskDefinitionImplBuilder withWidgets(List<WidgetData> widgets) {
            this.widgets = widgets;
            return this;
        }

        @Override
        public TaskDefinitionImpl build() {
            return new TaskDefinitionImpl(this);
        }

    }
}
