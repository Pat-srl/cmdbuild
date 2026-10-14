/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.mta;

import org.cmdbuild.email.EmailAccount;
import static org.cmdbuild.email.mta.EmailTestHelper.mockEmailAccountWithPassword;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class EmailMSGraphClientStrategyTest {

    public static final String PASSWORD_MS_DELEGATE = """
                                                        {
                                                            "clientId": "<aClientId>",
                                                            "password": "<aPwd>"
                                                        }
                                                      """;

    public static final String PASSWORD_MS_CLIENT_SECRET = """
                                                        {
                                                            "clientId": "<aClientId>",
                                                            "tenantId": "<aTenantId>",
                                                            "clientSecret": "<aSecret>"
                                                        }
                                                      """;

    public static final String PASSWORD_MS_CLIENT_CERTIFICATE = """
                                                        {
                                                            "clientId": "<aClientId>",
                                                            "tenantId": "<aTenantId>",
                                                            "clientCertificate": "<aCertificate>"
                                                        }
                                                      """;

    /**
     * Test of fetchMSConf method, of class EmailMSGraphClientStrategy.
     */
    @Test
    public void testFetchMSConf_Delegate() {
        System.out.println("fetchMSConf_Delegate");

        // arrange:
        String passwordJson = PASSWORD_MS_DELEGATE;
        EmailAccount emailAccount = mockEmailAccountWithPassword(passwordJson);
        EmailMSGraphClientStrategy instance = new EmailMSGraphClientStrategy();
        JSONObject expResult = new JSONObject(passwordJson);

        // act:
        JSONObject result = instance.fetchMSConf(emailAccount);

        // assert:
        assertEquals(expResult.toString(), result.toString());
    }

    /**
     * Test of buildMSGraphClientProvider method, of class EmailMSGraphClientStrategy.
     */
    @Test
    public void testBuildMSGraphClientProvider_Delegate() {
        System.out.println("buildMSGraphClientProvider_Delegate");

        // arrange:
        EmailAccount emailAccount = mockEmailAccountWithPassword(PASSWORD_MS_DELEGATE);
        EmailMSGraphClientStrategy instance = new EmailMSGraphClientStrategy();

        // act:
        EmailMSGraphClientProvider result = instance.buildMSGraphClientProvider(emailAccount);

        // assert:
        assertTrue(result instanceof EmailMSGraphClient_Password);
    }

    /**
     * Test of buildMSGraphClientProvider method, of class
     * EmailMSGraphClientStrategy.
     */
    @Test
    public void testBuildMSGraphClientProvider_ClientSecret() {
        System.out.println("buildMSGraphClientProvider_ClientSecret");

        // arrange:
        EmailAccount emailAccount = mockEmailAccountWithPassword(PASSWORD_MS_CLIENT_SECRET);
        EmailMSGraphClientStrategy instance = new EmailMSGraphClientStrategy();

        // act:
        EmailMSGraphClientProvider result = instance.buildMSGraphClientProvider(emailAccount);

        // assert:
        assertTrue(result instanceof EmailMSGraphClient_ClientSecret);
    }

    /**
     * Test of buildMSGraphClientProvider method, of class
     * EmailMSGraphClientStrategy.
     */
    @Test
    public void testBuildMSGraphClientProvider_ClientCertificate() {
        System.out.println("buildMSGraphClientProvider_ClientCertificate");

        // arrange:
        EmailAccount emailAccount = mockEmailAccountWithPassword(PASSWORD_MS_CLIENT_CERTIFICATE);
        EmailMSGraphClientStrategy instance = new EmailMSGraphClientStrategy();

        // act:
        EmailMSGraphClientProvider result = instance.buildMSGraphClientProvider(emailAccount);

        // assert:
        assertTrue(result instanceof EmailMSGraphClient_ClientCertificate);
    }

}
