/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import org.cmdbuild.email.EmailAccount;

/**
 *
 * @author afelice
 */
public class EmailMSGraphClient_ClientSecret extends BaseEmailMSGraphClientProvider {

    public final static String CLIENT_ID_KEY = "clientId";
    public final static String TENANT_ID_KEY = "tenantId";
    public final static String CLIENT_SECRET_KEY = "clientSecret";

    public EmailMSGraphClient_ClientSecret(EmailAccount emailAccount) {
        super(emailAccount);
    }

    @Override
    protected TokenCredential buildCredential() {
        return new ClientSecretCredentialBuilder()
                .clientId(msConfig.getString(CLIENT_ID_KEY))
                .tenantId(msConfig.getString(TENANT_ID_KEY))
                .clientSecret(msConfig.getString(CLIENT_SECRET_KEY))
                .build();
    }
}
