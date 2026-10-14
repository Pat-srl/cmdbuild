/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import java.util.List;
import org.cmdbuild.debuginfo.InstanceInfoService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.email.EmailStatus;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailImpl;
import static org.cmdbuild.email.utils.EmailMtaUtils.fixEmailInlineAttachmentsWithOutgoingCidUrl;
import static org.cmdbuild.email.utils.EmailMtaUtils.fixEmailInlineHtmlAttachmentsWithCidBase64;
import static org.cmdbuild.email.template.EmailUtils.handleEmailSignatureForTemplate;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;

/**
 *
 * @author afelice
 */
public abstract class BaseEmailSenderProvider extends BaseEmailProvider implements EmailSenderProvider {

    protected final InstanceInfoService infoService;
    protected final EmailSignatureService signatureService;

    protected Logger logger;

    public BaseEmailSenderProvider(EmailAccount emailAccount, InstanceInfoService infoService, EmailSignatureService signatureService, Logger logger) {
        super(emailAccount);
        this.infoService = checkNotNull(infoService);
        this.signatureService = checkNotNull(signatureService);

        this.logger = logger;
    }

    protected Email prepareEmail(final Email email) {
        Email emailToSend = email;
        if (email.getFromRawAddressList().isEmpty()) {
            emailToSend = EmailImpl.copyOf(email).withFrom(emailAccount.getAddress()).build();
        }
        if (email.hasSignature() && isContentType(email.getContentType(), "text/html")) {
            emailToSend = EmailImpl.copyOf(emailToSend).withContent(handleEmailSignatureForTemplate(email.getContent(), signatureService.getSignatureHtmlForCurrentUser(email.getSignature()))).build();
        }

        if (email.hasAttachments()) {
            List<EmailAttachment> attachments = list(email.getAttachments()).map(a -> EmailAttachmentImpl.copyOf(a).withContentId(firstNotBlank(a.getContentId(), randomId())).build());
            String emailContent = emailToSend.getContent();
            if (isContentType(emailToSend.getContentType(), "text/html")) { //TODO handle also multipart email;
                emailContent = fixEmailInlineAttachmentsWithOutgoingCidUrl(emailContent, attachments);
                // add html inline images
                emailContent = fixEmailInlineHtmlAttachmentsWithCidBase64(emailContent);
            }
            emailToSend = EmailImpl.copyOf(emailToSend).withContent(emailContent).withAttachments(attachments).build();
        }
        return emailToSend;
    }

    protected Email buildMessageSent(Email emailToSend, final ZonedDateTime sentDateTime, String messageId, String rawHeaders) {
        logger.debug("sent message, id = {}", messageId);
        Email emailSent = EmailImpl.copyOf(emailToSend).withSentOrReceivedDate(firstNotNull(sentDateTime, now())).withStatus(EmailStatus.ES_SENT).withMessageId(messageId).withHeaders(rawHeaders).build();
        return emailSent;
    }

}
