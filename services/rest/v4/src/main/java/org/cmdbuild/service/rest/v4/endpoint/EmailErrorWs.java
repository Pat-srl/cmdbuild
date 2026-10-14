package org.cmdbuild.service.rest.v4.endpoint;

import com.google.common.collect.Ordering;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.EmailStatus;
import org.cmdbuild.service.rest.v4.model.WsEmailData;
import org.cmdbuild.service.rest.v4.serializationhelpers.EmailSerializationHelper;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EMAIL_ID;

@Path("administration/email/error/")
@Tag( name = "Email error", description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete an email error")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
@Component
public class EmailErrorWs {

    private final EmailService emailService;
    private final EmailSerializationHelper emailSerializationHelper;

    public EmailErrorWs(EmailService emailService, EmailSerializationHelper emailSerializationHelper) {
        this.emailService = checkNotNull(emailService);
        this.emailSerializationHelper = checkNotNull(emailSerializationHelper);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get emails in error state",
            description = "Get emails in error state",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of emails in error state"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getErrorEmails() {
        return response(emailService.getAllForErrorProcessing().stream().sorted(Ordering.natural().onResultOf(Email::getDate).reversed()).map(emailSerializationHelper::serializeBasicEmail));
    }

    @GET
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Get an email in error state",
            description = "Get an email in error state",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email in error state"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email") Long emailId
    ) {
        Email email = emailService.getOne(emailId);
        return response(emailSerializationHelper.serializeDetailedEmail(email));
    }

    @PUT
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Update an email in error state",
            description = "Update an email in error state",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of email in error state"),
                    @ApiResponse(responseCode = "404", description = "Email not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")

            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email") Long emailId,
            @RequestBody(description = "Email data", required = true) WsEmailData emailData
    ) {
        Email current = emailService.getOne(emailId);
        Email email = emailData.toEmail().withReference(current.getReference()).withId(emailId).withStatus(EmailStatus.ES_OUTGOING).withErrorCount(0).withDelay(0L).build();
        email = emailService.update(email);
        return response(emailSerializationHelper.serializeDetailedEmail(email));
    }

    @DELETE
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Delete an email in error state",
            description = "Delete an email in error state",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of email in error state"),
                    @ApiResponse(responseCode = "404", description = "Email not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email") Long emailId
    ) {
        emailService.delete(emailService.getOne(emailId));
        return success();
    }

}
