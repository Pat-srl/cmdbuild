/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth;

import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class DummyPrivilegeSubject implements PrivilegeSubjectWithInfo {

    private final String privilegeId;

    public DummyPrivilegeSubject(String privilegeId) {
        this.privilegeId = checkNotBlank(privilegeId);
    }

    @Override
    public String getPrivilegeId() {
        return privilegeId;
    }

    @Override
    public Long getId() {
        return 1l;
    }

    @Override
    public String getName() {
        return "dummy";
    }

    @Override
    public String getDescription() {
        return "dummy";
    }

}
