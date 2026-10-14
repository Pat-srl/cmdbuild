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
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/{typeName:cards|instances}/{" + CARD_ID + "}/history")
@Tag(name = "History", description = "Operations related to history of cards")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardHistoryWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CardHistoryWs cardHistoryWs;

    public CardHistoryWs(org.cmdbuild.service.rest.v4.endpoint.CardHistoryWs cardHistoryWs) {
        this.cardHistoryWs = checkNotNull(cardHistoryWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get history for a card",
            description = "Get history for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = "types", in = ParameterIn.QUERY, description = "Types of history to include in the report", schema = @Schema(allowableValues = {"cards", "instances"}))
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful retrieval of history data")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getHistory(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId, WsQueryOptions wsQueryOptions,
            @QueryParam("types") @DefaultValue("cards") String types
    ) {
        return cardHistoryWs.getHistory(classId, cardId, wsQueryOptions, types);
    }

    /**
     * History for given card attribute changes.
     *
     * <b>Note</b>: pagination not handled, because all values have to be
     * actually loaded.
     *
     * @param classId
     * @param cardId
     * @param wsQueryOptions
     * @param types
     * @return
     */
    @GET
    @Path("changes")
    @Operation(
            summary = "Get history for a card attribute changes",
            description = "Get history for a card attribute changes",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = "types", in = ParameterIn.QUERY, description = "Types of history to include in the report", schema = @Schema(allowableValues = {"cards", "instances"}))
            },
            requestBody = @RequestBody(description = "Query options"),
            responses = { @ApiResponse(responseCode = "200", description = "Successful retrieval of history data")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getHistoryOnlyChanges(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            WsQueryOptions wsQueryOptions,
            @QueryParam("types") @DefaultValue("cards") String types
    ) {
        return cardHistoryWs.getHistoryOnlyChanges(classId, cardId, wsQueryOptions, types);
    }

    @GET
    @Path("{recordId}")
    @Operation(
            summary = "Get a specific history record for a card",
            description = "Get a specific history record for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = RECORD_ID, in = ParameterIn.PATH, description = "ID of the history record to query")
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful retrieval of history record data")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getHistoryRecord(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long id,
            @PathParam(RECORD_ID) Long recordId
    ) {
        return cardHistoryWs.getHistoryRecord(classId, id, recordId);
    }
}
