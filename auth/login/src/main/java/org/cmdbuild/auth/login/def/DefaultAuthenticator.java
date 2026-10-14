/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.def;

import org.cmdbuild.auth.login.AuthRequestInfo;
import org.cmdbuild.auth.login.LoginModuleConfiguration;
import static org.cmdbuild.auth.login.LoginModuleConfiguration.DEFAULT_LOGIN_MODULE_TYPE;
import org.cmdbuild.auth.login.RequestAuthenticatorResponse;
import org.springframework.stereotype.Component;
import org.cmdbuild.auth.login.LoginModuleClientRequestAuthenticator;

@Component
public class DefaultAuthenticator implements LoginModuleClientRequestAuthenticator {

    @Override
    public String getType() {
        return DEFAULT_LOGIN_MODULE_TYPE;
    }

    @Override
    public RequestAuthenticatorResponse handleAuthRequest(AuthRequestInfo request, LoginModuleConfiguration loginModuleConfiguration) {
        return null;
    }

    @Override
    public RequestAuthenticatorResponse handleAuthResponse(AuthRequestInfo request, LoginModuleConfiguration loginModuleConfiguration) {
        return null;
    }

}
