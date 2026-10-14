/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.platform;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.eventbus.Subscribe;
import java.io.File;
import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import org.cmdbuild.cluster.ClusterMessageImpl;
import org.cmdbuild.cluster.ClusterMessageReceivedEvent;
import org.cmdbuild.cluster.ClusterService;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.debuginfo.BuildInfo;
import org.cmdbuild.debuginfo.BuildInfoService;
import org.cmdbuild.event.EventService;
import org.cmdbuild.minions.PostStartup;
import static org.cmdbuild.platform.PlatformUtils.isLinux;
import static org.cmdbuild.platform.UpgradeUtils.validateWarData;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import static org.cmdbuild.utils.io.CmIoUtils.cmTmpDir;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PlatformServiceImpl implements PlatformService {

    private final static String CLUSTER_MESSAGE_RESTART_LOCAL_WEBAPP = "org.cmdbuild.platform.RESTART_LOCAL_WEBAPP";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;
    private final BuildInfoService buildInfoService;
    private final EventService eventService;
    private final ClusterService clusterService;

    public PlatformServiceImpl(DirectoryService directoryService, BuildInfoService buildInfoService, EventService eventService, ClusterService clusterService) {
        this.directoryService = directoryService;
        this.buildInfoService = buildInfoService;
        this.eventService = eventService;
        this.clusterService = checkNotNull(clusterService);
        clusterService.getEventBus().register(new Object() {

            @Subscribe
            public void handleClusterMessageReceivedEvent(ClusterMessageReceivedEvent event) {
                if (event.isOfType(CLUSTER_MESSAGE_RESTART_LOCAL_WEBAPP)) {
                    logger.info("received local webapp restart message");
                    fireWebappRestart();
                }
            }
        });
    }

    @Override
    public void stopContainer() {
        eventService.sendBroadcastAlert("system is shutting down NOW");
        executeCommand("stop");
    }

    @Override
    public void restartContainer() {
        eventService.sendBroadcastAlert("system is restarting NOW");
        executeCommand("restart");
    }

    @Override
    public void restartCluster() {
        eventService.sendBroadcastAlert("Restarting now! We'll be back soon.");
        clusterService.sendMessage(ClusterMessageImpl.builder().withMessageType(CLUSTER_MESSAGE_RESTART_LOCAL_WEBAPP).build());
        fireWebappRestart();
    }

    @Override
    public void upgradeLocalWebapp(byte[] newWarData) {
        BuildInfo buildInfo = validateWarData(newWarData);

        logger.info("upgrade local cmdbuild webapp code, cur version = {}, new version = {}", buildInfoService.getCommitInfo(), buildInfo.getCommitInfo());
        File newWarFile = new File(cmTmpDir(), format("%s.war", randomId()));
        writeToFile(newWarData, newWarFile);

        eventService.sendBroadcastAlert("system upgrade in progress, restarting NOW");
        executeCommand("upgrade", directoryService.getWebappDirectory().getAbsolutePath(), newWarFile.getAbsolutePath());
    }

    @PostStartup
    public void fixPermissions() {
        if (isLinux() && directoryService.hasWebappDirectory()) {
            fixFilePermission(new File(directoryService.getWebappDirectory(), "cmdbuild.sh"));
        }
    }

    private void executeCommand(String command, String... otherParams) {
        checkArgument(isLinux(), "platform service is not available on this host os");
        checkNotBlank(command);
        String tomcatDir = directoryService.getContainerDirectory().getAbsolutePath();
        fixFilePermission(new File(tomcatDir, "bin/startup.sh"));
        fixFilePermission(new File(tomcatDir, "bin/shutdown.sh"));
        String scriptContent = readToString(getClass().getResourceAsStream("/org/cmdbuild/platform/scripts/cmdbuild_platform_helper.sh"));
        File file = new File(cmTmpDir(), format("%s_script.sh", randomId()));
        file.getParentFile().mkdirs();
        writeToFile(scriptContent, file);
        executeProcess(list("/bin/bash", "-l", file.getAbsolutePath(), tomcatDir, command).with(otherParams));
    }

    private void fixFilePermission(File file) {
        if (file.exists() && !file.canExecute()) {
            logger.info("set executable {}", file.getName());
            file.setExecutable(true, false);
        }
    }

    /**
     *
     * modifying context.xml file, tomcat catch this and automatically it will
     * restart the webapp
     */
    private void fireWebappRestart() {
        File webappContextFile = directoryService.getWebappContextFile();
        webappContextFile.setLastModified(now().toEpochSecond() * 1000);
    }
}
