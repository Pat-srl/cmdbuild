/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface SessionDataSupplier {

    @Nullable
    String getCurrentSessionData(String key);

    default String getCurrentSessionDataNotBlank(String key) {
        return checkNotBlank(getCurrentSessionData(key), "missing session data for key =< %s >", key);
    }

}
