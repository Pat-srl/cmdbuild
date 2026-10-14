package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsEmailData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/{typeName:cards|instances}/{" + CARD_ID + "}/emails")
@Tag(name = "Emails", description = "Operations about emails attached to cards")
@Produces(APPLICATION_JSON)
public class CardEmailWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final org.cmdbuild.service.rest.v4.endpoint.CardEmailWs cardEmailWs;

    public CardEmailWs(org.cmdbuild.service.rest.v4.endpoint.CardEmailWs cardEmailWs) {
        this.cardEmailWs = checkNotNull(cardEmailWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get emails for a card",
            description = "Get emails for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful operation")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId, WsQueryOptions wsQueryOptions
    ) {
        return cardEmailWs.readAll(classId, cardId, wsQueryOptions);
    }

    @GET
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Get a specific email",
            description = "Obtain details of a specific email attached to a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = EMAIL_ID, in = ParameterIn.PATH, description = "ID of the email to query")
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful operation")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam(EMAIL_ID) Long emailId
    ) {
        return cardEmailWs.read(classId, cardId, emailId);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new email",
            description = "Create a new email attached to a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = "apply_template", in = ParameterIn.QUERY, description = "Apply template to the email", schema = @Schema(type = "boolean", defaultValue = "false")),
                    @Parameter(name = "upload_template_attachments", in = ParameterIn.QUERY, description = "Upload template attachments", schema = @Schema(type = "boolean", defaultValue = "false")),
                    @Parameter(name = "template_only", in = ParameterIn.QUERY, description = "Template only", schema = @Schema(type = "boolean", defaultValue = "false")),
                    @Parameter(name = "attachments", in = ParameterIn.QUERY)
            },
            requestBody = @RequestBody(description = "Attachment parts"),
            responses = { @ApiResponse(responseCode = "200", description = "Successful operation")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) String cardId,
            @QueryParam("apply_template") @DefaultValue(FALSE) Boolean applyTemplate,
            @QueryParam("upload_template_attachments") @DefaultValue(FALSE) Boolean uploadTemplateAttachments,
            @QueryParam("template_only") @DefaultValue(FALSE) Boolean templateOnly,
            @QueryParam("attachments") List<String> tempId,
            List<Attachment> parts
    ) {
        return cardEmailWs.create(classId, cardId, applyTemplate, uploadTemplateAttachments, templateOnly, tempId, parts);
    }

    @POST
    @Path("load")
    @Operation(
            summary = "Load an email from raw data",
            description = "Load an email from raw data",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query")
            },
            requestBody = @RequestBody(description = "Raw data to load an email"),
            responses = { @ApiResponse(responseCode = "200", description = "Successful operation")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object load(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            org.cmdbuild.service.rest.v4.endpoint.CardEmailWs.WsEmailLoadData data
    ) {
        return cardEmailWs.load(classId, cardId, data);
    }

    @POST
    @Path("acquire")
    @Operation(
            summary = "Acquire an email from raw data",
            description = "Acquire an email from raw data",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            requestBody = @RequestBody(description = "Raw data to load an email"),
            responses = { @ApiResponse(responseCode = "200", description = "Successful operation")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object acquire(
            org.cmdbuild.service.rest.v4.endpoint.CardEmailWs.WsEmailLoadData data
    ) {
        return cardEmailWs.acquire(data);
    }

    @POST
    @Path("{" + EMAIL_ID + "}/send")
    @Operation(
            summary = "Send an email",
            description = "Send an email attached to a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = EMAIL_ID, in = ParameterIn.PATH, description = "ID of the email to query"),
                    @Parameter(name = "upload_template_attachments", in = ParameterIn.QUERY, description = "Upload template attachments", schema = @Schema(type = "boolean", defaultValue = "false")),
                    @Parameter(name = "template_only", in = ParameterIn.QUERY, description = "Template only", schema = @Schema(type = "boolean", defaultValue = "false")),
                    @Parameter(name = "delay", in = ParameterIn.QUERY, description = "Delay in seconds", schema = @Schema(type = "string", defaultValue = "skipDelay"))
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful operation")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object send(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam(EMAIL_ID) Long emailId,
            @QueryParam("upload_template_attachments") @DefaultValue(FALSE) Boolean uploadTemplateAttachments,
            @QueryParam("template_only") @DefaultValue(FALSE) Boolean templateOnly,
            @QueryParam("delay") @DefaultValue("skipDelay") String customDelay
    ) {
        return cardEmailWs.send(classId, cardId, emailId, uploadTemplateAttachments, templateOnly, customDelay);
    }

    @POST
    @Path("{" + EMAIL_ID + "}/abort")
    @Operation(
            summary = "Abort sending an email",
            description = "Abort sending an email attached to a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = EMAIL_ID, in = ParameterIn.PATH, description = "ID of the email to query")
            },
            requestBody = @RequestBody(description = "Email data"),
            responses = { @ApiResponse(responseCode = "200", description = "Successful operation")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object abort(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam(EMAIL_ID) Long emailId,
            WsEmailData emailData
    ) {
        return cardEmailWs.abort(classId, cardId, emailId, emailData);
    }

    @PUT
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Update an email",
            description = "Update an email attached to a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = "apply_template", in = ParameterIn.QUERY, description = "Apply template to the email", schema = @Schema(type = "boolean", defaultValue = "false")),
                    @Parameter(name = "upload_template_attachments", in = ParameterIn.QUERY, description = "Upload template attachments", schema = @Schema(type = "boolean", defaultValue = "false")),
                    @Parameter(name = "template_only", in = ParameterIn.QUERY, description = "Template only", schema = @Schema(type = "boolean", defaultValue = "false"))
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful operation")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId, @PathParam(EMAIL_ID) Long emailId, WsEmailData emailData,
            @QueryParam("apply_template") @DefaultValue(FALSE) Boolean applyTemplate,
            @QueryParam("upload_template_attachments") @DefaultValue(FALSE) Boolean uploadTemplateAttachments,
            @QueryParam("template_only") @DefaultValue(FALSE) Boolean templateOnly
    ) {
        return cardEmailWs.update(classId, cardId, emailId, emailData, applyTemplate, uploadTemplateAttachments, templateOnly);
    }

    @DELETE
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Delete an email",
            description = "Delete an email attached to a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "ID of the card to query"),
                    @Parameter(name = EMAIL_ID, in = ParameterIn.PATH, description = "ID of the email to query")
            },
            responses = { @ApiResponse(responseCode = "200", description = "Successful operation")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam(EMAIL_ID) Long emailId
    ) {
        return cardEmailWs.delete(classId, cardId, emailId);
    }
}
