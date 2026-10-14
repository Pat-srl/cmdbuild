package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsRelationCopyParams;
import org.cmdbuild.service.rest.v4.model.WsRelationData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.RELATION_ID;

@Path("domains/{domainId}/relations")
@Tag( name = "Relations", description = "Operations about relations between cards")
@Produces(APPLICATION_JSON)
public class RelationWs {

    private final org.cmdbuild.service.rest.v4.endpoint.RelationWs relationWs;

    public RelationWs(org.cmdbuild.service.rest.v4.endpoint.RelationWs relationWs) {
        this.relationWs = checkNotNull(relationWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get relations for a domain",
            description = "Get relations for a domain",
            parameters = {
                    @Parameter(name = "domainId", in = ParameterIn.PATH, description = "Domain ID")
            },
            requestBody = @RequestBody(description = "Query options for filtering and sorting relations"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of relations data"),
                    @ApiResponse(responseCode = "404", description = "Domain not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @PathParam("domainId") String domainId,
            WsQueryOptions queryOptions
    ) {
        return relationWs.readAll(domainId, queryOptions);
    }

    @GET
    @Path("{relationId}/")
    @Operation(
            summary = "Get a specific relation",
            description = "Get a specific relation",
            parameters = {
                    @Parameter(name = "domainId", in = ParameterIn.PATH, description = "Domain ID"),
                    @Parameter(name = RELATION_ID, in = ParameterIn.PATH, description = "Relation ID")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of relation data"),
                    @ApiResponse(responseCode = "404", description = "Relation not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("domainId") String domainId,
            @PathParam(RELATION_ID) Long relationId
    ) {
        return relationWs.read(domainId, relationId);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a relation",
            description = "Create a relation",
            parameters = {
                    @Parameter(name = "domainId", in = ParameterIn.PATH, description = "Domain ID")
            },
            requestBody = @RequestBody(description = "Relation data to create"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of relation data"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam("domainId") String domainId,
            WsRelationData relationData
    ) {
        return relationWs.create(domainId, relationData);
    }

    @PUT
    @Path("{relationId}/")
    @Operation(
            summary = "Update a relation",
            description = "Update a relation",
            parameters = {
                    @Parameter(name = "domainId", in = ParameterIn.PATH, description = "Domain ID"),
                    @Parameter(name = RELATION_ID, in = ParameterIn.PATH, description = "Relation ID")
            },
            requestBody = @RequestBody(description = "Relation data to update"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of relation data"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "Relation not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("domainId") String domainId,
            @PathParam(RELATION_ID) Long relationId,
            WsRelationData relationData
    ) {
        return relationWs.update(domainId, relationId, relationData);
    }

    @DELETE
    @Path("{relationId}/")
    @Operation(
            summary = "Delete a relation",
            description = "Delete a relation",
            parameters = {
                    @Parameter(name = "domainId", in = ParameterIn.PATH, description = "Domain ID"),
                    @Parameter(name = RELATION_ID, in = ParameterIn.PATH, description = "Relation ID")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of relation data"),
                    @ApiResponse(responseCode = "404", description = "Relation not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("domainId") String domainId,
            @PathParam(RELATION_ID) Long relationId
    ) {
        return relationWs.delete(domainId, relationId);
    }

    @POST
    @Path("_ANY/move")
    @Operation(
            summary = "Move relations between cards",
            description = "Move relations between cards",
            parameters = {
                    @Parameter(name = "domainId", in = ParameterIn.PATH, description = "Domain ID")
            },
            requestBody = @RequestBody(description = "Parameters for moving relations"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful move of relations"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object moveManyRelations(
            @PathParam("domainId") String domainId,
            WsRelationCopyParams params
    ) {
        return relationWs.moveManyRelations(domainId, params);
    }

    @POST
    @Path("_ANY/copy")
    @Operation(
            summary = "Copy relations between cards",
            description = "Copy relations between cards",
            parameters = {
                    @Parameter(name = "domainId", in = ParameterIn.PATH, description = "Domain ID")
            },
            requestBody = @RequestBody(description = "Parameters for copying relations"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful copy of relations"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object copyManyRelations(
            @PathParam("domainId") String domainId,
            WsRelationCopyParams params
    ) {
        return relationWs.copyManyRelations(domainId, params);
    }
}
