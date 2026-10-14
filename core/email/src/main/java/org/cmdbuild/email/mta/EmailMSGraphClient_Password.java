/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import org.cmdbuild.email.EmailAccount;

/**
 *
 * @author afelice
 */
public class EmailMSGraphClient_Password extends BaseEmailMSGraphClientProvider {

    public final static String CLIENT_ID_KEY = "clientId";
    public final static String PASSWORD_KEY = "password";

    public EmailMSGraphClient_Password(EmailAccount emailAccount) {
        super(emailAccount);
    }

    @Override
    protected TokenCredential buildCredential() {
        return new UsernamePasswordCredentialBuilder()
                .clientId(msConfig.getString(CLIENT_ID_KEY))
                .username(emailAccount.getUsername())
                .password(msConfig.getString(PASSWORD_KEY))
                .build();
    }
}
