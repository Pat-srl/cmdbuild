/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.inner;

import jakarta.annotation.Nullable;
import org.cmdbuild.auth.login.SessionDataSupplier;
import org.cmdbuild.auth.session.model.SessionData;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface SessionDataService extends SessionDataSupplier {

    /**
     * return a session data instance useful to store data in current session;
     * if there is not a valid session, the returned object will discard any
     * write operation, and always return null on read
     *
     * @return current session data
     */
    SessionData getCurrentSessionDataSafe();

    @Nullable
    @Override
    default String getCurrentSessionData(String key) {
        return getCurrentSessionDataSafe().get(checkNotBlank(key));
    }
}
