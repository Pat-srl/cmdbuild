/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.service.rest.v4.command.EmailAccountWsCommand;
import org.cmdbuild.service.rest.v4.model.WsEmailAccountData;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.services.serialization.EmailAccountSerializationHelper.*;


/**
 *
 * @author schursin
 */
@Path("administration/email/accounts/")
@Tags({
        @Tag(name = "Email Accounts", description = "APIs to manage email accounts."),
        @Tag(name = "Administration")
})
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class EmailAccountWs_Administration {

    private final EmailAccountService emailAccountService;
    private final EmailAccountWsCommand command;

    public EmailAccountWs_Administration(EmailAccountService emailAccountService, EmailAccountWsCommand command) {
        this.emailAccountService = checkNotNull(emailAccountService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all email accounts",
            description = "Obtain a list of all email accounts for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    public Object readAll(
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response") Boolean detailed
    ) {
        List<EmailAccount> emailAccountList = command.doReadAll(emailAccountService::getAll);
        return response(paged(emailAccountList, detailed ?
                        e -> serializeDetailedAccount(e, emailAccountService) :
                        e -> serializeBasicAccount(e),
                offset, limit));
    }

    @GET
    @Path("{accountId}/")
    @Operation(
            summary = "Get a specific email account",
            description = "Obtain a specific email account for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "The email account was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    public Object read(
            @PathParam(ACCOUNT_ID) @Parameter(description = "Id of the account") String idOrCode
    ) {
        EmailAccount account = command.doRead(idOrCode);
        return response(serializeDetailedAccount(account, emailAccountService).accept(m -> m.put("password", account.getPassword())));
    }

    @GET
    @Path("{accountId}/public")
    @Operation(
            summary = "Get a specific email account (public)",
            description = "Obtain a specific email account for the current user (public)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "The email account was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    public Object readPublic(
            @PathParam(ACCOUNT_ID) @Parameter(description = "Id of the account") String idOrCode
    ) {
        EmailAccount emailAccount = command.doRead(idOrCode);
        return response(serializePublicAccount(emailAccount));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new email account",
            description = "Create a new email account for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of email account"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "Email Account data", required = true, content = @Content(schema = @Schema(implementation = WsEmailAccountData.class))) WsEmailAccountData data
    ) {
        EmailAccount emailAccount = command.doCreate(data);
        return response(serializeDetailedAccount(emailAccount, emailAccountService));
    }

    @PUT
    @Path("{accountId}/")
    @Operation(
            summary = "Update an existing email account",
            description = "Update an existing email account for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of email account"),
                    @ApiResponse(responseCode = "404", description = "The email account was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(ACCOUNT_ID) @Parameter(description = "Id of the account") Long id,
            @RequestBody(description = "Email Account data", required = true, content = @Content(schema = @Schema(implementation = WsEmailAccountData.class))) WsEmailAccountData data
    ) {
        EmailAccount account = command.doUpdate(id, data);
        return response(serializeDetailedAccount(account, emailAccountService));
    }

    @DELETE
    @Path("{accountId}/")
    @Operation(
            summary = "Delete an exixting email account",
            description = "Delete an existing email account for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of email account"),
                    @ApiResponse(responseCode = "404", description = "The email account was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(ACCOUNT_ID) @Parameter(description = "Id of the account") Long id
    ) {
        command.doDelete(id);
        return success();
    }

    @POST
    @Path("_NEW/test")
    @Operation(
            summary = "Test an email account configuration",
            description = "Test an email account configuration",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful test of email account configuration"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    public Object testAccountConfig(
            @RequestBody(description = "Email Account data", required = true, content = @Content(schema = @Schema(implementation = WsEmailAccountData.class), examples = {})) WsEmailAccountData data
    ) {
        return command.doTestAccountConfig(data.toEmailAccount().build());
    }

    @POST
    @Path("{accountId}/test")
    @Operation(
            summary = "Test an existing email account configuration",
            description = "Test an existing email account configuration",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful test of email account configuration"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    public Object testExistingAccount(
            @PathParam(ACCOUNT_ID) @Parameter(description = "Id of the account") String idOrCode,
            @Nullable @RequestBody(description = "Email Account data", required = true, content = @Content(schema = @Schema(implementation = WsEmailAccountData.class), examples = {})) WsEmailAccountData data
    ) {
        return command.doTestExistingAccount(idOrCode, data);
    }
}
