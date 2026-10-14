/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.role;

import com.google.common.collect.ImmutableSet;
import static java.util.Collections.singleton;
import java.util.Map;
import java.util.Set;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_ACCESS;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_ALL;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.auth.role.RolePrivilege.RP_BASE_ALL;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_ALL_READONLY;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_SYSCONFIG_MODIFY;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_USERS_MODIFY;
import static org.cmdbuild.auth.role.RolePrivilege.RP_SYSTEM_ACCESS;

public enum RoleType {
    ADMIN, ADMIN_LIMITED, ADMIN_READONLY, ADMIN_USERS, DEFAULT;

    public static Map<RoleType, Set<RolePrivilege>> ROLE_TYPE_MAPPING = CmMapUtils.<RoleType, Set<RolePrivilege>, Object>map(ADMIN, ImmutableSet.of(RP_BASE_ALL, RP_ADMIN_ALL),
            ADMIN_LIMITED, ImmutableSet.of(RP_BASE_ALL, RP_ADMIN_ACCESS, RP_SYSTEM_ACCESS, RP_ADMIN_SYSCONFIG_MODIFY),
            ADMIN_USERS, ImmutableSet.of(RP_BASE_ALL, RP_ADMIN_ACCESS, RP_ADMIN_USERS_MODIFY),
            ADMIN_READONLY, ImmutableSet.of(RP_BASE_ALL, RP_ADMIN_ALL_READONLY),
            DEFAULT, singleton(RP_BASE_ALL)
    ).immutable();
}
