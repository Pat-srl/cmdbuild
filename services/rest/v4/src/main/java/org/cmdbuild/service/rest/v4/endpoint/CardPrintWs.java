package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.command.CardPrintWsCommand;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("classes/{" + CLASS_ID + "}/cards/")
@Tag(name = "Card print", description = "Operations related to printing cards")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class CardPrintWs {

    private final CardPrintWsCommand command;

    public CardPrintWs(CardPrintWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{" + CARD_ID + "}/print/{file}")
    @Operation(
            summary = "Print a card",
            description = "Generate a report for a specific card",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful generation of report"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested card"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler readOne(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @QueryParam(EXTENSION) @Parameter(description = "Report extension to generate") String extension
    ) {
        return command.doReadOne(classId, cardId, extension);
    }
}
