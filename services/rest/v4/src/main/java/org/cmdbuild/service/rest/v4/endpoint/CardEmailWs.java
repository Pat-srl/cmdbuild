package org.cmdbuild.service.rest.v4.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.CalendarEventEmailWsCommand;
import org.cmdbuild.service.rest.v4.model.WsEmailData;
import org.cmdbuild.service.rest.v4.serializationhelpers.EmailSerializationHelper;
import org.cmdbuild.service.rest.v4.serializationhelpers.EmailTemplateSerializationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import static org.cmdbuild.email.utils.EmailMtaUtils.acquireEmail;
import static org.cmdbuild.email.utils.EmailMtaUtils.parseEmail;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.v4.command.CalendarEventEmailWsCommand.isSinthesizedResource;
import static org.cmdbuild.service.rest.v4.command.CalendarEventEmailWsCommand.templateSerializationType;
import static org.cmdbuild.service.rest.v4.serializationhelpers.EmailTemplateSerializationType.*;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytes;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.*;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

@Path("{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/{" + TYPE_NAME + ":cards|instances}/{" + CARD_ID + "}/emails")
@Tag(name = "Emails", description = "Operations about emails attached to cards")
@Produces(APPLICATION_JSON)
@Component
public class CardEmailWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final EmailService emailService;
    private final OperationUserSupplier operationUser;
    private final CalendarEventEmailWsCommand calendarEventEmailWsCommand;
    private final EmailSerializationHelper emailSerializationHelper;

    public CardEmailWs(DaoService dao, EmailService emailService, OperationUserSupplier operationUser, CalendarEventEmailWsCommand calendarEventEmailWsCommand, EmailSerializationHelper emailSerializationHelper) {
        this.dao = checkNotNull(dao);
        this.emailService = checkNotNull(emailService);
        this.operationUser = checkNotNull(operationUser);
        this.calendarEventEmailWsCommand = checkNotNull(calendarEventEmailWsCommand);
        this.emailSerializationHelper = checkNotNull(emailSerializationHelper);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get emails for a card",
            description = "Get emails for a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Card not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        DaoQueryOptions queryOptions = wsQueryOptions.getQuery();
        Card card = dao.getCard(classId, cardId);
        Collection<Email> list = emailService.getAllForCard(card.getId(), queryOptions);
        return response(paged(list, wsQueryOptions.isDetailed() ? emailSerializationHelper::serializeDetailedEmail : emailSerializationHelper::serializeBasicEmail, toInt(wsQueryOptions.getOffset()), toInt(wsQueryOptions.getLimit())));
    }

    @GET
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Get a specific email",
            description = "Obtain details of a specific email attached to a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Email not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to query") Long emailId
    ) {
        dao.getCard(classId, cardId); //TODO check user permissions
        Email email = emailService.getOne(emailId);
        return response(emailSerializationHelper.serializeDetailedEmail(email));//TODO check email id
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new email",
            description = "Create a new email attached to a card",
            requestBody = @RequestBody(description = "Multipart body: the 'email' part holds the email data as json, any other part is an attachment file", required = true, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "email", schema = @Schema(implementation = WsEmailData.class)),
                    @SchemaProperty(name = "attachments", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
            })),
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Card not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") String cardId,
            @QueryParam("apply_template") @Parameter(description = "Apply template to the email") @DefaultValue(FALSE) Boolean applyTemplate,
            @QueryParam("upload_template_attachments") @Parameter(description = "Upload template attachments to the email") @DefaultValue(FALSE) Boolean uploadTemplateAttachments,
            @QueryParam("template_only") @Parameter(description = "Only create template") @DefaultValue(FALSE) Boolean templateOnly,
            @QueryParam(ATTACHMENTS) @Parameter(description = "List of attachment ids to attach to the email") List<String> tempId,
            List<Attachment> parts
    ) {
        List<Attachment> attachments = parts.size() == 1 ? emptyList() : parts.stream().filter(a -> !equal(a.getDataHandler().getName(), "email")).collect(toImmutableList());
        Attachment body = parts.size() == 1 ? getOnlyElement(parts) : parts.stream().filter(a -> equal(a.getDataHandler().getName(), "email")).collect(onlyElement("missing 'email' multipart json payload"));
        List<EmailAttachment> emailAttachments = calendarEventEmailWsCommand.convertAttachmentsToEmailAttachments(attachments, tempId);

        logger.debug("attachments in input [{}], email attachments created [{}]", attachments.size(), emailAttachments.size());

        WsEmailData emailData = fromJson(readToString(body.getDataHandler()), WsEmailData.class);

        Set<EmailTemplateSerializationType> templateSerializationType = templateSerializationType(applyTemplate, templateOnly, uploadTemplateAttachments);

        Card card;
        if (isSinthesizedResource(cardId)) {
            card = emailData.hasCardData() ? CardImpl.buildCard(dao.getClasse(isSinthesizedResource(classId) ? toStringNotBlank(emailData.getCardData().getValues().get("_type")) : checkNotBlank(classId)), emailData.getCardData().getValues()) : null;
        } else {
            card = dao.getCard(classId, toLong(cardId)); //TODO check user permissions
        }
        Email email = emailData.toEmail().withReference(card == null ? null : card.getId()).build();

        Boolean expr = calendarEventEmailWsCommand.getExpr(emailData, templateSerializationType, email, card);
        email = calendarEventEmailWsCommand.handleTemplate(email, emailData, card);
        email = toBooleanOrDefault(expr, templateSerializationType.contains(ETS_APPLYTEMPLATE)) ? calendarEventEmailWsCommand.applyTemplate(email, emailData, card) : email;

        email = EmailImpl.copyOf(email).withAbortableByUser(operationUser.getUsername()).build();

        if (!templateSerializationType.contains(ETS_TEMPLATEONLY)) {
            logger.debug("to email=< {} > adding {} input attachments =< {} >", email.getId(), emailAttachments.size(), emailAttachments);
            email = EmailImpl.copyOf(email).addAttachments(emailAttachments).build();
            email = emailService.create(email);
        }

        if (templateSerializationType.contains(ETS_UPLOADTEMPLATEATTACHMENTS)) {
            calendarEventEmailWsCommand.handleTemplateAttachments(email, emailData, card);
        }

        return response(emailSerializationHelper.serializeDetailedEmail(email, expr));
    }

    @POST
    @Path("load")
    @Operation(
            summary = "Load an email from raw data",
            description = "Load an email from raw data",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Email not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object load(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @RequestBody(description = "Email data", required = true) WsEmailLoadData data
    ) {
        Email email = emailService.create(EmailImpl.copyOf(parseEmail(data.data)).withReference(cardId).build());
        logger.info("loaded email = {}", email);
        return response(emailSerializationHelper.serializeDetailedEmail(email));
    }

    @POST
    @Path("acquire")
    @Operation(
            summary = "Acquire an email from raw data",
            description = "Acquire an email from raw data",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Email not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object acquire(
            @RequestBody(description = "Email data", required = true) WsEmailLoadData data
    ) {
        Email email = emailService.create(acquireEmail(unpackBytes(data.data)));
        logger.info("acquired email = {}", email);
        return response(emailSerializationHelper.serializeDetailedEmail(email));
    }

    @POST
    @Path("{" + EMAIL_ID + "}/send")
    @Operation(
            summary = "Send an email",
            description = "Send an email attached to a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Email not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object send(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to query") Long emailId,
            @QueryParam("upload_template_attachments") @Parameter(description = "Upload template attachments to the email") @DefaultValue(FALSE) Boolean uploadTemplateAttachments,
            @QueryParam("template_only") @Parameter(description = "Only create template") @DefaultValue(FALSE) Boolean templateOnly,
            @QueryParam("delay") @Parameter(description = "Delay in seconds") @DefaultValue("skipDelay") String customDelay
    ) {
        Card card = dao.getCard(classId, cardId); //TODO check user permissions
        Email current = emailService.getOne(emailId);
        checkArgument(equal(ES_DRAFT, current.getStatus()), "cannot send email with status = %s", current.getStatus());
        Email email = current;
        if (!templateOnly) {
            checkArgument(customDelay.equals("skipDelay") || isCreatable(customDelay), "cannot send email with delay = %s", customDelay);
            if (customDelay.equals("skipDelay")) {
                email = EmailImpl.copyOf(current).withStatus(ES_OUTGOING).withReference(card.getId()).withId(emailId).withAbortableByUser(operationUser.getUsername()).build();
            } else {
                email = EmailImpl.copyOf(current).withStatus(ES_OUTGOING).withReference(card.getId()).withId(emailId).withAbortableByUser(operationUser.getUsername()).withDelay(toLong(customDelay)).build();
            }
            email = emailService.update(email);
        }

        if (uploadTemplateAttachments) {
            calendarEventEmailWsCommand.handleTemplateAttachments(email, null, card);
        }

        return response(emailSerializationHelper.serializeDetailedEmail(email));
    }

    @POST
    @Path("{" + EMAIL_ID + "}/abort")
    @Operation(
            summary = "Abort sending an email",
            description = "Abort sending an email attached to a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Email not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object abort(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to query") Long emailId,
            @RequestBody(description = "Email data", required = true) WsEmailData emailData
    ) {
        Email current = emailService.getOne(emailId);
        checkArgument(current.isOutgoing() && current.isAbortableByUser(operationUser.getUsername()) && emailData.hasStatus(ES_DRAFT), "cannot abort email with status = %s from user = %s", current.getStatus(), operationUser.getUsername());
        Email email = emailService.update(EmailImpl.copyOf(current).withDelay(emailData.toEmail().build().getDelay()).withStatus(ES_DRAFT).build());
        return response(emailSerializationHelper.serializeDetailedEmail(email));
    }

    @PUT
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Update an email",
            description = "Update an email attached to a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Email not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to query") Long emailId,
            @RequestBody(description = "Email data", required = true) WsEmailData emailData,
            @QueryParam("apply_template") @Parameter(description = "Apply template to the email") @DefaultValue(FALSE) Boolean applyTemplate,
            @QueryParam("upload_template_attachments") @Parameter(description = "Upload template attachments to the email") @DefaultValue(FALSE) Boolean uploadTemplateAttachments,
            @QueryParam("template_only") @Parameter(description = "Template only") @DefaultValue(FALSE) Boolean templateOnly
    ) {
        Card card = dao.getCard(classId, cardId); //TODO check user permissions
        Email current = emailService.getOne(emailId);
        if (current.isReceived()) {
            Email email = emailService.update(EmailImpl.copyOf(current).withIsReadByUser(emailData.isReadByUser()).build());
            return response(emailSerializationHelper.serializeDetailedEmail(email));
        } else if (current.isOutgoing() && current.isAbortableByUser(operationUser.getUsername()) && emailData.hasStatus(ES_DRAFT)) {
            Email email = emailService.update(EmailImpl.copyOf(current).withStatus(ES_DRAFT).build());
            return response(emailSerializationHelper.serializeDetailedEmail(email));
        } else {
            checkArgument(equal(ES_DRAFT, current.getStatus()), "cannot update email with status = %s", current.getStatus());
            Email email = emailData.toEmail().withReference(card.getId()).withId(emailId).withAbortableByUser(operationUser.getUsername()).build();

            Set<EmailTemplateSerializationType> templateSerializationType = templateSerializationType(applyTemplate, templateOnly, uploadTemplateAttachments);

            Boolean expr = calendarEventEmailWsCommand.getExpr(emailData, templateSerializationType, email, card);
            email = calendarEventEmailWsCommand.handleTemplate(email, emailData, card);
            email = toBooleanOrDefault(expr, templateSerializationType.contains(ETS_APPLYTEMPLATE)) ? calendarEventEmailWsCommand.applyTemplate(email, emailData, card) : email;

            if (!templateSerializationType.contains(ETS_TEMPLATEONLY)) {
                email = emailService.update(email);
            }
            if (templateSerializationType.contains(ETS_UPLOADTEMPLATEATTACHMENTS)) {
                calendarEventEmailWsCommand.handleTemplateAttachments(email, emailData, card);
            }
            return response(emailSerializationHelper.serializeDetailedEmail(email, expr));
        }
    }

    @DELETE
    @Path("{" + EMAIL_ID + "}/")
    @Operation(
            summary = "Delete an email",
            description = "Delete an email attached to a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Email not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId,
            @PathParam(EMAIL_ID) @Parameter(description = "Id of the email to query") Long emailId
    ) {
        Card card = dao.getCard(classId, cardId); //TODO check user permissions
        emailService.delete(emailService.getOne(emailId));//TODO check email id
        return success();
    }

    public static class WsEmailLoadData {

        private final String data;

        public WsEmailLoadData(@JsonProperty("data") String data) {
            this.data = checkNotBlank(data);
        }

    }

}
