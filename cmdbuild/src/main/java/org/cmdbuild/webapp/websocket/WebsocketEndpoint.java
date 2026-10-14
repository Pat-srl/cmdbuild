/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.websocket;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import jakarta.annotation.Nullable;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.PongMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;
import java.io.EOFException;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_AUTHORIZATION_COOKIE;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_CLIENT_ID;
import org.cmdbuild.event.EventService;
import org.cmdbuild.event.RawEvent;
import static org.cmdbuild.event.RawEvent.EVENT_CODE_ACTION;
import static org.cmdbuild.event.RawEvent.EventDirection.ED_INCOMING;
import static org.cmdbuild.event.RawEvent.EventDirection.ED_OUTGOING;
import org.cmdbuild.event.RawEventImpl;
import org.cmdbuild.event.WebsocketSessionClosedEvent;
import org.cmdbuild.event.WebsocketSessionCreatedEvent;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.webapp.websocket.WebsocketEndpoint.MyWebsocketEndpointConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.socket.server.standard.SpringConfigurator;

@ServerEndpoint(value = "/services/websocket/v1/main", configurator = MyWebsocketEndpointConfigurator.class)
public class WebsocketEndpoint {

    private static final int MAX_MESSAGE_SIZE = 10000000;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static WebsocketSessionHandlerRepository HANDLER_REPOSITORY = new WebsocketSessionHandlerRepository();

    private final EventService eventService;
    private final SessionService sessionService;

    public WebsocketEndpoint(EventService eventService, SessionService sessionService) {
        this.eventService = checkNotNull(eventService);
        this.sessionService = checkNotNull(sessionService);
        logger.info("ready");
    }

    @OnOpen
    public void onOpen(Session websocketSession) {
        initMdc(websocketSession);
        try {
            logger.debug("new websocket session opened with id = {} and user data = \n{}", websocketSession.getId(), mapToLoggableStringLazy(websocketSession.getUserProperties()));
            WebsocketSessionHandler sessionHandler = new WebsocketSessionHandler(websocketSession);
            HANDLER_REPOSITORY.put(sessionHandler);
            String sessionId = toStringOrNull(websocketSession.getUserProperties().get(CMDBUILD_AUTHORIZATION_COOKIE));
            if (isNotBlank(sessionId)) {
                authenticateWebsocketSession(sessionHandler, sessionId, toStringOrNull(websocketSession.getUserProperties().get(CMDBUILD_CLIENT_ID)));
            }
            logActiveSessions();
            websocketSession.setMaxTextMessageBufferSize(MAX_MESSAGE_SIZE);
        } catch (Exception ex) {
            logger.warn("error processing open session event", ex);
        } finally {
            MDC.clear();
        }
    }

    @OnMessage
    public void onMessage(Session websocketSession, String message) {
        initMdc(websocketSession);
        WebsocketSessionHandler sessionHandler = null;
        try {
            logger.trace("string message received from ui, session = {}, message = {}", websocketSession.getId(), abbreviate(message));
            sessionHandler = HANDLER_REPOSITORY.getByWebsocketSessionId(websocketSession.getId());

            Map<String, Object> payload = fromJson(message, MAP_OF_OBJECTS);
            String action = toStringNotBlank(payload.get("_action"), "missing required '_action' param");
            WebsocketSessionHandler handler = HANDLER_REPOSITORY.getByWebsocketSessionId(websocketSession.getId());
            if (handler.hasSessionInfo()) {
                sessionService.getSessionById(handler.getSessionId());
                eventService.handleReceivedEventMessage(RawEventImpl.builder()
                        .withDirection(ED_INCOMING)
                        .withEventCode(EVENT_CODE_ACTION)
                        .withMessageId(toStringNotBlank(payload.get("_id"), "missing required '_id' param"))
                        .withSessionId(handler.getSessionId())
                        .withClientId(handler.getClientId())
                        .withPayload(payload)
                        .build());
            } else {
                checkArgument(Objects.equals(action, "socket.session.login"), "this websocket connection has not completed login sequence; only accepted action is 'socket.session.login'");
                authenticateWebsocketSession(sessionHandler, toStringNotBlank(payload.get("token"), "missing required 'token' param"), toStringOrNull(payload.get("clientId")));
            }
        } catch (Exception ex) {
            logger.error("error processing message event", ex);
            if (sessionHandler != null) {
                sessionHandler.sendMessageSafe(RawEventImpl.builder().withDirection(ED_OUTGOING).withEventCode("socket.error").withSessionId("x").withPayload("message", ex.toString()).build());
            }
        } finally {
            MDC.clear();
        }
    }

    private void authenticateWebsocketSession(WebsocketSessionHandler sessionHandler, String sessionId, @Nullable String clientId) {
        logger.debug("identifying websocket session with token = {} clientId =< {} >", sessionId, clientId);
        org.cmdbuild.auth.session.model.Session session = sessionService.getSessionById(sessionId);
        sessionHandler.setSessionInfo(session.getSessionId(), firstNotBlank(clientId, randomId()));
        HANDLER_REPOSITORY.put(sessionHandler);
        sessionHandler.sendSessionOkMessage();
        eventService.handleReceivedEventMessage(new WebsocketSessionCreatedEventImpl(sessionId));
    }

