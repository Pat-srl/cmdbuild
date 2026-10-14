package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.media.SchemaProperty;
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
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper;
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper.WsAttachmentData;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/{" + TYPE_NAME + ":cards|instances}/{" + CARD_ID + "}/emails/{" + EMAIL_ID + "}/attachments|calendar/events/{" + CARD_ID + "}/emails/{" + EMAIL_ID + "}/attachments/")
@Tag(name = "Email attachment", description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete an attachments of the chosen Email")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class CardEmailAttachmentWs {//note: duplicate code from AttachmentWs; would be great if it could be possible to merge CardEmailAttachmentWs and AttachmentWs

    private final AttachmentWsHelper service;

    public CardEmailAttachmentWs(AttachmentWsHelper attachmentWs) {
        this.service = checkNotNull(attachmentWs);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            requestBody = @RequestBody(description = "Attachment metadata (optional) and the attachment file (optional, may be omitted when copying from an existing attachment)", required = false,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = ATTACHMENT, schema = @Schema(implementation = WsAttachmentData.class)),
                    @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary"))
                    })),
            summary = "Create an attachment for an email",
            description = "Creates an attachment for an email",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of attachment"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object create(
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email") Long emailId,
            @Multipart(value = ATTACHMENT, required = false) @Nullable @Parameter(schema = @Schema(implementation = WsAttachmentData.class)) WsAttachmentData attachment,
            @Multipart(value = FILE, required = false) @Parameter(schema = @Schema(implementation = DataHandler.class)) DataHandler dataHandler,
            @QueryParam("copyFrom_class") @Parameter(description = "Name of the class to copy from") String sourceClassId,
            @QueryParam("copyFrom_card") @Parameter(description = "Id of the card to copy from") Long sourceCardId,
            @QueryParam("copyFrom_id") @Parameter(description = "Id of the attachment to copy from") String sourceAttachmentId,
            @QueryParam("tempId") @Parameter(description = "Temporary id to use for the attachment") List<String> tempId
    ) throws IOException {
        if (sourceClassId == null) {
            if (dataHandler != null) {
                return service.create(EMAIL_CLASS_NAME, emailId, attachment, dataHandler);
            } else {
                return service.create(EMAIL_CLASS_NAME, emailId, attachment, tempId);
            }
        } else {
            return service.copyFrom(EMAIL_CLASS_NAME, emailId, attachment, sourceClassId, sourceCardId, sourceAttachmentId);
        }
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all attachments for an email",
            description = "Get all attachments for an email",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of attachment data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @BeanParam WsQueryOptions wsQueryOptions,
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to query") Long emailId
    ) {
        return service.readMany(wsQueryOptions, EMAIL_CLASS_NAME, emailId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Get a specific attachment for an email",
            description = "Get a specific attachment for an email",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of attachment data"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to query") Long emailId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment to query") String attachmentId
    ) {
        return service.readOne(EMAIL_CLASS_NAME, emailId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/{file}")
    @Operation(
            summary = "Download an attachment file",
            description = "Download an attachment file for an email",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to query") Long emailId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment to query") String attachmentId
    ) {
        return service.download(EMAIL_CLASS_NAME, emailId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/preview")
    @Operation(
            summary = "Get a preview of a specific attachment for an email",
            description = "Get a preview of a specific attachment for an email",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of attachment preview data"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object preview(
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to query") Long emailId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment to query") String attachmentId
    ) {
        return service.preview(EMAIL_CLASS_NAME, emailId, attachmentId);
    }

    @PUT
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            requestBody = @RequestBody(description = "Updated attachment metadata (optional) and replacement file (optional)", required = false,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = ATTACHMENT, schema = @Schema(implementation = WsAttachmentData.class)),
                    @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary"))
                    })),
            summary = "Update an attachment for an email",
            description = "Update an attachment for an email",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of attachment"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object update(
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to update") Long emailId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment to update") String attachmentId,
            @Nullable @Multipart(value = ATTACHMENT, required = false) @Parameter(schema = @Schema(implementation = WsAttachmentData.class)) WsAttachmentData attachment,
            @Nullable @Multipart(value = FILE, required = false) @Parameter(schema = @Schema(implementation = DataHandler.class)) DataHandler dataHandler
    ) {
        return service.update(EMAIL_CLASS_NAME, emailId, attachmentId, attachment, dataHandler);
    }

    @DELETE
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Delete an attachment from an email",
            description = "Delete an attachment from an email",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of attachment"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to delete") Long emailId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment to delete") String attachmentId
    ) {
        return service.delete(EMAIL_CLASS_NAME, emailId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history")
    @Operation(
            summary = "Get attachment history for an email",
            description = "Get attachment history for an email",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of attachment history data"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAttachmentHistory(
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to query") Long emailId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment to query") String attachmentId
    ) {
        return service.getAttachmentHistory(EMAIL_CLASS_NAME, emailId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history/{version}/{file}")
    @Operation(
            summary = "Download a previous version of an attachment",
            description = "Download a previous version of an attachment for an email",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Attachment version not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadPreviousVersion(
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to query") Long emailId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment to query") String attachmentId,
            @PathParam(VERSION) @Parameter(description = "Version id of the attachment to query") String versionId
    ) {
        return service.downloadPreviousVersion(EMAIL_CLASS_NAME, emailId, attachmentId, versionId);
    }

}
