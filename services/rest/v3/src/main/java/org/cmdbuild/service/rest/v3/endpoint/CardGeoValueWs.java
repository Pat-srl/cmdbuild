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
import org.cmdbuild.service.rest.v4.model.WsGisValue;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/{typeName:cards|instances}/{" + CARD_ID + "}/geovalues")
@Tag(name = "Geo values", description = "Operations related to geo values attached to cards")
@Produces(APPLICATION_JSON)
public class CardGeoValueWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CardGeoValueWs cardGeoValueWs;

    public CardGeoValueWs(org.cmdbuild.service.rest.v4.endpoint.CardGeoValueWs cardGeoValueWs) {
        this.cardGeoValueWs = checkNotNull(cardGeoValueWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get geo values for a card",
            description = "Get geo values for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query")
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful retrieval of geo values")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAllForCard(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId
    ) {
        return cardGeoValueWs.getAllForCard(classId, cardId);
    }

    @GET
    @Path("/{attributeId}")
    @Operation(
            summary = "Get geo value for a card",
            description = "Get geo value for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = "attributeId", in = ParameterIn.PATH, description = "ID of the attribute to query")
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful retrieval of geo value")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object get(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam("attributeId") String attributeId
    ) {
        return cardGeoValueWs.get(classId, cardId, attributeId);
    }

    @PUT
    @Path("/{attributeId}")
    @Operation(
            summary = "Set geo value for a card",
            description = "Set geo value for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = "attributeId", in = ParameterIn.PATH, description = "ID of the attribute to query"),
            },
            requestBody = @RequestBody(description = "Geo value data"),
            responses = { @ApiResponse(responseCode = "200", description = "Successful setting of geo value")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object set(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam("attributeId") String attributeId,
            WsGisValue data
    ) {
        return cardGeoValueWs.set(classId, cardId, attributeId, data);
    }

    @DELETE
    @Path("/{attributeId}")
    @Operation(
            summary = "Delete geo value for a card",
            description = "Delete geo value for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = "attributeId", in = ParameterIn.PATH, description = "ID of the attribute to query")
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful deletion of geo value")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam("attributeId") String attributeId
    ) {
        return cardGeoValueWs.delete(classId, cardId, attributeId);
    }
}
