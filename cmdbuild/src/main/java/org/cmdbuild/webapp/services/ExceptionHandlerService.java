/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.services;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.cmdbuild.auth.login.AuthenticationException;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.fault.FaultEventCollector;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.fault.FaultLeveOrderErrorsFirst;
import org.cmdbuild.fault.FaultLevel;
import org.cmdbuild.fault.FaultUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

@Provider
public class ExceptionHandlerService implements ExceptionMapper<Exception> {

    private static final String MESSAGE_LEVEL_INFO = "INFO", MESSAGE_LEVEL_WARNING = "WARNING", MESSAGE_LEVEL_ERROR = "ERROR";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FaultEventCollectorService errorAndWarningCollectorService;

    public ExceptionHandlerService(FaultEventCollectorService errorAndWarningCollectorService) {
        this.errorAndWarningCollectorService = checkNotNull(errorAndWarningCollectorService);
    }

    @Override
    public Response toResponse(Exception exception) {
        Response.Status status = getResponseStatus(exception);
        List messages;
        if (status == Response.Status.UNAUTHORIZED) {
            logger.warn("ws access denied (unauthorized)");
            logger.debug("ws access error", exception);
            messages = list(map("level", MESSAGE_LEVEL_ERROR, "show_user", true, "message", "access denied"));
        } else {
            logger.error("ws processing error", exception);
            FaultEventCollector collector = errorAndWarningCollectorService.getCurrentRequestEventCollector();
            collector.addError(exception);
            messages = buildResponseMessages(collector);
        }
        return Response
                .status(status)
                .entity(map("success", false, "messages", messages))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    private Response.Status getResponseStatus(Exception exception) {
        if (exception instanceof AuthenticationException || exception instanceof AccessDeniedException) {
            return Response.Status.UNAUTHORIZED; // TODO: this may be wrong, check
        } else if (exception instanceof IllegalArgumentException) {
            return Response.Status.BAD_REQUEST;
        } else if (exception.toString().toLowerCase().contains("not found")) {
            return Response.Status.NOT_FOUND;
        } else {
            return Response.Status.INTERNAL_SERVER_ERROR;//TODO error codes mapping
        }
    }

    public static List buildResponseMessages(FaultEventCollector errors) {
        return buildResponseMessages(errors.getCollectedEvents());
    }

    public static List buildResponseMessages(List<FaultEvent> events) {
        return events.stream().sorted(Ordering.from(FaultLeveOrderErrorsFirst.INSTANCE).onResultOf(FaultEvent::getLevel)).map(ExceptionHandlerService::errorToMessage).flatMap(List::stream).collect(toList());
    }

    public static List<Object> errorToMessage(FaultEvent event) {
        return FaultUtils.errorToMessages(event).stream().map(e -> map("level", serializeLevel(e.getLevel()), "show_user", e.showUser(), "message", e.getMessage())).collect(toList());
    }

    private static String serializeLevel(FaultLevel level) {
        return switch (level) {
            case FL_ERROR ->
                MESSAGE_LEVEL_ERROR;
            case FL_WARNING ->
                MESSAGE_LEVEL_WARNING;
            case FL_INFO ->
                MESSAGE_LEVEL_INFO;
            default ->
                throw unsupported("unsupported message level = %s", level);
        };
    }
}
