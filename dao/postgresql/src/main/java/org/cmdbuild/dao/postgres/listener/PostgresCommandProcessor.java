/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.listener;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.concurrent.ExecutorService;
import jakarta.annotation.PreDestroy;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cluster.ClusterService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.platform.PlatformService;
import org.cmdbuild.requestcontext.RequestContextService;
import org.cmdbuild.syscommand.SysCommand;
import org.cmdbuild.syscommand.SysCommandImpl;
import static org.cmdbuild.utils.lang.CmExecutorUtils.executorService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class PostgresCommandProcessor {//TODO merge with CommandExecProcessorHelperService

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GlobalConfigService configService;
    private final CacheService cacheService;
    private final PostgresNotificationService notificationService;
    private final PlatformService platformService;
    private final EventBus sysCommandBus;
    private final ClusterService clusterService;
    private final ExecutorService processor;

    public PostgresCommandProcessor(ClusterService clusterService, RequestContextService contextService, PlatformService platformService, GlobalConfigService configService, CacheService cacheService, PostgresNotificationEventService eventService, PostgresNotificationService notificationService, EventBusService eventBusService) {
        this.configService = checkNotNull(configService);
        this.cacheService = checkNotNull(cacheService);
        this.notificationService = checkNotNull(notificationService);
        this.platformService = checkNotNull(platformService);
        this.sysCommandBus = eventBusService.getSysCommandEventBus();
        this.clusterService = checkNotNull(clusterService);
        processor = executorService(getClass().getName(), () -> {
            MDC.put("cm_type", "sys");
            MDC.put("cm_id", "pg:command");
            contextService.initCurrentRequestContext("postgres notification processing job");
        });
        eventService.getEventBus().register(new Object() {

            @Subscribe
            public void handlePostgresNotificationEvent(PostgresNotificationEvent event) {
                processor.submit(safe(() -> new CommandProcessor(event).handleCommand()));
            }

        });
    }

    @PreDestroy
    public void shutdown() {
        logger.debug("shutdown");
        shutdownQuietly(processor);
    }

    private class CommandProcessor {

        private final PostgresNotificationEvent event;
        private String action;

        public CommandProcessor(PostgresNotificationEvent event) {
            this.event = checkNotNull(event);
        }

        public void handleCommand() {
            try {
                if (equal(event.getChannel(), "cmevents")) {
                    if (event.isCommand()) {
                        action = event.getAction();
                        doHandleCommand();
                    }
                }
            } catch (Exception ex) {
                logger.error("error processing postgres notification event = {}", event, ex);
                notifyError("error processing message: " + ex);
            }
        }

        private void doHandleCommand() {
            switch (action.toLowerCase()) {
                case "reload" -> {
                    logger.info("system reload requested from postgres process");
                    configService.reload();
                    cacheService.invalidateAll();
                    notifySuccess("system reload completed");
                }
                case "restart" -> {
                    logger.info("system restart requested from postgres process");
                    notifySuccess("system restart requested");
                    platformService.restartContainer();
                }
                case "shutdown" -> {
                    logger.info("system shutdown requested from postgres process");
                    notifySuccess("system shutdown requested");
                    platformService.stopContainer();
                }
                case "test" -> {
                    logger.info("received TEST command from postres process = {} via channel = {}; raw payload = {}", event.getServerPid(), event.getChannel(), abbreviate(event.getPayload()));
                    notifySuccess("test OK");
                }
                default ->
                    sendSyscommandEvent(new SysCommandImpl(action, event.getData()));
            }
        }

        private void notifySuccess(String message) {
            notificationService.sendInfo("CMDBuild %s command: %s", action, checkNotBlank(message));
        }

        private void notifyError(String message) {
            notificationService.sendInfo("CMDBUild command error: %s", checkNotBlank(message));
        }

        private void sendSyscommandEvent(SysCommand command) {
            if (command.runOnAllClusterNodes() || clusterService.isActiveNodeForKey(command.getId())) {
                sysCommandBus.post(command);
            }

        }
    }

}
