package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.HistorySerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.CardHistoryWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElement;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;

@Path("{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/{" + TYPE_NAME + ":cards|instances}/{" + CARD_ID + "}/history")
@Tag(name = "History", description = "Operations related to history of cards")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class CardHistoryWs {

    private final CardWsSerializationHelperv3 cardWsSerializationHelperv3;
    private final HistorySerializationHelper historySerializationHelper;
    private final CardHistoryWsCommand command;

    public CardHistoryWs(CardWsSerializationHelperv3 cardWsSerializationHelperv3, HistorySerializationHelper historySerializationHelper, CardHistoryWsCommand command) {
        this.cardWsSerializationHelperv3 = checkNotNull(cardWsSerializationHelperv3);
        this.historySerializationHelper = checkNotNull(historySerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get history for a card",
            description = "Get history for a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of history data"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Card not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getHistory(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam(TYPES) @Parameter(description = "List of types to include in the history") @DefaultValue(CARDS) String types
    ) {
        PagedElements<DatabaseRecord> history = command.doGetHistory(classId, cardId, wsQueryOptions, types);
        return response(history.stream().map(
                        h -> {
                            if (wsQueryOptions.isDetailed()) {
                                Card record = command.doGetHistoryRecord(classId, cardId, h.getId());
                                return cardWsSerializationHelperv3.serializeCard(record).with(
                                        "_endDate", toIsoDateTime(record.getEndDate()),
                                        "_status", record.getCardStatus().name());
                            } else {
                                return historySerializationHelper.serializeBasicHistory(h);
                            }
                        }),
                history.totalSize());
    }

    @GET
    @Path("changes")
    @Operation(
            summary = "Get history for a card attribute changes",
            description = "Get history for a card attribute changes",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of history data"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Card not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getHistoryOnlyChanges(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam(TYPES) @Parameter(description = "List of types to include in the history") @DefaultValue(CARDS) String types
    ) {
        String attr;
        try {
            attr = getOnlyElement(wsQueryOptions.getQuery().getAttrs(), "CMO 999: history change can handle only one attribute");
        } catch (IllegalArgumentException ex) {
            throw illegalArgument("CMO 999: history change can handle only one attribute", ex);
        }
        List<Card> history = command.doGetHistoryOnlyChanges(classId, cardId, wsQueryOptions, types);

        Stream data = history.stream().map(h -> historySerializationHelper.serializeBasicHistory(h).with(cardWsSerializationHelperv3.serializeAttributeValue(h.getType(), attr, h.get(attr)))); // Add chosen attribute detail

        return response(data, history.size());
    }

    @GET
    @Path("{recordId}")
    @Operation(
            summary = "Get a specific history record for a card",
            description = "Get a specific history record for a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of history record data"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Card not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getHistoryRecord(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long id,
            @PathParam("recordId") @Parameter(description = "Id of the history record to query") Long recordId
    ) {
        Card record = command.doGetHistoryRecord(classId, id, recordId);
        return response(cardWsSerializationHelperv3.serializeCard(record).with(
                "_endDate", toIsoDateTime(record.getEndDate()),
                "_status", record.getCardStatus().name()));
    }
}
