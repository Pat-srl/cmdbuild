package org.cmdbuild.workflow.model;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class TaskAttributeImpl implements TaskAttribute {

    private final String name;
    private final boolean writable, mandatory, action;

    private TaskAttributeImpl(TaskAttributeImplBuilder builder) {
        this.name = checkNotBlank(builder.name);
        this.writable = checkNotNull(builder.writable);
        this.mandatory = checkNotNull(builder.mandatory);
        this.action = checkNotNull(builder.action);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isWritable() {
        return writable;
    }

    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public boolean isAction() {
        return action;
    }

    public static TaskAttributeImplBuilder builder() {
        return new TaskAttributeImplBuilder();
    }

    public static TaskAttributeImplBuilder copyOf(TaskAttributeImpl source) {
        return new TaskAttributeImplBuilder()
                .withName(source.getName())
                .withWritable(source.isWritable())
                .withMandatory(source.isMandatory())
                .withAction(source.isAction());
    }

    @Override
    public String toString() {
        return "TaskAttribute{" + "name=" + name + ", writable=" + writable + ", mandatory=" + mandatory + ", action=" + action + '}';
    }

    public static class TaskAttributeImplBuilder implements Builder<TaskAttributeImpl, TaskAttributeImplBuilder> {

        private String name;
        private boolean writable = false;
        private boolean mandatory = false;
        private boolean action = false;

        public TaskAttributeImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public TaskAttributeImplBuilder withWritable(boolean writable) {
            this.writable = writable;
            return this;
        }

        public TaskAttributeImplBuilder withMandatory(boolean mandatory) {
            this.mandatory = mandatory;
            return this;
        }

        public TaskAttributeImplBuilder withAction(boolean action) {
            this.action = action;
            return this;
        }

        @Override
        public TaskAttributeImpl build() {
            return new TaskAttributeImpl(this);
        }

    }
}
