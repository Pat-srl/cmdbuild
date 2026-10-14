/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysnotify;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.concurrent.ExecutorService;
import jakarta.annotation.PreDestroy;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_ACCESS;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.fault.FaultUtils;
import org.cmdbuild.fault.FaultLevel;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.event.EventService;
import static org.cmdbuild.event.RawEvent.ALERT_LEVEL;
import static org.cmdbuild.event.RawEvent.ALERT_LEVEL_ERROR;
import static org.cmdbuild.event.RawEvent.ALERT_LEVEL_INFO;
import static org.cmdbuild.event.RawEvent.ALERT_LEVEL_WARNING;
import static org.cmdbuild.event.RawEvent.ALERT_MESSAGE;
import static org.cmdbuild.event.RawEvent.ALERT_MESSAGE_SHOW_USER;
import static org.cmdbuild.event.RawEvent.EVENT_CODE_ALERT;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.executorService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.fault.FaultEvent;

@Component
public class SysnotifyServiceImpl implements SysnotifyService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ExecutorService executorService;

    private final EventService eventService;
    private final SessionService sessionService;
    private final CoreConfiguration coreConfiguration;

    public SysnotifyServiceImpl(EventService eventService, SessionService sessionService, CoreConfiguration coreConfiguration, RequestContextService contextService) {
        this.eventService = checkNotNull(eventService);
        this.sessionService = checkNotNull(sessionService);
        this.coreConfiguration = checkNotNull(coreConfiguration);
        checkNotNull(contextService);
        executorService = executorService(getClass().getName(), () -> contextService.initCurrentRequestContext("sys notification processing job"), contextService::destroyCurrentRequestContext);
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executorService);
    }

    @Override
    public void notifyJobErrors(String key, List<FaultEvent> collectedEvents) {
        executorService.submit(() -> {
            try {
                FaultLevel notificationEventLevel = coreConfiguration.getNotificationMessagesLevelThreshold();
                sessionService.getAllSessions().stream().filter(s -> s.getOperationUser().hasPrivileges(RP_ADMIN_ACCESS)).forEach(s -> {
                    collectedEvents.stream().filter(e -> e.hasLevel(notificationEventLevel)).map(FaultUtils::errorToMessages).flatMap(List::stream).forEach(e -> {
                        String level;
                        switch (e.getLevel()) {
                            case FL_ERROR:
                                level = ALERT_LEVEL_ERROR;
                                break;
                            case FL_WARNING:
                                level = ALERT_LEVEL_WARNING;
                                break;
                            case FL_INFO:
                            default:
                                level = ALERT_LEVEL_INFO;
                        }
                        eventService.sendEventMessage(s.getSessionId(), EVENT_CODE_ALERT, map(ALERT_LEVEL, level, ALERT_MESSAGE, e.getMessage(), ALERT_MESSAGE_SHOW_USER, e.showUser()));
                    });
                });
            } catch (Exception ex) {
                logger.error("error processing job error notifications", ex);
            }

        });
    }

}
