/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.base.Splitter;
import com.google.firebase.database.utilities.Pair;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsEmailData;
import org.cmdbuild.service.rest.v4.serializationhelpers.EmailTemplateSerializationType;
import org.cmdbuild.temp.TempService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.calendar.beans.CalendarEvent.EVENT_TABLE;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import static org.cmdbuild.service.rest.v4.serializationhelpers.EmailTemplateSerializationType.*;
import static org.cmdbuild.utils.io.CmIoUtils.*;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.*;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

/**
 * @author ldare
 */
@Component
public class CalendarEventEmailWsCommand {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DmsService dmsService;
    private final TempService tempService;
    private final CalendarService calendarService;
    private final EmailService emailService;
    private final OperationUserSupplier operationUserSupplier;
    private final DaoService daoService;
    private final EmailTemplateService emailTemplateService;
    private final EmailAccountService emailAccountService;

    public CalendarEventEmailWsCommand(DmsService dmsService, TempService tempService, CalendarService calendarService, EmailService emailService, OperationUserSupplier operationUserSupplier, DaoService daoService, EmailTemplateService emailTemplateService, EmailAccountService emailAccountService) {
        this.dmsService = checkNotNull(dmsService);
        this.tempService = checkNotNull(tempService);
        this.calendarService = checkNotNull(calendarService);
        this.emailService = checkNotNull(emailService);
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.daoService = checkNotNull(daoService);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.emailAccountService = checkNotNull(emailAccountService);
    }

    public List<EmailAttachment> convertAttachmentsToEmailAttachments(List<Attachment> attachments, List<String> tempId) {
        return listOf(EmailAttachment.class).accept(l -> {
            attachments.stream().map(a -> {
                DataHandler data = newDataHandler(toByteArray(a.getDataHandler()), a.getContentType().toString(), a.getContentDisposition().getFilename());
                dmsService.checkRegularFileAttachment(DocumentDataImpl.copyOf(data).build(), null);
                return EmailAttachmentImpl.copyOf(toDataSource(data)).build();
            }).forEach(l::add);
            nullToEmpty(tempId).stream().flatMap(t -> Splitter.on(",").omitEmptyStrings().splitToList(t).stream()).map(t -> {
                DataHandler data = tempService.getTempData(t);
                if (!tempService.getTempInfo(t).isSourceSecure()) {
                    dmsService.checkRegularFileAttachment(DocumentDataImpl.copyOf(data).build(), null);
                }
                return EmailAttachmentImpl.copyOf(toDataSource(data)).build();
            }).forEach(l::add);
        });
    }

    public Pair<Email, Boolean> doCreate(Long eventId, Boolean applyTemplate, Boolean templateOnly, List<String> tempId, List<Attachment> parts) {
        calendarService.getUserEvent(eventId);
        List<Attachment> attachments;
        Attachment body;
        if (parts.size() == 1) {
            attachments = emptyList();
            body = getOnlyElement(parts);
        } else {
            attachments = parts.stream().filter(a -> !equal(a.getDataHandler().getName(), "email")).collect(toImmutableList());
            body = parts.stream().filter(a -> equal(a.getDataHandler().getName(), "email")).collect(onlyElement("missing 'email' multipart json payload"));
        }
        List<EmailAttachment> emailAttachments = convertAttachmentsToEmailAttachments(attachments, tempId);
        WsEmailData emailData = fromJson(readToString(body.getDataHandler()), WsEmailData.class);

        Set<EmailTemplateSerializationType> templateSerializationType = templateSerializationType(applyTemplate, templateOnly, false);

        Card card = getCard(eventId, emailData);
        Email email = emailData.toEmail().withReference(card == null ? null : card.getId()).build();

        Boolean expr = getExpr(emailData, templateSerializationType, email, card);
        email = handleTemplate(email, emailData, card);
        email = toBooleanOrDefault(expr, templateSerializationType.contains(ETS_APPLYTEMPLATE)) ? applyTemplate(email, emailData, card) : email;

        email = EmailImpl.copyOf(email).withAbortableByUser(operationUserSupplier.getUsername()).build();

        if (!templateSerializationType.contains(ETS_TEMPLATEONLY)) {
            logger.debug("to email=< {} > adding {} input attachments =< {} >", email.getId(), emailAttachments.size(), emailAttachments);
            email = EmailImpl.copyOf(email).addAttachments(emailAttachments).build();
            email = emailService.create(email);
        }

        if (templateSerializationType.contains(ETS_UPLOADTEMPLATEATTACHMENTS)) {
            handleTemplateAttachments(email, emailData, card);
        }

        return new Pair<>(email, expr);
    }

    public Card getCard(Long eventId, WsEmailData emailData) {
        Card card;
        if (isSinthesizedResource(eventId.toString())) {
            card = emailData.hasCardData() ? CardImpl.buildCard(daoService.getClasse(isSinthesizedResource(EVENT_TABLE) ? toStringNotBlank(emailData.getCardData().getValues().get("_type")) : checkNotBlank(EVENT_TABLE)), emailData.getCardData().getValues()) : null;
        } else {
            card = daoService.getCard(EVENT_TABLE, toLong(eventId.toString())); //TODO check user permissions
        }
        return card;
    }

