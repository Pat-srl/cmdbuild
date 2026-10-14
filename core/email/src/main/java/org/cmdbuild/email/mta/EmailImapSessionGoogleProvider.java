/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import org.cmdbuild.email.EmailAccount;
import org.slf4j.Logger;

/**
 *
 * @author afelice
 */
public class EmailImapSessionGoogleProvider extends EmailImapSessionJavaMailProvider {

    public EmailImapSessionGoogleProvider(EmailAccount emailAccount, Logger logger) {
        super(emailAccount, new EmailAuthenticatorImapGoogle(), logger);
    }

}
