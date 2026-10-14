package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;

import org.cmdbuild.service.rest.v4.model.WsRelationData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/{typeName:cards|instances}/{" + CARD_ID + "}/relations/")
@Tag(name = "Relations", description = "Operations about relations between cards")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardRelationWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CardRelationWs cardRelationWs;

    public CardRelationWs(org.cmdbuild.service.rest.v4.endpoint.CardRelationWs cardRelationWs) {
        this.cardRelationWs = checkNotNull(cardRelationWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get relations for a card",
            description = "Get relations for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0"),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Include or not full details in the response", schema = @Schema(type = "boolean", defaultValue = "false")),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter string to apply to the query"),
                    @Parameter(name = SORT, in = ParameterIn.QUERY, description = "How to order results", schema = @Schema(ref = "DefaultSortExample"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of relations"),
                    @ApiResponse(responseCode = "404", description = "Card not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String className,
            @PathParam(CARD_ID) @Parameter(description = "Identifier of the card to query") Long cardId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response") Boolean detailed,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort
    ) {
        return cardRelationWs.read(className, cardId, limit, offset, detailed, filterStr, sort);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new relation",
            description = "Create a new relation between two cards",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query")
            },
            requestBody = @RequestBody(description = "Relation Data"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of relation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam(CARD_ID) @Parameter(description = "Identifier of the card to query") Long cardId,
            WsRelationData relationData
    ) {
        return cardRelationWs.create(cardId, relationData);
    }

    @PUT
    @Path("{" + RELATION_ID + "}/")
    @Operation(
            summary = "Update an existing relation",
            description = "Update an existing relation between two cards",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = RELATION_ID, in = ParameterIn.PATH, description = "Id of the relation to update")
            },
            requestBody = @RequestBody(description = "Relation Data"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of relation"),
                    @ApiResponse(responseCode = "404", description = "Relation not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(CARD_ID)  @Parameter(description = "Identifier of the card to update") Long cardId,
            @PathParam(RELATION_ID) @Parameter( description = "Id of the relation to update") Long relationId,
            @Parameter(schema = @Schema(implementation = WsRelationData.class)) WsRelationData relationData
    ) {
        return cardRelationWs.update(cardId, relationId, relationData);
    }

    @DELETE
    @Path("{" + RELATION_ID + "}/")
    @Operation(
            summary = "Delete a relation",
            description = "Delete a specific relation between two cards",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = RELATION_ID, in = ParameterIn.PATH, description = "Id of the relation to delete")
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful deletion of relation"),
                    @ApiResponse( responseCode = "404", description = "Relation not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(RELATION_ID) @Parameter( description = "Id of the relation to delete") Long relationId
    ) {
        return cardRelationWs.delete(relationId);
    }
}
