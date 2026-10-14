/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import java.lang.invoke.MethodHandles;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.beans.EmailAccountImpl;
import static org.cmdbuild.email.mta.TokenLoaderHelper.loadCredentialsStr;

/**
 *
 * @author afelice
 */
public class EmailAccountJavaMailHelper {

    static String A_KNOWN_EMAIL_PASSWORD;
    private static final String A_KNOWN_EMAIL_PASSWORD_FILE_PATH = "/org/cmdbuild/modernauth/service/test/tokens/cmdbwf_pwd.encr";
    static final String A_KNOWN_EMAIL_INCOMING_FOLDER = "INBOX"; // mail.com ha: Drafts, INBOX, OUTBOX, Sent, Spam, Trash

    static {
        // Decrypt using CM3EASY
        A_KNOWN_EMAIL_PASSWORD = loadCredentialsStr(A_KNOWN_EMAIL_PASSWORD_FILE_PATH, MethodHandles.lookup().lookupClass().getName());
    }

    static EmailAccount buildEmailAccount_Receiver() {
        // @todo collegare normale account aziendale #7177 con crypt CM3EASY
        EmailAccount emailAccount = EmailAccountImpl.builder()
                .withAuthenticationType(EmailAccount.AUTHENTICATION_TYPE_DEFAULT)
                .withUsername("cmdbwf@tecnotools.it")
                .withAddress("cmdbwf@tecnotools.it")
                .withName("cmdbwf")
                .withPassword(A_KNOWN_EMAIL_PASSWORD)
                .withImapServer("imap.tecnoteca.com")
                .withImapPort(143)
                .withImapSsl(false)
                .withImapStartTls(true)
                .withConfig("mail.imap.ssl.trust", "imap.tecnoteca.com")
                .build();
        return emailAccount;
    }

    static EmailAccount buildEmailAccount_Sender() {
        // @todo collegare normale account aziendale #7177 con crypt CM3EASY
        EmailAccount emailAccount = EmailAccountImpl.builder()
                .withAuthenticationType(EmailAccount.AUTHENTICATION_TYPE_DEFAULT)
                .withUsername("cmdbwf@tecnotools.it")
                .withAddress("cmdbwf@tecnotools.it")
                .withName("cmdbwf")
                .withPassword(A_KNOWN_EMAIL_PASSWORD)
                .withSmtpServer("mail.tecnoteca.com")
                .withSmtpSsl(false)
                .withSmtpPort(587)
                .withSmtpStartTls(true)
                .withConfig("mail.smtp.ssl.trust", "mail.tecnoteca.com")
                .build();
        return emailAccount;
    }
}
