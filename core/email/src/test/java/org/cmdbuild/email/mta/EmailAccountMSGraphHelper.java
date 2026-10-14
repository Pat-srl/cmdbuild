/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.beans.EmailAccountImpl;

import java.lang.invoke.MethodHandles;

import static org.cmdbuild.email.EmailAccount.AUTHENTICATION_TYPE_MS_OAUTH2;
import static org.cmdbuild.email.mta.TokenEncrypter.getCustomCm3EasyCryptoUtils;
import static org.cmdbuild.email.mta.TokenLoaderHelper.loadCredentialsStr;
import static org.cmdbuild.utils.encode.CmPackUtils.pack;

/**
 *
 * @author afelice
 */
public class EmailAccountMSGraphHelper {

    protected static final int SMALL_ATTACHMENT_MAX_SIZE_BYTES = 2 * 1024 * 1024;
    protected static final int SEND_MESSAGE_TIMEOUT_SECONDS = 30;
    //    protected static final HeaderOption HEADER_OPTION_IMMUTABLE = new HeaderOption("Prefer", "IdType=\"ImmutableId\"");
    protected static final String SENT_MESSAGE_INFOS = "id,parentFolderId,internetMessageId,sentDateTime,internetMessageHeaders";

    protected static final String CM_INSTANCE_VERSION = "3.4.1";
    protected static final String CM_INSTANCE_REVISION = "rev. 3.4.1";

    protected static final String A_KNOWN_SMALL_ATTACHMENT = "/org/cmdbuild/modernauth/service/test/attachments/small.txt";
    protected static final String A_KNOWN_BIG_ATTACHMENT = "/org/cmdbuild/modernauth/service/test/attachments/4MBFile.zip";
    protected static final String ANOTHER_KNOWN_BIG_ATTACHMENT = "/org/cmdbuild/modernauth/service/test/attachments/4MBFile_1.zip";

    private static final String A_KNOWN_MICROSOFT365_USER_EMAIL = "be60162de3988d23c93147805673bab7a8c4320075a95f6a50c058d20b4d0b7ef86a6a";
    private static final String A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_DELEGATED_CONFIG_FILE_PATH = "/org/cmdbuild/modernauth/service/test/tokens/cmdbuild.onmicrosoft_Delegated_MS_Pwd.encr";
    private static final String A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_DELEGATED_CONFIG = loadCredentialsStr(A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_DELEGATED_CONFIG_FILE_PATH, MethodHandles.lookup().lookupClass().getName());
    private static final String A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_SECRET_CONFIG_FILE_PATH = "/org/cmdbuild/modernauth/service/test/tokens/cmdbuild.onmicrosoft_MS_ClientSecret.encr";
    private static final String A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_SECRET_CONFIG = loadCredentialsStr(A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_SECRET_CONFIG_FILE_PATH, MethodHandles.lookup().lookupClass().getName());
    private static final String A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_CERTIFICATE_PRIVATE_KEY_FILE_PATH = "/org/cmdbuild/modernauth/service/test/tokens/cmdbuild.onmicrosoft_MS_ClientCertificate_PrivateKey.encr";
    private static final String A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_CERTIFICATE_PRIVATE_KEY = loadCredentialsStr(A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_CERTIFICATE_PRIVATE_KEY_FILE_PATH, MethodHandles.lookup().lookupClass().getName());
    private static final String A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_CERTIFICATE_CERTIFICATE_FILE_PATH = "/org/cmdbuild/modernauth/service/test/tokens/cmdbuild.onmicrosoft_MS_ClientCertificate_Certificate.encr";
    private static final String A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_CERTIFICATE_CERTIFICATE = loadCredentialsStr(A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_CERTIFICATE_CERTIFICATE_FILE_PATH, MethodHandles.lookup().lookupClass().getName());

    // TODO collegare normale account aziendale #7167 con crypt CM3EASY
    // See https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/identity/azure-identity/src/main/java/com/azure/identity/implementation/IdentityClient.java#L214
    // Requires both application Private Key PEM and application Certificate PEM in the same PEM field
    private static final String A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_CERTIFICATE_CONFIG = String.format("""
            {
                "clientId": "222d69bf-a63b-4715-af86-b38ce27f297d",
                "tenantId": "f7978900-f968-4f8e-b527-ce1688e8c254",
                "clientPrivateKey": "%s",
                "clientCertificate": "%s"
            }""",
            pack(A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_CERTIFICATE_PRIVATE_KEY),
            pack(A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_CERTIFICATE_CERTIFICATE));

    static EmailAccount buildEmailAccount_ClientSecret() {
        String msConfigs = A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_SECRET_CONFIG;

        return buildEmailAccount(msConfigs, A_KNOWN_MICROSOFT365_USER_EMAIL);
    }

    static EmailAccount buildEmailAccount_ClientCertificate() {
        String msConfigs = A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_CLIENT_CERTIFICATE_CONFIG;

        return buildEmailAccount(msConfigs, A_KNOWN_MICROSOFT365_USER_EMAIL);
    }

    static EmailAccount buildEmailAccount_Delegated() {
        String msConfigs = A_KNOWN_MICROSOFT365_APPLICATION_OAUTH2_DELEGATED_CONFIG;

        return buildEmailAccount(msConfigs, A_KNOWN_MICROSOFT365_USER_EMAIL);
    }

    private static EmailAccount buildEmailAccount(String msConfigs, String username) {
        EmailAccount emailAccount = EmailAccountImpl.builder()
                .withName("test")
                .withUsername(getCustomCm3EasyCryptoUtils().decryptValue(username))
                .withAddress(getCustomCm3EasyCryptoUtils().decryptValue(username))
                .withPassword(msConfigs)
                .withAuthenticationType(AUTHENTICATION_TYPE_MS_OAUTH2)
                .build();
        return emailAccount;
    }
}
