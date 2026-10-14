/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.MessagingException;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.data.EmailRepository;
import static org.cmdbuild.email.mta.EmailAccountGoogleHelper.buildEmailAccount_Receiver;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailReceiveConfig_DoNothing;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailReceiveConfig_MoveReceivedMsgs;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailRepository;
import static org.cmdbuild.email.mta.EmailTestHelper.mockLockService;
import org.cmdbuild.lock.LockService;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class EmailReceiverGoogleIT {

    @Test
    public void testConnection() {
        System.out.println("testConnection");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Receiver();
        EmailReceiverGoogle instance = buildEmailReceiver(emailAccount);

        // act:
        instance.testConnection();

        // assert:
        // nothing to assert
    }

    @Test
    public void testReceiveMails() throws MessagingException {
        System.out.println("receiveMails");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Receiver();
        EmailReceiverGoogle instance = buildEmailReceiver(emailAccount);

        // act:
        instance.receiveMails(mockEmailReceiveConfig_DoNothing());

        // assert:
        // nothing to assert
    }

    /**
     * Test of receiveMails method, moving received messages to another folder,
     * of class EmailReceiverGoogle
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    public void testReceiveMails_MoveReceivedMsgs() throws MessagingException {
        System.out.println("receiveMails_MoveReceivedMsgs");

        // arrange:
        final EmailAccount emailAccount = buildEmailAccount_Receiver();
        EmailReceiverGoogle instance = buildEmailReceiver(emailAccount);

        // act:
        // Throws MessagingException
        instance.receiveMails(mockEmailReceiveConfig_MoveReceivedMsgs());

        // assert:
        // Nothing to assert
    }

    private EmailReceiverGoogle buildEmailReceiver(EmailAccount emailAccount) {
        EmailRepository mockEmailRepository = mockEmailRepository();
        LockService mockLockService = mockLockService();
        EmailReceiverGoogle instance = new EmailReceiverGoogle(emailAccount, mockLockService, mockEmailRepository);
        return instance;
    }

}
