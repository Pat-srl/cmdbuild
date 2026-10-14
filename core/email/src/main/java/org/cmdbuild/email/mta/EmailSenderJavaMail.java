/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.annotation.Nullable;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import java.time.ZonedDateTime;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.config.EmailQueueConfiguration;
import org.cmdbuild.debuginfo.InstanceInfoService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.EmailSignatureService;
import static org.cmdbuild.email.mta.EmailMtaServiceImpl.EMAIL_HEADER_MESSAGE_ID;
import static org.cmdbuild.email.template.EmailUtils.parseEmailHeaderToken;
import static org.cmdbuild.email.utils.EmailMtaUtils.getMessageHeader;
import static org.cmdbuild.email.utils.EmailMtaUtils.getMessageInfoSafe;
import static org.cmdbuild.email.utils.EmailMtaUtils.serializeMessageHeaders;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class EmailSenderJavaMail extends BaseEmailSenderProvider {

    private final EmailQueueConfiguration queueConfig;

    public EmailSenderJavaMail(EmailAccount emailAccount, EmailQueueConfiguration queueConfig, InstanceInfoService infoService, EmailSignatureService signatureService) {
        super(emailAccount, infoService, signatureService, LoggerFactory.getLogger(EmailSenderJavaMail.class));
        logger = LoggerFactory.getLogger(getClass());

        this.queueConfig = checkNotNull(queueConfig);
    }

    @Override
    public Email sendEmail(Email email) throws MessagingException {
        checkArgument(email.hasDestinationAddress(), "invalid email: no destination address found (TO, CC or BCC)");
        Email emailToSend = prepareEmail(email);
        try (EmailSmtpSessionProvider emailJavaMailSmtpSessionProvider = buildEmailSmtpSessionProvider(emailAccount, queueConfig.getSmtpTimeoutSeconds(), logger)) {
            logger.debug("send email = {}", emailToSend);
            Message message = buildMessage(emailToSend, infoService, emailJavaMailSmtpSessionProvider.getSession());
            logger.debug("send message = {}", getMessageInfoSafe(message));
            emailJavaMailSmtpSessionProvider.getTransport().sendMessage(message, message.getAllRecipients());
            String messageId = checkNotBlank(parseEmailHeaderToken(getMessageHeader(message, EMAIL_HEADER_MESSAGE_ID)), "error: message sent, but message id header is null");
            String rawHeaders = serializeMessageHeaders(message);
            final ZonedDateTime sentDateTime = toDateTime(message.getSentDate());

            Email emailSent = buildMessageSent(emailToSend, sentDateTime, messageId, rawHeaders);

            if (isNotBlank(super.emailAccount.getSentEmailFolder())) {
                EmailMoverJavaMail emailMover = new EmailMoverJavaMail(emailAccount, logger);
                try {
                    emailMover.storeEmail(emailSent, super.emailAccount.getSentEmailFolder(), infoService);
                } catch (Exception ex) {
                    logger.warn(marker(), "error while storing sent email to imap folder =< %s > for email = %s", super.emailAccount.getSentEmailFolder(), emailSent, ex);
                }
            }
            return emailSent;
        }
    }

    @Override
    public void testConnection() {
        logger.info("test smtp connection for account = {}", emailAccount);

        Transport transport;
        try (EmailSmtpSessionProvider smtpSessionProvider = buildEmailSmtpSessionProvider(emailAccount, null, logger)) {
            transport = smtpSessionProvider.getTransport();
            checkArgument(transport.isConnected(), "smtp is not connected");
            logger.info("smtp connection ok");
        } catch (MessagingException ex) {
            throw new EmailException(ex);
        }

        logger.info("test smtp connection for account = {}", emailAccount);
    }

    protected EmailSmtpSessionProvider buildEmailSmtpSessionProvider(EmailAccount emailAccount, @Nullable Integer smtpTimeoutSeconds, Logger logger) throws MessagingException {
        return new EmailSmtpSessionJavaMailProvider(emailAccount, smtpTimeoutSeconds, logger);
    }

}
