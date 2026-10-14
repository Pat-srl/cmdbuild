/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import static org.cmdbuild.dao.logging.LoggingSupport.logger;
import org.cmdbuild.email.EmailAccount;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class EmailImapSessionJavaMailProviderIT {

    /**
     * Test of getStore method, of class EmailImapSessionJavaMailProvider.
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    public void testGetStore() throws MessagingException {
        System.out.println("getStore");

        // arrange:
        EmailAccount emailAccount = EmailAccountJavaMailHelper.buildEmailAccount_Receiver();

        Store store;
        try (EmailImapSessionProvider emailImapSessionProvider = new EmailImapSessionJavaMailProvider(emailAccount, new EmailAuthenticatorJavaMail(), logger)) {
            // act:
            store = emailImapSessionProvider.getStore();

            // assert:
            assertTrue(store.isConnected());
        } // assert fro close():
        assertFalse(store.isConnected());
    }

}
