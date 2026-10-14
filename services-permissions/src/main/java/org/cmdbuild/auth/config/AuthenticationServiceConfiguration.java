/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.config;

import java.util.Collection;
import jakarta.annotation.Nullable;

public interface AuthenticationServiceConfiguration {

    boolean isDefaultEnabled();

    boolean isFileEnabled();

    boolean isRsaEnabled();

    boolean isLoginMobileUsernameEnabled();

    public Collection<String> getLoginInputSource();

    @Nullable
    Integer getMaxLoginAttempts();

    @Nullable
    Integer getMaxLoginAttemptsWindowSeconds();

    LoginServiceReturnId getLoginServiceReturnIdMode();

    enum LoginServiceReturnId {
        RI_AUTO, RI_ALWAYS
    }
}
