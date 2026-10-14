/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.session.SessionExpirationStrategy;
import org.cmdbuild.auth.user.OperationUserStack;
import org.cmdbuild.ui.TargetDevice;

/**
 * this interface represents an user session
 */
public interface Session extends SessionData, Serializable {

    /**
     * return operation user (stack) for this session
     *
     * @return operation user (stack)
     */
    OperationUserStack getOperationUser();

    /**
     * return session id
     *
     * @return session id
     */
    String getSessionId();

    /**
     * return creation date for this session
     *
     * @return begin date
     */
    ZonedDateTime getBeginDate();

    /**
     * return the last time this session was active
     *
     * @return last active date
     */
    ZonedDateTime getLastActiveDate();

    /**
     * return the last time this session was persisted (null if session has not
     * yet been persisted on db)
     *
     * @return last save date
     */
    @Nullable
    ZonedDateTime getLastSavedDate();

    @Nullable
    ZonedDateTime getExpirationDate();

    /**
     * return true if session is new (and not yet persisted on db)
     *
     * @return
     */
    default boolean isNew() {
        return getLastSavedDate() == null;
    }

    /**
     * return true if this session data has been modified since last storage on
     * db, false otherwise
     *
     * @return true if session data is dirty
     */
    boolean isDirty();

    SessionExpirationStrategy getExpirationStrategy();

    default TargetDevice getTargetDevice() {
        return getOperationUser().getTargetDevice();
    }
}
