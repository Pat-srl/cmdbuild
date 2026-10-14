/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.config;

import jakarta.annotation.Nullable;
import org.cmdbuild.utils.date.Interval;

public interface UserRepositoryConfig {

    boolean isCaseInsensitive();

    UserRepoLoginAttributeMode getLoginAttributeMode();

    @Nullable
    Interval getExpireUnusedUsersAfterDuration();

    default boolean expireUnusedUsers() {
        return getExpireUnusedUsersAfterDuration() != null && !getExpireUnusedUsersAfterDuration().isZero();
    }

    enum UserRepoLoginAttributeMode {
        LAM_AUTO_DETECT_EMAIL, LAM_USERNAME, LAM_EMAIL
    }

}
