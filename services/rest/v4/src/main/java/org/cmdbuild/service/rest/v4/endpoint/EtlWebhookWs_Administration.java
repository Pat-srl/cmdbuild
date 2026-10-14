/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
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
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.etl.webhook.WebhookConfig;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.EtlWebhookWsCommand;
import org.cmdbuild.service.rest.v4.model.WsEtlWebhookData;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.serializationhelpers.EtlWebhookSerializationHelper.filterAndApplySerialization;
import static org.cmdbuild.service.rest.common.serializationhelpers.EtlWebhookSerializationHelper.serializeDetailedWebhook;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.alwaysTrue;

/**
 * @author ldare
 */
@Path("administration/etl/webhook/")
@Tags({
        @Tag( name = "ETL Webhooks", description = "APIs to manage ETL webhooks." ),
        @Tag( name = "Administration" )
})
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class EtlWebhookWs_Administration {

    private final ClasseRepository classeRepository;
    private final EtlWebhookWsCommand command;

    public EtlWebhookWs_Administration(ClasseRepository classeRepository, EtlWebhookWsCommand command) {
        this.classeRepository = checkNotNull(classeRepository);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all ETL webhooks",
            description = "Obtain a list of all ETL webhooks for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL webhooks"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
    public Object readAll(
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        FluentList<WebhookConfig> listWebhookConfig = command.doReadAll(alwaysTrue());
        return response(paged(filterAndApplySerialization(listWebhookConfig, wsQueryOptions, classeRepository), wsQueryOptions.getQuery()));
    }

    @GET
    @Path("{webhookId}/")
    @Operation(
            summary = "Get a specific ETL webhook",
            description = "Get a specific ETL webhook",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL webhook"),
                    @ApiResponse(responseCode = "404", description = "The ETL webhook was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
    public Object readOne(
            @PathParam("webhookId") @Parameter(description = "Id or code of the webhook", schema = @Schema(type = "string")) String idOrCode
    ) {
        WebhookConfig webhook = command.doReadOne(idOrCode);
        return response(serializeDetailedWebhook(webhook, classeRepository.getClasse(webhook.getTarget())));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new ETL webhook",
            description = "Create a new ETL webhook",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of ETL webhook"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "Webhook data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsEtlWebhookData.class))) WsEtlWebhookData data
    ) {
        WebhookConfig webhookConfig = command.doCreate(data);
        return response(serializeDetailedWebhook(webhookConfig, classeRepository.getClasse(data.getTarget())));
    }

    @PUT
    @Path("{webhookId}/")
    @Operation(
            summary = "Update an existing ETL webhook",
            description = "Update an existing ETL webhook",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of ETL webhook"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("webhookId") @Parameter(description = "Id or code of the webhook", schema = @Schema(type = "string")) String webhookId,
            @RequestBody(description = "Webhook data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsEtlWebhookData.class))) WsEtlWebhookData data
    ) {
        WebhookConfig webhookConfig = command.doUpdate(webhookId, data);
        return response(serializeDetailedWebhook(webhookConfig, classeRepository.getClasse(data.getTarget())));
    }

    @DELETE
    @Path("{webhookId}/")
    @Operation(
            summary = "Delete an ETL webhook",
            description = "Delete a specific ETL webhook",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful deletion of ETL webhook"),
                    @ApiResponse(responseCode = "404", description = "The ETL webhook was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_ETL_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("webhookId") @Parameter(description = "Id or code of the webhook", schema = @Schema(type = "string")) String templateName
    ) {
        command.doDelete(templateName);
        return success();
    }
}
