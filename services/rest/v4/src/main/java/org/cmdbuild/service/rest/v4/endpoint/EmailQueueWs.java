package org.cmdbuild.service.rest.v4.endpoint;

import com.google.common.collect.Ordering;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.queue.EmailQueueService;
import org.cmdbuild.service.rest.v4.serializationhelpers.EmailSerializationHelper;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.WILDCARD;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EMAIL_ID;

@Path("administration/email/queue/")
@Tag(name = "Email queue", description = "Operations related to the email queue")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
@Component
public class EmailQueueWs {

    private final EmailSerializationHelper emailSerializationHelper;
    private final EmailService emailService;
    private final EmailQueueService queueService;

    public EmailQueueWs(EmailSerializationHelper emailSerializationHelper, EmailService emailService, EmailQueueService queueService) {
        this.emailSerializationHelper = checkNotNull(emailSerializationHelper);
        this.emailService = checkNotNull(emailService);
        this.queueService = checkNotNull(queueService);
    }

    @POST
    @Path("trigger")
    @Operation(
            summary = "Trigger the email queue",
            description = "Trigger the email queue",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object triggerQueue() {
        queueService.triggerEmailQueue();
        return success();
    }

    @GET
    @Path("outgoing/")
    @Operation(
            summary = "Get outgoing emails",
            description = "Get outgoing emails",
            responses = {@ApiResponse(responseCode = "200", description = "Successful operation")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getOutgoingEmails() {
        return response(emailService.getAllForOutgoingProcessing().stream().sorted(Ordering.natural().onResultOf(Email::getDate).reversed()).map(emailSerializationHelper::serializeBasicEmail));
    }

    @POST
    @Path("outgoing/{" + EMAIL_ID + "}/trigger")
    @Operation(
            summary = "Send a single outgoing email",
            description = "Send a single outgoing email",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object sendSingleEmail(
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to send") Long emailId
    ) {
        queueService.sendSingleEmail(emailId);
        return success();
    }

}
