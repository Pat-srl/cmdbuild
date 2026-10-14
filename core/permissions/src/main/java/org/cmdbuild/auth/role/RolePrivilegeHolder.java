/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.role;

import java.util.Map;
import java.util.Set;
import static java.util.function.Function.identity;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_ACCESS;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;

public interface RolePrivilegeHolder {

    Set<RolePrivilege> getRolePrivileges();

    @Deprecated
    default boolean hasAdminAccess() {
        return hasPrivileges(RP_ADMIN_ACCESS);
    }

    default Map<RolePrivilege, Boolean> getRolePrivilegesAsMap() {
        return list(RolePrivilege.values()).stream().collect(toMap(identity(), this::hasPrivileges));
    }

    default boolean hasPrivileges(RolePrivilege privilege) {
        return getRolePrivileges().contains(privilege);
    }
}
