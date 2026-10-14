/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.bim.BimObject;
import org.cmdbuild.service.rest.v4.command.CardBimValueWsCommand;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/{" + TYPE_NAME + ":cards|instances}/{" + CARD_ID + "}/bimvalue")
@Tag( name = "BIM Values", description = "Operations related to BIM values associated to cards")
@Produces(APPLICATION_JSON)
@Component
public class CardBimValueWs {

    private final CardBimValueWsCommand command;

    public CardBimValueWs(CardBimValueWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get BIM value for a card",
            description = "Obtain the BIM object associated to a specific card. Optionally, include related BIM objects via navigation tree.",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
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
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @QueryParam("if_exists") @Parameter(description = "Check if the BIM object exists") @DefaultValue(FALSE) Boolean checkIfExists,
            @QueryParam("include_related") @Parameter(description = "Include related BIM objects via navigation tree") @DefaultValue(FALSE) Boolean includeRelated
    ) {
        BimObject bimObject = command.doGetAllForCard(classId, cardId, checkIfExists, includeRelated);
        return response(map().accept(m -> {
            if (bimObject != null) {
                m.put("_id", bimObject.getId(),
                        "projectId", bimObject.getProjectId(),
                        "globalId", bimObject.getGlobalId(),
                        "_owner_type", bimObject.getOwnerClassId(),
                        "_owner_id", bimObject.getOwnerCardId());
            }
            if (checkIfExists) {
                m.put("exists", bimObject != null);
            }
        }));
    }
}