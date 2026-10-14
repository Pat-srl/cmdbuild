/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import java.io.Serializable;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.auth.role.RolePrivilege;
import org.cmdbuild.auth.role.RolePrivilegeUtils;
import static org.cmdbuild.auth.role.RoleType.ADMIN;

public enum SystemPrivilegeContext implements UserPrivileges, Serializable {

	INSTANCE;
	private final Set<RolePrivilege> ALL_PERMISSIONS = RolePrivilegeUtils.processRolePermissions(ADMIN, emptyMap()).getRolePrivileges();

	public static final UserPrivileges systemPrivilegeContext() {
		return INSTANCE;
	}

	@Override
	public Map<String, UserPrivilegesForObject> getAllPrivileges() {
		return emptyMap();
	}

	@Override
	public boolean hasAdminAccess() {
		return true;
	}

	@Override
	public boolean hasServicePrivilege(GrantPrivilege requested, PrivilegeSubject privilegedObject) {
		return true;
	}

	@Override
	public boolean hasReadAccess(PrivilegeSubject privilegedObject) {
		return true;
	}

	@Override
	public boolean hasWriteAccess(PrivilegeSubject privilegedObject) {
		return true;
	}

	@Override
	public UserPrivilegesForObject getPrivilegesForObject(PrivilegeSubject object) {
		return NoUserPrivilegesForObject.INSTANCE;//TODO replace with "all privileges" const value
	}

	@Override
	public Set<RolePrivilege> getRolePrivileges() {
		return ALL_PERMISSIONS;
	}

}
