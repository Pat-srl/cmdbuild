/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.Objects;
import jakarta.annotation.Nullable;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailException;
import static org.cmdbuild.email.utils.EmailMtaUtils.JAVAX_NET_SSL_SSL_SOCKET_FACTORY;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_IMAPS_HOST;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_IMAPS_PORT;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_IMAP_HOST;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_IMAP_PORT;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_IMAP_SOCKET_FACTORY_CLASS;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_IMAP_STARTTLS_ENABLE;
import static org.cmdbuild.email.utils.EmailMtaUtils.MAIL_STORE_PROTOCOL;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.log.LogUtils.printStreamFromLogger;
import org.slf4j.Logger;

/**
 * Autocloseable store for an IMAP {@link jakarta.mail.Session} on given
 * <code>emailAccount</code>
 *
 * If correctly constructed, the contained {@link jakarta.mail.Store} is already
 * connected, and automatically closed if built in a <code>try-resource</code>.
 *
 * @author afelice
 */
public class EmailImapSessionJavaMailProvider implements EmailImapSessionProvider {

    private final Logger logger;

    private Session session;
    private Store store;

    public EmailImapSessionJavaMailProvider(EmailAccount emailAccount,
                                            Logger logger) {
        this(emailAccount, new EmailAuthenticatorJavaMail(), logger);
    }

    protected EmailImapSessionJavaMailProvider(EmailAccount emailAccount,
                                               EmailAuthenticator emailAuthenticator,
            Logger logger) {
        this.logger = logger;

        try {
            session = createImapSession(emailAccount, emailAuthenticator);
            store = session.getStore();
            store.connect();
        } catch (MessagingException ex) {
            throw new EmailException(ex, "error creating imap session for account = %s", emailAccount);
        }
    }

    /**
     * @return a {@link jakarta.mail.Session} for given emailAcocunt
     */
    @Override
    public Session getSession() {
        return session;
    }

    /**
     * {@link jakarta.mail.Session#getStore()}
     *
     * @return a already connected {@link jakarta.mail.Store}
     * @see jakarta.mail.Store#connect()
     */
    @Override
    public Store getStore() {
        return store;
    }

    @Override
    public void close() {
        if (store != null) {
            logger.debug("close imap connection");
            try {
                if (store.isConnected()) {
                    store.close();
                }
            } catch (MessagingException ex) {
                logger.warn("error closing receiver mta session", ex);
            }
            store = null;
        }
        session = null;
    }

    private Session createImapSession(EmailAccount account, EmailAuthenticator emailAuthenticator) {
        return createImapSession(account, null, emailAuthenticator);
    }

    private Session createImapSession(EmailAccount account, @Nullable Integer imapTimeoutSeconds,
            EmailAuthenticator emailAuthenticator) {
        checkArgument(account.isImapConfigured(), "cannot open imap connection, imap not configured for account = %s", account);
        CmMapUtils.FluentMap<String, String> properties = mapOf(String.class, String.class).with(
                //                                "mail.imap.fetchsize", "1048576", //"52428800"
                //                                "mail.imaps.fetchsize", "1048576" //"52428800"
                "mail.imap.partialfetch", "false",
                "mail.imaps.partialfetch", "false"
        ).with(System.getProperties());
        if (isNotNullAndGtZero(imapTimeoutSeconds)) {
            String timeout = Integer.toString(imapTimeoutSeconds * 1000);
            properties.put(
                    "mail.imap.connectiontimeout", timeout,
                    "mail.imap.timeout", timeout,
                    "mail.imap.writetimeout", timeout,
                    "mail.imaps.connectiontimeout", timeout,
                    "mail.imaps.timeout", timeout,
                    "mail.imaps.writetimeout", timeout
            );
        }
        properties.put(MAIL_STORE_PROTOCOL, account.getImapSsl() ? "imaps" : "imap");
        properties.put(MAIL_IMAP_STARTTLS_ENABLE, (account.getImapStartTls() ? TRUE : FALSE).toString());
        if (account.getImapSsl()) {
            properties.put(MAIL_IMAPS_HOST, account.getImapServer());
            if (isNotNullAndGtZero(account.getImapPort())) {
                properties.put(MAIL_IMAPS_PORT, account.getImapPort().toString());
            }
            properties.put(MAIL_IMAP_SOCKET_FACTORY_CLASS, JAVAX_NET_SSL_SSL_SOCKET_FACTORY);
        } else {
            properties.put(MAIL_IMAP_HOST, account.getImapServer());
            if (isNotNullAndGtZero(account.getImapPort())) {
                properties.put(MAIL_IMAP_PORT, account.getImapPort().toString());
            }
        }
        account.getConfig().forEach(properties::put);

        Authenticator authenticator = null;
        if (account.isAuthenticationEnabled()) {
            authenticator = emailAuthenticator.buildAuthenticator(account, properties);
        }

        properties.filterValues(Objects::nonNull);

        logger.trace("imap server configuration:\n{}", mapToLoggableString(properties));
        logger.debug("open imap connection for account = {}", account);
        Session createdSession = Session.getInstance(properties.toProperties(), authenticator);
        if (logger.isTraceEnabled()) {
            createdSession.setDebugOut(printStreamFromLogger(logger::trace));
            createdSession.setDebug(true);
        }
        return createdSession;
    }

}
