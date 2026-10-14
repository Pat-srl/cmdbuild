package org.cmdbuild.auth.login;

import static com.google.common.base.Preconditions.checkNotNull;

public class PasswordCheckResultImpl implements PasswordCheckResult {

    final PasswordCheckStatus status;
    final LoginUserIdentity user;

    public PasswordCheckResultImpl(PasswordCheckStatus status, LoginUserIdentity user) {
        this.status = checkNotNull(status);
        this.user = checkNotNull(user);
    }

    @Override
    public PasswordCheckStatus getStatus() {
        return status;
    }

    @Override
    public LoginUserIdentity getLogin() {
        return user;
    }

    @Override
    public String toString() {
        return "PasswordCheckResult{" + "status=" + status + ", user=" + user + '}';
    }

}
