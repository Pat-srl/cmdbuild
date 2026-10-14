package org.cmdbuild.workflow.model;

import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class TaskMetadata {

    private final String name;
    private final String value;

    public TaskMetadata(String name, String value) {
        this.name = checkNotBlank(name, "name cannot be empty");
        this.value = value;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getValue() {
        return value;
    }

}
