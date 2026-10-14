/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.data.EmailRepository;
import org.cmdbuild.lock.LockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class EmailReceiverGoogle extends EmailReceiverJavaMail {

    public EmailReceiverGoogle(EmailAccount emailAccount, LockService lockService, EmailRepository repository) {
        super(emailAccount, lockService, repository);

        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    protected EmailImapSessionProvider buildEmailImapSessionProvider(EmailAccount emailAccount, Logger logger) {
        return new EmailImapSessionGoogleProvider(emailAccount, logger);
    }
}
