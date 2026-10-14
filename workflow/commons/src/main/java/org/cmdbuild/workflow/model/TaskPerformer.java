package org.cmdbuild.workflow.model;

import java.util.Objects;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

public class TaskPerformer {

    private final String value;
    private final TaskPerformerType type;

    private TaskPerformer(TaskPerformerType type, String value) {
        this.value = value;
        this.type = type;
    }

    public TaskPerformerType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean isRole(String name) {
        return (type == TaskPerformerType.ROLE) && (value.equals(name));
    }

    public boolean isExpression() {
        return (type == TaskPerformerType.EXPRESSION);
    }

    public boolean isAdmin() {
        return (type == TaskPerformerType.ADMIN);
    }

    public static TaskPerformer newRolePerformer(String name) {
        checkNotNull(name);
        return new TaskPerformer(TaskPerformerType.ROLE, name);
    }

    public static TaskPerformer newExpressionPerformer(String expression) {
        checkNotNull(expression);
        return new TaskPerformer(TaskPerformerType.EXPRESSION, expression);
    }

    public static TaskPerformer newAdminPerformer() {
        return new TaskPerformer(TaskPerformerType.ADMIN, null);
    }

    public static TaskPerformer newUnknownPerformer() {
        return new TaskPerformer(TaskPerformerType.UNKNOWN, null);
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof TaskPerformer)) {
            return false;
        }
        TaskPerformer activityPerformer = TaskPerformer.class.cast(object);
        return (type.equals(activityPerformer.type) && (value.equals(activityPerformer.value)));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.value);
        hash = 23 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public String toString() {
        return "TaskPerformer{" + "value=" + value + ", type=" + type + '}';
    }

    public static enum TaskPerformerType {
        ROLE, EXPRESSION, ADMIN, UNKNOWN // fake performer for our ugly stuff
    }

}
