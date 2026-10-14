/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.user;

import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.grant.GrantPrivilege;
import org.cmdbuild.auth.grant.PrivilegeSubject;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import org.cmdbuild.auth.role.RolePrivilege;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface UserDaoHelperService extends OperationUserSupplier, UserPrivileges, UserTenantContext {

    @Override
    default boolean hasServicePrivilege(GrantPrivilege privilege, PrivilegeSubject subject) {
        return getUser().hasServicePrivilege(privilege, subject);
    }

    @Override
    default Map<String, UserPrivilegesForObject> getAllPrivileges() {
        return getUser().getAllPrivileges();
    }

    @Override
    default UserPrivilegesForObject getPrivilegesForObject(PrivilegeSubject object) {
        return getUser().getPrivilegesForObject(object);
    }

    @Override
    default Set<RolePrivilege> getRolePrivileges() {
        return getUser().getRolePrivileges();
    }

    @Override
    default Set<Long> getActiveTenantIds() {
        return getUser().getUserTenantContext().getActiveTenantIds();
    }

    @Override
    default boolean ignoreTenantPolicies() {
        return getUser().getUserTenantContext().ignoreTenantPolicies();
    }

    @Override
    @Nullable
    default Long getDefaultTenantId() {
        return getUser().getUserTenantContext().getDefaultTenantId();
    }

    default String getUserPrivilegesChecksum() {
        return list(getUsername()).with(getUser().getActiveGroupNames()).collect(joining("|"));
    }

    default String getRolePrivilegesChecksum() {
        return getUser().getActiveGroupNames().stream().collect(joining("|"));
    }
}
