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
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.endpoint.EtlWebhookWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.EtlWebhookWs_Management;
import org.cmdbuild.service.rest.v4.model.WsEtlWebhookData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;


@Path("etl/webhook/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Tag(name = "ETL Webhook", description = "ETL Webhook")
public class EtlWebhookWs {

    private final EtlWebhookWs_Administration etlWebhookWs_adm;
    private final EtlWebhookWs_Management etlWebhookWs_mng;

    public EtlWebhookWs(EtlWebhookWs_Administration etlWebhookWs_adm, EtlWebhookWs_Management etlWebhookWs_mng) {
        this.etlWebhookWs_adm = checkNotNull(etlWebhookWs_adm);
        this.etlWebhookWs_mng = checkNotNull(etlWebhookWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all ETL webhooks",
            description = "Get all ETL webhooks. If the user has admin view permissions, all webhooks will be returned. Otherwise, only webhooks for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "filter", in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = "limit", in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = "start", in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(ref = "DefaultLimitExample"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL webhooks data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view ETL webhooks"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            WsQueryOptions wsQueryOptions
    ) {
        if (isAdminViewMode(viewMode)) {
            return etlWebhookWs_adm.readAll(wsQueryOptions);
        }
        return etlWebhookWs_mng.readAll(wsQueryOptions);
    }

    @GET
    @Path("{webhookId}/")
    @Operation(
            summary = "Get an ETL webhook",
            description = "Get an ETL webhook by its ID or code",
            parameters = { @Parameter(name = "webhookId", in = ParameterIn.PATH, description = "ID or code of the ETL webhook to retrieve", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL webhook data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the ETL webhook"),
                    @ApiResponse(responseCode = "404", description = "Not found - no ETL webhook exists with the provided ID or code"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam("webhookId") String idOrCode
    ) {
        return etlWebhookWs_mng.readOne(idOrCode);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new ETL webhook",
            description = "Create a new ETL webhook. The webhook code must be unique and can only contain letters, numbers, underscores and hyphens",
            requestBody = @RequestBody(description = "Data for the new ETL webhook", required = true, content = @Content(schema = @Schema(implementation = WsEtlWebhookData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of ETL webhook"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the webhook code is not unique or contains invalid characters, or the request body is invalid"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create ETL webhooks"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            WsEtlWebhookData data
    ) {
        return etlWebhookWs_adm.create(data);
    }

    @PUT
    @Path("{webhookId}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update an ETL webhook",
            description = "Update an ETL webhook. The webhook code must be unique and can only contain letters, numbers, underscores and hyphens",
            parameters = {
                    @Parameter(name = "webhookId", in = ParameterIn.PATH, description = "ID or code of the ETL webhook to update")
            },
            requestBody = @RequestBody(description = "Data for updating the ETL webhook", required = true, content = @Content(schema = @Schema(implementation = WsEtlWebhookData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of ETL webhook"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the webhook code is not unique or contains invalid characters, or the request body is invalid"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update ETL webhooks or the ETL webhook does not exist"),
                    @ApiResponse(responseCode = "404", description = "Not found - no ETL webhook exists with the provided ID or code"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("webhookId") String webhookId,
            WsEtlWebhookData data
    ) {
        return etlWebhookWs_adm.update(webhookId, data);
    }

    @DELETE
    @Path("{webhookId}/")
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete an ETL webhook",
            description = "Delete an ETL webhook",
            parameters = { @Parameter(name = "webhookId", in = ParameterIn.PATH, description = "ID or code of the ETL webhook to delete") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of ETL webhook"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete ETL webhooks or the ETL webhook does not exist"),
                    @ApiResponse(responseCode = "404", description = "Not found - no ETL webhook exists with the provided ID or code"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("webhookId") String templateName
    ) {
        return etlWebhookWs_adm.delete(templateName);
    }
}
