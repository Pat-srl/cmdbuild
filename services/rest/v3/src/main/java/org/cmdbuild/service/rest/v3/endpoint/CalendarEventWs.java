package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsEventData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("calendar/events/")
@Tag(name = "Calendar Events", description = "Calendar Events")
@Produces(APPLICATION_JSON)
public class CalendarEventWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CalendarEventWs calendarEventWs;

    public CalendarEventWs(org.cmdbuild.service.rest.v4.endpoint.CalendarEventWs calendarEventWs) {
        this.calendarEventWs = checkNotNull(calendarEventWs);
    }

    @GET
    @Path("{eventId}")
    @Operation(
            summary = "Get the data of a calendar event",
            description = "Obtain the details of a specific calendar event",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the calendar event to retrieve"),
                    @Parameter(name = "includeStats", in = ParameterIn.QUERY, description = "Whether to include statistical data related to the calendar event in the response", schema = @Schema(type = "boolean", defaultValue = FALSE))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of calendar event data"),
                    @ApiResponse(responseCode = "404", description = "Calendar event not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested calendar event"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readOne(
            @PathParam("eventId") Long eventId,
            @QueryParam("includeStats") @DefaultValue(FALSE) Boolean includeStats
    ) {
        return calendarEventWs.readOne(eventId, includeStats);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get a list of calendar events",
            description = "Retrieve a list of calendar events with optional filtering, sorting, and pagination",
            parameters = {
                    @Parameter(name = "filter", in = ParameterIn.QUERY, description = "How to filter results", schema = @Schema(ref = "DefaultCalendarEventsFilterExample")),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, description = "How to order results", schema = @Schema(ref = "DefaultSortExample")),
                    @Parameter(name = "limit", in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = "offset", in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0"),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Include or not full details in the response", schema = @Schema(type = "boolean", defaultValue = FALSE)),
                    @Parameter(name = "positionOf", in = ParameterIn.QUERY, description = "Position of the event in the list of events", schema = @Schema(type = "integer", minimum = "0")),
                    @Parameter(name = "goToPage", in = ParameterIn.QUERY, description = "Whether to go to the page specified in the positionOf parameter", schema = @Schema(type = "boolean", defaultValue = TRUE))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of calendar events list"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested calendar events"),
                    @ApiResponse(responseCode = "404", description = "No calendar events found matching the query parameters"),
                    @ApiResponse(responseCode = "406", description = "Requested pagination parameters are invalid"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readMany(
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT)  String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE)  Boolean detailed,
            @QueryParam(POSITION_OF) Long positionOf,
            @QueryParam(POSITION_OF_GOTOPAGE) @DefaultValue(TRUE) Boolean goToPage) {//TODO improve this, auto processing of params in dao query options
        return calendarEventWs.readMany(filterStr, sort, limit, offset, detailed, positionOf, goToPage);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create a new calendar event",
            description =  "Create a new calendar event with the provided data",
            requestBody = @RequestBody( description = "Data of the calendar event to create", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of calendar event") ,
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to create a new calendar event"),
                    @ApiResponse(responseCode = "409", description = "Calendar event with the same ID already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object createUserEvent(
            WsEventData data
    ) {
        return calendarEventWs.createUserEvent(data);
    }

    @PUT
    @Path("{eventId}")
    @Operation(
            summary =  "Update an existing calendar event",
            description =  "Update the details of an existing calendar event identified by its ID",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the calendar event to update")
            },
            requestBody = @RequestBody( description = "Data of the calendar event to update", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of calendar event") ,
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to update the requested calendar event"),
                    @ApiResponse(responseCode = "404", description = "Calendar event not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object update(
            @PathParam("eventId") Long eventId,
            WsEventData data
    ) {
        return calendarEventWs.update(eventId, data);
    }

    @DELETE
    @Path("{eventId}/")
    @Operation(
            summary =  "Delete a calendar event",
            description =  "Delete an existing calendar event identified by its ID",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the calendar event to delete")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of calendar event"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to delete the requested calendar event"),
                    @ApiResponse(responseCode = "404", description = "Calendar event not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object delete(
            @PathParam("eventId") Long eventId
    ) {
        return calendarEventWs.delete(eventId);
    }

    @GET
    @Path("{eventId}/history")
    @Operation(
            summary =  "Get the history of a calendar event",
            description =  "Retrieve the history of changes made to a specific calendar event",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the calendar event to retrieve the history for"),
                    @Parameter(name = "types", in = ParameterIn.QUERY, description = "Types of history records to retrieve", schema = @Schema(type = "string", defaultValue = "cards"))
            },
            requestBody = @RequestBody( description = "Query parameters to filter the history records to retrieve"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of calendar event history"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested calendar event history"),
                    @ApiResponse(responseCode = "404", description = "Calendar event not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getHistory(
            @PathParam("eventId") Long eventId,
            WsQueryOptions wsQueryOptions,
            @QueryParam("types") @DefaultValue("cards") String types
    ) {
        return calendarEventWs.getHistory(eventId, wsQueryOptions, types);
    }

    @GET
    @Path("{eventId}/history/{recordId}")
    @Operation(
            summary =  "Get a specific history record of a calendar event",
            description =  "Retrieve a specific history record of changes made to a calendar event",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the calendar event to retrieve the history record for"),
                    @Parameter(name = RECORD_ID, in = ParameterIn.PATH, description = "Id of the history record to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of specific calendar event history record"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested calendar event history record"),
                    @ApiResponse(responseCode = "404", description = "Calendar event or history record not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getHistoryRecord(
            @PathParam("eventId") @Parameter( description = "Id of the event" ) Long eventId,
            @PathParam(RECORD_ID) @Parameter( description = "Id of the record") Long recordId
    ) {
        return calendarEventWs.getHistoryRecord(eventId, recordId);
    }

    @GET
    @Path("/print/{file}")
    @Operation(
            summary =  "Generate a report of calendar events",
            description =  "Generate and download a report of calendar events in the specified format",
            parameters = {
                    @Parameter(name = "extension", in = ParameterIn.QUERY, description = "File extension of the report to generate", schema = @Schema(type = "string", defaultValue = "pdf")),
                    @Parameter(name = "attributes", in = ParameterIn.QUERY, description = "Attributes to include in the report", schema = @Schema(type = "string", defaultValue = "all"))
            },
            requestBody = @RequestBody( description = "Query parameters to filter the calendar events to generate the report for"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful generation of calendar event report"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to generate the requested calendar event report"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printCalendarEventReport(
             WsQueryOptions wsQueryOptions,
            @QueryParam(EXTENSION) String extension,
            @QueryParam("attributes") String attributes
    ) {
        return calendarEventWs.printCalendarEventReport(wsQueryOptions, extension, attributes);
    }

}
