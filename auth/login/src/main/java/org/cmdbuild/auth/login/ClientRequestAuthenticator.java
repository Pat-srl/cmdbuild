package org.cmdbuild.auth.login;

import jakarta.annotation.Nullable;

public interface ClientRequestAuthenticator {

    boolean isEnabled();

    @Nullable
    RequestAuthenticatorResponse<LoginUserIdentity> authenticate(AuthRequestInfo request);

    @Nullable
    default RequestAuthenticatorResponse<Void> logout(@Nullable Object request) {
        return null;
    }
}
