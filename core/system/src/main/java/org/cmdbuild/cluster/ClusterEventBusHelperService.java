package org.cmdbuild.cluster;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.Subscribe;
import java.util.concurrent.TimeUnit;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.encode.CmEncodeUtils.xdecodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.xencodeBytes;
import static org.cmdbuild.utils.io.CmIoUtils.deserializeObject;
import static org.cmdbuild.utils.io.CmIoUtils.serializeObject;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClusterEventBusHelperService {

    private final static String FORWARDED_EVENTBUS_EVENT_MESSAGE_TYPE = "eventbus.event",
            SYSTEM_EVENT_DATA = "data",
            SYSTEM_EVENT_EVENTBUS = "eventbus";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClusterService clusterService;
    private final EventBusService eventBusService;

    private final Cache<Integer, Integer> eventsFromOtherNodes = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();

    public ClusterEventBusHelperService(ClusterService clusterService, EventBusService eventBusService) {
        this.clusterService = checkNotNull(clusterService);
        this.eventBusService = checkNotNull(eventBusService);
        eventBusService.getAllByCode().forEach((k, b) -> b.register(new Object() {
            @Subscribe
            public void handleSystemEventForAllClusterNodes(SystemEventForAllClusterNodes event) {
                handleBusEventForClusterNodes(k, event);
            }
        }));
        eventBusService.getClusterMessagesEventBus().register(new Object() {

            @Subscribe
            public void handleClusterMessageReceivedEvent(ClusterMessageReceivedEvent event) {
                if (event.isOfType(FORWARDED_EVENTBUS_EVENT_MESSAGE_TYPE)) {
                    handleClusterMessageWithForwardedEventbusEvent(event);
                }
            }

        });
    }

    private void handleBusEventForClusterNodes(String eventbus, SystemEventForAllClusterNodes event) {
        logger.trace("received event = {} from eventbus =< {} >", event, eventbus);
        int id = System.identityHashCode(event);
        if (eventsFromOtherNodes.getIfPresent(id) == null) {
            logger.debug("forward event = {} from eventbus =< {} > to all other cluster nodes", event, eventbus);
            clusterService.sendMessage(ClusterMessageImpl.builder()
                    .withMessageType(FORWARDED_EVENTBUS_EVENT_MESSAGE_TYPE)
                    .withMessageData(map(SYSTEM_EVENT_DATA, xencodeBytes(serializeObject(event)), SYSTEM_EVENT_EVENTBUS, eventbus))
                    .build());
        }
    }

    private void handleClusterMessageWithForwardedEventbusEvent(ClusterMessageReceivedEvent message) {
        logger.debug("handle eventbus event cluster message = {}", message);
        SystemEventForAllClusterNodes event = deserializeObject(xdecodeBytes(message.<String>getData(SYSTEM_EVENT_DATA)));
        String eventbus = checkNotBlank(message.<String>getData(SYSTEM_EVENT_EVENTBUS));
        int id = System.identityHashCode(event);
        eventsFromOtherNodes.put(id, id);
        logger.debug("received event = {}, forward to for eventbus =< {} >", event, eventbus);
        eventBusService.getByCode(eventbus).post(event);
    }
}
