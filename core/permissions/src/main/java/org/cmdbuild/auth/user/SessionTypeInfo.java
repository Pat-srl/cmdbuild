/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.user;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.auth.user.SessionType.ST_BATCH;
import static org.cmdbuild.auth.user.SessionType.ST_INTERACTIVE;

public interface SessionTypeInfo {

    SessionType getSessionType();

    default boolean isInteractive() {
        return equal(getSessionType(), ST_INTERACTIVE);
    }

    default boolean isBatch() {
        return equal(getSessionType(), ST_BATCH);
    }
}
