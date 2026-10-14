/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.Objects;
import jakarta.annotation.Nullable;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import org.cmdbuild.email.EmailAccount;
import static org.cmdbuild.email.utils.EmailMtaUtils.JAVAX_NET_SSL_SSL_SOCKET_FACTORY;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_SMPT_SOCKET_FACTORY_CLASS;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_SMPT_SOCKET_FACTORY_FALLBACK;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_SMTPS_AUTH;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_SMTPS_HOST;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_SMTPS_PORT;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_SMTP_AUTH;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_SMTP_HOST;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_SMTP_PORT;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_SMTP_STARTTLS_ENABLE;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_TRANSPORT_PROTOCOL;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.log.LogUtils.printStreamFromLogger;
import org.slf4j.Logger;

/**
 * Autocloseable transport for an SMTP {@link jakarta.mail.Session} on given
 * <code>emailAccount</code>
 *
 * @author afelice
 */
public class EmailSmtpSessionJavaMailProvider implements EmailSmtpSessionProvider {

    private final Logger logger;

    private Session session;
    private Transport transport;
    private final EmailAccount emailAccount;

    /**
     * Autocloseable transport for an IMAP {@link jakarta.mail.Session} on given
     * <code>emailAccount</code>
     *
     * If correctly constructed, the contained {@link jakarta.mail.Transport} is
     * already * connected, and automatically closed if built in a
     * <code>try-resource</code>.
     *
     * @param emailAccount
     * @param smtpTimeoutSeconds
     * @param logger
     * @throws MessagingException
     */
    public EmailSmtpSessionJavaMailProvider(EmailAccount emailAccount, @Nullable Integer smtpTimeoutSeconds, Logger logger) throws MessagingException {
        this(emailAccount, smtpTimeoutSeconds, new EmailAuthenticatorJavaMail(), logger);
    }

    protected EmailSmtpSessionJavaMailProvider(EmailAccount emailAccount, @Nullable Integer smtpTimeoutSeconds, EmailAuthenticator emailAuthenticator, Logger logger) throws MessagingException {
        this.logger = logger;

        this.emailAccount = emailAccount;
        session = createSmtpSession(emailAccount, smtpTimeoutSeconds, emailAuthenticator);

        // Raises MessagingException
        transport = session.getTransport();
        transport.connect();
    }

    /**
     * @return a {@link jakarta.mail.Session} for given emailAcocunt
     */
    @Override
    public Session getSession() {
        return session;
    }

    /**
     * {@link jakarta.mail.Session#getTransport()}
     *
     * @return a already connected {@link jakarta.mail.Transport}
     * @see jakarta.mail.Transport#connect()
     */
    @Override
    public Transport getTransport() {
        return transport;
    }

    @Override
    public void close() {
        if (transport != null) {
            logger.debug("closing smtp connection for account = {}",
                    emailAccount);
            try {
                if (transport.isConnected()) {
                    transport.close();
                }
            } catch (MessagingException ex) {
                logger.warn("error closing sender mta session", ex);
            }
            transport = null;
        }
        session = null;
    }

    private Session createSmtpSession(EmailAccount account, @Nullable Integer smtpTimeoutSeconds, EmailAuthenticator emailAuthenticator) {
        CmMapUtils.FluentMap<String, String> properties = map(System.getProperties());
        if (isNotNullAndGtZero(smtpTimeoutSeconds)) {
            String timeout = Integer.toString(smtpTimeoutSeconds * 1000);
            properties.put(
                    "mail.smtp.connectiontimeout", timeout,
                    "mail.smtp.timeout", timeout,
                    "mail.smtp.writetimeout", timeout,
                    "mail.smtps.connectiontimeout", timeout,
                    "mail.smtps.timeout", timeout,
                    "mail.smtps.writetimeout", timeout
            );
        }
        properties.put(MAIL_TRANSPORT_PROTOCOL, account.getSmtpSsl() ? "smtps" : "smtp");
        properties.put(MAIL_SMTP_STARTTLS_ENABLE, (account.getSmtpStartTls() ? TRUE : FALSE).toString());
        if (account.getSmtpSsl()) {
            properties.put(MAIL_SMTPS_HOST, account.getSmtpServer());
            if (isNotNullAndGtZero(account.getSmtpPort())) {
                properties.put(MAIL_SMTPS_PORT, account.getSmtpPort().toString());
            }
            properties.put(MAIL_SMTPS_AUTH, Boolean.toString(account.isAuthenticationEnabled()));
            properties.put(MAIL_SMPT_SOCKET_FACTORY_CLASS, JAVAX_NET_SSL_SSL_SOCKET_FACTORY);
            properties.put(MAIL_SMPT_SOCKET_FACTORY_FALLBACK, FALSE.toString());
        } else {
            properties.put(MAIL_SMTP_HOST, account.getSmtpServer());
            if (isNotNullAndGtZero(account.getSmtpPort())) {
                properties.put(MAIL_SMTP_PORT, account.getSmtpPort().toString());
            }
            properties.put(MAIL_SMTP_AUTH, Boolean.toString(account.isAuthenticationEnabled()));
        }
        account.getConfig().forEach(properties::put);

        Authenticator authenticator = null;
        if (account.isAuthenticationEnabled()) {
            authenticator = emailAuthenticator.buildAuthenticator(account, properties);
        }

        properties.filterValues(Objects::nonNull);

        logger.trace("smtp server configuration:\n{}", mapToLoggableString(properties));
        logger.debug("opening smtp connection for account = {}", account);
        Session newSession = Session.getInstance(properties.toProperties(), authenticator);
        if (logger.isTraceEnabled()) {
            newSession.setDebugOut(printStreamFromLogger(logger::trace));
            newSession.setDebug(true);
        }
        return newSession;
    }

}
