package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.WILDCARD;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EMAIL_ID;

@Path("email/queue/")
@Tag(name = "Email queue", description = "Operations related to the email queue")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
public class EmailQueueWs {

    private final org.cmdbuild.service.rest.v4.endpoint.EmailQueueWs emailQueueWs;

    public EmailQueueWs(org.cmdbuild.service.rest.v4.endpoint.EmailQueueWs emailQueueWs) {
        this.emailQueueWs = checkNotNull(emailQueueWs);
    }

    @POST
    @Path("trigger")
    @Operation(
            summary = "Trigger the email queue",
            description = "Trigger the email queue",
            responses = {@ApiResponse(responseCode = "200", description = "Successful operation")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object triggerQueue() {
        return emailQueueWs.triggerQueue();
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
        return emailQueueWs.getOutgoingEmails();
    }

    @POST
    @Path("outgoing/{" + EMAIL_ID + "}/trigger")
    @Operation(
            summary = "Send a single outgoing email",
            description = "Send a single outgoing email",
            parameters = { @Parameter( name = EMAIL_ID, description = "Id of the email to send", required = true)},
            responses = { @ApiResponse(responseCode = "200", description = "Successful operation")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object sendSingleEmail(
            @PathParam(EMAIL_ID) Long emailId
    ) {
        return emailQueueWs.sendSingleEmail(emailId);
    }

}
