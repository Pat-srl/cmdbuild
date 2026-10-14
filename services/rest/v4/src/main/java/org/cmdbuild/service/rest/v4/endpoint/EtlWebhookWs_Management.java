/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.etl.webhook.WebhookConfig;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.EtlWebhookWsCommand;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.serializationhelpers.EtlWebhookSerializationHelper.filterAndApplySerialization;
import static org.cmdbuild.service.rest.common.serializationhelpers.EtlWebhookSerializationHelper.serializeDetailedWebhook;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

/**
 * @author ldare
 */
@Path("etl/webhook/")
@Tag(name = "ETL Webhooks")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class EtlWebhookWs_Management {

    private final ClasseRepository classeRepository;
    private final EtlWebhookWsCommand command;

    public EtlWebhookWs_Management(ClasseRepository classeRepository, EtlWebhookWsCommand command) {
        this.classeRepository = checkNotNull(classeRepository);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all active webhooks",
            description = "Obtain a list of all active ETL webhooks",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        FluentList<WebhookConfig> listWebhookConfig = command.doReadAll(WebhookConfig::isActive);
        return response(paged(filterAndApplySerialization(listWebhookConfig, wsQueryOptions, classeRepository), wsQueryOptions.getQuery()));
    }

    @GET
    @Path("{webhookId}/")
    @Operation(
            summary = "Get a specific webhook",
            description = "Obtain details of a specific ETL webhook",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "The webhook was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam("webhookId") @Parameter(description = "Id or code of the webhook", schema = @Schema(type = "string")) String idOrCode
    ) {
        WebhookConfig webhook = command.doReadOne(idOrCode);
        return response(serializeDetailedWebhook(webhook, classeRepository.getClasse(webhook.getTarget())));
    }
}
