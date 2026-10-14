package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.media.SchemaProperty;
import com.google.firebase.database.utilities.Pair;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.email.Email;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.CalendarEventEmailWsCommand;
import org.cmdbuild.service.rest.v4.model.WsEmailData;
import org.cmdbuild.service.rest.v4.serializationhelpers.EmailSerializationHelper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;

@Path("calendar/events/{eventId}/emails")
@Tag(
        name = "Calendar event email attachment",
        description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete an attachments of an Email that is attached to the chosen Card"
)
@Component
public class CalendarEventEmailWs {

    private final EmailSerializationHelper emailSerializationHelper;
    private final CalendarEventEmailWsCommand command;

    public CalendarEventEmailWs(EmailSerializationHelper emailSerializationHelper,
                                CalendarEventEmailWsCommand command) {
        this.emailSerializationHelper = checkNotNull(emailSerializationHelper);
        this.command = checkNotNull(command);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            requestBody = @RequestBody(description = "Email data (part 'email', JSON) and the email attachments (all other parts)", required = true,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "email", schema = @Schema(implementation = WsEmailData.class, requiredMode = Schema.RequiredMode.REQUIRED)),
                    @SchemaProperty(name = "attachments", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
                    })),
            summary = "Create an email attachment for a calendar event",
            description = "Creates an email attachment for a calendar event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email attachment created successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam(EVENT_ID) @Parameter(description = "Id of event", schema = @Schema(type = "long")) Long eventId,
            @QueryParam("apply_template") @DefaultValue(FALSE) @Parameter(description = "Apply template to the email", schema = @Schema(type = "boolean")) Boolean applyTemplate,
            @QueryParam("template_only") @DefaultValue(FALSE) @Parameter(description = "Only apply template to the email", schema = @Schema(type = "boolean")) Boolean templateOnly,
            @QueryParam(ATTACHMENTS) @Parameter(array = @ArraySchema(schema = @Schema(type = "string")), description = "List of temporary attachment identifiers for uploaded files") List<String> tempId,
            List<Attachment> parts)
    {
        Pair<Email, Boolean> pair = command.doCreate(eventId, applyTemplate, templateOnly, tempId, parts);
        return response(emailSerializationHelper.serializeDetailedEmail(pair.getFirst(), pair.getSecond()));
    }

    @PUT
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Update an existing email attachment for a calendar event",
            description = "Update an existing email attachment for a calendar event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email attachment updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(EVENT_ID) @Parameter(description = "Id of event", schema = @Schema(type = "long")) Long eventId,
            @PathParam(EMAIL_ID) @Parameter(description = "Id of email attachment", schema = @Schema(type = "long")) Long emailId,
            @RequestBody(description = "Email data", required = true) WsEmailData emailData
    ) {
        Email email = command.doUpdate(eventId, emailId, emailData);
        return response(emailSerializationHelper.serializeDetailedEmail(email, null));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all email attachments for a calendar event",
            description = "Retrieve all email attachments for a calendar event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of email attachments"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @PathParam(EVENT_ID) @Parameter(description = "Id of event", schema = @Schema(type = "long")) Long eventId,
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        Collection<Email> emailCollection = command.doReadAll(eventId, wsQueryOptions);
        return response(paged(emailCollection, wsQueryOptions.isDetailed() ? emailSerializationHelper::serializeDetailedEmail : emailSerializationHelper::serializeBasicEmail, toInt(wsQueryOptions.getOffset()), toInt(wsQueryOptions.getLimit())));
    }

    @GET
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Get a specific email attachment for a calendar event",
            description = "Retrieve a specific email attachment for a calendar event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of the email attachment"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(EVENT_ID) @Parameter(description = "Id of event", schema = @Schema(type = "long")) Long eventId,
            @PathParam(EMAIL_ID) @Parameter(description = "Id of email attachment", schema = @Schema(type = "long")) Long emailId
    ) {
        Email email = command.doRead(eventId, emailId);
        return response(emailSerializationHelper.serializeDetailedEmail(email));//TODO check email id
    }

    @DELETE
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Delete an email attachment from a calendar event",
            description = "Deletes an email attachment from a calendar event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email attachment deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(EVENT_ID) @Parameter(description = "Id of event", schema = @Schema(type = "long")) Long eventId,
            @PathParam(EMAIL_ID) @Parameter(description = "Id of email attachment", schema = @Schema(type = "long")) Long emailId
    ) {
        command.doDelete(eventId, emailId);
        return success();
    }
}
