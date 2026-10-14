package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.media.SchemaProperty;
import com.google.common.base.Splitter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.CmIoUtils;
import org.springframework.stereotype.Component;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper;
import org.cmdbuild.service.rest.common.helpers.AttachmentWsHelper.WsAttachmentData;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.CmIoUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static jakarta.ws.rs.core.MediaType.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.CmIoUtils;
import org.springframework.stereotype.Component;

import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmZipUtils.buildZipFile;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/{" + TYPE_NAME + ":cards|instances}/{" + CARD_ID + "}/attachments/")
@Tag( name = "Card Attachments", description = "Manage attachments of a specific card" )
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class CardAttachmentWs {

    private final AttachmentWsHelper attachmentWsHelper;

    public CardAttachmentWs(AttachmentWsHelper attachmentWsHelper) {
        this.attachmentWsHelper = checkNotNull(attachmentWsHelper);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new attachment",
            description = "Create a new attachment for a specific card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            requestBody = @RequestBody(description = "Multipart body: optional attachment metadata (json) and the optional file to upload; alternatively use the copyFrom_* query parameters to copy an existing attachment", required = false, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = ATTACHMENT, schema = @Schema(implementation = WsAttachmentData.class)),
                    @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary"))
            })),
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
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card") Long cardId,
            @Multipart(value = ATTACHMENT, required = false) @Nullable WsAttachmentData attachment,
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            @QueryParam("copyFrom_class") @Parameter(description = "Copy from class") String sourceClassId,
            @QueryParam("copyFrom_card") @Parameter(description = "Copy from card") Long sourceCardId,
            @QueryParam("copyFrom_id") @Parameter(description = "Copy from attachment id") String sourceAttachmentId,
            @QueryParam("tempId") @Parameter(description = "Temp id") List<String> tempId
    ) throws IOException {
        if (sourceClassId == null) {
            if (dataHandler != null) {
                return attachmentWsHelper.create(classId, cardId, attachment, dataHandler);
            } else {
                return attachmentWsHelper.create(classId, cardId, attachment, tempId);
            }
        } else {
            return attachmentWsHelper.copyFrom(classId, cardId, attachment, sourceClassId, sourceCardId, sourceAttachmentId);
        }
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all attachments for a specific card",
            description = "Obtain a list of all attachments for a specific card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @BeanParam WsQueryOptions wsQueryOptions,
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "ID of the card to query") Long cardId
    ) {
        return attachmentWsHelper.readMany(wsQueryOptions, classId, cardId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Get a specific attachment",
            description = "Obtain details of a specific attachment for a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "ID of the card to query") Long cardId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "ID of the attachment to query") String attachmentId,
            @QueryParam("includeWidgets") @Parameter(description = "Include widgets", schema = @Schema(type = "boolean", defaultValue = "false")) @DefaultValue(FALSE) Boolean includeWidgets
    ) {
        return attachmentWsHelper.readOne(classId, cardId, attachmentId, includeWidgets);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/{file}")
    @Operation(
            summary = "Download an attachment file",
            description = "Download the file of a specific attachment for a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
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
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "ID of the card to query") Long cardId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "ID of the attachment to query") String attachmentId
    ) {
        return attachmentWsHelper.download(classId, cardId, attachmentId);
    }

    @GET
    @Path("_MANY/{file}")
    @Operation(
            summary = "Download multiple attachment files",
            description = "Download multiple attachment files for a specific card as a zip file",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
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
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "ID of the card to query") Long cardId,
            @QueryParam(ATTACHMENT_ID) @Parameter(description = "ID of the attachment", array = @ArraySchema(schema = @Schema(implementation = String.class))) List<String> attachmentId
    ) {
        checkArgument(!isNullOrEmpty(attachmentId), "empty attachmentId param");
        if (attachmentId.size() == 1 && getOnlyElement(attachmentId).equals("_ALL")) {
            attachmentId = attachmentWsHelper.getAllDocumentIdFromCard(classId, cardId);
        }
        Map<String, BigByteArray> map = attachmentId.stream().flatMap(a -> Splitter.on(",").splitToList(a).stream()).map(a -> attachmentWsHelper.download(classId, cardId, a)).collect(toMap(DataHandler::getName, CmIoUtils::toBigByteArray));//TODO improve split
        return newDataHandler(buildZipFile(map), "application/zip", "attachments.zip");
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/preview")
    @Operation(
            summary = "Preview an attachment",
            description = "Obtain a preview of a specific attachment for a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object preview(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "ID of the card to query") Long cardId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "ID of the attachment to query") String attachmentId
    ) {
        return attachmentWsHelper.preview(classId, cardId, attachmentId);
    }

    @PUT
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Update an existing attachment",
            description = "Update an existing attachment for a specific card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            requestBody = @RequestBody(description = "Multipart body: optional attachment metadata (json) and optional replacement file", required = false, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = ATTACHMENT, schema = @Schema(implementation = WsAttachmentData.class)),
                    @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary"))
            })),
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
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "ID of the card to query") Long cardId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "ID of the attachment to query") String attachmentId,
            @Nullable @Multipart(value = ATTACHMENT, required = false) WsAttachmentData attachment,
            @Nullable @Multipart(value = FILE, required = false) DataHandler dataHandler,
            @QueryParam("tempId") @Parameter(description = "Temp id") String tempId
    ) {
        if (isNotBlank(tempId)) {
            return attachmentWsHelper.update(classId, cardId, attachmentId, attachment, tempId);
        } else {
            return attachmentWsHelper.update(classId, cardId, attachmentId, attachment, dataHandler);
        }
    }

    @DELETE
    @Path("{" + ATTACHMENT_ID + "}/")
    @Operation(
            summary = "Delete an attachment",
            description = "Delete a specific attachment from a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "ID of the card to query") Long cardId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "ID of the attachment to query") String attachmentId
    ) {
        return attachmentWsHelper.delete(classId, cardId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history")
    @Operation(
            summary = "Get attachment history",
            description = "Obtain the history of a specific attachment for a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAttachmentHistory(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "ID of the card to query") Long cardId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "ID of the attachment to query") String attachmentId
    ) {
        return attachmentWsHelper.getAttachmentHistory(classId, cardId, attachmentId);
    }

    @GET
    @Path("{" + ATTACHMENT_ID + "}/history/{version}/{file}")
    @Operation(
            summary = "Download a previous version of an attachment",
            description = "Download a specific previous version of an attachment for a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
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
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "ID of the card to query") Long cardId,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "ID of the attachment to query") String attachmentId,
            @PathParam(VERSION) @Parameter(description = "Version ID") String versionId
    ) {
        return attachmentWsHelper.downloadPreviousVersion(classId, cardId, attachmentId, versionId);
    }

    @DELETE
    @Path("")
    @Operation(
            summary = "Delete multiple attachments",
            description = "Delete multiple attachments from a specific card based on query options",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachments deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deleteMany(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "ID of the card to query") Long cardId,
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        attachmentWsHelper.deleteAttachments(classId, cardId, wsQueryOptions.getQuery().getFilter());
        return success();
    }

}
