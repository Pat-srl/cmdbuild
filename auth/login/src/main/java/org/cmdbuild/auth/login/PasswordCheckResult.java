package org.cmdbuild.auth.login;

public interface PasswordCheckResult {

    PasswordCheckStatus getStatus();

    LoginUserIdentity getLogin();

}
