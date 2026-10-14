package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsAttachmentData;

import java.io.IOException;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("calendar/events/{eventId}/attachments")
@Tag(
        name = "Calendar event attachment",
        description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete an attachments of the chosen Calendar Event"
)
public class CalendarEventAttachmentsWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CalendarEventAttachmentsWs calendarEventAttachmentsWs;

    public CalendarEventAttachmentsWs(org.cmdbuild.service.rest.v4.endpoint.CalendarEventAttachmentsWs calendarEventAttachmentsWs) {
        this.calendarEventAttachmentsWs = calendarEventAttachmentsWs;
    }

    @POST
    @Path(EMPTY)
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    @Operation(
            summary = "Create a new attachment for the chosen event",
            description = "Create a new attachment for the chosen event. You can either upload a new file or copy an existing attachment from another card.",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the event to attach the file to"),
                    @Parameter(name = "copyFrom_class", in = ParameterIn.QUERY, description = "Class Id to copy from"),
                    @Parameter(name = "copyFrom_card", in = ParameterIn.QUERY, description = "Card Id to copy from"),
                    @Parameter(name = "copyFrom_id", in = ParameterIn.QUERY, description = "Attachment Id to copy from"),
                    @Parameter(name = "tempId", in = ParameterIn.QUERY, description = "List of temporary attachment identifiers for uploaded files")
            },
            requestBody = @RequestBody(description = "The attachment data to create", content = @Content(mediaType = MULTIPART_FORM_DATA, schema = @Schema(implementation = WsAttachmentData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment created successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APISuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object create(
            @PathParam("eventId") Long eventId,
            @Multipart(value = ATTACHMENT, required = false) @Nullable  WsAttachmentData attachment,
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            @QueryParam("copyFrom_class") String sourceClassId,
            @QueryParam("copyFrom_card") Long sourceCardId,
            @QueryParam("copyFrom_id") String sourceAttachmentId,
            @QueryParam("tempId") List<String> tempId
    ) throws IOException {
        return calendarEventAttachmentsWs.create(eventId, attachment, dataHandler, sourceClassId, sourceCardId, sourceAttachmentId, tempId);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all the attachments for the chosen event",
            description = "Get all the attachments for the chosen event",
            parameters = {
                    @Parameter(name = "query", in = ParameterIn.QUERY, description = "Query string to filter the results"),
            },
            requestBody = @RequestBody(description = "The query options to use", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsQueryOptions.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachments retrieved successfully") ,
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readmany(
            WsQueryOptions wsQueryOptions,
            @PathParam("eventId") Long eventId
    ) {
        return calendarEventAttachmentsWs.readmany(wsQueryOptions, eventId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Get a specific attachment for the chosen event",
            description = "Get a specific attachment for the chosen event",
            parameters = {
                    @Parameter( name = "eventId", in = ParameterIn.PATH, description = "Id of the event to attach the file to" ),
                    @Parameter( name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "Id of the attachment" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readOne(
            @PathParam("eventId") Long eventId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return calendarEventAttachmentsWs.readOne(eventId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/{file}")
    @Operation(
            summary = "Download a specific attachment for the chosen event",
            description = "Download a specific attachment for the chosen event.",
            parameters = {
                    @Parameter( name = "eventId", in = ParameterIn.PATH, description = "Id of the event to attach the file to" ),
                    @Parameter( name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "Id of the attachment" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(
            @PathParam("eventId") Long eventId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return calendarEventAttachmentsWs.download(eventId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/preview")
    @Operation(
            summary = "Get a preview of a specific attachment for the chosen event",
            description = "Get a preview of a specific attachment for the chosen event.",
            parameters = {
                    @Parameter( name = "eventId", in = ParameterIn.PATH, description = "Id of the event to attach the file to" ),
                    @Parameter( name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "Id of the attachment" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Preview of attachment retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object preview(
            @PathParam("eventId") Long eventId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return calendarEventAttachmentsWs.preview(eventId, attachmentId);
    }

    @PUT
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Update an attachment for the chosen event",
            description = "Update an attachment for the chosen event.",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the event to attach the file to"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "Id of the attachment")
            },
            requestBody = @RequestBody(description = "The attachment data to update", content = @Content(mediaType = MULTIPART_FORM_DATA, schema = @Schema(implementation = WsAttachmentData.class))),
            responses = {
                    @ApiResponse (responseCode = "200", description = "Attachment updated successfully"),
                    @ApiResponse (responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object update(
            @PathParam("eventId") Long eventId,
            @PathParam(ATTACHMENT_ID) String attachmentId,
            @Nullable @Multipart(value = ATTACHMENT, required = false) WsAttachmentData attachment,
            @Nullable @Multipart(value = FILE, required = false) DataHandler dataHandler
    ) {
        return calendarEventAttachmentsWs.update(eventId, attachmentId, attachment, dataHandler);
    }

    @DELETE
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Delete an attachment for the chosen event",
            description = "Delete an attachment for the chosen event.",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the event to attach the file to"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "Id of the attachment")
            },
            responses = {
                    @ApiResponse (responseCode = "200", description = "Attachment deleted successfully"),
                    @ApiResponse (responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object delete(
            @PathParam("eventId") Long eventId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return calendarEventAttachmentsWs.delete(eventId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history")
    @Operation(
            summary = "Get the history of an attachment for the chosen event",
            description = "Get the history of an attachment for the chosen event.",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the event to attach the file to"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "Id of the attachment")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment history retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getAttachmentHistory(
            @PathParam("eventId") Long eventId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return calendarEventAttachmentsWs.getAttachmentHistory(eventId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history/{version}/{file}")
    @Operation(
            summary = "Download a previous version of an attachment for the chosen event",
            description =  "Download a previous version of an attachment for the chosen event.",
            parameters = {
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "Id of the event to attach the file to"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "Id of the attachment"),
                    @Parameter(name = "version", in = ParameterIn.PATH, description = "Id of the version")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Previous version of attachment retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadPreviousVersion(
            @PathParam("eventId") Long eventId,
            @PathParam(ATTACHMENT_ID) String attachmentId,
            @PathParam("version") String versionId
    ) {
        return calendarEventAttachmentsWs.downloadPreviousVersion(eventId, attachmentId, versionId); 
    }
}
