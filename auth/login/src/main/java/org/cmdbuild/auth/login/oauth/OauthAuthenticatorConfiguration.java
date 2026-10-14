/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.oauth;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.auth.login.LoginModuleConfiguration;

public interface OauthAuthenticatorConfiguration extends LoginModuleConfiguration {

    final String OAUTH_LOGIN_MODULE_TYPE = "oauth";

    OauthProtocol getOauthProtocol();

    String getOauthResourceId();

    String getOauthClientId();

    String getOauthClientSecret();

    String getOauthTenantId();

    String getOauthServiceUrl();

    String getOauthRedirectUrl();

    boolean isOauthLogoutEnabled();

    String getOauthLogoutRedirectUrl();

    String getOauthScope();

    String getOauthLoginAttr();

    String getOauthLoginType();

    default boolean isProtocol(OauthProtocol protocol) {
        return equal(getOauthProtocol(), protocol);
    }

}
