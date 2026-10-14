/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.test;

import org.cmdbuild.email.Email;
import static org.cmdbuild.email.EmailStatus.ES_DRAFT;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.beans.EmailImpl;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EmailTest {

    @Test
    public void testEmailBuilder() {
        Email email = EmailImpl.builder().withStatus(ES_DRAFT)
                .withFrom("oneuser@onehost.net")
                .withTo("seconduser@secondhost.net")
                .withCc("cc@others.net")
                .withBcc("bcc@otherhost.net")
                .build();

        assertEquals("oneuser@onehost.net", email.getFrom());
        assertEquals("seconduser@secondhost.net", email.getTo());
        assertEquals("cc@others.net", email.getCc());
        assertEquals("bcc@otherhost.net", email.getBcc());

        email = EmailImpl.copyOf(email)
                .withFrom("from1@host.net")
                .withToAddresses(list("to1a@host.net", "to1b@host.net"))
                .withCcAddresses(list("cc1a@host.net", "\"cc1\" <cc1b@host.net>"))
                .withBccAddresses(list("bcc1a@host.net", "bcc1b@host.net"))
                .build();

        assertEquals("from1@host.net", email.getFrom());
        assertEquals("to1a@host.net, to1b@host.net", email.getTo());
        assertEquals("cc1a@host.net, \"cc1\" <cc1b@host.net>", email.getCc());
        assertEquals("bcc1a@host.net, bcc1b@host.net", email.getBcc());
        assertEquals(list("to1a@host.net", "to1b@host.net"), email.getToEmailAddressList());
        assertEquals(list("cc1a@host.net", "cc1b@host.net"), email.getCcEmailAddressList());
        assertEquals(list("bcc1a@host.net", "bcc1b@host.net"), email.getBccEmailAddressList());
        assertEquals(list("to1a@host.net", "to1b@host.net"), email.getToRawAddressList());
        assertEquals(list("cc1a@host.net", "cc1 <cc1b@host.net>"), email.getCcRawAddressList());
        assertEquals(list("bcc1a@host.net", "bcc1b@host.net"), email.getBccRawAddressList());

        Email otherEmail = EmailImpl.copyOf(email)
                .addToAddress("\"to\" <to2@host.net>")
                .addCcAddress("\"cc\" <cc2@host.net>")
                .addBccAddress("\"bcc\" <bcc2@host.net>")
                .build();

        assertEquals("from1@host.net", otherEmail.getFrom());
        assertEquals("to1a@host.net, to1b@host.net, \"to\" <to2@host.net>", otherEmail.getTo());
        assertEquals("cc1a@host.net, cc1 <cc1b@host.net>, \"cc\" <cc2@host.net>", otherEmail.getCc());
        assertEquals("bcc1a@host.net, bcc1b@host.net, \"bcc\" <bcc2@host.net>", otherEmail.getBcc());
        assertEquals(list("to1a@host.net", "to1b@host.net", "to2@host.net"), otherEmail.getToEmailAddressList());
        assertEquals(list("cc1a@host.net", "cc1b@host.net", "cc2@host.net"), otherEmail.getCcEmailAddressList());
        assertEquals(list("bcc1a@host.net", "bcc1b@host.net", "bcc2@host.net"), otherEmail.getBccEmailAddressList());
        assertEquals(list("to1a@host.net", "to1b@host.net", "to <to2@host.net>"), otherEmail.getToRawAddressList());
        assertEquals(list("cc1a@host.net", "cc1 <cc1b@host.net>", "cc <cc2@host.net>"), otherEmail.getCcRawAddressList());
        assertEquals(list("bcc1a@host.net", "bcc1b@host.net", "bcc <bcc2@host.net>"), otherEmail.getBccRawAddressList());

        otherEmail = EmailImpl.copyOf(email)
                .addToAddresses(list("\"to\" <to2@host.net>"))
                .addCcAddresses(list("\"cc\" <cc2@host.net>"))
                .addBccAddresses(list("\"bcc\" <bcc2@host.net>"))
                .build();

        assertEquals("from1@host.net", otherEmail.getFrom());
        assertEquals("to1a@host.net, to1b@host.net, \"to\" <to2@host.net>", otherEmail.getTo());
        assertEquals("cc1a@host.net, cc1 <cc1b@host.net>, \"cc\" <cc2@host.net>", otherEmail.getCc());
        assertEquals("bcc1a@host.net, bcc1b@host.net, \"bcc\" <bcc2@host.net>", otherEmail.getBcc());
        assertEquals(list("to1a@host.net", "to1b@host.net", "to2@host.net"), otherEmail.getToEmailAddressList());
        assertEquals(list("cc1a@host.net", "cc1b@host.net", "cc2@host.net"), otherEmail.getCcEmailAddressList());
        assertEquals(list("bcc1a@host.net", "bcc1b@host.net", "bcc2@host.net"), otherEmail.getBccEmailAddressList());
        assertEquals(list("to1a@host.net", "to1b@host.net", "to <to2@host.net>"), otherEmail.getToRawAddressList());
        assertEquals(list("cc1a@host.net", "cc1 <cc1b@host.net>", "cc <cc2@host.net>"), otherEmail.getCcRawAddressList());
        assertEquals(list("bcc1a@host.net", "bcc1b@host.net", "bcc <bcc2@host.net>"), otherEmail.getBccRawAddressList());
    }

    @Test
    public void testEmailContent4() {
        Email email = EmailImpl.builder().withStatus(ES_OUTGOING).withFrom("my.sender@email.net").withTo("my.dest@email.net").withSubject("My Subject")
                .withContentType("application/octet-stream")
                .withContent(readToString(getClass().getResourceAsStream("/org/cmdbuild/test/core/email/email_4_content.txt"))).build();
        assertEquals("text/html", email.getContentType());
    }

    @Test
    public void testEmailContent5() {
        Email email = EmailImpl.builder().withStatus(ES_OUTGOING).withFrom("my.sender@email.net").withTo("my.dest@email.net").withSubject("My Subject")
                .withContentType("application/octet-stream")
                .withContent(readToString(getClass().getResourceAsStream("/org/cmdbuild/test/core/email/email_5_content.txt"))).build();
        assertEquals("text/html", email.getContentType());
    }

}
