/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.function.Function;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.session.model.Session;

public interface SessionRepository {

    @Nullable
    Session getSessionByIdOrNull(String sessionId);

    /**
     * update session data on cache/db (actual db update may be deferred or
     * skipped if session is not dirty and has been saved on db recently, see
     * {@link CmdbuildConfiguration#getSessionPersistDelay()})
     *
     * @param session
     * @return new (updated) session object (to be stored in cache etc)
     */
    Session createOrUpdateSession(Session session);

    void deleteSession(String sessionId);

    void deleteAll();

    int getActiveSessionCount();

    List<Session> getAllSessions();

    default Session getSessionById(String sessionId) {
        return checkNotNull(getSessionByIdOrNull(sessionId), "session not found for id = %s", sessionId);
    }

    default Session updateSession(String sessionId, Function<Session, Session> fun) {
        Session session = getSessionByIdOrNull(sessionId);
        session = fun.apply(session);
        session = createOrUpdateSession(session);
        return session;
    }

}
