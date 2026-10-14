/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cluster;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import static java.util.stream.Collectors.joining;
import static org.cmdbuild.cluster.ClusterCheckService.CLUSTER_CHECK_SERVICE_NAME;
import org.cmdbuild.dao.postgres.listener.PostgresNotificationEvent;
import org.cmdbuild.dao.postgres.listener.PostgresNotificationService;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.minions.PostStartup;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(CLUSTER_CHECK_SERVICE_NAME)
public class ClusterCheckServiceImpl implements ClusterCheckService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String EVENT_CODE = "org.cmdbuild.cluster.signal_alive_event", INSTANCE_INFO = "info", CLUSTER_NODE_ID = "node", BUILD_INFO = "build";

    private final ClusterService clusterService;
    private final PostgresNotificationService notificationService;
    private final BuildInfoService buildInfo;
    private final EventBus eventBus;

    public ClusterCheckServiceImpl(ClusterService clusterService, PostgresNotificationService notificationService, BuildInfoService buildInfo, EventBusService eventBusService) {
        this.clusterService = checkNotNull(clusterService);
        this.notificationService = checkNotNull(notificationService);
        this.buildInfo = checkNotNull(buildInfo);
        this.eventBus = eventBusService.getSystemEventBus();
        notificationService.getEventBus().register(new Object() {
            @Subscribe
            public void handlePostgresNotificationEvent(PostgresNotificationEvent event) {
                if (event.isEvent(EVENT_CODE)) {
                    String otherNodeInfo = toStringNotBlank(event.getData().get(INSTANCE_INFO)),
                            otherNodeId = toStringNotBlank(event.getData().get(CLUSTER_NODE_ID)),
                            thisBuildInfo = getBuildInfo(),
                            otherBuildInfo = toStringNotBlank(event.getData().get(BUILD_INFO));
                    if (!clusterService.isRunning()) {
                        logger.warn(marker(), "cluster service is not running, but detected another node active on this database =< {} > (THIS WILL CAUSE PROBLEMS)", otherNodeInfo);
                        eventBus.post(DisconnectedNodeDetectedEvent.DNDE_FAILURE);
                    } else if (clusterService.hasOtherKnownNodeForId(otherNodeId)) {
                        logger.debug("confirmed active node =< {} >", otherNodeInfo);
                    } else {
                        logger.warn(marker(), "detected node not connected to cluster service =< {} > ( connected nodes =< {} >, this node info =< {} > ) (will try to reconnect - if reconnection fails THIS WILL CAUSE PROBLEMS)",
                                otherNodeInfo, clusterService.getOtherClusterNodes().stream().map(ClusterNode::getNodeId).collect(joining(",")), buildLocalInstanceInfo());
                        eventBus.post(DisconnectedNodeDetectedEvent.DNDE_FAILURE);
                    }
                    if (!equal(otherBuildInfo, thisBuildInfo)) {
                        logger.warn(marker(), "detected node with mismatching build info! this note build info =< {} >, node =< {} > has build info =< {} > (THIS MAY CAUSE SERIOUS PROBLEMS)", thisBuildInfo, otherNodeInfo, otherBuildInfo);
                    }
                }
            }
        });
    }

    @PostStartup(delay = "PT5S")
    @ScheduledJob(value = "0 */1 * * * ?", persistRun = false) //run every 1 minute
    public void sendHeartbeatNotificationViaPostgresChannel() {
        if (!clusterService.isFailed()) {
            String info = buildLocalInstanceInfo();
            logger.debug("send notification with local instance info =< {} > to postgres notification channel", info);
            notificationService.sendEvent(EVENT_CODE, INSTANCE_INFO, info, CLUSTER_NODE_ID, clusterService.getNodeId(), BUILD_INFO, getBuildInfo());
        }
    }

    private String buildLocalInstanceInfo() {
        return clusterService.getNodeInfo();
    }

    private String getBuildInfo() {
        return buildInfo.getCommitInfo();
    }

}
