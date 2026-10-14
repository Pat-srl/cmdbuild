/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import org.cmdbuild.auth.login.cas.CasAuthenticatorConfiguration;
import static org.cmdbuild.auth.login.cas.CasAuthenticatorConfiguration.CAS_LOGIN_MODULE_TYPE;
import org.cmdbuild.config.api.ConfigCategory;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(value = "org.cmdbuild.auth.module." + CAS_LOGIN_MODULE_TYPE, module = true)
public final class CasModuleConfigurationImpl implements CasAuthenticatorConfiguration {

    private final String code;

    @ConfigValue(key = "description", description = "", defaultValue = "Jasig CAS", category = ConfigCategory.CC_ENV)
    private String description;

    @ConfigValue(key = "handlerScript", description = "modify/create/check cas login user or role before the effective login, must return login attribute, for example login = username", defaultValue = "", category = ConfigCategory.CC_ENV)
    private String loginHandlerScript;

    @ConfigValue(key = "icon", description = "", defaultValue = "", category = ConfigCategory.CC_ENV)
    private String icon;

    @ConfigValue(key = "enabled", description = "", defaultValue = TRUE, category = ConfigCategory.CC_ENV)
    private Boolean enabled;

    @ConfigValue(key = "hidden", description = "", defaultValue = FALSE, category = ConfigCategory.CC_ENV)
    private Boolean hidden;

    @ConfigValue(key = "server.url", description = "", defaultValue = "https://cas-test:9443/cas", category = ConfigCategory.CC_ENV)
    private String casServerUrl;

    @ConfigValue(key = "login.page", description = "", defaultValue = "/login", category = ConfigCategory.CC_ENV)
    private String casLoginPage;

    @ConfigValue(key = "ticket.param", description = "", defaultValue = "ticket", category = ConfigCategory.CC_ENV)
    private String casTicketParam;

    @ConfigValue(key = "service.param", description = "", defaultValue = "service", category = ConfigCategory.CC_ENV)
    private String casServiceParam;

    public CasModuleConfigurationImpl() {
        this("___DUMMY___");
    }

    public CasModuleConfigurationImpl(String code) {
        this.code = checkNotBlank(code);
    }

    @Override
    public String getType() {
        return CAS_LOGIN_MODULE_TYPE;
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
    public String getCasServerUrl() {
        return casServerUrl;
    }

    @Override
    public String getCasLoginPage() {
        return casLoginPage;
    }

    @Override
    public String getCasTicketParam() {
        return casTicketParam;
    }

    @Override
    public String getCasServiceParam() {
        return casServiceParam;
    }

}
