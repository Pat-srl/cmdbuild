/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import static java.lang.String.format;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.beans.EmailAccountImpl;
import org.cmdbuild.email.mta.EmailTestHelper.MockInstanceInfoService;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailQueueConfiguration;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailRepository;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailSignatureService;
import static org.cmdbuild.email.mta.EmailTestHelper.mockLockService;
import static org.cmdbuild.utils.encode.CmPackUtils.pack;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author afelice
 */
public class EmailProviderStrategyTest {

    public static final String PASSWORD_MS_DELEGATE = """
                                                        {
                                                            "clientId": "<aClientId>",
                                                            "password": "<aPwd>"
                                                        }
                                                      """;

    public static final String PASSWORD_MS_CLIENT_SECRET = """
                                                        {
                                                            "clientId": "<aClientId>",
                                                            "tenantId": "-aTenantId-",
                                                            "clientSecret": "<aSecret>"
                                                        }
                                                      """; // tenantId must use only letters, numbers or minus

    public static final String PASSWORD_MS_CLIENT_CERTIFICATE = format(
            """
                                                        {
                                                            "clientId": "<aClientId>",
                                                            "tenantId": "-aTenantId-",
                                                            "clientPrivateKey": "%s",
                                                            "clientCertificate": "%s"
                                                        }
                                                      """, // tenantId must use only letters, numbers or minus
            pack("aClientPrivateKey"),
            pack("aClientCertificate"));

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    /**
     * Test of buildSender method, of class EmailProviderStrategy, default
     * authentication type.
     */
    @Test
    public void testBuildSender_Default() {
        System.out.println("buildSender_Default");

        // arrange:
        EmailAccount emailAccount = EmailAccountJavaMailHelper.buildEmailAccount_Sender();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act:
        EmailSenderProvider result = instance.buildSender(emailAccount, mockEmailQueueConfiguration(), new MockInstanceInfoService(), mockEmailSignatureService());

        // assert:
        assertTrue(result instanceof EmailSenderJavaMail);
    }

    /**
     * Test of buildSender method, of class EmailProviderStrategy, Google
     * authentication type.
     */
    @Test
    public void testBuildSender_Google() {
        System.out.println("buildSender_Google");

        // arrange:
        EmailAccount emailAccount = EmailAccountGoogleHelper.buildEmailAccount_Sender();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act:
        EmailSenderProvider result = instance.buildSender(emailAccount, mockEmailQueueConfiguration(), new MockInstanceInfoService(), mockEmailSignatureService());

        // assert:
        assertTrue(result instanceof EmailSenderJavaMail);
    }

    /**
     * Test of buildSender method, of class EmailProviderStrategy, Microsoft
     * authentication type, delegate password.
     */
    @Test
    public void testBuildSender_MicrosoftDelegate() {
        System.out.println("buildSender_MicrosoftDelegate");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_Delegated();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act:
        EmailSenderProvider result = instance.buildSender(emailAccount, mockEmailQueueConfiguration(), new MockInstanceInfoService(), mockEmailSignatureService());

        // assert:
        assertTrue(result instanceof EmailSenderMSGraph);
        assertTrue(((EmailSenderMSGraph) result).emailMsGraphClientProvider instanceof EmailMSGraphClient_Password);
    }

    /**
     * Test of buildSender method, of class EmailProviderStrategy, Microsoft
     * authentication type, client secret.
     */
    @Test
    public void testBuildSender_MicrosoftClientSecret() {
        System.out.println("buildSender_MicrosoftClientSecret");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientSecret();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act:
        EmailSenderProvider result = instance.buildSender(emailAccount, mockEmailQueueConfiguration(), new MockInstanceInfoService(), mockEmailSignatureService());

        // assert:
        assertTrue(result instanceof EmailSenderMSGraph);
        assertTrue(((EmailSenderMSGraph) result).emailMsGraphClientProvider instanceof EmailMSGraphClient_ClientSecret);
    }

    /**
     * Test of buildSender method, of class EmailProviderStrategy, Microsoft
     * authentication type, client certificate.
     */
    @Test
    public void testBuildSender_MicrosoftClientCertificate() {
        System.out.println("buildSender_MicrosoftClientSecret");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientCertificate();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act:
        EmailSenderProvider result = instance.buildSender(emailAccount, mockEmailQueueConfiguration(), new MockInstanceInfoService(), mockEmailSignatureService());

        // assert:
        assertTrue(result instanceof EmailSenderMSGraph);
        assertTrue(((EmailSenderMSGraph) result).emailMsGraphClientProvider instanceof EmailMSGraphClient_ClientCertificate);
    }

