package org.cmdbuild.service.rest.v2.endpoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.EmailStatus;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import org.cmdbuild.email.beans.EmailImpl;
import static org.cmdbuild.email.EmailStatus.parseEmailStatus;
import static org.cmdbuild.email.EmailStatus.serializeEmailStatus;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("classes/{classId}/cards/{cardId}/emails")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardEmailsWsV2 {

    private final DaoService dao;
    private final EmailService emailService;

    public CardEmailsWsV2(DaoService dao, EmailService emailService) {
        this.dao = checkNotNull(dao);
        this.emailService = checkNotNull(emailService);
    }

    @GET
    @Path("statuses/")
    public Object statuses() {
        //TODO? in v2.5 this was not working
        return null;
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("classId") String classId, @PathParam("cardId") Long cardId, WsEmailData emailData) {
        Card card;
        if (equal(classId, "_ANY") && equal(cardId, "_ANY")) {
            card = null;
        } else {
            card = dao.getCard(classId, toLong(cardId));
        }
        Email email = emailData.toEmail().withReference(card == null ? null : card.getId()).build();

        email = emailService.create(email);
        return map("data", serializeDetailedEmail(email), "meta", map());
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("classId") String classId, @PathParam("cardId") Long cardId) {
        Card card = dao.getCard(classId, cardId);
        Collection<Email> list = emailService.getAllForCard(card.getId());
        return map("data", list.stream().map(this::serializeDetailedEmail).collect(toList()), "meta", map("total", list.size()));
    }

    @GET
    @Path("{emailId}/")
    public Object readOne(@PathParam("classId") String classId, @PathParam("cardId") Long cardId, @PathParam("emailId") Long emailId) {
        Card card = dao.getCard(classId, cardId);
        Email email = emailService.getOne(emailId);
        return map("data", serializeDetailedEmail(email), "meta", map());
    }

    @PUT
    @Path("{emailId}/")
    public Object update(@PathParam("classId") String classId, @PathParam("cardId") Long cardId, @PathParam("emailId") Long emailId, WsEmailData emailData) {
        Card card = dao.getCard(classId, cardId); //TODO check user permissions
        Email current = emailService.getOne(emailId);
        checkArgument(equal(ES_DRAFT, current.getStatus()), "cannot update email with status = %s", current.getStatus());
        Email email = emailData.toEmail().withReference(card.getId()).withId(emailId).build();
        email = emailService.update(email);
        return map("data", serializeDetailedEmail(email), "meta", map());
    }

    @DELETE
    @Path("{emailId}/")
    public Object delete(@PathParam("classId") String classId, @PathParam("cardId") Long cardId, @PathParam("emailId") Long emailId) {
        Card card = dao.getCard(classId, cardId);
        emailService.delete(emailService.getOne(emailId));
        return success();
    }

    public CmMapUtils.FluentMap<String, Object> serializeBasicEmail(Email email) {
        return map(
                "_id", email.getId(),
                "from", email.getFrom(),
                "replyTo", email.getReplyTo(),
                "to", email.getTo(),
                "cc", email.getCc(),
                "bcc", email.getBcc(),
                "subject", email.getSubject(),
                "body", email.getContent(),
                "contentType", email.getContentType(),
                "_content_plain", email.getContentPlaintext(),
                "date", toIsoDateTime(email.getDate()),
                "status", serializeEmailStatus(email.getStatus())
        );
    }

    public CmMapUtils.FluentMap<String, Object> serializeDetailedEmail(Email email) {
        return serializeBasicEmail(email).with(
                "account", email.getAccount(),
                "delay", email.getDelay(),
                "template", email.getTemplate(),
                "autoReplyTemplate", email.getAutoReplyTemplate(),
                "keepSynchronization", email.getKeepSynchronization(),
                "noSubjectPrefix", email.getNoSubjectPrefix(),
                "promptSynchronization", email.getPromptSynchronization(),
                "_content_html", email.getContentHtmlOrWrappedPlaintext(),
                "card", email.getReference(),
                "attachment", email.getAttachments().isEmpty() ? emptyList() : list(email.getAttachments()).map(EmailAttachment::getFileName)                
        ).skipNullValues();
    }

    public static class WsEmailData {

        private final Long delay, account, template, autoReplyTemplate;
        private final String from, to, cc, bcc, subject, body, replyTo, contentType;
        private final Boolean keepSynchronization, noSubjectPrefix, promptSynchronization;
        private final EmailStatus status;
        private final WsCardData card;

        public WsEmailData(
                @JsonProperty("delay") Long delay,
                @JsonProperty("from") String from,
                @JsonProperty("replyTo") String replyTo,
                @JsonProperty("to") String to,
                @JsonProperty("cc") String cc,
                @JsonProperty("bcc") String bcc,
                @JsonProperty("subject") String subject,
                @JsonProperty("contentType") String contentType,
                @JsonProperty("body") String body,
                @JsonProperty("account") Long account,
                @JsonProperty("template") Long template,
                @JsonProperty("autoReplyTemplate") Long autoReplyTemplate,
                @JsonProperty("keepSynchronization") Boolean keepSynchronization,
                @JsonProperty("noSubjectPrefix") Boolean noSubjectPrefix,
                @JsonProperty("promptSynchronization") Boolean promptSynchronization,
                @JsonProperty("status") String status,
                @JsonProperty("_card") WsCardData card) {
            this.delay = delay;
            this.from = from;
            this.replyTo = replyTo;
            this.to = to;
            this.cc = cc;
            this.bcc = bcc;
            this.subject = subject;
            this.contentType = contentType;
            this.body = body;
            this.account = account;
            this.template = template;
            this.autoReplyTemplate = autoReplyTemplate;
            this.keepSynchronization = keepSynchronization;
            this.noSubjectPrefix = noSubjectPrefix;
            this.promptSynchronization = promptSynchronization;
            this.status = firstNonNull(parseEmailStatus(status), ES_DRAFT);
            this.card = card;
        }

        public EmailImpl.EmailImplBuilder toEmail() {
            return EmailImpl.builder()
                    .withReplyTo(replyTo)
                    .withDelay(delay)
                    .withAccount(account)
                    .withBcc(bcc)
                    .withCc(cc)
                    .withContent(body)
                    .withContentType(contentType)
                    .withDelay(delay)
                    .withFrom(from)
                    .withKeepSynchronization(keepSynchronization)
                    .withNoSubjectPrefix(noSubjectPrefix)
                    .withPromptSynchronization(promptSynchronization)
                    .withStatus(status)
                    .withSubject(subject)
                    .withTemplate(template)
                    .withAutoReplyTemplate(autoReplyTemplate)
                    .withTo(to);
        }

        public boolean hasCardData() {
            return card != null;
        }

        public WsCardData getCardData() {
            return checkNotNull(card);
        }

    }

    public static class WsCardData {

        private final Map<String, Object> values;

        @JsonCreator
        public WsCardData(Map<String, Object> values) {
            this.values = values;//mapClassValues(checkNotNull(values)).immutable();    TODO
        }

        public Map<String, Object> getValues() {
            return values;
        }

    }

}