    public Email doUpdate(Long eventId, Long emailId, WsEmailData emailData) {
        calendarService.getUserEvent(eventId);
        Email current = emailService.getOne(emailId);
        checkArgument(equal(ES_DRAFT, current.getStatus()), "cannot update email with status = %s", current.getStatus());
        Email email = emailData.toEmail().withReference(eventId).withId(emailId).build();
        return emailService.update(email);
    }

    public Collection<Email> doReadAll(Long eventId, WsQueryOptions wsQueryOptions) {
        calendarService.getUserEvent(eventId);
        DaoQueryOptions queryOptions = wsQueryOptions.getQuery();
        return emailService.getAllForCard(eventId, queryOptions);
    }

    public Email doRead(Long eventId, Long emailId) {
        calendarService.getUserEvent(eventId);
        return emailService.getOne(emailId);
    }

    public void doDelete(Long eventId, Long emailId) {
        calendarService.getUserEvent(eventId);
        emailService.delete(emailService.getOne(emailId));
    }

    public static Set<EmailTemplateSerializationType> templateSerializationType(boolean applyTemplate, boolean templateOnly, boolean applyTemplateAttachments) {
        FluentSet<EmailTemplateSerializationType> setTemplateSerializationType = set();
        if (templateOnly) {
            setTemplateSerializationType.with(ETS_APPLYTEMPLATE, ETS_TEMPLATEONLY);
        }
        if (applyTemplate) {
            setTemplateSerializationType.with(ETS_APPLYTEMPLATE);
        }
        if (applyTemplateAttachments) {
            setTemplateSerializationType.with(ETS_UPLOADTEMPLATEATTACHMENTS);
        }
        return setTemplateSerializationType;
    }

    public Boolean getExpr(WsEmailData emailData, Set<EmailTemplateSerializationType> templateSerializationType, Email email, Card card) {
        Boolean expr = null;
        if (templateSerializationType.contains(ETS_APPLYTEMPLATE)) {
            if (emailData.hasExpr()) {
                expr = handleTemplateExpr(email.getTemplate(), emailData, card);
            }
        }
        return expr;
    }


    public boolean handleTemplateExpr(long template, WsEmailData emailData, Card serverCard) {
        Card clientCard;
        if (emailData.hasCardData()) {
            clientCard = CardImpl.copyOf(serverCard).withAttributes(emailData.getCardData().getValues()).build();
        } else {
            clientCard = serverCard;
        }
        return toBooleanOrDefault(emailService.applyTemplateExpr(template, emailData.getExpr(), clientCard, serverCard), false);
    }

    /**
     * Check if synthesized by UI to handle generation of a report even if a
     * card is not available, after an import/export operation
     *
     * @param resourceId
     * @return
     */
    public static boolean isSinthesizedResource(String resourceId) {
        return equal(resourceId, "_ANY");
    }

    public Email handleTemplate(Email email, WsEmailData emailData, @Nullable Card serverCard) {
        if (!email.hasTemplate() && isNotBlank(emailData.getTemplate()) && !emailTemplateService.isSysTemplate(emailData.getTemplate())) {
            email = EmailImpl.copyOf(email).withTemplate(emailTemplateService.getByNameOrId(emailData.getTemplate()).getId()).build();
        }
        if (!email.hasAccount() && isNotBlank(emailData.getAccount())) {
            email = EmailImpl.copyOf(email).withAccount(emailAccountService.getAccountByIdOrCode(emailData.getAccount()).getId()).build();
        }
        return email;
    }

    public void handleTemplateAttachments(Email email, WsEmailData emailData, @Nullable Card serverCard) {
        checkArgument(email.getId() != null, "cannot upload attachments to email with id null");
        if (email.getTemplate() != null && emailTemplateService.getById(email.getTemplate()).hasTemplateAttachments()) {
            List<EmailAttachment> emailAttachments = applyTemplate(email, emailData, serverCard).getAttachments();
            logger.debug("email =< {} > adding {} template generated attachments =< {} >", email, emailAttachments.size(), emailAttachments);
            emailService.saveEmailAttachments(EmailImpl.copyOf(email).addAttachments(emailAttachments).build(), true);
        }
    }

    public Email applyTemplate(Email email, @Nullable WsEmailData emailData, @Nullable Card serverCard) {
        if (!email.hasTemplate()) {
            return emailService.applySysTemplate(email, emailData.getTemplate());
        } else {
            if (emailData != null && emailData.hasCardData()) {
                Card clientCard = CardImpl.copyOf(serverCard).withAttributes(emailData.getCardData().getValues()).build();
                return emailService.applyTemplate(email, clientCard, serverCard);
            } else if (serverCard != null) {
                return emailService.applyTemplate(email, serverCard);
            } else {
                return emailService.applyTemplate(email);
            }
        }
    }


}