    /**
     * Test of buildSender method, of class EmailProviderStrategy, unhandled
     * authentication type.
     */
    @Test
    public void testBuildSender_Unhandled() {
        System.out.println("buildSender_Unhandled");

        // arrange:
        String aUnknownAuth = "UNKNOWN";
        EmailAccount emailAccount = EmailAccountImpl.builder().withAuthenticationType(aUnknownAuth).withPassword(PASSWORD_MS_DELEGATE).build();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act&assert:
        exceptionRule.expect(EmailException.class);
        exceptionRule.expectMessage(format("sender unsupported authentication type =< %s >", aUnknownAuth));
        instance.buildSender(emailAccount, mockEmailQueueConfiguration(), new MockInstanceInfoService(), mockEmailSignatureService());
    }

    /**
     * Test of buildReceiver method, of class EmailProviderStrategy, default
     * authentication type.
     */
    @Test
    public void testBuildReceiver_Default() {
        System.out.println("buildReceiver_Default");

        // arrange:
        EmailAccount emailAccount = EmailAccountJavaMailHelper.buildEmailAccount_Receiver();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act:
        EmailReceiverProvider result = instance.buildReceiver(emailAccount, mockLockService(), mockEmailRepository());

        // assert:
        assertTrue(result instanceof EmailReceiverJavaMail);
    }

    /**
     * Test of buildReceiver method, of class EmailProviderStrategy, Google
     * authentication type.
     */
    @Test
    public void testBuildReceiver_Google() {
        System.out.println("buildReceiver_Google");

        // arrange:
        EmailAccount emailAccount = EmailAccountGoogleHelper.buildEmailAccount_Receiver();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act:
        EmailReceiverProvider result = instance.buildReceiver(emailAccount, mockLockService(), mockEmailRepository());

        // assert:
        assertTrue(result instanceof EmailReceiverGoogle);
    }

    /**
     * Test of buildReceiver method, of class EmailProviderStrategy, Microsoft
     * authentication type, delegate password.
     */
    @Test
    public void testBuildReceiver_MicrosoftDelegate() {
        System.out.println("buildReceiver_MicrosoftDelegate");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_Delegated();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act:
        EmailReceiverProvider result = instance.buildReceiver(emailAccount, mockLockService(), mockEmailRepository());

        // assert:
        assertTrue(result instanceof EmailReceiverMSGraph);
        assertTrue(((EmailReceiverMSGraph) result).emailMsGraphClientProvider instanceof EmailMSGraphClient_Password);
    }

    /**
     * Test of buildReceiver method, of class EmailProviderStrategy, unhandled
     * authentication type.
     */
    @Test
    public void testBuildReceiver_Unhandled() {
        System.out.println("buildReceiver_Unhandled");
        final String aUnknownAuth = "UNKNOWN";

        // arrange:
        EmailAccount emailAccount = EmailAccountImpl.builder().withAuthenticationType(aUnknownAuth).withPassword(PASSWORD_MS_DELEGATE).build();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act&assert:
        exceptionRule.expect(EmailException.class);
        exceptionRule.expectMessage(format("receiver unsupported authentication type =< %s >", aUnknownAuth));
        instance.buildReceiver(emailAccount, mockLockService(), mockEmailRepository());
    }

    /**
     * Test of buildReceiver method, of class EmailProviderStrategy, Microsoft
     * authentication type, client secret.
     */
    @Test
    public void testBuildReceiver_MicrosoftClientSecret() {
        System.out.println("buildReceiver_MicrosoftClientSecret");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientSecret();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act:
        EmailReceiverProvider result = instance.buildReceiver(emailAccount, mockLockService(), mockEmailRepository());

        // assert:
        assertTrue(result instanceof EmailReceiverMSGraph);
        assertTrue(((EmailReceiverMSGraph) result).emailMsGraphClientProvider instanceof EmailMSGraphClient_ClientSecret);
    }

    /**
     * Test of buildReceiver method, of class EmailProviderStrategy, Microsoft
     * authentication type, client certificate.
     */
    @Test
    public void testBuildReceiver_MicrosoftClientCertificate() {
        System.out.println("buildReceiver_MicrosoftClientSecret");

        // arrange:
        EmailAccount emailAccount = EmailAccountMSGraphHelper.buildEmailAccount_ClientCertificate();
        EmailProviderStrategy instance = new EmailProviderStrategy();

        // act&assert:
        EmailReceiverProvider result = instance.buildReceiver(emailAccount, mockLockService(), mockEmailRepository());

        // assert:
        assertTrue(result instanceof EmailReceiverMSGraph);
        assertTrue(((EmailReceiverMSGraph) result).emailMsGraphClientProvider instanceof EmailMSGraphClient_ClientCertificate);
    }

}
