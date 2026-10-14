package org.cmdbuild.authorization;

import static com.google.common.base.Preconditions.checkArgument;
import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;

import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.data.UiComponentData;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_CUSTOMPAGE;

public class CustomPageAsPrivilegeSubject implements PrivilegeSubjectWithInfo {

    private final long id;
    private final String name, description;

    public CustomPageAsPrivilegeSubject(UiComponentInfo delegate) {
        this.id = delegate.getId();
        this.name = delegate.getName();
        this.description = delegate.getDescription();
        checkArgument(delegate.isOfType(UCT_CUSTOMPAGE));
    }

    public CustomPageAsPrivilegeSubject(UiComponentData delegate) {
        this.id = delegate.getId();
        this.name = delegate.getName();
        this.description = delegate.getDescription();
        checkArgument(delegate.isOfType(UCT_CUSTOMPAGE));
    }

    @Override
    public String getPrivilegeId() {
        return privilegeId(PS_CUSTOMPAGE, getId());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
