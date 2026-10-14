package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.CardRelationWsCommand;
import org.cmdbuild.service.rest.v4.model.WsRelationData;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/{" + TYPE_NAME + ":cards|instances}/{" + CARD_ID + "}/relations/")
@Tag(name = "Relations", description = "Operations about relations between cards")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class CardRelationWs {

    private final CardWsSerializationHelperv3 cardWsSerializationHelperv3;
    private final CardRelationWsCommand command;

    public CardRelationWs(CardWsSerializationHelperv3 cardWsSerializationHelperv3, CardRelationWsCommand command) {
        this.cardWsSerializationHelperv3 = checkNotNull(cardWsSerializationHelperv3);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get relations for a card",
            description = "Get relations for a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
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
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @QueryParam(LIMIT) @Parameter(description = "Limit the number of relations returned") Long limit,
            @QueryParam(START) @Parameter(description = "Offset the number of relations returned") Long offset,
            @QueryParam(DETAILED) @Parameter(description = "Return detailed information about relations") @DefaultValue(FALSE) Boolean detailed,
            @QueryParam(FILTER) @Parameter(description = "Filter relations by attribute") String filterStr,
            @QueryParam(SORT) @Parameter(description = "Sort relations by attribute") String sort) {
        PagedElements<CMRelation> relations = command.doRead(className, cardId, limit, offset, filterStr, sort);
        return response(relations.map(detailed ? cardWsSerializationHelperv3::serializeDetailedRelation : cardWsSerializationHelperv3::serializeMinimalRelation));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new relation",
            description = "Create a new relation between two cards",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of relation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @RequestBody(description = "Relation data", required = true) WsRelationData relationData
    ) {
        CMRelation relation = command.doCreate(cardId, relationData);

        return response(cardWsSerializationHelperv3.serializeDetailedRelation(relation));
    }

    @PUT
    @Path("{" + RELATION_ID + "}/")
    @Operation(
            summary = "Update an existing relation",
            description = "Update an existing relation between two cards",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of relation"),
                    @ApiResponse(responseCode = "404", description = "Relation not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(CARD_ID) @Parameter(description = "Id of card") Long cardId,
            @PathParam(RELATION_ID) @Parameter(description = "Id of the relation to update") Long relationId,
            @RequestBody(description = "Relation data", required = true) WsRelationData relationData
    ) {
        CMRelation relation = command.doUpdate(cardId, relationId, relationData);
        return response(cardWsSerializationHelperv3.serializeDetailedRelation(relation));
    }

    @DELETE
    @Path("{" + RELATION_ID + "}/")
    @Operation(
            summary = "Delete a relation",
            description = "Delete a specific relation between two cards",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful deletion of relation"),
                    @ApiResponse( responseCode = "404", description = "Relation not found"),
                    @ApiResponse( responseCode = "403", description = "Forbidden"),
                    @ApiResponse( responseCode = "401", description = "Unauthorized"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(RELATION_ID) @Parameter(description = "Id of the relation to delete") Long relationId
    ) {
        command.doDelete(relationId);
        return success();
    }
}
