/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import java.util.List;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import static org.cmdbuild.email.mta.EmailTestHelper.*;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class EmailSenderMSGraphIT {

    protected static final String A_KNOWN_SMALL_ATTACHMENT = "/org/cmdbuild/modernauth/service/test/attachments/small.txt";
    protected static final String A_KNOWN_BIG_ATTACHMENT = "/org/cmdbuild/modernauth/service/test/attachments/4MBFile.zip";

    /**
     * Test of sendMail method, authentication delegated, of class
     * EmailSenderMSGraph.
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    public void testSendMail_Delegated() throws MessagingException {
        System.out.println("SendMail_Delegated");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_Delegated();
        EmailSenderMSGraph instance = buildEmailSenderMSGraph(emailAccount, new EmailMSGraphClient_Password(emailAccount));
        // (No) Attachments
        List<EmailAttachment> attachments = list();
        Email emailToSend = buildTestEmail(List.of(A_USER_DESTINATION_EMAIL), attachments, A_KNOWN_IN_REPLY_TO);

        // act:
        instance.sendEmail(emailToSend);

        // assert:
        // Nothing to assert
    }

    /**
     * Test of testConnection method, authentication delegated, of class
     * EmailSenderMSGraph.
     */
    @Test
    public void testTestConnection_Delegated() {
        System.out.println("testConnection_Delegated");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_Delegated();
        EmailSenderMSGraph instance = buildEmailSenderMSGraph(emailAccount, new EmailMSGraphClient_Password(emailAccount));

        // act:
        instance.testConnection();

        // assert:
        // Nothing to assert
    }

    /**
     * Test of sendMail method, authentication Client secret, of class
     * EmailSenderMSGraph.
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    public void testSendMail_ClientSecret() throws MessagingException {
        System.out.println("sendMail_ClientSecret");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientSecret();
        EmailSenderMSGraph instance = buildEmailSenderMSGraph(emailAccount, new EmailMSGraphClient_ClientSecret(emailAccount));

        // (No) Attachments
        List<EmailAttachment> attachments = list();
        Email emailToSend = buildTestEmail(List.of(A_USER_DESTINATION_EMAIL), attachments, A_KNOWN_IN_REPLY_TO);

        // act:
        instance.sendEmail(emailToSend);

        // assert:
        // Nothing to assert
    }

    /**
     * Test of testConnection method, authentication Client secret, of class
     * EmailReceiverMSGraph.
     */
    @Test
    public void testTestConnection_ClientSecret() {
        System.out.println("testConnection_ClientSecret");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientSecret();
        EmailSenderMSGraph instance = buildEmailSenderMSGraph(emailAccount, new EmailMSGraphClient_ClientSecret(emailAccount));

        // act:
        instance.testConnection();

        // assert:
        // Nothing to assert
    }

    /**
     * Test of sendMail method, authentication Client certificate, of class
     * EmailSenderMSGraph.
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    @Ignore
    public void testSendMail_ClientCertificate() throws MessagingException {
        System.out.println("sendMail_ClientCertificate");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientCertificate();
        EmailSenderMSGraph instance = buildEmailSenderMSGraph(emailAccount, new EmailMSGraphClient_ClientCertificate(emailAccount));

        // (No) Attachments
        List<EmailAttachment> attachments = list();
        Email emailToSend = buildTestEmail(List.of(A_USER_DESTINATION_EMAIL), attachments, A_KNOWN_IN_REPLY_TO);

        // act:
        instance.sendEmail(emailToSend);

        // assert:
        // Nothing to assert
    }

    /**
     * Test of testConnection method, authentication Client certificate, of
     * class EmailSenderMSGraph.
     */
    @Test
    @Ignore
    public void testTestConnection_ClientCertificate() {
        System.out.println("testConnection_ClientCertificate");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientCertificate();
        EmailSenderMSGraph instance = buildEmailSenderMSGraph(emailAccount, new EmailMSGraphClient_ClientCertificate(emailAccount));

        // act:
        instance.testConnection();

        // assert:
        // Nothing to assert
    }

    // ===<< TEST ATTACHMENTS >>====
    /**
     * Test of sendMail method, authentication Client secret, with small
     * attachment, of class EmailSenderMSGraph.
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    public void testSendMail_SmallAttachment() throws MessagingException {
        System.out.println("sendMail_ClientSecret_SmallAttachment");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientSecret();
        EmailSenderMSGraph instance = buildEmailSenderMSGraph(emailAccount, new EmailMSGraphClient_ClientSecret(emailAccount));

        // Small attachment
        EmailAttachment attachment = EmailAttachmentImpl.copyOf(getResourceDataSource(A_KNOWN_SMALL_ATTACHMENT)).build();
        List<EmailAttachment> attachments = List.of(attachment);
        Email emailToSend = buildTestEmail("smallAttachmentTest", List.of(A_USER_DESTINATION_EMAIL), attachments, A_KNOWN_IN_REPLY_TO);

        // act:
        instance.sendEmail(emailToSend);

        // assert:
        // Nothing to assert
    }

    /**
     * Test of sendMail method, authentication Client secret, with big
     * attachment, of class EmailSenderMSGraph.
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    public void testSendMail_BigAttachment() throws MessagingException {
        System.out.println("sendMail_ClientSecret_BigAttachment");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientSecret();
        EmailSenderMSGraph instance = buildEmailSenderMSGraph(emailAccount, new EmailMSGraphClient_ClientSecret(emailAccount));

        // Big attachment
        EmailAttachment attachment = EmailAttachmentImpl.copyOf(getResourceDataSource(A_KNOWN_BIG_ATTACHMENT)).build();
        List<EmailAttachment> attachments = List.of(attachment);
        Email emailToSend = buildTestEmail("bigAttachmentTest", List.of(A_USER_DESTINATION_EMAIL), attachments, A_KNOWN_IN_REPLY_TO);

        // act:
        instance.sendEmail(emailToSend);

        // assert:
        // Nothing to assert
    }

    private static EmailSenderMSGraph buildEmailSenderMSGraph(EmailAccount emailAccount, BaseEmailMSGraphClientProvider emailMSGraphClientProvider) {
        return new EmailSenderMSGraph(emailAccount, emailMSGraphClientProvider, new EmailTestHelper.MockInstanceInfoService(), mockEmailSignatureService());
    }

    private DataSource getResourceDataSource(String filenameWithJavaPath) {
        return newDataSource(EmailTestHelper.getResourceInputStream(filenameWithJavaPath));
    }

}
