/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dashboard;

import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;

import jakarta.annotation.Nullable;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;

public interface DashboardData extends PrivilegeSubjectWithInfo {

    String getCode();

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
        return privilegeId(PS_DASHBOARD, getId());
    }

}
