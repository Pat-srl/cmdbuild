/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.event;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jakarta.annotation.PreDestroy;
import org.cmdbuild.cluster.ClusterMessageImpl;
import org.cmdbuild.cluster.ClusterMessageReceivedEvent;
import org.cmdbuild.cluster.ClusterService;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import static org.cmdbuild.event.RawEvent.EventDirection.ED_OUTGOING;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EventServiceImpl implements EventService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String CLUSTER_MESSAGE_TYPE_SEND_EVENT = "org.cmdbuild.event.SEND_EVENT";

    private final ExecutorService executorService = Executors.newCachedThreadPool(namedThreadFactory(getClass()));
    private final EventBus eventBus = new EventBus(logExceptions(logger));

    private final ClusterService clusterService;

    public EventServiceImpl(ClusterService clusterService) {
        this.clusterService = checkNotNull(clusterService);
        clusterService.getEventBus().register(new Object() {
            @Subscribe
            public void handleClusterMessageReceivedEvent(ClusterMessageReceivedEvent clusterEvent) {
                if (clusterEvent.isOfType(CLUSTER_MESSAGE_TYPE_SEND_EVENT)) {
                    try {
                        RawEvent event = deserializeClusterMessageData(clusterEvent.getClusterMessage().getMessageData());
                        doSendEventMessage(event);
                    } catch (Exception ex) {
                        logger.error("error processing cluster message event = {}", clusterEvent, ex);
                    }
                }
            }

        });
        eventBus.register(new Object() {
            @Subscribe
            public void handleEvents(RawEvent event) {
                if (equal(event.getStringValue("_action"), "socket.status.get")) {
                    sendEventMessage(event.getSessionIdOrNull(), "socket.status", map("status", "OK"));
                }
            }
        });
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executorService);
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public void sendEventMessage(RawEvent event) {
        clusterService.sendMessage(ClusterMessageImpl.builder()
                .withMessageType(CLUSTER_MESSAGE_TYPE_SEND_EVENT)
                .withMessageData(serializeToClusterMessageData(event))
                .build());
        doSendEventMessage(event);
    }

    private void doSendEventMessage(RawEvent event) {
        if (event.isBroadcast()) {
            logger.info("processing broadcast event = {}", event);
        } else {
            logger.debug("processing event = {}", event);
        }
        eventBus.post(new OutgoingEventImpl(event));
    }

    @Override
    public void handleReceivedEventMessage(Event event) {
        if (event instanceof RawEvent) {
            checkArgument(((RawEvent) event).isIncoming());
        }
        executorService.submit(() -> {
            eventBus.post(event);
        });
    }

    private RawEvent deserializeClusterMessageData(Map<String, Object> messageData) {
        return RawEventImpl.builder()
                .withDirection(ED_OUTGOING)
                .withMessageId(toStringNotBlank(messageData.get("id")))
                .withEventCode(toStringNotBlank(messageData.get("code")))
                .withSessionId(toStringOrNull(messageData.get("session")))
                .withTimestamp(toDateTime(messageData.get("timestamp")))
                .withPayload((Map) messageData.get("payload"))//TODO check this
                .build();
    }

    private Map<String, Object> serializeToClusterMessageData(RawEvent event) {
        checkArgument(event.isOutgoing());
        return map(
                "id", event.getMessageId(),
                "code", event.getEventCode(),
                "session", event.getSessionIdOrNull(),
                "timestamp", toIsoDateTime(event.getTimestamp()),
                "payload", event.getPayload()//TODO check this
        );
    }

    private static class OutgoingEventImpl implements OutgoingEvent {

        private final RawEvent event;

        public OutgoingEventImpl(RawEvent event) {
            checkArgument(event.isOutgoing());
            this.event = checkNotNull(event);
        }

        @Override
        public RawEvent getRawEvent() {
            return event;
        }

    }
}
