/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.providers;

import static com.google.common.base.Preconditions.checkNotNull;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.cmdbuild.auth.login.AuthenticationException;
import org.cmdbuild.auth.UnauthorizedAccessException;
import static org.cmdbuild.fault.FaultUtils.buildMessageListForResponse;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.utils.ws3.utils.Ws3Exception;
import org.cmdbuild.utils.ws3.utils.Ws3FrameworkException;
import static org.cmdbuild.fault.FaultLevel.FL_ERROR;

@Component
@Primary
public class ExceptionHandlerService implements ExceptionMapper<Exception> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FaultEventCollectorService errorAndWarningCollectorService;

    public ExceptionHandlerService(FaultEventCollectorService errorAndWarningCollectorService) {
        this.errorAndWarningCollectorService = checkNotNull(errorAndWarningCollectorService);
    }

    @Override
    public Response toResponse(Exception exception) {
        Throwable inner = getInner(exception);
        Response.Status status = getResponseStatus(inner);
        List messages;
        if (status == Response.Status.UNAUTHORIZED) {
            logger.warn("ws access denied (unauthorized)");
            logger.debug("ws access error", exception);
            messages = buildMessageListForResponse(FL_ERROR, true, "access denied");
        } else {
            logger.error("ws processing error", exception);
            messages = errorAndWarningCollectorService.buildMessagesForJsonResponse(inner);
        }
        return Response
                .status(status)
                .entity(map("success", false, "messages", messages))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    private Response.Status getResponseStatus(Throwable exception) {
        if (exception instanceof AuthenticationException || exception instanceof AccessDeniedException || exception instanceof UnauthorizedAccessException) {
            return Response.Status.UNAUTHORIZED; // TODO: this may be wrong, check
        } else if (exception instanceof IllegalArgumentException) {
            return Response.Status.BAD_REQUEST;
        } else if (exception.toString().toLowerCase().contains("not found")) {
            return Response.Status.NOT_FOUND;
        } else {
            return Response.Status.INTERNAL_SERVER_ERROR;//TODO error codes mapping
        }
    }

    private static Throwable getInner(Throwable ex) {
        Throwable inner = ex;
        while ((inner instanceof InvocationTargetException) || (inner instanceof Ws3Exception)) {
            inner = inner.getCause();
        }
        if (inner instanceof Ws3FrameworkException) {
            return ex;
        } else {
            return inner;
        }
    }

}
