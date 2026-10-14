/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.annotation.Nullable;
import jakarta.mail.MessagingException;
import org.cmdbuild.config.EmailQueueConfiguration;
import org.cmdbuild.debuginfo.InstanceInfoService;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailSignatureService;
import org.slf4j.Logger;

/**
 *
 * @author afelice
 */
public class EmailSenderGoogle extends EmailSenderJavaMail {

    public EmailSenderGoogle(EmailAccount emailAccount, EmailQueueConfiguration queueConfig, InstanceInfoService infoService, EmailSignatureService signatureService) {
        super(emailAccount, queueConfig, infoService, signatureService);
    }

    @Override
    protected EmailSmtpSessionProvider buildEmailSmtpSessionProvider(EmailAccount emailAccount, @Nullable Integer smtpTimeoutSeconds, Logger logger) throws MessagingException {
        return new EmailSmtpSessionGoogleProvider(emailAccount, smtpTimeoutSeconds, logger);
    }
}
