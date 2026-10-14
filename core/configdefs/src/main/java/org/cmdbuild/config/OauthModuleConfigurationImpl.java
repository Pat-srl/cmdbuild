/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import jakarta.annotation.Nullable;
import org.cmdbuild.auth.login.oauth.OauthAuthenticatorConfiguration;
import static org.cmdbuild.auth.login.oauth.OauthAuthenticatorConfiguration.OAUTH_LOGIN_MODULE_TYPE;
import org.cmdbuild.auth.login.oauth.OauthProtocol;
import org.cmdbuild.config.api.ConfigCategory;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(value = "org.cmdbuild.auth.module." + OAUTH_LOGIN_MODULE_TYPE, module = true)
public final class OauthModuleConfigurationImpl implements OauthAuthenticatorConfiguration {

    private final String code;

    @ConfigValue(key = "description", description = "", defaultValue = "Oauth2", category = ConfigCategory.CC_ENV)
    private String description;

    @ConfigValue(key = "handlerScript", description = "modify/create/check oauth login user or role before the effective login, must return login attribute, for example login = username", defaultValue = "", category = ConfigCategory.CC_ENV)
    private String loginHandlerScript;

    @ConfigValue(key = "icon", description = "", defaultValue = "", category = ConfigCategory.CC_ENV)
    private String icon;

    @ConfigValue(key = "enabled", description = "", defaultValue = TRUE, category = ConfigCategory.CC_ENV)
    private Boolean enabled;

    @ConfigValue(key = "hidden", description = "", defaultValue = FALSE, category = ConfigCategory.CC_ENV)
    private Boolean hidden;

    @ConfigValue(key = "protocol", description = "oauth protocol (es: `msazureoauth2`)", category = ConfigCategory.CC_ENV)
    private OauthProtocol oauthProtocol;

    @ConfigValue(key = "resourceId", description = "oauth resource id", category = ConfigCategory.CC_ENV)
    private String oauthResourceId;

    @ConfigValue(key = "clientId", description = "oauth client id", category = ConfigCategory.CC_ENV)
    private String oauthClientId;

    @ConfigValue(key = "tenantId", description = "oauth tenant id", category = ConfigCategory.CC_ENV)
    private String oauthTenantId;

    @ConfigValue(key = "serviceUrl", description = "oauth service url", category = ConfigCategory.CC_ENV)
    private String oauthServiceUrl;

    @ConfigValue(key = "redirectUrl", description = "oauth redirect url (optional, local url accepted from oauth provider; if not set, url will be build from current request)", category = ConfigCategory.CC_ENV)
    private String oauthRedirectUrl;

    @ConfigValue(key = "logout.enabled", description = "enable slo (single log out)", defaultValue = ConfigValue.FALSE, category = ConfigCategory.CC_ENV)
    private Boolean oauthLogoutEnabled;

    @ConfigValue(key = "logout.redirectUrl", description = "oauth logout redirect url (optional, local url accepted from oauth provider for logout redirect; if not set, url will be build from current config)", category = ConfigCategory.CC_ENV)
    private String oauthLogoutRedirectUrl;

    @ConfigValue(key = "clientSecret", description = "oauth client secret", category = ConfigCategory.CC_ENV)
    private String oauthClientSecret;

    @ConfigValue(key = "scope", description = "oauth scope", defaultValue = "openid", category = ConfigCategory.CC_ENV)
    private String oauthScope;

    @ConfigValue(key = "login.attr", description = "oauth login attr (attr in oauth response to be used to match cmdbuild users)", category = ConfigCategory.CC_ENV)
    private String oauthLoginAttr;

    @ConfigValue(key = "login.type", description = "oauth login type, for example `email` or `username`", defaultValue = "auto", category = ConfigCategory.CC_ENV)
    private String oauthLoginType;

    public OauthModuleConfigurationImpl() {
        this("___DUMMY___");
    }

    public OauthModuleConfigurationImpl(String code) {
        this.code = checkNotBlank(code);
    }

    @Override
    public String getType() {
        return OAUTH_LOGIN_MODULE_TYPE;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getLoginHandlerScript() {
        return loginHandlerScript;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public boolean isOauthLogoutEnabled() {
        return oauthLogoutEnabled;
    }

    @Override
    @Nullable
    public String getOauthLogoutRedirectUrl() {
        return oauthLogoutRedirectUrl;
    }

    @Override
    public OauthProtocol getOauthProtocol() {
        return oauthProtocol;
    }

    @Override
    public String getOauthResourceId() {
        return oauthResourceId;
    }

    @Override
    public String getOauthClientId() {
        return oauthClientId;
    }

    @Override
    public String getOauthClientSecret() {
        return oauthClientSecret;
    }

    @Override
    public String getOauthTenantId() {
        return oauthTenantId;
    }

    @Override
    public String getOauthServiceUrl() {
        return oauthServiceUrl;
    }

    @Override
    public String getOauthRedirectUrl() {
        return oauthRedirectUrl;
    }

    @Override
    public String getOauthScope() {
        return oauthScope;
    }

    @Override
    public String getOauthLoginAttr() {
        return oauthLoginAttr;
    }

    @Override
    public String getOauthLoginType() {
        return oauthLoginType;
    }

}
