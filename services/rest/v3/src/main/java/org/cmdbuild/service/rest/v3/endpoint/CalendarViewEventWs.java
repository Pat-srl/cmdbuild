package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("calendar/views/{viewId}/events/")
@Tag(name = "Calendar Events", description = "Calendar Events")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CalendarViewEventWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CalendarViewEventWs calendarViewEventWs;

    public CalendarViewEventWs(org.cmdbuild.service.rest.v4.endpoint.CalendarViewEventWs calendarViewEventWs) {
        this.calendarViewEventWs = checkNotNull(calendarViewEventWs);
    }

    @GET
    @Path("{eventId}")
    @Operation(
            summary = "Get a calendar event",
            description = "Get a calendar event",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the calendar event to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of calendar event data"),
                    @ApiResponse(responseCode = "404", description = "Calendar event not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested calendar event"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam("eventId") Long eventId
    ) {
        return calendarViewEventWs.readOne(eventId);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get calendar events",
            description = "Get calendar events",
            parameters = {
                    @Parameter(name = "viewId", in = ParameterIn.PATH, description = "Id of the calendar view to retrieve events from"),
                    @Parameter(name = LIMIT, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0"),
                    @Parameter(name = DETAILED, description = "Include or not full details in the response"),
                    @Parameter(name = FILTER, description = "Filter string to apply to the query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of calendar event data"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "404", description = "Calendar view not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested calendar view"),
                    @ApiResponse(responseCode = "406", description = "Requested pagination parameters are invalid"),
                    @ApiResponse(responseCode = "416", description = "Requested range is outside the allowed range"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @PathParam("viewId") String viewId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) boolean detailed,
            @QueryParam(FILTER) String filterStr
    ) {
        return calendarViewEventWs.readMany(viewId, limit, offset, detailed, filterStr);
    }

}
