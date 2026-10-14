/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.listener;

import com.google.common.eventbus.Subscribe;
import static groovy.json.JsonOutput.toJson;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import static org.cmdbuild.dao.postgres.listener.PostgresNotificationEvent.PG_NOTIFICATION_OUTPUT;
import static org.cmdbuild.dao.postgres.listener.PostgresNotificationEvent.PG_NOTIFICATION_TYPE_RESPONSE;
import org.cmdbuild.dao.postgres.listener.PostgresNotificationService;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.jobs.JobExecutorService.JOBUSER_SYSTEM;
import org.cmdbuild.jobs.JobSessionService;
import org.cmdbuild.script.ScriptService;
import org.cmdbuild.script.ScriptType;
import org.cmdbuild.syscommand.SysCommand;
import static org.cmdbuild.utils.io.CmIoUtils.isJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.exceptionToMessage;
import static org.cmdbuild.utils.lang.CmExecutorUtils.executorService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class CommandExecProcessorHelperService {//TODO merge with PostgresCommandProcessor

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ScriptService scriptService;
    private final PostgresNotificationService notificationService;
    private final ExecutorService processor;

    public CommandExecProcessorHelperService(JobSessionService sessionService, ScriptService scriptService, EventBusService eventBusService, PostgresNotificationService notificationService) {
        this.scriptService = checkNotNull(scriptService);
        this.notificationService = checkNotNull(notificationService);
        processor = executorService(getClass().getName(), () -> {
            MDC.put("cm_type", "sys");
            MDC.put("cm_id", "sys:command");
            sessionService.createJobSessionContextWithUser(JOBUSER_SYSTEM, "command exec processing job");
        }, sessionService::destroyJobSessionContext);
        eventBusService.getSysCommandEventBus().register(new Object() {
            @Subscribe
            public void handleSysCommand(SysCommand command) {
                processor.submit(safe(() -> {
                    switch (command.getAction()) {
                        case "eval" ->
                            eval(command);
                    }
                }));
            }

        });
    }

    @PreDestroy
    public void shutdown() {
        logger.debug("shutdown");
        shutdownQuietly(processor);
    }

    private void eval(SysCommand command) {//TODO improve this, duplicate code with sys ws
        Map<String, Object> response;
        try {
            logger.info("handle eval sys command {}", command.getId());
            Object output = scriptService.helper(getClass())
                    .withScript(command.get("script", String.class), parseEnumOrNull(command.get("language", String.class), ScriptType.class))
                    .executeForOutput("data", command.getData());
            logger.debug("raw output =< {} > ( {} )", output, getClassOfNullable(output).getName());
            if (output != null && (!(output instanceof String) || !isJson((String) output))) {
                output = toJson(output);
            }
            response = map("id", command.getId(), "success", true, PG_NOTIFICATION_OUTPUT, output);
        } catch (Exception ex) {
            logger.error("error processing sys eval command = {}", command.getId(), ex);
            response = map("id", command.getId(), "success", false, "message", exceptionToMessage(ex));
        }
//        tempService.putTempData(toJson(response), map("type", "pg_commad_response", "command_id", command.getId()));//TODO check this
        notificationService.sendMessage(PG_NOTIFICATION_TYPE_RESPONSE, response);//TODO check this, send response only for commands coming from postgres
    }

}
