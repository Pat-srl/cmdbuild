package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.CalendarEventAttachmentsWsCommand;
import org.cmdbuild.service.rest.v4.model.WsAttachmentData;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.*;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.v4.command.CalendarEventAttachmentsWsCommand.toDataUrl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElement;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("calendar/events/{eventId}/attachments")
@Tag(
        name = "Calendar event attachment",
        description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete an attachments of the chosen Calendar Event"
)
@Component
public class CalendarEventAttachmentsWs {

    private final CardWsSerializationHelperv3 cardWsSerializationHelperv3;
    private final CalendarService calendarService;
    private final CalendarEventAttachmentsWsCommand command;

    public static final String EVENT_CLASS_NAME = "_CalendarEvent";

    public CalendarEventAttachmentsWs(CalendarEventAttachmentsWsCommand command, CalendarService calendarService, CardWsSerializationHelperv3 cardWsSerializationHelperv3) {
        this.command = checkNotNull(command);
        this.calendarService = checkNotNull(calendarService);
        this.cardWsSerializationHelperv3 = checkNotNull(cardWsSerializationHelperv3);
    }

    @POST
    @Path(EMPTY)
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    @Operation(
            summary = "Create a new attachment for the chosen event",
            description = "Create a new attachment for the chosen event. You can either upload a new file or copy an existing attachment from another card.",
            requestBody = @RequestBody(description = "Multipart body: optional attachment metadata (json) and the optional file to upload; alternatively use the copyFrom_* query parameters to copy an existing attachment", required = false, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = ATTACHMENT, schema = @Schema(implementation = WsAttachmentData.class)),
                    @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary"))
            })),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment created successfully"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource"),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object create(
            @PathParam(EVENT_ID) @Parameter(description = "Id of the event to attach the file to", schema = @Schema(type = "integer")) Long eventId,
            @Multipart(value = ATTACHMENT, required = false) @Nullable WsAttachmentData attachment,
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            @QueryParam("copyFrom_class") @Parameter(description = "Class Id to copy from") String sourceClassId,
            @QueryParam("copyFrom_card") @Parameter(description = "Card Id to copy from") Long sourceCardId,
            @QueryParam("copyFrom_id")  @Parameter(description = "Attachment Id to copy from") String sourceAttachmentId,
            @QueryParam("tempId") @Parameter(description = "List of temporary attachment identifiers for uploaded files") List<String> tempId
    ) throws IOException {
        calendarService.getUserEvent(eventId);
        if (sourceClassId == null) {
            if (dataHandler != null) {
                DocumentInfoAndDetail documentInfoAndDetail = command.doCreate(EVENT_CLASS_NAME, eventId, attachment, dataHandler, true);
                return response(cardWsSerializationHelperv3.serializeAttachment_FullDetail(EVENT_CLASS_NAME, documentInfoAndDetail, false));
            } else {
                List<DocumentInfoAndDetail> documentInfoAndDetailList = command.doCreate(EVENT_CLASS_NAME, eventId, attachment, tempId);
                return documentInfoAndDetailList.size() == 1 ? cardWsSerializationHelperv3.serializeAttachment_FullDetail(EVENT_CLASS_NAME, getOnlyElement(documentInfoAndDetailList), false) : success();
            }
        } else {
            DocumentInfoAndDetail documentInfoAndDetail = command.doCopyFrom(EVENT_CLASS_NAME, eventId, attachment, sourceClassId, sourceCardId, sourceAttachmentId);
            return response(cardWsSerializationHelperv3.serializeAttachment_FullDetail(EVENT_CLASS_NAME, documentInfoAndDetail, false));
        }
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all the attachments for the chosen event",
            description = "Get all the attachments for the chosen event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachments retrieved successfully") ,
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readmany(
            @BeanParam WsQueryOptions wsQueryOptions,
            @PathParam(EVENT_ID) @Parameter(description = "Id of the event to attach the file to", schema = @Schema(type = "integer")) Long eventId
    ) {
        calendarService.getUserEvent(eventId);
        List<DocumentInfoAndDetail> documentInfoAndDetailList = command.doReadMany(wsQueryOptions, EVENT_CLASS_NAME, eventId);
        return response(documentInfoAndDetailList.stream().map(d -> cardWsSerializationHelperv3.serializeAttachment_FullDetail(EVENT_CLASS_NAME, d, false)).collect(toList()));
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Get a specific attachment for the chosen event",
            description = "Get a specific attachment for the chosen event",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readOne(
            @PathParam(EVENT_ID) @Parameter(description = "Id of the event to attach the file to", schema = @Schema(type = "integer")) Long eventId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment")  String attachmentId
    ) {
        calendarService.getUserEvent(eventId);
        DocumentInfoAndDetail documentInfoAndDetail = command.doReadOne(EVENT_CLASS_NAME, eventId, attachmentId);
        return response(cardWsSerializationHelperv3.serializeAttachment_FullDetail(EVENT_CLASS_NAME, documentInfoAndDetail, false));
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/{file}")
    @Operation(
            summary = "Download a specific attachment for the chosen event",
            description = "Download a specific attachment for the chosen event.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(
            @PathParam(EVENT_ID) @Parameter(description = "Id of the event to attach the file to", schema = @Schema(type = "integer")) Long eventId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment") String attachmentId
    ) {
        calendarService.getUserEvent(eventId);
        return command.doDownload(EVENT_CLASS_NAME, eventId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/preview")
    @Operation(
            summary = "Get a preview of a specific attachment for the chosen event",
            description = "Get a preview of a specific attachment for the chosen event.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Preview of attachment retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object preview(
            @PathParam(EVENT_ID) @Parameter(description = "Id of the event to attach the file to", schema = @Schema(type = "integer")) Long eventId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment") String attachmentId
    ) {
        calendarService.getUserEvent(eventId);
        Optional<DataHandler> preview = command.doPreview(EVENT_CLASS_NAME, eventId, attachmentId);
        return map("success", true, "data", map().accept((map) -> {
            map.put("hasPreview", preview.isPresent());
            if (preview.isPresent()) {
                map.put("dataUrl", toDataUrl(preview.get()));
            }
        }));
    }

    @PUT
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Update an attachment for the chosen event",
            description = "Update an attachment for the chosen event.",
            requestBody = @RequestBody(description = "Multipart body: optional attachment metadata (json) and optional replacement file", required = false, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = ATTACHMENT, schema = @Schema(implementation = WsAttachmentData.class)),
                    @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary"))
            })),

            responses = {
                    @ApiResponse (responseCode = "200", description = "Attachment updated successfully"),
                    @ApiResponse (responseCode = "404", description = "Attachment not found"),
                    @ApiResponse (responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object update(
            @PathParam(EVENT_ID) @Parameter(description = "Id of the event to attach the file to", schema = @Schema(type = "integer")) Long eventId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment") String attachmentId,
            @Nullable @Multipart(value = ATTACHMENT, required = false) WsAttachmentData attachment,
            @Nullable @Multipart(value = FILE, required = false) DataHandler dataHandler
    ) {
        calendarService.getUserEvent(eventId);
        DocumentInfoAndDetail documentInfoAndDetail = command.doUpdate(EVENT_CLASS_NAME, eventId, attachmentId, attachment, dataHandler);
        return response(cardWsSerializationHelperv3.serializeAttachment_FullDetail(EVENT_CLASS_NAME, documentInfoAndDetail, false));
    }

    @DELETE
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Delete an attachment for the chosen event",
            description = "Delete an attachment for the chosen event.",
            responses = {
                    @ApiResponse (responseCode = "200", description = "Attachment deleted successfully"),
                    @ApiResponse (responseCode = "404", description = "Attachment not found"),
                    @ApiResponse (responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object delete(
            @PathParam("eventId") @Parameter(description = "Id of the event to attach the file to", schema = @Schema(type = "integer"))  Long eventId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment")  String attachmentId
    ) {
        calendarService.getUserEvent(eventId);
        command.doDelete(EVENT_CLASS_NAME, eventId, attachmentId);
        return success();
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history")
    @Operation(
            summary = "Get the history of an attachment for the chosen event",
            description = "Get the history of an attachment for the chosen event.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment history retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getAttachmentHistory(
            @PathParam(EVENT_ID) @Parameter(description = "Id of the event to attach the file to", schema = @Schema(type = "integer")) Long eventId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment") String attachmentId
    ) {
        calendarService.getUserEvent(eventId);
        List<DocumentInfoAndDetail> documentInfoAndDetailList = command.doGetAttachmentHistory(EVENT_CLASS_NAME, eventId, attachmentId);
        return response(documentInfoAndDetailList.stream().map(d -> cardWsSerializationHelperv3.serializeAttachment_FullDetail(EVENT_CLASS_NAME, d, false)).collect(toList()));
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history/{version}/{file}")
    @Operation(
            summary = "Download a previous version of an attachment for the chosen event",
            description =  "Download a previous version of an attachment for the chosen event.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Previous version of attachment retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Version not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadPreviousVersion(
            @PathParam(EVENT_ID) @Parameter(description = "Id of the event to attach the file to", schema = @Schema(type = "integer")) Long eventId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment") String attachmentId,
            @PathParam(VERSION) @Parameter(description = "Id of the version")  String versionId
    ) {
        calendarService.getUserEvent(eventId);
        return command.doDownloadPreviousVersion(EVENT_CLASS_NAME, eventId, attachmentId, versionId);
    }
}
