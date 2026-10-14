/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Iterables.transform;
import com.google.gson.JsonElement;
import static java.lang.Long.parseLong;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.chat.ChatMessage;
import org.cmdbuild.chat.ChatMessageImpl;
import org.cmdbuild.chat.ChatMessageSourceType;
import org.cmdbuild.chat.ChatMessageStatus;
import org.cmdbuild.chat.ChatMessageType;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.client.rest.api.SessionApi;
import org.cmdbuild.client.rest.model.Session;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;

public class SessionApiImpl extends AbstractServiceClientImpl implements SessionApi {

    public SessionApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public SessionInfo getSessionInfo() {
        JsonElement response = get("sessions/current").asJson();
        return toSessionInfo(response.getAsJsonObject().get("data"));
    }

    @Override
    public void deleteAll() {
        delete("sessions/all");
    }

    @Override
    public List<SessionInfo> getAllSessionsInfo() {
        return list(transform(get("sessions").asJson().getAsJsonObject().getAsJsonArray("data"), this::toSessionInfo));
    }

    private SessionInfo toSessionInfo(JsonElement e) {
        return new SessionInfoImpl(toString(e.getAsJsonObject().getAsJsonPrimitive("_id")),
                e.getAsJsonObject().getAsJsonPrimitive("username").getAsString(),
                CmDateUtils.toDateTime(e.getAsJsonObject().getAsJsonPrimitive("lastActive").getAsString()));
    }

    @Override
    public Map<String, String> getPreferences() {
        logger.debug("getPreferences");
        Map<String, String> map = map();
        get("sessions/current/preferences").asJson().getAsJsonObject().getAsJsonObject("data").entrySet().forEach((entry) -> {
            map.put(entry.getKey(), toString(entry.getValue()));
        });
        return map;
    }

    @Override
    public Map<String, String> getSystemConfig() {
        logger.debug("getSystemConfig");
        Map<String, String> map = map();
        get("configuration").asJson().getAsJsonObject().getAsJsonObject("data").entrySet().forEach((entry) -> {
            map.put(entry.getKey(), toString(entry.getValue()));
        });
        return map;
    }

    @Override
    public SessionApiWithSession updateSession(Session session) {
        Session response = fromJson(put("sessions/current", session).asJackson().get("data"), Session.class);
        return new SessionApiWithSession() {
            @Override
            public SessionApi then() {
                return SessionApiImpl.this;
            }

            @Override
            public Session getSession() {
                return response;
            }
        };
    }

    @Override
    public List<ChatMessage> getChatMessages() {
        return list(get("sessions/current/messages?detailed=true").asJackson().get("data").elements()).map(e -> fromJson(e, MAP_OF_OBJECTS)).map(m -> ChatMessageImpl.builder()
                .withId(CmConvertUtils.toLong(m.get("_id")))
                .withMessageId((String) m.get("messageId"))
                .withSubject((String) m.get("subject"))
                .withContent((String) m.get("content"))
                .withTarget((String) m.get("target"))
                .withThread((String) m.get("thread"))
                .withSourceType(parseEnum((String) m.get("sourceType"), ChatMessageSourceType.class))
                .withSourceName((String) m.get("sourceName"))
                .withSourceDescription((String) m.get("sourceDescription"))
                .withTimestamp(CmDateUtils.toDateTime(m.get("timestamp")))
                .withType(parseEnum((String) m.get("type"), ChatMessageType.class))
                .withStatus(parseEnum((String) m.get("status"), ChatMessageStatus.class))
                .build());
    }

    private static class SessionInfoImpl implements SessionInfo {

        private final String sessionToken, username;
        private final ZonedDateTime lastActive;

        public SessionInfoImpl(String sessionToken, String username, ZonedDateTime lastActive) {
            this.sessionToken = emptyToNull(sessionToken);
            this.username = checkNotBlank(username);
            this.lastActive = checkNotNull(lastActive);
        }

        @Override
        @Nullable
        public String getSessionToken() {
            return sessionToken;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public ZonedDateTime getLastActive() {
            return lastActive;
        }

        @Override
        public String toString() {
            return "SimpleSessionInfo{" + "sessionToken=" + sessionToken + '}';
        }

    }

}
