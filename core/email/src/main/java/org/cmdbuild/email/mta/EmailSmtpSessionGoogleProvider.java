/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.annotation.Nullable;
import jakarta.mail.MessagingException;
import org.cmdbuild.email.EmailAccount;
import org.slf4j.Logger;

/**
 *
 * @author afelice
 */
public class EmailSmtpSessionGoogleProvider extends EmailSmtpSessionJavaMailProvider {

    public EmailSmtpSessionGoogleProvider(EmailAccount emailAccount,
                                          @Nullable Integer smtpTimeoutSeconds,
                                          Logger logger) throws MessagingException {
        super(emailAccount, smtpTimeoutSeconds,
              new EmailAuthenticatorSmtpGoogle(), logger);
    }

}
