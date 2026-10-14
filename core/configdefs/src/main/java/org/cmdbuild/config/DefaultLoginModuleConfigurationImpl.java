/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import org.cmdbuild.auth.login.LoginModuleConfiguration;
import static org.cmdbuild.auth.login.LoginModuleConfiguration.DEFAULT_LOGIN_MODULE_TYPE;
import org.cmdbuild.config.api.ConfigCategory;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(value = "org.cmdbuild.auth.module." + DEFAULT_LOGIN_MODULE_TYPE, module = true)
public final class DefaultLoginModuleConfigurationImpl implements LoginModuleConfiguration {

    private final String code;

    @ConfigValue(key = "description", description = "", defaultValue = "DEFAULT", category = ConfigCategory.CC_ENV)
    private String description;

    @ConfigValue(key = "handlerScript", description = "modify/create/check default login user or role before the effective login, must return login attribute, for example login = username", defaultValue = "", category = ConfigCategory.CC_ENV)
    private String loginHandlerScript;

    @ConfigValue(key = "icon", description = "", defaultValue = "", category = ConfigCategory.CC_ENV)
    private String icon;

    @ConfigValue(key = "enabled", description = "", defaultValue = TRUE, category = ConfigCategory.CC_ENV)
    private Boolean enabled;

    @ConfigValue(key = "hidden", description = "", defaultValue = FALSE, category = ConfigCategory.CC_ENV)
    private Boolean hidden;

    public DefaultLoginModuleConfigurationImpl() {
        this("___DUMMY___");
    }

    public DefaultLoginModuleConfigurationImpl(String code) {
        this.code = checkNotBlank(code);
    }

    @Override
    public String getType() {
        return DEFAULT_LOGIN_MODULE_TYPE;
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

}