    @OnMessage
    public void onMessage(Session websocketSession, byte[] message) {
        initMdc(websocketSession);
        try {
            logger.warn("received unsupported binary message from session = {}, message will be ignored", websocketSession.getId());
        } finally {
            MDC.clear();
        }
    }

    @OnMessage
    public void onMessage(Session websocketSession, PongMessage message) {
        initMdc(websocketSession);
        try {
            logger.debug("pong message received from ui, session = {}, message = {}", websocketSession.getId(), message);
            // TODO Handle pong messages
        } finally {
            MDC.clear();
        }
    }

    @OnClose
    public void onClose(Session websocketSession) {
        initMdc(websocketSession);
        try {
            logger.debug("closed session with id = {}", websocketSession.getId());
            WebsocketSessionHandler handler = HANDLER_REPOSITORY.getByWebsocketSessionId(websocketSession.getId());
            HANDLER_REPOSITORY.remove(handler);
            if (handler.hasSessionInfo()) {
                eventService.handleReceivedEventMessage(new WebsocketSessionClosedEventImpl(handler.getSessionId(), handler.getClientId()));
            }
            logActiveSessions();
        } catch (Exception ex) {
            logger.error("error processing close session event", ex);
        } finally {
            MDC.clear();
        }
    }

    @OnError
    public void onError(Session websocketSession, Throwable throwable) {
        initMdc(websocketSession);
        try {
            if (throwable instanceof EOFException) {
                logger.debug("detected closed connection error, session = {}", websocketSession.getId(), throwable);
            } else {
                logger.warn("error, session = {}", websocketSession.getId(), throwable);
            }
        } finally {
            MDC.clear();
        }
    }

    public static boolean isConnected(String sessionId) {
        checkNotBlank(sessionId);
        return HANDLER_REPOSITORY.hasHandlerForSessionId(sessionId);
    }

    public static void sendEventMessage(RawEvent message) {
        if (message.isBroadcast()) {
            LOGGER.debug("send broadcast message = {}", message);
            HANDLER_REPOSITORY.getAll().forEach(h -> h.sendMessageSafe(message));
        } else {
            checkArgument(message.hasSessionId(), "invalid outgoing message: session id is null");
            if (message.hasClientId()) {
                Optional.ofNullable(HANDLER_REPOSITORY.getBySessionIdClientIdOrNull(message.getSessionId(), message.getClientId())).ifPresent(h -> h.sendMessageSafe(message));
            } else {
                HANDLER_REPOSITORY.getBySessionId(message.getSessionId()).forEach(h -> h.sendMessageSafe(message));
            }
        }
    }

    private void initMdc(Session websocketSession) {
        MDC.put("cm_type", "req");
        MDC.put("cm_id", format("websocket:%s", websocketSession.getId()));
    }

    private void logActiveSessions() {
        logger.debug("active sessions = {}", HANDLER_REPOSITORY.size() < 10 && HANDLER_REPOSITORY.size() > 0
                ? HANDLER_REPOSITORY.getAll().stream().sorted(comparing(WebsocketSessionHandler::getWebsocketSessionId))
                        .map(s -> s.hasSessionInfo() ? format("%s/%s_%s", s.getWebsocketSessionId(), s.getSessionId(), s.getClientId()) : s.getWebsocketSessionId()).collect(joining(", "))
                : format("%s sessions", HANDLER_REPOSITORY.size()));
    }

    private static class WebsocketSessionHandlerRepository {//TODO add session expiration/invalidation

        private final Map<String, WebsocketSessionHandler> sessionByWebsocketSessionId = new ConcurrentHashMap<>(), sessionBySessionIdAndClientId = new ConcurrentHashMap<>();
        private final Multimap<String, WebsocketSessionHandler> sessionBySessionId = Multimaps.synchronizedMultimap(HashMultimap.create());

        public void put(WebsocketSessionHandler sessionHandler) {
            sessionByWebsocketSessionId.put(sessionHandler.getWebsocketSessionId(), sessionHandler);
            if (sessionHandler.hasSessionInfo()) {
                sessionBySessionId.put(sessionHandler.getSessionId(), sessionHandler);
                sessionBySessionIdAndClientId.put(key(sessionHandler.getSessionId(), sessionHandler.getClientIdOrNull()), sessionHandler);
            }
        }

        public void remove(WebsocketSessionHandler sessionHandler) {
            sessionByWebsocketSessionId.remove(sessionHandler.getWebsocketSessionId());
            if (sessionHandler.hasSessionInfo()) {
                sessionBySessionId.remove(sessionHandler.getSessionId(), sessionHandler);
                sessionBySessionIdAndClientId.remove(key(sessionHandler.getSessionId(), sessionHandler.getClientIdOrNull()));
            }
        }

