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
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/{typeName:cards|instances}/{" + CARD_ID + "}/lock")
@Tag(name = "Card lock", description = "Operations related to card locks")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardLockWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CardLockWs cardLockWs;

    public CardLockWs(org.cmdbuild.service.rest.v4.endpoint.CardLockWs cardLockWs) {
        this.cardLockWs = checkNotNull(cardLockWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get lock for a card",
            description = "Get lock for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query")
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful retrieval of lock data") },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getLock(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query")  String classId,
            @PathParam(CARD_ID) Long cardId
    ) {
        return cardLockWs.getLock(cardId);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create lock for a card",
            description = "Create lock for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query")
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful creation of lock data") },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object createLock(
            @PathParam(CARD_ID) Long cardId
    ) {
        return cardLockWs.createLock(cardId);
    }

    @DELETE
    @Path("")
    @Operation(
            summary = "Release lock for a card",
            description = "Release lock for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query")
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful release of lock data") },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object releaseLock(
            @PathParam(CARD_ID) Long cardId
    ) {
        return cardLockWs.releaseLock(cardId);
    }


}
