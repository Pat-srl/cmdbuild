package org.cmdbuild.auth.login;

import org.cmdbuild.auth.user.UserType;

public interface AuthenticationStore {

	UserType getType();

	void setType(UserType type);

	LoginUserIdentity getLogin();

	void setLogin(LoginUserIdentity login);

}
