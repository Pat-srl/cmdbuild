package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.model.WsEmailData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EMAIL_ID;

@Path("email/error/")
@Tag(
        name = "Email error",
        description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild " +
                "in order to retrieve, create, update or delete an email error"
)
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
public class EmailErrorWs {

    private final org.cmdbuild.service.rest.v4.endpoint.EmailErrorWs emailErrorWs;

    public EmailErrorWs(org.cmdbuild.service.rest.v4.endpoint.EmailErrorWs emailErrorWs) {
        this.emailErrorWs = checkNotNull(emailErrorWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get emails in error state",
            description = "Get emails in error state",
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of emails in error state")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getErrorEmails() {
        return emailErrorWs.getErrorEmails();
    }

    @GET
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Get an email in error state",
            description = "Get an email in error state",
            parameters = { @Parameter(name = EMAIL_ID, description = "Id of the email to retrieve")},
            responses = { @ApiResponse(responseCode = "200", description = "Successful retrieval of email in error state")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(EMAIL_ID) Long emailId
    ) {
        return emailErrorWs.read(emailId);
    }

    @PUT
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Update an email in error state",
            description = "Update an email in error state",
            parameters = { @Parameter(name = EMAIL_ID, description = "Id of the email to update")},
            requestBody = @RequestBody(description = "Email data to update", required = true),
            responses = { @ApiResponse(responseCode = "200", description = "Successful update of email in error state")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(EMAIL_ID) Long emailId,
            WsEmailData emailData
    ) {
        return emailErrorWs.update(emailId, emailData);
    }

    @DELETE
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Delete an email in error state",
            description = "Delete an email in error state",
            parameters = { @Parameter(name = EMAIL_ID, description = "Id of the email to delete")},
            responses = {@ApiResponse(responseCode = "200", description = "Successful deletion of email in error state")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(EMAIL_ID) Long emailId
    ) {
        return emailErrorWs.delete(emailId);
    }

}
