/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.endpoint.CalendarTriggerWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.CalendarTriggerWs_Management;
import org.cmdbuild.service.rest.v4.model.WsTriggerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_VIEW_AUTHORITY;

@Path("calendar/triggers/")
@Tag(
        name = "Calendar Trigger",
        description = "The following documentation aims to illustrate the usage of the REST APIs provided by CMDBuild for calendars, providing examples on how to correctly manage schedulers and creating new calendar rules"
)
@Produces(APPLICATION_JSON)
public class CalendarTriggerWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CalendarTriggerWs_Administration calendarTriggerWs_adm;
    private final CalendarTriggerWs_Management calendarTriggerWs_mng;

    public CalendarTriggerWs(CalendarTriggerWs_Administration calendarTriggerWs_adm, CalendarTriggerWs_Management calendarTriggerWs_mng) {
        this.calendarTriggerWs_adm = checkNotNull(calendarTriggerWs_adm);
        this.calendarTriggerWs_mng = checkNotNull(calendarTriggerWs_mng);
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
            @PathParam("triggerId") @Parameter(description = "Id of the trigger") Long triggerId
    ) {
        return calendarTriggerWs_mng.readOne(triggerId);
    }

    @GET
    @Path("{triggerId}/generate-sequence")
    @Operation(
            summary = "Generate a sequence",
            description = "Generate a sequence based on the provided data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarTriggerGenerateSequenceSuccessExample"))),
                    @ApiResponse(responseCode = "400", description = "Invalid date", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarTrigger400InvalidDate"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required"),
                    @ApiResponse(responseCode = "500", description = "No such element", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarTrigger500NoSuchElement")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getSequencePreview(
            @PathParam("triggerId") @Parameter(description = "Id of the trigger", schema = @Schema(minimum = "0")) Long triggerId,
            @QueryParam("date") @Parameter(description = "Date of the sequence", schema = @Schema(format = "date")) String dateValue
    ) {
        return calendarTriggerWs_mng.getSequencePreview(triggerId, dateValue);
    }

    @POST
    @Path("{triggerId}/create-events")
    @Operation(
            summary = "Create events from trigger",
            description = "Create events for all cards matching the trigger",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Events created successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarTriggerCreateEventsSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required"),
                    @ApiResponse(responseCode = "500", description = "No such element", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultCalendarTrigger500NoSuchElement")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object createEvents(
            @PathParam("triggerId") String triggerIdOrCode,
            @Parameter(schema = @Schema(implementation = WsQueryOptions.class)) WsQueryOptions query
    ) {
        return calendarTriggerWs_mng.createEvents(triggerIdOrCode, query);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all triggers",
            description = "Obtain a list of all calendar triggers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of triggers", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarTriggerListSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_VIEW_AUTHORITY)
    public Object readMany(
            @Parameter(schema = @Schema(implementation = WsQueryOptions.class)) WsQueryOptions query
    ) {
        return calendarTriggerWs_adm.readMany(query);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new trigger",
            description = "Create a new calendar trigger with the provided data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trigger created successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarTriggerSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object create(
            @Parameter(schema = @Schema(implementation = WsTriggerData.class)) WsTriggerData data
    ) {
        return calendarTriggerWs_adm.create(data);
    }

    @PUT
    @Path("{triggerId}/")
    @Operation(
            summary = "Update an existing trigger",
            description = "Update an existing calendar trigger with the provided data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trigger updated successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APICalendarTriggerSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("triggerId") Long triggerId,
           @Parameter(schema = @Schema(implementation = WsTriggerData.class)) WsTriggerData data
    ) {
        return calendarTriggerWs_adm.update(triggerId, data);
    }

    @DELETE
    @Path("{triggerId}/")
    @Operation(
            summary = "Delete a trigger",
            description = "Delete a specific calendar trigger",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trigger deleted successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("triggerId") Long triggerId
    ) {
        return calendarTriggerWs_adm.delete(triggerId);
    }

}
