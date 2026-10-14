/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.calendar.beans.CalendarTrigger;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.CalendarTriggerWsCommand;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;

/**
 * @author ldare
 */
@Path("calendar/triggers/")
@Tag(
        name = "Calendar Trigger",
        description = "The following documentation aims to illustrate the usage of the REST APIs provided by CMDBuild for calendars, providing examples on how to correctly manage schedulers and creating new calendar rules"
)
@Produces(APPLICATION_JSON)
@Component
public class CalendarTriggerWs_Management {

    private final CalendarWsSerializationHelper calendarWsSerializationHelper;
    private final CalendarTriggerWsCommand command;

    public CalendarTriggerWs_Management(CalendarWsSerializationHelper calendarWsSerializationHelper, CalendarTriggerWsCommand command) {
        this.calendarWsSerializationHelper = checkNotNull(calendarWsSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{triggerId}/")
    @Operation(
            summary = "Get the data of a trigger",
            description = "Obtain the details of a specific trigger",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarTriggerSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "No such element", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarTrigger500NoSuchElement")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readOne(
            @PathParam("triggerId") @Parameter(description = "Id of the trigger", schema = @Schema(type = "long")) Long triggerId
    ) {
        CalendarTrigger calendarTrigger = command.doReadOne(triggerId);
        return response(calendarWsSerializationHelper.serializeDetailedTrigger(calendarTrigger));
    }

    @GET
    @Path("{triggerId}/generate-sequence")
    @Operation(
            summary = "Generate a sequence",
            description = "Generate a sequence based on the provided data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarTriggerGenerateSequenceSuccessExample"))),
                    @ApiResponse(responseCode = "400", description = "Invalid date", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarTrigger400InvalidDate"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "No such element", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarTrigger500NoSuchElement")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getSequencePreview(
            @PathParam("triggerId") @Parameter(description = "Id of the trigger", schema = @Schema(type = "long")) Long triggerId,
            @QueryParam("date") @Parameter(description = "Date of the sequence", schema = @Schema(format = "date")) String dateValue
    ) {
        CalendarSequence sequence = command.doGetSequencePreview(triggerId, dateValue);
        return response(calendarWsSerializationHelper.serializeDetailedSequence(sequence));
    }

    @POST
    @Path("{triggerId}/create-events")
    @Operation(
            summary = "Create events from trigger",
            description = "Create events for all cards matching the trigger",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Events created successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarTriggerCreateEventsSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "No such element", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarTrigger500NoSuchElement")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object createEvents(
            @PathParam("triggerId") @Parameter(description = "Id of the trigger", schema = @Schema(type = "long")) String triggerIdOrCode,
            @BeanParam WsQueryOptions query
    ) {
        command.doCreateEvents(triggerIdOrCode, query);
        return success();
    }
}
