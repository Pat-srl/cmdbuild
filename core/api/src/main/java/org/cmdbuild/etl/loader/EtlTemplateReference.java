package org.cmdbuild.etl.loader;

import jakarta.annotation.Nullable;
import static org.cmdbuild.auth.grant.PrivilegeSubject.PS_ETLTEMPLATE;
import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;

public interface EtlTemplateReference extends PrivilegeSubjectWithInfo {

    String getCode();

    @Override
    String getDescription();

    boolean isActive();

    boolean isDynamic();

    @Nullable
    @Override
    @Deprecated
    default Long getId() {
        return null;//TODO
    }

    @Override
    public default String getName() {
        return getCode();
    }

    @Override
    default String getPrivilegeId() {
        return privilegeId(PS_ETLTEMPLATE, getCode());//TODO check this
    }
}
