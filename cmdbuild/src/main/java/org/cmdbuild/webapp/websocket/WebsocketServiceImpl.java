/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.websocket;

import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.cmdbuild.event.EventService;
import org.cmdbuild.event.EventService.OutgoingEvent;
import org.cmdbuild.event.RawEvent;
import org.cmdbuild.event.WebsocketService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WebsocketServiceImpl implements WebsocketService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ExecutorService executorService = Executors.newCachedThreadPool(namedThreadFactory(getClass()));

    public WebsocketServiceImpl(EventService eventService) {
        eventService.getEventBus().register(new Object() {
            @Subscribe
            public void handleOutgoingEvent(OutgoingEvent event) {
                sendEventMessageBackground(event.getRawEvent());
            }

        });
    }

    @Override
    public void sendEventMessage(RawEvent event) {
        WebsocketEndpoint.sendEventMessage(event);
    }

    @Override
    public boolean isConnected(String sessionId) {
        return WebsocketEndpoint.isConnected(sessionId);
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executorService);
    }

    private void sendEventMessageBackground(RawEvent event) {
        executorService.submit(() -> {
            try {
                sendEventMessage(event);
            } catch (Exception ex) {
                logger.error("error sending event message = {}", event, ex);
            }
        });
    }

}
