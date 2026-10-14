package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.endpoint.EmailAccountWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.EmailAccountWs_Management;
import org.cmdbuild.service.rest.v4.model.WsEmailAccountData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("email/accounts/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Tag(name = "Email Accounts", description = "Operations related to email accounts")
public class EmailAccountWs {

    private final EmailAccountWs_Administration emailAccountWs_adm;
    private final EmailAccountWs_Management emailAccountWs_mng;

    public EmailAccountWs(EmailAccountWs_Administration emailAccountWs_adm, EmailAccountWs_Management emailAccountWs_mng) {
        this.emailAccountWs_adm = checkNotNull(emailAccountWs_adm);
        this.emailAccountWs_mng = checkNotNull(emailAccountWs_mng);
    }

    @GET
    @Path(EMPTY)
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    @Operation(
            summary = "Get all email accounts",
            description = "Get all email accounts. If the user has admin view permissions, all email accounts will be returned. Otherwise, only email accounts for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information about email accounts in the response, such as the configuration of the email account" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email accounts data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view email accounts"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed
    ) {
        return emailAccountWs_adm.readAll(limit, offset, detailed);
    }

    @GET
    @Path("{accountId}/")
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    @Operation(
            summary = "Get an email account by id or code",
            description = "Get an email account by id or code. If the user has admin view permissions, the email account will be returned if it exists, regardless of the user's management permissions for that email account. Otherwise, the email account will only be returned if the user has management permissions for it. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "accountId", in = ParameterIn.PATH, description = "Id or code of the email account to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email account data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the email account or the email account does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("accountId") String idOrCode
    ) {
        return emailAccountWs_adm.read(idOrCode);
    }

    @GET
    @Path("{accountId}/public")
    @Operation(
            summary = "Get public information of an email account by id or code",
            description = "Get public information of an email account by id or code. This endpoint can be accessed by any authenticated user and returns only public information about the email account, such as its name and description, without exposing sensitive configuration details. The email account will be returned if it exists and the user has at least view permissions for it",
            parameters = {
                    @Parameter(name = "accountId", in = ParameterIn.PATH, description = "Id or code of the email account to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email account public information"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the email account or the email account does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readPublic(
            @PathParam("accountId") String idOrCode
    ) {
        return emailAccountWs_mng.read(idOrCode);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new email account",
            description = "Create a new email account. The user must have admin permissions for email accounts to perform this operation.",
            requestBody = @RequestBody(description = "Email account data to create", required = true, content = @Content(schema = @Schema(implementation = WsEmailAccountData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of email account"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid email account data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create email accounts"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            WsEmailAccountData data
    ) {
        return emailAccountWs_adm.create(data);
    }

    @PUT
    @Path("{accountId}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update an email account",
            description = "Update an email account. The user must have admin permissions for email accounts to perform this operation.",
            parameters = { @Parameter(name = "accountId", in = ParameterIn.PATH, description = "ID of the email account to update", required = true)},
            requestBody = @RequestBody(description = "Email account data to update", required = true, content = @Content(schema = @Schema(implementation = WsEmailAccountData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of email account"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid email account data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update email accounts or the email account does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("accountId") Long id,
            WsEmailAccountData data
    ) {
        return emailAccountWs_adm.update(id, data);
    }

    @DELETE
    @Path("{accountId}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete an email account",
            description = "Delete an email account. The user must have admin permissions for email accounts to perform this operation.",
            parameters = { @Parameter(name = "accountId", in = ParameterIn.PATH, description = "ID of the email account to delete", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of email account"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete email accounts or the email account does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("accountId") Long id
    ) {
        return emailAccountWs_adm.delete(id);
    }

    @POST
    @Path("_NEW/test")
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    @Operation(
            summary = "Test email account configuration",
            description = "Test email account configuration. This endpoint allows testing the connection to the email server using the provided email account configuration data without creating a new email account. The user must have admin view permissions for email accounts to perform this operation.",
            requestBody = @RequestBody(description = "Email account configuration data to test", required = true, content = @Content(schema = @Schema(implementation = WsEmailAccountData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful test of email account configuration"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid email account configuration data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to test email account configuration"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object testAccountConfig(
            WsEmailAccountData data
    ) {
        return emailAccountWs_adm.testAccountConfig(data);
    }

    @POST
    @Path("{accountId}/test")
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    @Operation(
            summary = "Test existing email account configuration",
            description = "Test existing email account configuration. This endpoint allows testing the connection to the email server using the configuration of an existing email account identified by its id or code, with the option to override the existing configuration with the provided email account configuration data in the request body. The user must have admin view permissions for email accounts to perform this operation and must have at least view permissions for the email account being tested.",
            requestBody = @RequestBody(description = "Email account configuration data to test", content = @Content(schema = @Schema(implementation = WsEmailAccountData.class))),
            parameters = { @Parameter(name = "accountId", in = ParameterIn.PATH, description = "Id or code of the email account to test", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful test of email account configuration"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid email account configuration data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to test email account configuration or the email account does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object testExistingAccount(
            @PathParam("accountId") String idOrCode,
            @Nullable WsEmailAccountData data
    ) {
        return emailAccountWs_adm.testExistingAccount(idOrCode, data);
    }
}

