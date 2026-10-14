/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import java.lang.invoke.MethodHandles;
import org.cmdbuild.email.EmailAccount;
import static org.cmdbuild.email.EmailAccount.AUTHENTICATION_TYPE_GOOGLE_OAUTH2;
import org.cmdbuild.email.beans.EmailAccountImpl;
import static org.cmdbuild.email.mta.TokenLoaderHelper.loadCredentialsStr;

/**
 *
 * @author afelice
 */
public class EmailAccountGoogleHelper {

    static String GOOGLE_SERVICE_ACCOUNT_CREDENTIALS;
    // @todo collegare account aziendale Google #7177
    private static final String GOOGLE_SERVICE_ACCOUNT_CREDENTIALS_FILE_PATH = "/org/cmdbuild/modernauth/service/test/tokens/cmdbuild-test-google.encr";

    private static final String GOOGLE_SERVICE_ACCOUNT_RELATED_USER_EMAIL_NAME = "tecnotecaService";
    private static final String GOOGLE_SERVICE_ACCOUNT_RELATED_USER_EMAIL = "service@tecnoteca.com";

    static {
        GOOGLE_SERVICE_ACCOUNT_CREDENTIALS = loadCredentialsStr(GOOGLE_SERVICE_ACCOUNT_CREDENTIALS_FILE_PATH, MethodHandles.lookup().lookupClass().getName());
    }

    static EmailAccount buildEmailAccount_Receiver() {
        // @todo collegare normale account aziendale #7177
        return EmailAccountImpl.builder()
                .withAuthenticationType(AUTHENTICATION_TYPE_GOOGLE_OAUTH2)
                .withUsername(GOOGLE_SERVICE_ACCOUNT_RELATED_USER_EMAIL)
                .withAddress(GOOGLE_SERVICE_ACCOUNT_RELATED_USER_EMAIL)
                .withName(GOOGLE_SERVICE_ACCOUNT_RELATED_USER_EMAIL_NAME)
                .withPassword(GOOGLE_SERVICE_ACCOUNT_CREDENTIALS)
                .withImapServer("imap.gmail.com")
                .withImapPort(993)
                .withImapSsl(true)
                .withImapStartTls(false)
                .build();
    }

    static EmailAccount buildEmailAccount_Sender() {
        // @todo collegare normale account aziendale #7177 con crypt CM3EASY
        return EmailAccountImpl.builder()
                .withAuthenticationType(AUTHENTICATION_TYPE_GOOGLE_OAUTH2)
                .withUsername(GOOGLE_SERVICE_ACCOUNT_RELATED_USER_EMAIL)
                .withAddress(GOOGLE_SERVICE_ACCOUNT_RELATED_USER_EMAIL)
                .withName(GOOGLE_SERVICE_ACCOUNT_RELATED_USER_EMAIL_NAME)
                .withPassword(GOOGLE_SERVICE_ACCOUNT_CREDENTIALS)
                .withSmtpPort(587)
                .withSmtpServer("smtp.gmail.com")
                .withSmtpStartTls(true)
                .build();
    }

}
