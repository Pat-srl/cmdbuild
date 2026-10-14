/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.homepage;

import jakarta.annotation.Nullable;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;

import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;

public interface HomepageData extends PrivilegeSubjectWithInfo {

    String getCode();

    String getGroupName();

    String getConfig();

    boolean isActive();

    @Nullable
    String getIconFont();

    @Override
    public default String getName() {
        return getCode();
    }

    @Override
    public default String getPrivilegeId() {
        return privilegeId(PS_HOMEPAGE, getId());
    }
}
