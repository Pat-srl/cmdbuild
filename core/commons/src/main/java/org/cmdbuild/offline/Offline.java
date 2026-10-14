/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.offline;

import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;

/**
 *
 * @author ataboga
 */
public interface Offline extends PrivilegeSubjectWithInfo {

    @Override
    Long getId();

    String getCode();

    @Override
    default String getName() {
        return getCode();
    }

    @Override
    String getDescription();

    String getMetadata();

    Boolean isActive();

    @Override
    default String getPrivilegeId() {
        return privilegeId(PS_OFFLINE, getCode());
    }
}
