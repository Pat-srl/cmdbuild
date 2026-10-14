/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session;

import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface ImpersonateRequest {

    @Nullable
    String getUsername();

    @Nullable
    String getGroup();

    @Nullable
    String getSponsor();

    boolean isTransient();

    default boolean hasUsername() {
        return isNotBlank(getUsername());
    }

    default boolean hasGroup() {
        return isNotBlank(getGroup());
    }

    default boolean hasSponsor() {
        return isNotBlank(getSponsor());
    }
}
