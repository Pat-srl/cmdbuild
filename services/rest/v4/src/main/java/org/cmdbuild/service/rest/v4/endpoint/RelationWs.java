package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.RelationWsCommand;
import org.cmdbuild.service.rest.v4.model.WsRelationCopyParams;
import org.cmdbuild.service.rest.v4.model.WsRelationData;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DOMAIN_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.RELATION_ID;

@Path("domains/{" + DOMAIN_ID + "}/relations")
@Tag( name = "Relations", description = "Operations about relations between cards")
@Produces(APPLICATION_JSON)
@Component
public class RelationWs {

    private final CardWsSerializationHelperv3 cardWsSerializationHelperv3;
    private final RelationWsCommand command;

    public RelationWs(CardWsSerializationHelperv3 cardWsSerializationHelperv3, RelationWsCommand command) {
        this.cardWsSerializationHelperv3 = checkNotNull(cardWsSerializationHelperv3);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get relations for a domain",
            description = "Get relations for a domain",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of relations data"),
                    @ApiResponse(responseCode = "404", description = "Domain not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @PathParam(DOMAIN_ID) @Parameter(description = "Domain id", required = true) String domainId,
            @BeanParam WsQueryOptions queryOptions
    ) {
        PagedElements<CMRelation> relations = command.doReadAll(domainId, queryOptions);
        return response(relations.map(queryOptions.isDetailed() ? cardWsSerializationHelperv3::serializeDetailedRelation : cardWsSerializationHelperv3::serializeMinimalRelation));
    }

    @GET
    @Path("{relationId}/")
    @Operation(
            summary = "Get a specific relation",
            description = "Get a specific relation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of relation data"),
                    @ApiResponse(responseCode = "404", description = "Relation not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(DOMAIN_ID) @Parameter(description = "Domain id", required = true) String domainId,
            @PathParam(RELATION_ID) @Parameter(description = "Relation id", required = true) Long relationId
    ) {
        CMRelation relation = command.doRead(domainId, relationId);
        return response(cardWsSerializationHelperv3.serializeDetailedRelation(relation));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a relation",
            description = "Create a relation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of relation data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Domain not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam(DOMAIN_ID) @Parameter(description = "Domain id", required = true) String domainId,
            @RequestBody(description = "Relation data", required = true) WsRelationData relationData
    ) {
        CMRelation relation = command.doCreate(domainId, relationData);
        return response(cardWsSerializationHelperv3.serializeDetailedRelation(relation));
    }

    @PUT
    @Path("{relationId}/")
    @Operation(
            summary = "Update a relation",
            description = "Update a relation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of relation data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Relation not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(DOMAIN_ID) @Parameter(description = "Domain id", required = true) String domainId,
            @PathParam(RELATION_ID) @Parameter(description = "Relation id", required = true) Long relationId,
            @RequestBody(description = "Relation data", required = true) WsRelationData relationData
    ) {
        CMRelation relation = command.doUpdate(domainId, relationId, relationData);
        return response(cardWsSerializationHelperv3.serializeDetailedRelation(relation));
    }

    @DELETE
    @Path("{relationId}/")
    @Operation(
            summary = "Delete a relation",
            description = "Delete a relation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of relation data"),
                    @ApiResponse(responseCode = "404", description = "Relation not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(DOMAIN_ID) @Parameter(description = "Domain id", required = true) String domainId,
            @PathParam(RELATION_ID) @Parameter(description = "Relation id", required = true) Long relationId
    ) {
        command.doDelete(domainId, relationId);
        return success();
    }

    @POST
    @Path("_ANY/move")
    @Operation(
            summary = "Move relations between cards",
            description = "Move relations between cards",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful move of relations"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Domain not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object moveManyRelations(
            @PathParam(DOMAIN_ID) @Parameter(description = "Domain id", required = true) String domainId,
            @RequestBody(description = "Parameters of the relations to move (source and destination cards)", required = true) WsRelationCopyParams params
    ) {
        command.doMoveManyRelations(domainId, params);
        return success();
    }

    @POST
    @Path("_ANY/copy")
    @Operation(
            summary = "Copy relations between cards",
            description = "Copy relations between cards",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful copy of relations"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Domain not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object copyManyRelations(
            @PathParam(DOMAIN_ID) @Parameter(description = "Domain id", required = true) String domainId,
            @RequestBody(description = "Parameters of the relations to copy (source and destination cards)", required = true) WsRelationCopyParams params
    ) {
        command.doCopyManyRelations(domainId, params);
        return success();
    }
}
