package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

import static jakarta.ws.rs.core.MediaType.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/{typeName:cards|instances}/{" + CARD_ID + "}/attachments/")
@Tag( name = "Card Attachments", description = "Manage attachments of a specific card" )
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardAttachmentWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CardAttachmentWs cardAttachmentWs;

    public CardAttachmentWs(org.cmdbuild.service.rest.v4.endpoint.CardAttachmentWs cardAttachmentWs) {
        this.cardAttachmentWs = checkNotNull(cardAttachmentWs);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new attachment",
            description = "Create a new attachment for a specific card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = ATTACHMENT, in = ParameterIn.QUERY, description = "Attachment data", schema = @Schema(implementation = WsAttachmentData.class)),
                    @Parameter(name = FILE, in = ParameterIn.QUERY, description = "File data", schema = @Schema(implementation = DataHandler.class)),
                    @Parameter(name = "copyFrom_class", in = ParameterIn.QUERY, description = "Name of the class to copy from"),
                    @Parameter(name = "copyFrom_card", in = ParameterIn.QUERY, description = "ID of the card to copy from"),
                    @Parameter(name = "copyFrom_id", in = ParameterIn.QUERY, description = "ID of the attachment to copy from"),
                    @Parameter(name = "tempId", in = ParameterIn.QUERY, description = "Temporary ID of the attachment to copy from")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment created successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object create(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @Multipart(value = ATTACHMENT, required = false) @Nullable WsAttachmentData attachment,
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            @QueryParam("copyFrom_class") String sourceClassId,
            @QueryParam("copyFrom_card") Long sourceCardId,
            @QueryParam("copyFrom_id") String sourceAttachmentId,
            @QueryParam("tempId") List<String> tempId
    ) throws IOException {
        return cardAttachmentWs.create(classId, cardId, attachment, dataHandler, sourceClassId, sourceCardId, sourceAttachmentId, tempId);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all attachments for a specific card",
            description = "Obtain a list of all attachments for a specific card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            requestBody = @RequestBody( description = ""),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            WsQueryOptions wsQueryOptions,
            @PathParam(CLASS_ID)  String classId,
            @PathParam(CARD_ID) Long cardId
    ) {
        return cardAttachmentWs.readMany(wsQueryOptions, classId, cardId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Get a specific attachment",
            description = "Obtain details of a specific attachment for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query"),
                    @Parameter(name = "includeWidgets", in = ParameterIn.QUERY, description = "Include widgets in the response")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam(ATTACHMENT_ID) String attachmentId,
            @QueryParam("includeWidgets") @DefaultValue(FALSE) Boolean includeWidgets
    ) {
        return cardAttachmentWs.readOne(classId, cardId, attachmentId, includeWidgets);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/{file}")
    @Operation(
            summary = "Download an attachment file",
            description = "Download the file of a specific attachment for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return cardAttachmentWs.download(classId, cardId, attachmentId);
    }

    @GET
    @Path("_MANY/{file}")
    @Operation(
            summary = "Download multiple attachment files",
            description = "Download multiple attachment files for a specific card as a zip file",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.QUERY, description = "List of attachment IDs to download")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadMany(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @QueryParam(ATTACHMENT_ID) List<String> attachmentId
    ) {
        return cardAttachmentWs.downloadMany(classId, cardId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/preview")
    @Operation(
            summary = "Preview an attachment",
            description = "Obtain a preview of a specific attachment for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object preview(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return cardAttachmentWs.preview(classId, cardId, attachmentId);
    }

    @PUT
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Update an existing attachment",
            description = "Update an existing attachment for a specific card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query"),
                    @Parameter(name = ATTACHMENT, in = ParameterIn.QUERY, description = "Attachment data", schema = @Schema(implementation = WsAttachmentData.class)),
                    @Parameter(name = FILE, in = ParameterIn.QUERY, description = "File data", schema = @Schema(implementation = DataHandler.class)),
                    @Parameter(name = "tempId", in = ParameterIn.QUERY, description = "Temporary ID of the attachment to copy from")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Object update(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam(ATTACHMENT_ID) String attachmentId,
            @Nullable @Multipart(value = ATTACHMENT, required = false) WsAttachmentData attachment,
            @Nullable @Multipart(value = FILE, required = false)DataHandler dataHandler,
            @QueryParam("tempId") String tempId
    ) {
        return cardAttachmentWs.update(classId, cardId, attachmentId, attachment, dataHandler, tempId);
    }

    @DELETE
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Delete an attachment",
            description = "Delete a specific attachment from a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return cardAttachmentWs.delete(classId, cardId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history")
    @Operation(
            summary = "Get attachment history",
            description = "Obtain the history of a specific attachment for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAttachmentHistory(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return cardAttachmentWs.getAttachmentHistory(classId, cardId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history/{version}/{file}")
    @Operation(
            summary = "Download a previous version of an attachment",
            description = "Download a specific previous version of an attachment for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to query"),
                    @Parameter(name = "version", in = ParameterIn.PATH, description = "Version ID of the attachment to download")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadPreviousVersion(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam(ATTACHMENT_ID) String attachmentId,
            @PathParam("version") String versionId
    ) {
        return cardAttachmentWs.downloadPreviousVersion(classId, cardId, attachmentId, versionId);
    }

    @DELETE
    @Path("")
    @Operation(
            summary = "Delete multiple attachments",
            description = "Delete multiple attachments from a specific card based on query options",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query")
            },
            requestBody = @RequestBody(description = ""),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachments deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deleteMany(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            WsQueryOptions wsQueryOptions
    ) {
        return cardAttachmentWs.deleteMany(classId, cardId, wsQueryOptions);
    }

}
