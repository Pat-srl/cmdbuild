package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsEmailData;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EMAIL_ID;

@Path("calendar/events/{eventId}/emails")
@Tag(
        name = "Calendar event email attachment",
        description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete an attachments of an Email that is attached to the chosen Card"
)
public class CalendarEventEmailWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CalendarEventEmailWs calendarEventEmailWs;

    public CalendarEventEmailWs(org.cmdbuild.service.rest.v4.endpoint.CalendarEventEmailWs calendarEventEmailWs) {
        this.calendarEventEmailWs = checkNotNull(calendarEventEmailWs);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create an email attachment for a calendar event",
            description = "Creates an email attachment for a calendar event",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the calendar event to which the email attachment will be associated"),
                    @Parameter(name = "apply_template", in = ParameterIn.QUERY, description = "Whether to apply the email template associated to the calendar event (if any) when creating the email attachment"),
                    @Parameter(name = "template_only", in = ParameterIn.QUERY, description = "Whether to create the email attachment using only the email template associated to the calendar event (if any), without including the email body and attachments of the original email attachment)"),
                    @Parameter(name = "attachments", in = ParameterIn.QUERY, description = "List of temporary ids of the attachments to be included in the email attachment", array = @ArraySchema(schema = @Schema(implementation = String.class)) )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email attachment created successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam("eventId") @Parameter( description = "Id of event") Long eventId,
            @QueryParam("apply_template") @DefaultValue(FALSE) Boolean applyTemplate,
            @QueryParam("template_only") @DefaultValue(FALSE) Boolean templateOnly,
            @QueryParam("attachments") @Parameter(array = @ArraySchema(schema = @Schema(implementation = Attachment.class))) List<String> tempId, List<Attachment> parts) {
        return calendarEventEmailWs.create(eventId, applyTemplate, templateOnly, tempId, parts);
    }

    @PUT
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Update an existing email attachment for a calendar event",
            description = "Update an existing email attachment for a calendar event",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the calendar event to which the email attachment belongs"),
                    @Parameter(name = EMAIL_ID, in = ParameterIn.PATH, description = "Id of the email attachment to be updated"),
                    @Parameter(name = "emailData", in = ParameterIn.QUERY, description = "Data to update the email attachment with", schema = @Schema(implementation = WsEmailData.class))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email attachment updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("eventId") Long eventId,
            @PathParam(EMAIL_ID) Long emailId,
            @Parameter(schema = @Schema(implementation = WsEmailData.class)) WsEmailData emailData
    ) {
        return calendarEventEmailWs.update(eventId, emailId, emailData);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all email attachments for a calendar event",
            description = "Retrieve all email attachments for a calendar event",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the calendar event to which the email attachments belong"),
                    @Parameter(name = "query", in = ParameterIn.QUERY, description = "Query parameters to filter the email attachments", schema = @Schema(implementation = WsQueryOptions.class))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email attachments"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @PathParam("eventId") Long eventId,
            @Parameter(schema = @Schema(implementation = WsQueryOptions.class)) WsQueryOptions wsQueryOptions
    ) {
        return calendarEventEmailWs.readAll(eventId, wsQueryOptions);
    }

    @GET
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Get a specific email attachment for a calendar event",
            description = "Retrieve a specific email attachment for a calendar event",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the calendar event to which the email attachment belongs"),
                    @Parameter(name = EMAIL_ID, in = ParameterIn.PATH, description = "Id of the email attachment to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of the email attachment"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("eventId") Long eventId,
            @PathParam(EMAIL_ID) Long emailId
    ) {
        return calendarEventEmailWs.read(eventId, emailId);
    }

    @DELETE
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Delete an email attachment from a calendar event",
            description = "Deletes an email attachment from a calendar event",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the calendar event from which the email attachment will be deleted"),
                    @Parameter(name = EMAIL_ID, in = ParameterIn.PATH, description = "Id of the email attachment to be deleted")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email attachment deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("eventId") Long eventId,
            @PathParam(EMAIL_ID) Long emailId
    ) {
        return calendarEventEmailWs.delete(eventId, emailId);
    }
}
