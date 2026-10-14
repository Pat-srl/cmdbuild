package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.CalendarSequenceWsCommand;
import org.cmdbuild.service.rest.v4.model.WsSequenceData;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("calendar/sequences/")
@Tag(
        name = "Calendar Sequence",
        description = "The following documentation aims to illustrate the usage of the REST APIs provided by CMDBuild for sequences, providing examples on how to correctly manage sequences and generate events"
)
@Produces(APPLICATION_JSON)
@Component
public class CalendarSequenceWs {

    private final CalendarWsSerializationHelper calendarWsSerializationHelper;
    private final CalendarSequenceWsCommand command;

    public CalendarSequenceWs(CalendarWsSerializationHelper calendarWsSerializationHelper, CalendarSequenceWsCommand command) {
        this.calendarWsSerializationHelper = checkNotNull(calendarWsSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{sequenceId}/")
    @Operation(
            summary = "Get all data of a sequence",
            description = "Obtain the details of a specific sequence, with the possibility of including the related events",
            parameters = {
                    @Parameter(name = SEQUENCE_ID, description = "Id of the sequence", schema = @Schema(type = "long")),
                    @Parameter(name = "includeEvents", description = "If true the events will be included", schema = @Schema(type = "boolean")),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource"),
                    @ApiResponse(responseCode = "404", description = "Sequence not found"),
                    @ApiResponse(responseCode = "500", description = "Invalid sequence id")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readOne(
            @PathParam(SEQUENCE_ID) @Parameter(description = "Id of the sequence", schema = @Schema(minimum = "0")) Long sequenceId,
            @QueryParam("includeEvents") @Parameter(description = "If true the events will be included") @DefaultValue(FALSE) boolean includeEvents
    ) {
        CalendarSequence calendarSequence = command.doReadOne(sequenceId, includeEvents);
        return response(calendarWsSerializationHelper.serializeDetailedSequence(calendarSequence)); //TODO card access control, sequence access control
    }

    @GET
    @Path("by-card/{" + CARD_ID + "}")
    @Operation(
            summary = "Get sequences by card",
            description = "Obtain a list of sequences related to a specific card",
            parameters = {
                    @Parameter(name = CARD_ID, description = "Id of the card", schema = @Schema(type = "long")),
                    @Parameter(name = DETAILED, description = "Indicates whether to return the detailed data", schema = @Schema(type = "boolean")),
                    @Parameter(name = "includeEvents", description = "If true the events will be included", schema = @Schema(type = "boolean")),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource"),
                    @ApiResponse(responseCode = "404", description = "Sequence not found"),
                    @ApiResponse(responseCode = "500", description = "Invalid card id")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readManyByCard(
            @PathParam(CARD_ID) @Parameter(description = "Id of the card") Long cardId,
            @QueryParam(DETAILED) @Parameter(description = "Indicates whether to return the detailed data") @DefaultValue(FALSE) boolean detailed,
            @QueryParam("includeEvents") @Parameter(description = "If true the events will be included") @DefaultValue(FALSE) boolean includeEvents
    ) {
        List<CalendarSequence> sequences = command.doReadManyByCard(cardId, includeEvents);
        return response(sequences.stream().map(detailed ? calendarWsSerializationHelper::serializeDetailedSequence : CalendarWsSerializationHelper::serializeBasicSequence));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new sequence",
            description = "Create a new sequence with the provided data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource"),
                    @ApiResponse(responseCode = "500", description = "Sequence already exists for specified card and trigger")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object create(
             @RequestBody(description = "Sequence data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarSequenceCreationUpdateExample"))) WsSequenceData data
    ) {
        CalendarSequence sequence = command.doCreate(data);
        return response(calendarWsSerializationHelper.serializeDetailedSequence(sequence));
    }

    @PUT
    @Path("{sequenceId}/")
    @Operation(
            summary = "Update an existing sequence",
            description = "Update an existing sequence",
            parameters = {
                    @Parameter(name = SEQUENCE_ID, description = "Id of the sequence", schema = @Schema(type = "long")),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sequence Updated successfully"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource"),
                    @ApiResponse(responseCode = "404", description = "Sequence not found"),
                    @ApiResponse(responseCode = "500", description = "Invalid trigger id")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object update(
            @PathParam(SEQUENCE_ID) @Parameter(description = "Id of the calendar sequence") Long sequenceId,
            @RequestBody(description = "Sequence data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsSequenceData.class))) WsSequenceData data
    ) {
        CalendarSequence sequence = command.doUpdate(sequenceId, data);
        return response(calendarWsSerializationHelper.serializeDetailedSequence(sequence));
    }

    @DELETE
    @Path("{sequenceId}/")
    @Operation(
            summary = "Delete a sequence",
            description = "Remove a specific sequence",
            parameters = {
                    @Parameter(name = SEQUENCE_ID, description = "Id of the sequence", schema = @Schema(type = "long")),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sequence deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource"),
                    @ApiResponse(responseCode = "404", description = "Sequence not found"),
                    @ApiResponse(responseCode = "500", description = "Invalid sequence id")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object delete(
            @PathParam(SEQUENCE_ID) @Parameter(description = "Id of the sequence to delete", schema = @Schema( minimum = "0")) Long sequenceId
    ) {
        command.doDelete(sequenceId);
        return success();
    }

    @POST
    @Path("_ANY/generate-events")
    @Operation(
            summary = "Create event from sequence",
            description = "Generate events based on the provided data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sequence Updated successfully"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource"),
                    @ApiResponse(responseCode = "404", description = "Sequence not found"),
                    @ApiResponse(responseCode = "500", description = "Unknown time-zone")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getEventsPreview(
            @RequestBody(description = "Sequence data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsSequenceData.class))) WsSequenceData data
    ) {
        List<CalendarEvent> events = command.doGetEventsPreview(data);
        return response(events.stream().map(calendarWsSerializationHelper::serializeDetailedEvent).collect(toList()));
    }

}
