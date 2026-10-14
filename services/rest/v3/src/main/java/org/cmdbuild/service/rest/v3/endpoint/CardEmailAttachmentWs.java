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
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper.WsAttachmentData;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/{typeName:cards|instances}/{" + CARD_ID + "}/emails/{" + EMAIL_ID + "}/attachments|calendar/events/{" + CARD_ID + "}/emails/{" + EMAIL_ID + "}/attachments/")
@Tag(name = "Email attachment", description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete an attachments of the chosen Email")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardEmailAttachmentWs {//note: duplicate code from AttachmentWs; would be great if it could be possible to merge CardEmailAttachmentWs and AttachmentWs

    private final org.cmdbuild.service.rest.v4.endpoint.CardEmailAttachmentWs cardEmailAttachmentWs;

    public CardEmailAttachmentWs(org.cmdbuild.service.rest.v4.endpoint.CardEmailAttachmentWs cardEmailAttachmentWs) {
        this.cardEmailAttachmentWs = checkNotNull(cardEmailAttachmentWs);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create an attachment for an email",
            description = "Creates an attachment for an email",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = "eventId", in = ParameterIn.PATH, description = "ID of the event to query"),
                    @Parameter(name = "copyFrom_class", in = ParameterIn.QUERY, description = "Name of the class to copy from"),
                    @Parameter(name = "copyFrom_card", in = ParameterIn.QUERY, description = "ID of the card to copy from"),
                    @Parameter(name = "copyFrom_id", in = ParameterIn.QUERY, description = "ID of the attachment to copy from"),
                    @Parameter(name = "tempId", in = ParameterIn.QUERY, description = "Temporary ID of the attachment to create")
            },
            requestBody = @RequestBody(description = "", content = @Content(mediaType = MULTIPART_FORM_DATA, schema = @Schema(implementation = WsAttachmentData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of attachment"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object create(
            @PathParam(EMAIL_ID) Long emailId,
            @Multipart(value = ATTACHMENT, required = false) @Nullable WsAttachmentData attachment,
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            @QueryParam("copyFrom_class") String sourceClassId,
            @QueryParam("copyFrom_card") Long sourceCardId,
            @QueryParam("copyFrom_id") String sourceAttachmentId,
            @QueryParam("tempId") List<String> tempId
    ) throws IOException {
        return cardEmailAttachmentWs.create(emailId, attachment, dataHandler, sourceClassId, sourceCardId, sourceAttachmentId, tempId);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all attachments for an email",
            description = "Get all attachments for an email",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = EMAIL, in = ParameterIn.PATH, description = "ID of the email to query")
            },
            requestBody = @RequestBody(description = "", content = @Content(mediaType = MULTIPART_FORM_DATA, schema = @Schema(implementation = WsQueryOptions.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of attachment data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @Parameter(schema = @Schema(implementation = WsQueryOptions.class)) WsQueryOptions wsQueryOptions,
            @PathParam(EMAIL_ID) Long emailId
    ) {
        return cardEmailAttachmentWs.readMany(wsQueryOptions, emailId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Get a specific attachment for an email",
            description = "Get a specific attachment for an email",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = EMAIL, in = ParameterIn.PATH, description = "ID of the email to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of attachment data"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam(EMAIL_ID) Long emailId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return cardEmailAttachmentWs.readOne(emailId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/{file}")
    @Operation(
            summary = "Download an attachment file",
            description = "Download an attachment file for an email",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = EMAIL, in = ParameterIn.PATH, description = "ID of the email to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query")
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
            @PathParam(EMAIL_ID) Long emailId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return cardEmailAttachmentWs.download(emailId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/preview")
    @Operation(
            summary = "Get a preview of a specific attachment for an email",
            description = "Get a preview of a specific attachment for an email",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = EMAIL, in = ParameterIn.PATH, description = "ID of the email to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of attachment preview data"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object preview(
            @PathParam(EMAIL_ID) Long emailId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return cardEmailAttachmentWs.preview(emailId, attachmentId);
    }

    @PUT
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Update an attachment for an email",
            description = "Update an attachment for an email",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = EMAIL, in = ParameterIn.PATH, description = "ID of the email to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query")
            },
            requestBody = @RequestBody(description = "", content = @Content(mediaType = MULTIPART_FORM_DATA, schema = @Schema(implementation = WsAttachmentData.class))),
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
            @PathParam(EMAIL_ID) Long emailId,
            @PathParam(ATTACHMENT_ID) String attachmentId,
            @Nullable @Multipart(value = ATTACHMENT, required = false) @Parameter(schema = @Schema(implementation = WsAttachmentData.class)) WsAttachmentData attachment,
            @Nullable @Multipart(value = FILE, required = false) @Parameter(schema = @Schema(implementation = DataHandler.class)) DataHandler dataHandler
    ) {
        return cardEmailAttachmentWs.update(emailId, attachmentId, attachment, dataHandler);
    }

    @DELETE
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Delete an attachment from an email",
            description = "Delete an attachment from an email",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = EMAIL, in = ParameterIn.PATH, description = "ID of the email to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of attachment"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(EMAIL_ID) Long emailId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return cardEmailAttachmentWs.delete(emailId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history")
    @Operation(
            summary = "Get attachment history for an email",
            description = "Get attachment history for an email",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = EMAIL, in = ParameterIn.PATH, description = "ID of the email to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of attachment history data"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAttachmentHistory(
            @PathParam(EMAIL_ID) Long emailId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return cardEmailAttachmentWs.getAttachmentHistory(emailId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history/{version}/{file}")
    @Operation(
            summary = "Download a previous version of an attachment",
            description = "Download a previous version of an attachment for an email",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = EMAIL, in = ParameterIn.PATH, description = "ID of the email to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query"),
                    @Parameter(name = "version", in = ParameterIn.PATH, description = "Version of the attachment to query")
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
            @PathParam(EMAIL_ID) Long emailId,
            @PathParam(ATTACHMENT_ID) String attachmentId,
            @PathParam("version") String versionId
    ) {
        return cardEmailAttachmentWs.downloadPreviousVersion(emailId, attachmentId, versionId);
    }

}
