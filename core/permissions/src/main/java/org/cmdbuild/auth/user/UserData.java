/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.user;

import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;

public interface UserData {

    static final String USER_CLASS_NAME = "User",
            USER_ATTR_USERNAME = "Username",
            USER_ATTR_EMAIL = "Email";

    @Nullable
    Long getId();

    @Nullable
    String getDescription();

    String getUsername();

    @Nullable
    String getPassword();

    @Nullable
    String getRecoveryToken();

    @Nullable
    String getEmail();

    boolean isActive();

    boolean isService();

    default boolean hasId() {
        return isNotNullAndGtZero(getId());
    }

    default boolean isNotService() {
        return !isService();
    }

    default boolean hasEmail() {
        return isNotBlank(getEmail());
    }

    default boolean hasPassword() {
        return isNotBlank(getPassword());
    }

    default boolean hasRecoveryToken() {
        return isNotBlank(getRecoveryToken());
    }

    @Nullable
    default String getRecoveryTokenOrPassword() {
        return firstNotBlankOrNull(getRecoveryToken(), getPassword());
    }

    @Nullable
    default String getPasswordOrRecoveryToken() {
        return firstNotBlankOrNull(getPassword(), getRecoveryToken());
    }

}
