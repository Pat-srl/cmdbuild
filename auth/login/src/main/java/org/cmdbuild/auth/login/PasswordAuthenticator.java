package org.cmdbuild.auth.login;

public interface PasswordAuthenticator {

    boolean isEnabled();

    default PasswordCheckStatus verifyPassword(LoginUserIdentity login, String password) {
        return checkPassword(login, password).getStatus();
    }

    default PasswordCheckResult checkPassword(LoginUserIdentity login, String password) {
        return new PasswordCheckResultImpl(verifyPassword(login, password), login);
    }

}
