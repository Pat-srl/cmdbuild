/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.chat.ChatMessage;
import org.cmdbuild.client.rest.model.Session;

public interface SessionApi {

    SessionInfo getSessionInfo();

    Map<String, String> getPreferences();

    Map<String, String> getSystemConfig();

    SessionApiWithSession updateSession(Session session);

    List<SessionInfo> getAllSessionsInfo();
    
    List<ChatMessage> getChatMessages();

    void deleteAll();

    interface SessionInfo {

        @Nullable
        String getSessionToken();

        String getUsername();

        ZonedDateTime getLastActive();

        default boolean hasSessionToken() {
            return isNotBlank(getSessionToken());
        }
    }

    interface SessionApiWithSession {

        SessionApi then();

        Session getSession();
    }

}
