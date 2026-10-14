/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.platform;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import org.cmdbuild.cluster.ClusterMessageImpl;
import org.cmdbuild.cluster.ClusterMessageReceivedEvent;
import org.cmdbuild.cluster.ClusterService;
import static org.cmdbuild.platform.UpgradeUtils.validateWarData;
import org.cmdbuild.temp.TempService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UpgradeHelperServiceImpl implements UpgradeHelperService {

    private final static String CLUSTER_MESSAGE_UPGRADE_WEBAPP = "org.cmdbuild.platform.UPGRADE_WEBAPP",
            CLUSTER_MESSAGE_TEMP_KEY_PARAM = "key";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TempService tempService;
    private final ClusterService clusterService;
    private final PlatformService platformService;

    public UpgradeHelperServiceImpl(TempService tempService, ClusterService clusterService, PlatformService platformService) {
        this.tempService = checkNotNull(tempService);
        this.clusterService = checkNotNull(clusterService);
        this.platformService = checkNotNull(platformService);
        clusterService.getEventBus().register(new Object() {

            @Subscribe
            public void handleClusterMessageReceivedEvent(ClusterMessageReceivedEvent event) {
                if (event.isOfType(CLUSTER_MESSAGE_UPGRADE_WEBAPP)) {
                    String key = event.getData(CLUSTER_MESSAGE_TEMP_KEY_PARAM);
                    logger.info("received cluster upgrade message with temp key = {}", key);
                    upgradeWebappFromTemp(key);
                }
            }

        });
    }

    @Override
    public void upgradeWebapp(byte[] newWarData) {
        validateWarData(newWarData);
        if (clusterService.isSingleActiveNode()) {
            logger.info("cluster not enabled or only one node is active, executing single-node upgrade");
            platformService.upgradeLocalWebapp(newWarData);
        } else {
            upgradeWebappOnCluster(newWarData);
        }
    }

    private void upgradeWebappOnCluster(byte[] newWarData) {
        logger.info("cluster enabled, preparing for cluster-wide upgrade");
        logger.info("store war data on temp storage");
        String key = tempService.putTempData(newWarData);
        logger.info("trigger cluster upgrade");
        clusterService.sendMessage(ClusterMessageImpl.builder().withMessageType(CLUSTER_MESSAGE_UPGRADE_WEBAPP).withMessageData(map(CLUSTER_MESSAGE_TEMP_KEY_PARAM, key)).build());
        logger.info("wait for cluster upgrade propagation");
        sleepSafe(3000);
        platformService.upgradeLocalWebapp(newWarData);
    }

    private void upgradeWebappFromTemp(String warTempKey) {
        logger.info("executing local node upgrade");
        byte[] newWarData = tempService.getTempDataBytes(warTempKey);
        platformService.upgradeLocalWebapp(newWarData);
    }

}
