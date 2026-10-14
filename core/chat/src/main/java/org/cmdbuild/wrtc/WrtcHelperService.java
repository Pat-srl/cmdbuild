/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.wrtc;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.Subscribe;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.cluster.ClusterMessageImpl;
import org.cmdbuild.cluster.ClusterMessageReceivedEvent;
import org.cmdbuild.cluster.ClusterService;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.event.EventService;
import org.cmdbuild.event.RawEvent;
import org.cmdbuild.event.WebsocketSessionClosedEvent;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WrtcHelperService {

    private final static String CLUSTER_EVENT_ADD_PEER = "cm_wrtc_join", CLUSTER_EVENT_REMOVE_PEER = "cm_wrtc_leave";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventService eventService;
    private final SessionService sessionService;
    private final CoreConfiguration coreConfiguration;
    private final ClusterService clusterService;

    private final CommunityRepository communityRepository = new CommunityRepository();//TODO move to service, cluster support, session expiration

    public WrtcHelperService(EventService eventService, SessionService sessionService, CoreConfiguration coreConfiguration, ClusterService clusterService) {
        this.eventService = checkNotNull(eventService);
        this.sessionService = checkNotNull(sessionService);
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.clusterService = checkNotNull(clusterService);
        eventService.getEventBus().register(new Object() {

            @Subscribe
            public void handleRawEvent(RawEvent event) {
                handleEvent(event);
            }

            @Subscribe
            public void handleWebsocketSessionClosedEvent(WebsocketSessionClosedEvent event) {
                removePeer(event.getClientId());
                clusterService.sendMessage(ClusterMessageImpl.builder().withMessageType(CLUSTER_EVENT_REMOVE_PEER).withMessageData(map("clientId", event.getClientId())).build());
            }
        });
        clusterService.getEventBus().register(new Object() {
            @Subscribe
            public void handleClusterMessageReceivedEvent(ClusterMessageReceivedEvent event) {
                switch (event.getMessageType()) {
                    case CLUSTER_EVENT_ADD_PEER:
                        addPeer(event.getData("sessionId"), event.getData("clientId"));
                        break;
                    case CLUSTER_EVENT_REMOVE_PEER:
                        removePeer(event.getData("clientId"));
                        break;
                }
            }

        });
    }

    private void handleEvent(RawEvent event) {
        if (event.hasAction() && event.getAction().startsWith("wrtc")) {
            if (coreConfiguration.isChatEnabled()) {
                switch (event.getAction()) {
                    case "wrtc.community.join": {
                        ClientInfo client = addPeer(event.getSessionId(), event.getClientId());
                        clusterService.sendMessage(ClusterMessageImpl.builder().withMessageType(CLUSTER_EVENT_ADD_PEER).withMessageData(map("sessionId", event.getSessionId(), "clientId", event.getClientId())).build());
                        eventService.sendEventMessage(event.getSessionId(), event.getClientId(), "wrtc.community.welcome", map(
                                "username", client.getUsername(),
                                "clientId", client.getClientId()
                        ));
                        communityRepository.getAll().forEach(c -> {//TODO improve this, incremental update
                            eventService.sendEventMessage(c.getSessionId(), c.getClientId(), "wrtc.community.update",
                                    map("clients", toJson(communityRepository.getAll().stream()
                                            .filter(e -> !equal(e.getClientId(), c.getClientId()))
                                            .sorted(Ordering.natural().onResultOf(ClientInfo::getUsername)).map(r -> map(
                                            //                                "sessionId", cc.getLeft(),
                                            "username", r.getUsername(),
                                            "clientId", r.getClientId()
                                    )).collect(toList()))));
                        });
                    }
                    break;
                    default: {
                        String targetId = checkNotBlank(event.getStringValue("_target"), "missing wrtc target");
                        ClientInfo target = communityRepository.getByClientId(targetId);
                        eventService.sendEventMessage(target.getSessionId(), target.getClientId(), event.getAction(), event.getPayload());
                    }
                }
            } else {
                logger.warn(marker(), "received wrtc message, but chat is disabled; event = {}", event);
            }
        }
    }

    private ClientInfo addPeer(String sessionId, String clientId) {
        logger.info("client = {} {} joined community", sessionId, clientId);
        ClientInfo client = new ClientInfo(sessionId, clientId, sessionService.getSessionById(sessionId).getOperationUser().getUsername());
        communityRepository.add(client);
        return client;
    }

    private void removePeer(String clientId) {
        checkNotBlank(clientId);
        if (coreConfiguration.isChatEnabled()) {
            communityRepository.removeByClientId(clientId);
            communityRepository.getAll().forEach(c -> {//TODO improve this, incremental update
                eventService.sendEventMessage(c.getSessionId(), c.getClientId(), "wrtc.community.update",
                        map("clients", toJson(communityRepository.getAll().stream()
                                .filter(e -> !equal(e.getClientId(), c.getClientId()))
                                .sorted(Ordering.natural().onResultOf(ClientInfo::getUsername)).map(r -> map(
                                //                                "sessionId", cc.getLeft(),
                                "username", r.getUsername(),
                                "clientId", r.getClientId()
                        )).collect(toList()))));
            });
        }
    }

    private static class ClientInfo {

        private final String clientId, sessionId, username;

        public ClientInfo(String sessionId, String clientId, String username) {
            this.clientId = checkNotBlank(clientId);
            this.sessionId = checkNotBlank(sessionId);
            this.username = checkNotBlank(username);
        }

        public String getClientId() {
            return clientId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getUsername() {
            return username;
        }

    }

    private static class CommunityRepository {

        private final Map<String, ClientInfo> clientsByClientId = new ConcurrentHashMap<>();

        public void add(ClientInfo client) {
            clientsByClientId.put(client.getClientId(), client);
        }

        public Collection<ClientInfo> getAll() {
            return clientsByClientId.values();
        }

        public ClientInfo getByClientId(String clientId) {
            return checkNotNull(clientsByClientId.get(checkNotBlank(clientId)), "client not found for client id =< %s >", clientId);
        }

        public void removeByClientId(String clientId) {
            clientsByClientId.remove(checkNotBlank(clientId));
        }

    }

}
