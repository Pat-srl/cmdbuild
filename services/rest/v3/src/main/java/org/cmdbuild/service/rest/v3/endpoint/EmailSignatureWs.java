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
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.endpoint.EmailSignatureWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.EmailSignatureWs_Management;
import org.cmdbuild.service.rest.v4.model.WsEmailSignatureData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;


@Path("email/signatures/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Tag(name = "Email Signatures", description = "Operations related to email signatures")
public class EmailSignatureWs {

    private final EmailSignatureWs_Administration emailSignatureWs_adm;
    private final EmailSignatureWs_Management emailSignatureWs_mng;

    public EmailSignatureWs(EmailSignatureWs_Administration emailSignatureWs_adm, EmailSignatureWs_Management emailSignatureWs_mng) {
        this.emailSignatureWs_adm = checkNotNull(emailSignatureWs_adm);
        this.emailSignatureWs_mng = checkNotNull(emailSignatureWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all email signatures",
            description = "Get all email signatures. If the user has admin view permissions, all email signatures will be returned. Otherwise, only email signatures for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = SORT, in = ParameterIn.QUERY, description = "Sorting to apply to the query"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information about email signatures", schema = @Schema(type = "boolean"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email signatures"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view email signatures"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) boolean detailed
    ) {
        if (isAdminViewMode(viewMode)) {
            return emailSignatureWs_adm.readAll(filterStr, sort, limit, offset, detailed);
        }
        return emailSignatureWs_mng.readAll(filterStr, sort, limit, offset, detailed);
    }

    @GET
    @Path("{signatureId}/")
    @Operation(
            summary = "Get email signature by id",
            description = "Get email signature by id. If the user has admin view permissions, the email signature will be returned if it exists, regardless of the user's management permissions for that email signature. Otherwise, the email signature will only be returned if the user has management permissions for it. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "signatureId", in = ParameterIn.PATH, description = "Id of the email signature to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email signature"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the email signature or the email signature does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("signatureId") String id
    ) {
        return emailSignatureWs_mng.read(id);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new email signature",
            description = "Create a new email signature",
            requestBody = @RequestBody(description = "Email signature data to create", required = true, content = @Content(schema = @Schema(implementation = WsEmailSignatureData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of email signature"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid email signature data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create email signatures"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            WsEmailSignatureData data
    ) {
        return emailSignatureWs_adm.create(data);
    }

    @PUT
    @Path("{signatureId}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update an existing email signature",
            description = "Update an existing email signature. The email signature to update is identified by the 'signatureId' path parameter. The request body must contain the new data for the email signature",
            parameters = {
                    @Parameter(name = "signatureId", in = ParameterIn.PATH, description = "Id of the email signature to update", required = true)
            },
            requestBody = @RequestBody(description = "New data for the email signature", required = true, content = @Content(schema = @Schema(implementation = WsEmailSignatureData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of email signature"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid email signature data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update email signatures or the email signature does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("signatureId") Long signatureId,
            WsEmailSignatureData data
    ) {
        return emailSignatureWs_adm.update(signatureId, data);
    }

    @DELETE
    @Path("{signatureId}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete an email signature",
            description = "Delete an email signature. The email signature to delete is identified by the 'signatureId' path parameter",
            parameters = {
                    @Parameter(name = "signatureId", in = ParameterIn.PATH, description = "Id of the email signature to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of email signature"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete email signatures or the email signature does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("signatureId") Long signatureId
    ) {
        return emailSignatureWs_adm.delete(signatureId);
    }
}
