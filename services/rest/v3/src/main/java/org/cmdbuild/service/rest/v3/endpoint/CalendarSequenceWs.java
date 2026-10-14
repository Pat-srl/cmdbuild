package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.model.WsSequenceData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;

@Path("calendar/sequences/")
@Tag(
        name = "Calendar Sequence",
        description = "The following documentation aims to illustrate the usage of the REST APIs provided by CMDBuild for sequences, providing examples on how to correctly manage sequences and generate events"
)
@Produces(APPLICATION_JSON)
public class CalendarSequenceWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CalendarSequenceWs calendarSequenceWs;

    public CalendarSequenceWs(org.cmdbuild.service.rest.v4.endpoint.CalendarSequenceWs calendarSequenceWs) {
        this.calendarSequenceWs = checkNotNull(calendarSequenceWs);
    }

    @GET
    @Path("{sequenceId}/")
    @Operation(
            summary = "Get all data of a sequence",
            description = "Obtain the details of a specific sequence, with the possibility of including the related events",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarSequenceShowEventsSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Sequence not found", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarSequence404IdNotFound"))),
                    @ApiResponse(responseCode = "500", description = "Invalid sequence id")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readOne(
            @PathParam("sequenceId") @Parameter(description = "Id of the sequence", schema = @Schema(minimum = "0")) Long sequenceId,
            @QueryParam("includeEvents") @Parameter(description = "If true the events will be included") @DefaultValue(FALSE) boolean includeEvents
    ) {
        return calendarSequenceWs.readOne(sequenceId, includeEvents);
    }

    @GET
    @Path("by-card/{" + CARD_ID + "}")
    @Operation(
            summary = "Get sequences by card",
            description = "Obtain a list of sequences related to a specific card",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarSequenceShowEventsSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Sequence not found", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarSequence404IdNotFound"))),
                    @ApiResponse(responseCode = "500", description = "Invalid card id")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readManyByCard(
            @PathParam(CARD_ID) @Parameter(description = "Id of the card") Long cardId,
            @QueryParam(DETAILED) @Parameter(description = "Indicates whether to return the detailed data") @DefaultValue(FALSE) boolean detailed,
            @QueryParam("includeEvents") @Parameter(description = "If true the events will be included") @DefaultValue(FALSE) boolean includeEvents
    ) {
        return calendarSequenceWs.readManyByCard(cardId, detailed, includeEvents);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new sequence",
            description = "Create a new sequence with the provided data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarSequenceSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "Sequence already exists for specified card and trigger", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarSequence500AlreadyExists")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object create(
            @RequestBody(description = "Sequence data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarSequenceCreationUpdateExample"))) WsSequenceData data
    ) {
        return calendarSequenceWs.create(data);
    }

    @PUT
    @Path("{sequenceId}/")
    @Operation(
            summary = "Update an existing sequence",
            description = "Update an existing sequence",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sequence Updated successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarSequenceSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Sequence not found", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarSequence404IdNotFound"))),
                    @ApiResponse(responseCode = "500", description = "Invalid trigger id", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarSequence500NotExistingTrigger")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object update(
            @PathParam("sequenceId") @Parameter(description = "Id of the sequence") Long sequenceId,
            @RequestBody(description = "Sequence data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarSequenceCreationUpdateExample"))) WsSequenceData data
    ) {
        return calendarSequenceWs.update(sequenceId, data);
    }

    @DELETE
    @Path("{sequenceId}/")
    @Operation(
            summary = "Delete a sequence",
            description = "Remove a specific sequence",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sequence deleted successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APIDeleteSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Sequence not found", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarSequence404IdNotFound"))),
                    @ApiResponse(responseCode = "500", description = "Invalid sequence id")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object delete(
            @PathParam("sequenceId") @Parameter(description = "Id of the sequence to delete", schema = @Schema( minimum = "0")) Long sequenceId
    ) {
        return calendarSequenceWs.delete(sequenceId);
    }

    @POST
    @Path("_ANY/generate-events")
    @Operation(
            summary = "Create event from sequence",
            description = "Generate events based on the provided data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sequence Updated successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarSequenceGenerateEvent"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Sequence not found", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarSequence404IdNotFound"))),
                    @ApiResponse(responseCode = "500", description = "Unknown time-zone", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendar500TimeZoneError")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getEventsPreview(
            @RequestBody(description = "Sequence data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarSequenceGenerateEvent"))) WsSequenceData data
    ) {
        return calendarSequenceWs.getEventsPreview(data);
    }

}
