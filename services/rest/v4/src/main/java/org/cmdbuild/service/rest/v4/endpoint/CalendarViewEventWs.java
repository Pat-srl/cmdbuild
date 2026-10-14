package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.CalendarViewEventWsCommand;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("calendar/views/{viewId}/events/")
@Tag(name = "Calendar Events", description = "Calendar Events")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class CalendarViewEventWs {

    private final CalendarWsSerializationHelper calendarWsSerializationHelper;
    private final CalendarViewEventWsCommand command;

    public CalendarViewEventWs(CalendarWsSerializationHelper calendarWsSerializationHelper, CalendarViewEventWsCommand command) {
        this.calendarWsSerializationHelper = checkNotNull(calendarWsSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{eventId}")
    @Operation(
            summary = "Get a calendar event",
            description = "Get a calendar event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of calendar event data"),
                    @ApiResponse(responseCode = "404", description = "Calendar event not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested calendar event"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam("eventId") @Parameter(description = "Id of the event", schema = @Schema(type = "long")) Long eventId
    ) {
        //TODO access control
        CalendarEvent calendarEvent = command.doReadOne(eventId);
        return response(calendarWsSerializationHelper.serializeDetailedEvent(calendarEvent));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get calendar events",
            description = "Get calendar events",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of calendar event data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @PathParam("viewId") @Parameter(description = "Id of the view", schema = @Schema(type = "long")) String viewId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(DETAILED) @Parameter(description = "Whether to return detailed event information", schema = @Schema(type = "boolean")) @DefaultValue(FALSE) boolean detailed,
            @QueryParam(FILTER) @Parameter(description = "Filter string for the query", schema = @Schema(type = "string")) String filterStr
    ) {
        PagedElements<CalendarEvent> calendarEvents = command.doReadMany(viewId, limit, offset, filterStr);
        return response(calendarEvents.map(detailed ? calendarWsSerializationHelper::serializeDetailedEvent : calendarWsSerializationHelper::serializeEvent));
    }

}
