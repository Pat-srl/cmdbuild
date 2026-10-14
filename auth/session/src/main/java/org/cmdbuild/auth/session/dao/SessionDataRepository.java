/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.dao;

import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.session.dao.beans.SessionDataJsonBean;
import org.joda.time.Period;

public interface SessionDataRepository {

    @Nullable
    SessionData getSessionDataByIdOrNull(String sessionId);

    List<SessionData> getAllSessions();

    void deleteExpiredSessions(Period expireTime);
    
    void refreshActiveSessions(Period expireTime);

    void deleteSession(String sessionId);

    void deleteAll();

    SessionData createOrUpdateSession(String sessionId, SessionDataJsonBean sessionData);

    int getActiveSessionCount(Period activePeriod);

//    List<SessionData> getByUsername(String username);

}
