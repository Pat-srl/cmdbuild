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
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.config.EmailConfiguration;
import org.cmdbuild.email.EmailSignature;
import org.cmdbuild.service.rest.v4.command.EmailSignatureWsCommand;
import org.cmdbuild.service.rest.v4.model.WsEmailSignatureData;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.v4.serializationhelpers.EmailSignatureSerializationHelper.serializeSignature;

/**
 *
 * @author schursin
 */
@Path("administration/email/signatures/")
@Tags({
        @Tag(name = "Email Signatures", description = "APIs to manage email signatures."),
        @Tag(name = "Administration")}
)
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class EmailSignatureWs_Administration {

    private final EmailConfiguration emailConfiguration;
    private final ObjectTranslationService objectTranslationService;
    private final EmailSignatureWsCommand command;

    public EmailSignatureWs_Administration(EmailConfiguration emailConfiguration, ObjectTranslationService objectTranslationService, EmailSignatureWsCommand command) {
        this.emailConfiguration = checkNotNull(emailConfiguration);
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "List all email signatures",
            description = "Obtain a list of all email signatures",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email signatures"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    public Object readAll(
            @QueryParam(FILTER) @Parameter(description = "How to filter results", schema = @Schema(ref = "DefaultEmailFilter")) String filterStr,
            @QueryParam(SORT) @Parameter(description = "How to order results", schema = @Schema(ref = "DefaultSortExample")) String sort,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response", schema = @Schema(type = "boolean")) Boolean detailed
    ) {
        List<EmailSignature> listEmailSignature = command.doReadAll(sort, detailed, filterStr, limit, offset, false);
        return response(paged(listEmailSignature.stream().map(c -> serializeSignature(c, detailed, objectTranslationService, emailConfiguration)).collect(toList()), offset, limit));
    }

    @GET
    @Path("{signatureId}/")
    @Operation(
            summary = "Get a specific email signature",
            description = "Obtain a specific email signature",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email signature data"),
                    @ApiResponse(responseCode = "404", description = "The email signature was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    public Object read(
            @PathParam("signatureId") @Parameter(description = "Id of the email signature", schema = @Schema(type = "string")) String id
    ) {
        EmailSignature emailSignature = command.doRead(id);
        return response(serializeSignature(emailSignature, true, objectTranslationService, emailConfiguration));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new email signature",
            description = "Create a new email signature",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of email signature"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "Email Signature data", required = true, content = @Content(schema = @Schema(implementation = WsEmailSignatureData.class))) WsEmailSignatureData data
    ) {
        EmailSignature emailSignature = command.doCreate(data);
        return response(serializeSignature(emailSignature, true, objectTranslationService, emailConfiguration));
    }

    @PUT
    @Path("{signatureId}/")
    @Operation(
            summary = "Update an existing email signature",
            description = "Update an existing email signature",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of email signature"),
                    @ApiResponse(responseCode = "404", description = "The email signature was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("signatureId") @Parameter(description = "Id of the email signature", schema = @Schema(type = "long")) Long signatureId,
            @RequestBody(description = "Email Signature data", required = true, content = @Content(schema = @Schema(implementation = WsEmailSignatureData.class))) WsEmailSignatureData data
    ) {
        EmailSignature emailSignature = command.doUpdate(signatureId, data);
        return response(serializeSignature(emailSignature, true, objectTranslationService, emailConfiguration));
    }

    @DELETE
    @Path("{signatureId}/")
    @Operation(
            summary = "Delete an email signature",
            description = "Delete a specific email signature",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful deletion of email signature"),
                    @ApiResponse(responseCode = "404", description = "The email signature was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("signatureId") @Parameter(description = "Id of the email signature", schema = @Schema(type = "long")) Long signatureId
    ) {
        command.doDelete(signatureId);
        return success();
    }


}
