/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import java.io.Serializable;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.auth.grant.GrantPrivilege;
import org.cmdbuild.auth.grant.NoUserPrivilegesForObject;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.role.RolePrivilege;
import org.cmdbuild.auth.grant.PrivilegeSubject;

/**
 *
 * this may be serialized in session
 */
public class NullPrivilegeContext implements UserPrivileges, Serializable {

	/**
	 * Use factory method.
	 */
	private NullPrivilegeContext() {
	}

	@Override
	public boolean hasAdminAccess() {
		return false;
	}

	@Override
	public boolean hasServicePrivilege(final GrantPrivilege requested, final PrivilegeSubject privilegedObject) {
		return false;
	}

	@Override
	public boolean hasReadAccess(final PrivilegeSubject privilegedObject) {
		return false;
	}

	@Override
	public boolean hasWriteAccess(final PrivilegeSubject privilegedObject) {
		return false;
	}

//	@Override
//	public PrivilegedObjectMetadata getMetadata(final CMPrivilegedObject privilegedObject) {
//		return null;
//	}
//
//	@Override
//	public List<GroupOfPrivileges> getAllPrivilegesContainers(CMPrivilegedObject privilegedObject) {
//		return Collections.emptyList();
//	}
//
//	@Override
//	public List<UserPrivileges> getPrivilegeContextList() {
//		return Collections.emptyList();
//	}
	private static final UserPrivileges NULL_PRIVILEGE_CONTEXT = new NullPrivilegeContext();

	public static final UserPrivileges nullPrivilegeContext() {
		return NULL_PRIVILEGE_CONTEXT;
	}

	@Override
	public UserPrivilegesForObject getPrivilegesForObject(PrivilegeSubject object) {
		return NoUserPrivilegesForObject.INSTANCE;
	}

	@Override
	public Set<RolePrivilege> getRolePrivileges() {
		return emptySet();
	}

	@Override
	public Map<String, UserPrivilegesForObject> getAllPrivileges() {
		return emptyMap();
	}
}
