/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.MessagingException;
import java.util.List;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAttachment;
import static org.cmdbuild.email.mta.EmailAccountGoogleHelper.buildEmailAccount_Sender;
import static org.cmdbuild.email.mta.EmailTestHelper.A_KNOWN_IN_REPLY_TO;
import static org.cmdbuild.email.mta.EmailTestHelper.A_USER_DESTINATION_EMAIL;
import static org.cmdbuild.email.mta.EmailTestHelper.buildTestEmail;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailQueueConfiguration;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailSignatureService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class EmailSenderGoogleIT {

    @Test
    public void testSenderEmail() throws MessagingException {
        System.out.println("sendEmail");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Sender();
        EmailSenderGoogle instance = buildEmailSender(emailAccount);

        // (No) Attachments
        List<EmailAttachment> attachments = list();
        Email emailToSend = buildTestEmail(List.of(A_USER_DESTINATION_EMAIL), attachments, A_KNOWN_IN_REPLY_TO);
        // act:
        instance.sendEmail(emailToSend);

        // assert:
        // nothing to assert
    }

    @Test
    public void testConnection() {
        System.out.println("testConnection");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Sender();
        EmailSenderGoogle instance = buildEmailSender(emailAccount);

        // act:
        instance.testConnection();

        // assert:
        // nothing to assert
    }

    private EmailSenderGoogle buildEmailSender(EmailAccount emailAccount) {
        EmailSenderGoogle instance = new EmailSenderGoogle(emailAccount, mockEmailQueueConfiguration(), new EmailTestHelper.MockInstanceInfoService(), mockEmailSignatureService());
        return instance;
    }

}
