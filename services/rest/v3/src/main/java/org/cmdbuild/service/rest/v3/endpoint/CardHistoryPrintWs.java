package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/{typeName:cards|instances}/{" + CARD_ID + "}/history")
@Tag(name = "History", description = "Operations related to history of cards")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardHistoryPrintWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CardHistoryPrintWs cardHistoryPrintWs;

    public CardHistoryPrintWs(org.cmdbuild.service.rest.v4.endpoint.CardHistoryPrintWs cardHistoryPrintWs) {
        this.cardHistoryPrintWs = checkNotNull(cardHistoryPrintWs);
    }

    @GET
    @Path("print")
    @Operation(
            summary = "Print history report for a card",
            description = "Print history report for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = "attributes", in = ParameterIn.QUERY, description = "Attributes to include in the report"),
                    @Parameter(name = "types", in = ParameterIn.QUERY, description = "Types of history to include in the report", schema = @Schema(allowableValues = {"cards", "instances"}))
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful generation of history report")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printHistoryReport(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            WsQueryOptions wsQueryOptions,
            @QueryParam("attributes") String attributes,
            @QueryParam("types") @DefaultValue("cards") String types
    ) {
        return cardHistoryPrintWs.printHistoryReport(classId, cardId, wsQueryOptions, attributes, types);
    }
}