        public boolean hasHandlerForSessionId(String sessionId) {
            return sessionBySessionId.containsKey(checkNotBlank(sessionId));
        }

        public WebsocketSessionHandler getByWebsocketSessionId(String websocketSessionId) {
            return checkNotNull(sessionByWebsocketSessionId.get(checkNotBlank(websocketSessionId)), "hander not found for websocket session id =< %s >", websocketSessionId);
        }

        @Nullable
        public WebsocketSessionHandler getBySessionIdClientIdOrNull(String sessionId, String clientId) {
            return sessionBySessionIdAndClientId.get(key(checkNotBlank(sessionId), checkNotBlank(clientId)));
        }

        public Collection<WebsocketSessionHandler> getBySessionId(String sessionId) {
            return sessionBySessionId.get(checkNotBlank(sessionId));
        }

        public Collection<WebsocketSessionHandler> getAll() {
            return sessionByWebsocketSessionId.values();
        }

        public int size() {
            return sessionByWebsocketSessionId.size();
        }
    }

    private class WebsocketSessionHandler {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Session websocketSession;
        private String sessionId, clientId;

        public WebsocketSessionHandler(Session session) {
            this.websocketSession = checkNotNull(session);
        }

        public void setSessionInfo(String sessionId, String clientId) {
            checkArgument(isBlank(this.sessionId) && isBlank(this.clientId), "cannot set session info for this websocket: session info already present = %s", this);
            this.sessionId = checkNotBlank(sessionId);
            this.clientId = checkNotBlank(clientId);
        }

        public boolean hasSessionInfo() {
            return isNotBlank(sessionId);
        }

        @Nullable
        public String getSessionIdOrNull() {
            return sessionId;
        }

        @Nullable
        public String getClientIdOrNull() {
            return clientId;
        }

        public String getWebsocketSessionId() {
            return websocketSession.getId();
        }

        public String getSessionId() {
            return checkNotBlank(sessionId);
        }

        public String getClientId() {
            return checkNotBlank(clientId);
        }

        public Session getSession() {
            return websocketSession;
        }

        public void sendMessageSafe(RawEvent message) {
            checkArgument(message.isOutgoing());

            String payload = toJson(map("_id", message.getMessageId()).with(message.getPayload()).with("_event", message.getEventCode(), "_eventId", message.getMessageId()));

            sendMessageSafe(payload);
        }

        public void sendSessionOkMessage() {
            logger.debug("send session ok message");
            sendMessageSafe(RawEventImpl.builder().withDirection(ED_OUTGOING).withEventCode("socket.session.ok").withSessionId(sessionId).build());
        }

        private synchronized void sendMessageSafe(String payload) {
            try {
                logger.trace("send message to session = {} ({}) message = {}", sessionId, websocketSession.getId(), abbreviate(payload));
                websocketSession.getBasicRemote().sendText(checkNotBlank(payload));
            } catch (Exception ex) {
                logger.error("error sending websocket message = {} to session = {}", abbreviate(payload), websocketSession, ex);
            }
        }

        @Override
        public String toString() {
            return "WebsocketSessionHandler{" + "websocketSessionId=" + getWebsocketSessionId() + ", sessionId=" + sessionId + ", clientId=" + clientId + '}';
        }

    }

    private class WebsocketSessionCreatedEventImpl implements WebsocketSessionCreatedEvent {

        private final String sessionId;

        public WebsocketSessionCreatedEventImpl(String sessionId) {
            this.sessionId = checkNotBlank(sessionId);
        }

        @Override
        public String getSessionId() {
            return sessionId;
        }

    }

    private class WebsocketSessionClosedEventImpl implements WebsocketSessionClosedEvent {

        private final String sessionId, clientId;

        public WebsocketSessionClosedEventImpl(String sessionId, String clientId) {
            this.sessionId = sessionId;
            this.clientId = clientId;
        }

        @Override
        public String getSessionId() {
            return sessionId;
        }

        @Override
        public String getClientId() {
            return clientId;
        }

    }

    public static class MyWebsocketEndpointConfigurator extends SpringConfigurator {

        @Override
        public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
            try {
                Map<String, List<String>> headers = request.getHeaders();
                LOGGER.debug("processing request with headers = \n{}", mapToLoggableStringLazy(headers));
                list(CMDBUILD_AUTHORIZATION_COOKIE, CMDBUILD_CLIENT_ID).forEach(key -> {
                    String cookies = nullToEmpty(headers.getOrDefault("cookie", emptyList()).stream().collect(joining("; "))).toLowerCase();
                    Matcher matcher = Pattern.compile("(?i)" + Pattern.quote(key) + "[^a-z0-9]+?([a-z0-9]+)").matcher(cookies);
                    if (matcher.find()) {
                        String cookieValue = checkNotBlank(matcher.group(1));
                        LOGGER.debug("found authorization cookie =< {} >", cookieValue);
                        config.getUserProperties().put(key, cookieValue);
                    }
                });
            } catch (Exception ex) {
                LOGGER.error("error preprocessing websocket handshake request", ex);
            }
        }
    }
}
