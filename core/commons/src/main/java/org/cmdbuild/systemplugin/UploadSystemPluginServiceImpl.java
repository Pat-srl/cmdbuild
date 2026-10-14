/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.systemplugin;

import com.google.common.eventbus.Subscribe;
import jakarta.activation.DataSource;
import java.util.Collection;
import java.util.List;
import org.cmdbuild.cluster.ClusterMessageImpl;
import org.cmdbuild.cluster.ClusterMessageReceivedEvent;
import org.cmdbuild.cluster.ClusterService;
import org.cmdbuild.temp.TempService;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.fromListToString;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class UploadSystemPluginServiceImpl implements UploadSystemPluginService {

    private final static String CLUSTER_MESSAGE_UPLOAD_PLUGIN = "org.cmdbuild.systemplugin.UPLOAD_PLUGIN",
            CLUSTER_MESSAGE_TEMP_KEY_PARAM = "key";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SystemPluginService systemPluginService;
    private final TempService tempService;
    private final ClusterService clusterService;

    public UploadSystemPluginServiceImpl(SystemPluginService systemPluginService, TempService tempService, ClusterService clusterService) {
        this.systemPluginService = checkNotNull(systemPluginService);
        this.tempService = checkNotNull(tempService);
        this.clusterService = checkNotNull(clusterService);
        clusterService.getEventBus().register(new Object() {

            @Subscribe
            public void handleClusterMessageReceivedEvent(ClusterMessageReceivedEvent event) {
                if (event.isOfType(CLUSTER_MESSAGE_UPLOAD_PLUGIN)) {
                    String key = event.getData(CLUSTER_MESSAGE_TEMP_KEY_PARAM);
                    logger.info("received cluster upload message with temp key = {}", key);
                    uploadPluginFromTemp(key);
                }
            }
        });

    }

    @Override
    public void deploySystemPlugins(Collection<DataSource> dataFiles) {
        if (clusterService.isSingleActiveNode()) {
            logger.info("cluster not enabled or only one node is active, executing single-node upload plugin");
        } else {
            logger.info("cluster enabled, preparing for cluster-wide upload");
            logger.info("store plugin jar data on temp storage");
            String key = fromListToString(list(dataFiles).map(tempService::putTempData));
            logger.info("trigger cluster upload");
            clusterService.sendMessage(ClusterMessageImpl.builder().withMessageType(CLUSTER_MESSAGE_UPLOAD_PLUGIN).withMessageData(map(CLUSTER_MESSAGE_TEMP_KEY_PARAM, key)).build());
            logger.info("wait for cluster upload propagation");
            sleepSafe(3000);
        }
        systemPluginService.deploySystemPlugins(dataFiles);
    }

    private void uploadPluginFromTemp(String pluginTempKey) {
        logger.info("executing local node upload");
        List<DataSource> files = list(toListOfStrings(pluginTempKey)).map(tempService::getTempData).map(tempPlugin -> newDataSource(() -> tempPlugin.getInputStream(), tempPlugin.getContentType(), tempPlugin.getName()));
        systemPluginService.deploySystemPlugins(files);
    }
}
