/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.email.event;


import org.cmdbuild.email.EmailAccount;

/**
 * @author ldare
 */
public class EmailAccountCreateEvent {

    private final EmailAccount emailAccount;

    public EmailAccountCreateEvent(EmailAccount emailAccount) {
        this.emailAccount = emailAccount;
    }

    public EmailAccount getEmailAccount() {
        return this.emailAccount;
    }
}
