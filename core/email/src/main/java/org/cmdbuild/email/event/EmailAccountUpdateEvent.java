/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.email.event;


import org.cmdbuild.email.EmailAccount;

/**
 * @author ldare
 */
public class EmailAccountUpdateEvent {

    private final EmailAccount emailAccount;

    public EmailAccountUpdateEvent(EmailAccount emailAccount) {
        this.emailAccount = emailAccount;
    }

    public EmailAccount getEmailAccount() {
        return this.emailAccount;
    }
}
