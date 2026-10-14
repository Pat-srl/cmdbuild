package org.cmdbuild.auth.user;

import org.cmdbuild.auth.userrole.UserGroup;
import java.io.Serializable;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import java.util.Set;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private UserType userType;
	private Set<UserGroup> groups = emptySet();

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public Set<UserGroup> getGroups() {
		return unmodifiableSet(groups);
	}

	public void setGroups(Set<UserGroup> groups) {
		this.groups = groups;
	}

}
