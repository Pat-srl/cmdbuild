package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("classes/{" + CLASS_ID + "}/cards/")
@Tag(name = "Card print", description = "Operations related to printing cards")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardPrintWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CardPrintWs cardPrintWs;

    public CardPrintWs(org.cmdbuild.service.rest.v4.endpoint.CardPrintWs cardPrintWs) {
        this.cardPrintWs = checkNotNull(cardPrintWs);
    }

    @GET
    @Path("{" + CARD_ID + "}/print/{file}")
    @Operation(
            summary = "Print a card",
            description = "Generate a report for a specific card",
            parameters = {
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = EXTENSION, in = ParameterIn.QUERY, description = "Extension")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful generation of report"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler readOne(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            @QueryParam(EXTENSION) String extension
    ) {
        return cardPrintWs.readOne(classId, cardId, extension);
    }
}
