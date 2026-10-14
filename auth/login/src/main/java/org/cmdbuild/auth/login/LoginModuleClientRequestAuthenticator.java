package org.cmdbuild.auth.login;

import jakarta.annotation.Nullable;

public interface LoginModuleClientRequestAuthenticator<C extends LoginModuleConfiguration> {

    String getType();

    @Nullable
    RequestAuthenticatorResponse<LoginUserIdentity> handleAuthRequest(AuthRequestInfo request, C loginModuleConfiguration);

    @Nullable
    RequestAuthenticatorResponse<LoginUserIdentity> handleAuthResponse(AuthRequestInfo request, C loginModuleConfiguration);

    @Nullable
    default RequestAuthenticatorResponse<Void> logout(@Nullable Object request, C loginModuleConfiguration) {
        return null;
    }
}
