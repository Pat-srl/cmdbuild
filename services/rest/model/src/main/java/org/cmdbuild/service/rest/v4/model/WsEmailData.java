/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.email.EmailStatus;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import org.cmdbuild.email.beans.EmailImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import org.cmdbuild.utils.lang.CmPreconditions;

public class WsEmailData {

    private final Long delay;
    private final Long autoReplyTemplate, signature;
    private final String from;
    private final String to;
    private final String cc;
    private final String bcc;
    private final String subject;
    private final String body;
    private final String expr;
    private final String replyTo;
    private final String contentType;
    private final String template, account;
    private final Boolean keepSynchronization;
    private final Boolean isReadByUser;
    private final Boolean noSubjectPrefix;
    private final Boolean promptSynchronization;
    private final EmailStatus status;
    private final WsCardData card;

    public WsEmailData(
            @JsonProperty(value = "delay") Long delay,
            @JsonProperty(value = "from") String from,
            @JsonProperty(value = "replyTo") String replyTo,
            @JsonProperty(value = "to") String to,
            @JsonProperty(value = "cc") String cc,
            @JsonProperty(value = "bcc") String bcc,
            @JsonProperty(value = "subject") String subject,
            @JsonProperty(value = "body") String body,
            @JsonProperty(value = "contentType") String contentType,
            @JsonProperty(value = "account") String account,
            @JsonProperty(value = "signature") Long signature,
            @JsonProperty(value = "template") String template,
            @JsonProperty(value = "autoReplyTemplate") Long autoReplyTemplate,
            @JsonProperty(value = "keepSynchronization") Boolean keepSynchronization,
            @JsonProperty(value = "noSubjectPrefix") Boolean noSubjectPrefix,
            @JsonProperty(value = "promptSynchronization") Boolean promptSynchronization,
            @JsonProperty(value = "isReadByUser") Boolean isReadByUser,
            @JsonProperty(value = "status") String status,
            @JsonProperty(value = "_expr") String expr,
            @JsonProperty(value = "_card") WsCardData card) {
        this.delay = delay;
        this.from = from;
        this.replyTo = replyTo;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
        this.account = account;
        this.signature = signature;
        this.contentType = contentType;
        this.template = template;
        this.autoReplyTemplate = autoReplyTemplate;
        this.keepSynchronization = keepSynchronization;
        this.noSubjectPrefix = noSubjectPrefix;
        this.promptSynchronization = promptSynchronization;
        this.status = ObjectUtils.firstNonNull(EmailStatus.parseEmailStatus(status), EmailStatus.ES_DRAFT);
        this.expr = expr;
        this.card = card;
        this.isReadByUser = isReadByUser;
    }

    public Boolean isReadByUser() {
        return isReadByUser;
    }

    public EmailImpl.EmailImplBuilder toEmail() {
        return EmailImpl.builder()
                .withReplyTo(replyTo)
                .withDelay(delay)
                .withBcc(bcc).withCc(cc)
                .withSubject(subject)
                .withContent(body)
                .withContentType(contentType)
                .withFrom(from)
                .withKeepSynchronization(keepSynchronization)
                .withNoSubjectPrefix(noSubjectPrefix)
                .withPromptSynchronization(promptSynchronization)
                .withStatus(status)
                .withAutoReplyTemplate(autoReplyTemplate)
                .withTo(to)
                .withSignature(signature)
                .accept(b -> {
                    if (NumberUtils.isCreatable(template)) {
                        b.withTemplate(toLong(template));
                    }
                    if (NumberUtils.isCreatable(account)) {
                        b.withAccount(toLong(account));
                    }
                });
    }

    public boolean hasStatus(EmailStatus status) {
        return equal(status, ES_DRAFT);
    }

    public boolean hasCardData() {
        return card != null && !card.getValues().isEmpty();
    }

    public WsCardData getCardData() {
        return Preconditions.checkNotNull(card);
    }

    public boolean hasExpr() {
        return StringUtils.isNotBlank(expr);
    }

    public String getExpr() {
        return CmPreconditions.checkNotBlank(expr);
    }

    public String getTemplate() {
        return template;
    }

    public String getAccount() {
        return account;
    }

}
