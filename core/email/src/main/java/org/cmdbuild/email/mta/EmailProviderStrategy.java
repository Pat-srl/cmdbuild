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
import static org.cmdbuild.email.EmailAccount.AUTHENTICATION_TYPE_DEFAULT;
import static org.cmdbuild.email.EmailAccount.AUTHENTICATION_TYPE_GOOGLE_OAUTH2;
import static org.cmdbuild.email.EmailAccount.AUTHENTICATION_TYPE_MS_OAUTH2;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.email.data.EmailRepository;
import org.cmdbuild.lock.LockService;
import org.slf4j.Logger;

/**
 * Models different types of authentications for email providers:
 * <ol>
 * <li>username and password;
 * <li>google gmail with oath2;
 * <li>microsoft with oauth2; see
 * {@link org.cmdbuild.email.mta.EmailMSGraphClientStrategy} for details on
 * supported authentication types.
 * </ol>
 *
 * @author afelice
 */
public class EmailProviderStrategy {

    private final EmailMSGraphClientStrategy emailMSGraphClientStrategy = new EmailMSGraphClientStrategy();

    public EmailSenderProvider buildSender(EmailAccount emailAccount, EmailQueueConfiguration queueConfig, InstanceInfoService infoService, EmailSignatureService signatureService) {
        return switch (emailAccount.getAuthenticationType()) {
            case AUTHENTICATION_TYPE_MS_OAUTH2 ->
                new EmailSenderMSGraph(emailAccount, emailMSGraphClientStrategy.buildMSGraphClientProvider(emailAccount), infoService, signatureService);
            case AUTHENTICATION_TYPE_GOOGLE_OAUTH2 ->
                new EmailSenderGoogle(emailAccount, queueConfig, infoService, signatureService);
            case AUTHENTICATION_TYPE_DEFAULT ->
                new EmailSenderJavaMail(emailAccount, queueConfig, infoService, signatureService);
            default ->
                throw new EmailException("sender unsupported authentication type =< %s >", emailAccount.getAuthenticationType());
        };
    }

    public EmailReceiverProvider buildReceiver(EmailAccount emailAccount, LockService lockService, EmailRepository repository) {
        return switch (emailAccount.getAuthenticationType()) {
            case AUTHENTICATION_TYPE_MS_OAUTH2 ->
                new EmailReceiverMSGraph(emailAccount, emailMSGraphClientStrategy.buildMSGraphClientProvider(emailAccount), lockService, repository);
            case AUTHENTICATION_TYPE_GOOGLE_OAUTH2 ->
                new EmailReceiverGoogle(emailAccount, lockService, repository);
            case AUTHENTICATION_TYPE_DEFAULT ->
                new EmailReceiverJavaMail(emailAccount, lockService, repository);
            default ->
                throw new EmailException("receiver unsupported authentication type =< %s >", emailAccount.getAuthenticationType());
        };
    }

    public EmailImapSessionProvider buildImapSessionProvider(EmailAccount emailAccount, Logger logger) {
        return switch (emailAccount.getAuthenticationType()) {
            case AUTHENTICATION_TYPE_GOOGLE_OAUTH2 ->
                new EmailImapSessionGoogleProvider(emailAccount, logger);
            case AUTHENTICATION_TYPE_DEFAULT ->
                new EmailImapSessionJavaMailProvider(emailAccount, logger);
            default ->
                throw new EmailException("imap session provider unsupported authentication type =< %s >", emailAccount.getAuthenticationType());
        };
    }

    public EmailSmtpSessionProvider buildSmtpSessionProvider(
            EmailAccount emailAccount, @Nullable Integer smtpTimeoutSeconds,
            Logger logger) throws MessagingException {
        return switch (emailAccount.getAuthenticationType()) {
            case AUTHENTICATION_TYPE_GOOGLE_OAUTH2 ->
                new EmailSmtpSessionGoogleProvider(emailAccount, smtpTimeoutSeconds, logger);
            case AUTHENTICATION_TYPE_DEFAULT ->
                new EmailSmtpSessionJavaMailProvider(emailAccount, smtpTimeoutSeconds, logger);
            default ->
                throw new EmailException("smtp session provider unsupported authentication type =< %s >", emailAccount.getAuthenticationType());
        };
    }

}
