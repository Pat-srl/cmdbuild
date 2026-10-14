/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/{typeName:cards|instances}/{" + CARD_ID + "}/bimvalue")
@Tag( name = "BIM Values", description = "Operations related to BIM values associated to cards")
@Produces(APPLICATION_JSON)
public class CardBimValueWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CardBimValueWs cardBimValueWs;

    public CardBimValueWs(org.cmdbuild.service.rest.v4.endpoint.CardBimValueWs cardBimValueWs) {
        this.cardBimValueWs = checkNotNull(cardBimValueWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get BIM value for a card",
            description = "Obtain the BIM object associated to a specific card. Optionally, include related BIM objects via navigation tree.",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = "if_exists", in = ParameterIn.QUERY, description = "Check if the BIM value exists"),
                    @Parameter(name = "include_related", in = ParameterIn.QUERY, description = "Include related BIM objects")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAllForCard(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            @QueryParam("if_exists") @DefaultValue(FALSE) Boolean checkIfExists,
            @QueryParam("include_related") @DefaultValue(FALSE) Boolean includeRelated
    ) {
        return cardBimValueWs.getAllForCard(classId, cardId, checkIfExists, includeRelated);
    }

}
