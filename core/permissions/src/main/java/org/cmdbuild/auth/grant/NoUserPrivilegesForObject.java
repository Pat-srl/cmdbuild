/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.grant;

import static java.util.Collections.emptyList;
import java.util.List;
import org.cmdbuild.auth.grant.GroupOfNoPrivileges;
import org.cmdbuild.auth.grant.GroupOfPrivileges;

public enum NoUserPrivilegesForObject implements UserPrivilegesForObject {

	INSTANCE;

	@Override
	public GroupOfPrivileges getMinPrivilegesForAllRecords() {
		return GroupOfNoPrivileges.INSTANCE;
	}

	@Override
	public List<GroupOfPrivileges> getPrivilegeGroupsWithFilter() {
		return emptyList();
	}

	@Override
	public GroupOfPrivileges getMaxPrivilegesForSomeRecords() {
		return GroupOfNoPrivileges.INSTANCE;
	}

	@Override
	public boolean hasPrivilegesWithFilter() {
		return false;
	}

	@Override
	public String toString() {
		return "NoUserPrivilegesForObject{}";
	}

}
