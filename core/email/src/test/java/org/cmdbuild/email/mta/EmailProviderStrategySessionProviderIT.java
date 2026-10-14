/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import static java.lang.String.format;
import jakarta.mail.MessagingException;
import org.cmdbuild.email.EmailAccount;
import static org.cmdbuild.email.EmailAccount.AUTHENTICATION_TYPE_MS_OAUTH2;
import org.cmdbuild.email.EmailException;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class EmailProviderStrategySessionProviderIT {

    private static final Integer SMTP_TIMEOUT_SECONDS = null; // Timeout disabled

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * Test of buildImapSessionProvider method, of class EmailProviderStrategy,
     * Microsoft authentication type.
     */
    @Test
    public void testBuildImapSessionProvider_Microsoft() {
        System.out.println("buildImapSessionProvider_Microsoft");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_Delegated();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act&assert: 
        exceptionRule.expect(EmailException.class);
        exceptionRule.expectMessage(format("imap session provider unsupported authentication type =< %s >", AUTHENTICATION_TYPE_MS_OAUTH2));
        EmailImapSessionProvider result = instance.buildImapSessionProvider(emailAccount, LoggerFactory.getLogger(getClass()));
    }

    /**
     * Test of buildImapSessionProvider method, of class EmailProviderStrategy,
     * Google authentication type.
     */
    @Test
    public void testBuildImapSessionProvider_Google() {
        System.out.println("buildImapSessionProvider_Google");

        // arrange:
        EmailAccount emailAccount = EmailAccountGoogleHelper.buildEmailAccount_Receiver();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act: 
        EmailImapSessionProvider result = instance.buildImapSessionProvider(
                emailAccount,
                LoggerFactory.getLogger(getClass()));

        // assert:
        assertTrue(result instanceof EmailImapSessionGoogleProvider);
    }

    /**
     * Test of buildImapSessionProvider method, of class EmailProviderStrategy,
     * default authentication type.
     */
    @Test
    public void testBuildImapSessionProvider_default() {
        System.out.println("buildImapSessionProvider_default");

        // arrange:
        EmailAccount emailAccount = EmailAccountJavaMailHelper.buildEmailAccount_Receiver();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act: 
        EmailImapSessionProvider result = instance.buildImapSessionProvider(
                emailAccount,
                LoggerFactory.getLogger(getClass()));

        // assert:
        assertTrue(result instanceof EmailImapSessionJavaMailProvider);
    }

    /**
     * Test of buildSmtpSessionProvider method, of class EmailProviderStrategy,
     * Microsoft authentication type.
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    public void testBuildSmtpSessionProvider_Microsoft() throws MessagingException {
        System.out.println("buildSmtpSessionProvider_Microsoft");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_Delegated();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act&assert: 
        exceptionRule.expect(EmailException.class);
        exceptionRule.expectMessage(format(
                "smtp session provider unsupported authentication type =< %s >",
                AUTHENTICATION_TYPE_MS_OAUTH2));
        // Raises MessageException
        EmailSmtpSessionProvider result = instance.buildSmtpSessionProvider(
                emailAccount,
                SMTP_TIMEOUT_SECONDS,
                LoggerFactory.getLogger(getClass()));
    }

    /**
     * Test of buildSmtpSessionProvider method, of class EmailProviderStrategy,
     * Google authentication type.
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    public void testBuildSmtpSessionProvider_Google() throws MessagingException {
        System.out.println("buildSmtpSessionProvider_Google");

        // arrange:
        EmailAccount emailAccount = EmailAccountGoogleHelper.buildEmailAccount_Sender();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act: 
        // Raises MessageException
        EmailSmtpSessionProvider result = instance.buildSmtpSessionProvider(
                emailAccount,
                SMTP_TIMEOUT_SECONDS,
                LoggerFactory.getLogger(getClass()));

        // assert:
        assertTrue(result instanceof EmailSmtpSessionGoogleProvider);
    }

    /**
     * Test of buildSmtpSessionProvider method, of class EmailProviderStrategy,
     * default authentication type.
     *
     * @throws jakarta.mail.MessagingException
     */
    @Test
    public void testBuildSmtpSessionProvider_default() throws MessagingException {
        System.out.println("buildSmtpSessionProvider_default");

        // arrange:
        EmailAccount emailAccount = EmailAccountJavaMailHelper.buildEmailAccount_Sender();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act: 
        // Raises MessageException
        EmailSmtpSessionProvider result = instance.buildSmtpSessionProvider(
                emailAccount,
                SMTP_TIMEOUT_SECONDS,
                LoggerFactory.getLogger(getClass()));

        // assert:
        assertTrue(result instanceof EmailSmtpSessionJavaMailProvider);
    }

}
