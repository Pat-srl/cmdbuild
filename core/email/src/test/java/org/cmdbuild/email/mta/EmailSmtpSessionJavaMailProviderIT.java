/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import static org.cmdbuild.dao.logging.LoggingSupport.logger;
import org.cmdbuild.email.EmailAccount;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class EmailSmtpSessionJavaMailProviderIT {

    private static final Integer SMTP_TIMEOUT_SECONDS = null; // Timeout disabled

    /**
     * Test of getTransport method, of class EmailSmtpSessionJavaMailProvider.
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    public void testGetTransport() throws MessagingException {
        System.out.println("getTransport");

        // arrange:
        EmailAccount emailAccount = EmailAccountJavaMailHelper.buildEmailAccount_Sender();

        Transport transport;
        try (EmailSmtpSessionProvider emailSmtpSessionProvider = new EmailSmtpSessionJavaMailProvider(
                emailAccount, SMTP_TIMEOUT_SECONDS, logger)) {
            // act:
            transport = emailSmtpSessionProvider.getTransport();

            // assert: 
            assertTrue(transport.isConnected());
        } // assert fro close():
        assertFalse(transport.isConnected());
    }

}
