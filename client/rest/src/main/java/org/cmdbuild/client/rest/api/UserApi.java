/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.grant.GrantMode;
import org.cmdbuild.auth.role.RoleInfo;
import org.cmdbuild.auth.role.RolePrivilege;
import org.cmdbuild.auth.user.LoginUserInfo;
import org.cmdbuild.client.rest.model.ClassData;
import static org.cmdbuild.dao.utils.CmFilterUtils.noopFilter;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface UserApi {

    LoginUserInfo createUser(String username, @Nullable String password, String... groups);

    RoleInfo createRole(String rolename, RolePrivilege... rolePrivileges);

    UserApi setUserPreferences(Map<String, String> preferences);

    UserApi setRolePrivilegesOnClass(String rolename, String classId, GrantMode mode, CmdbFilter filter);

    UserApi setRolePrivilegesOnProcess(String rolename, String processId, GrantMode mode);

    UserApi setRolePrivilegesOnView(String rolename, String viewId, GrantMode mode);

    default UserApi setRolePrivilegesOnClass(String rolename, String classId, GrantMode mode) {
        return setRolePrivilegesOnClass(rolename, classId, mode, noopFilter());
    }

    default UserApi setRolePrivilegesOnClass(RoleInfo role, ClassData classe, GrantMode mode) {
        return setRolePrivilegesOnClass(role.getName(), classe.getName(), mode);
    }

    default UserApi setUserPreferences(String... preferences) {
        return setUserPreferences(map((Object[]) preferences));
    }
}
