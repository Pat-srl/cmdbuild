/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.wshelpers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.event.EventService;
import org.cmdbuild.event.WebsocketService;
import org.cmdbuild.event.WebsocketSessionClosedEvent;
import org.cmdbuild.log.LogService;
import org.cmdbuild.log.LogService.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Component
public class LogMessageStreamHelperImpl implements LogMessageStreamHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Cache<String, MyEventListener> eventListenersBySessionId = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.DAYS).removalListener(n -> doStopReceivingLogMessages((MyEventListener) n.getValue())).build();

    private final SessionService sessionService;
    private final LogService logService;
    private final EventService eventService;
    private final WebsocketService websocketService;

    public LogMessageStreamHelperImpl(SessionService sessionService, LogService logService, EventService eventService, WebsocketService websocketService) {
        this.sessionService = checkNotNull(sessionService);
        this.logService = checkNotNull(logService);
        this.eventService = checkNotNull(eventService);
        this.websocketService = checkNotNull(websocketService);
    }

    @Override
    public void startReceivingLogMessages() {
        //TODO handle log message stream by client (window), and not by session only 
        String sessionId = sessionService.getCurrentSession().getSessionId();
        logger.debug("preparing session = {} to receive log messages", sessionId);
        eventListenersBySessionId.invalidate(sessionId);
        checkArgument(websocketService.isConnected(sessionId), "current session is not connected to socket service: unable to start log message streaming");
        MyEventListener listener = new MyEventListener(sessionId);
        eventListenersBySessionId.put(sessionId, listener);
        eventService.getEventBus().register(listener);
        logService.getEventBus().register(listener);
        logger.info("session = {} is receiving log messages", sessionId);
    }

    @Override
    public void stopReceivingLogMessages() {
        String sessionId = sessionService.getCurrentSession().getSessionId();
        logger.debug("invalidate log message receiver for session = {}", sessionId);
        eventListenersBySessionId.invalidate(sessionId);
    }

    private void doStopReceivingLogMessages(MyEventListener listener) {
        logger.info("stop log message streaming for session = {}", listener.sessionId);
        eventService.getEventBus().unregister(listener);
        logService.getEventBus().unregister(listener);
    }

    private class MyEventListener {

        private final String sessionId;

        public MyEventListener(String sessionId) {
            this.sessionId = checkNotBlank(sessionId);
        }

        @Subscribe
        public void handleSessionClosedEvent(WebsocketSessionClosedEvent event) {
            if (equal(event.getSessionId(), sessionId)) {
                logger.debug("detected session closed, stop log message streaming for sessionId = {}", sessionId);
                eventListenersBySessionId.invalidate(sessionId);
            }
        }

        @Subscribe
        public void handleLogEvent(LogEvent event) {
            eventService.sendEventMessage(sessionId, "log.message", (Map) map(
                    "level", event.getLevel().name().toLowerCase().replaceFirst("ll_", ""),
                    "timestamp", toIsoDateTime(event.getTimestamp()),
                    "message", event.getMessage(),
                    "line", event.getLine()
            ).accept(m -> {
                if (event.hasException()) {
                    m.put("stacktrace", ExceptionUtils.getStackTrace(event.getException()));
                }
            }));
        }

    }

}
