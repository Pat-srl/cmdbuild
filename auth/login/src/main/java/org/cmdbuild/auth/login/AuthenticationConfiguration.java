/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import static com.google.common.base.Objects.equal;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.login.header.HeaderAuthenticatorConfiguration;
import org.cmdbuild.auth.login.ldap.LdapAuthenticatorConfiguration;
import org.cmdbuild.auth.config.AuthenticationServiceConfiguration;
import org.cmdbuild.auth.config.UserRepositoryConfig;
import static org.cmdbuild.auth.login.LoginModuleConfiguration.DEFAULT_LOGIN_MODULE_TYPE;
import org.cmdbuild.auth.login.custom.CustomLoginConfiguration;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface AuthenticationConfiguration extends HeaderAuthenticatorConfiguration, LdapAuthenticatorConfiguration, AuthenticationServiceConfiguration, UserRepositoryConfig, CustomLoginConfiguration {

    List<LoginModuleConfiguration> getLoginModules();

    PasswordAlgo getPreferredPasswordAlgorythm();

    @Nullable
    String getLogoutRedirectUrl();

    boolean isAutoSsoRedirectEnabled();
    
    @Nullable
    String getLoginHelp();

    default List<LoginModuleConfiguration> getNonDefaultNonHiddenActiveLoginModules() {
        return list(getLoginModules()).filter(c -> !equal(DEFAULT_LOGIN_MODULE_TYPE, c.getType()) && !c.isHidden() && c.isEnabled());
    }

    default List<LoginModuleConfiguration> getNonDefaultActiveLoginModules() {
        return list(getLoginModules()).filter(c -> !equal(DEFAULT_LOGIN_MODULE_TYPE, c.getType()) && c.isEnabled());
    }

    default boolean isDefaultLoginModuleEnabledAndVisible() {
        return getLoginModules().stream().anyMatch(m -> m.isEnabled() && !m.isHidden() && equal(m.getType(), DEFAULT_LOGIN_MODULE_TYPE));
    }

    default boolean isDefaultLoginModuleEnabled() {
        return getLoginModules().stream().anyMatch(m -> m.isEnabled() && equal(m.getType(), DEFAULT_LOGIN_MODULE_TYPE));
    }

    default LoginModuleConfiguration getLoginModuleByCode(String moduleCode) {
        return getLoginModules().stream().filter(m -> equal(m.getCode(), checkNotBlank(moduleCode))).collect(onlyElement("login module not found for code =< %s >", moduleCode));
    }

}
