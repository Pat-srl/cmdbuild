/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.CMEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.ClassMetadata;
import org.cmdbuild.dao.entrytype.ClassPermission;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.PermissionScope;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.model.Process;

public class ProcessImpl implements Process {

    private final Classe inner;
    private final String planId;
    private final Map<String, TaskDefinition> entryTasks;
    private final Map<String, TaskDefinition> tasksById;

    private ProcessImpl(ProcessImplBuilder builder) {
        this.inner = checkNotNull(builder.inner);
        checkArgument(inner.isProcess(), "cannot create process wrapper: this class = %s is not a process", builder.inner);
        if (!isBlank(builder.planId)) {
            this.planId = builder.planId;
            this.entryTasks = checkNotNull(builder.entryTasks);
            this.tasksById = checkNotNull(builder.tasksById);
        } else {
            this.planId = null;
            this.entryTasks = this.tasksById = null;
        }
    }

    @Override
    public List<String> getAncestors() {
        return inner.getAncestors();
    }

    @Override
    public Map<PermissionScope, Set<ClassPermission>> getPermissionsMap() {
        return inner.getPermissionsMap();
    }

    @Override
    public Map<String, Object> getOtherPermissions() {
        return inner.getOtherPermissions();
    }

    @Override
    public String getName() {
        return inner.getName();
    }

    @Override
    public ClassMetadata getMetadata() {
        return inner.getMetadata();
    }

    @Override
    public Map<String, Attribute> getAllAttributesAsMap() {
        return inner.getAllAttributesAsMap();
    }

    @Override
    public Long getId() {
        return inner.getId();
    }

    @Override
    @Nullable
    public String getPlanIdOrNull() {
        return planId;
    }

    @Override
    public void accept(CMEntryTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Map<String, TaskDefinition> getEntryTasksByGroup() {
        checkHasPlan();
        return checkNotNull(entryTasks);
    }

    @Override
    public Map<String, TaskDefinition> getTasksById() {
        checkHasPlan();
        return checkNotNull(tasksById);
    }

    private void checkHasPlan() {
        checkNotBlank(planId, "CM: no process definition available for this process = %s", getName());
    }

    @Override
    public String toString() {
        return "ProcessImpl{" + "classId=" + getName() + ", provider=" + firstNonNull(getProviderOrNull(), "null (use default)") + ", planId=" + planId + '}';
    }

    public static ProcessImplBuilder builder() {
        return new ProcessImplBuilder();
    }

    public static ProcessImplBuilder copyOf(Process source) {
        return new ProcessImplBuilder()
                .withInner(source)
                .withPlanId(source.getPlanIdOrNull())
                .withEntryTasks(source.getEntryTasksByGroup())
                .withTasksById(source.getTasksById());
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getDmsPermissions() {
        return inner.getDmsPermissions();
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getGisPermissions() {
        return emptyMap();
    }

    public static class ProcessImplBuilder implements Builder<ProcessImpl, ProcessImplBuilder> {

        private Classe inner;
        private String planId;
        private Map<String, TaskDefinition> entryTasks, tasksById;

        public ProcessImplBuilder withInner(Classe inner) {
            this.inner = inner;
            return this;
        }

        public ProcessImplBuilder withEntryTasks(Map<String, TaskDefinition> entryTasks) {
            this.entryTasks = entryTasks;
            return this;
        }

        public ProcessImplBuilder withTasksById(Map<String, TaskDefinition> tasksById) {
            this.tasksById = tasksById;
            return this;
        }

        public ProcessImplBuilder withPlanId(String planId) {
            this.planId = planId;
            return this;
        }

        @Override
        public ProcessImpl build() {
            return new ProcessImpl(this);
        }

    }
}
