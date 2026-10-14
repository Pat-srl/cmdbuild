/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.serializationhelpers;


import jakarta.annotation.Nullable;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import static org.cmdbuild.email.EmailStatus.serializeEmailStatus;
import static org.cmdbuild.email.utils.EmailMtaUtils.embedEmailInlineAttachmentsAsBase64;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
@Component
public class EmailSerializationHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DmsService dmsService;
    private final EmailTemplateService emailTemplateService;

    public EmailSerializationHelper(DmsService dmsService, EmailTemplateService emailTemplateService) {
        this.dmsService = dmsService;
        this.emailTemplateService = emailTemplateService;
    }

    public static CmMapUtils.FluentMap<String, Object> serializeSimpleEmail(Email email) {
        return map(
                "_id", email.getId(),
                "from", email.getFrom(),
                "replyTo", email.getReplyTo(),
                "to", email.getTo(),
                "cc", email.getCc(),
                "bcc", email.getBcc(),
                "subject", email.getSubject(),
                "contentType", email.getContentType(),
                "date", toIsoDateTime(email.getDate()),
                "status", serializeEmailStatus(email.getStatus()),
                "delay", email.getDelay(),
                "keepSynchronization", email.getKeepSynchronization(),
                "promptSynchronization", email.getPromptSynchronization(),
                "isReadByUser", email.isReadByUser(),
                "template", email.getTemplate()
        );
    }

    public CmMapUtils.FluentMap<String, Object> serializeBasicEmail(Email email) {
        return serializeSimpleEmail(email).accept(m -> {
            EmailTemplate emailTemplate = email.getTemplate() != null ? emailTemplateService.getByIdOrNull(email.getTemplate()) : null;
            m.put("_hasTemplateAttachments", emailTemplate != null ? emailTemplate.hasTemplateAttachments() : false);
        });
    }

    public CmMapUtils.FluentMap<String, Object> serializeDetailedEmail(Email email) {
        return serializeDetailedEmail(email, null);
    }

    public CmMapUtils.FluentMap<String, Object> serializeDetailedEmail(Email email, @Nullable Boolean expr) {
        String html = email.getContentHtmlOrWrappedPlaintext();
        if (email.hasId() && dmsService.isEnabled()) {
            html = embedEmailInlineAttachmentsAsBase64(html, list(dmsService.getCardAttachments(EMAIL_CLASS_NAME, email.getId())).map(d -> EmailAttachmentImpl.build(toDataSource(dmsService.download(d.getDocumentId(), null)))));
        }

        logger.debug("serializing email =< {} > with generated [{}] attachments =< {} >", email.getId(), email.getAttachments().size(), email.getAttachments());

        return serializeBasicEmail(email).with(
                "account", email.getAccount(),
                "signature", email.getSignature(),
                "autoReplyTemplate", email.getAutoReplyTemplate(),
                "noSubjectPrefix", email.getNoSubjectPrefix(),
                "body", email.getContent(),
                "_content_plain", email.getContentPlaintext(),
                "_content_html", html,
                "card", email.getReference()
        ).skipNullValues().with("_expr", expr);
    }
}
