/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import jakarta.mail.Authenticator;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.utils.MyAuthenticator;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 *
 * @author afelice
 */
public abstract class EmailAuthenticatorGoogle {

    public Authenticator buildAuthenticator(EmailAccount account, CmMapUtils.FluentMap<String, String> properties) {
        try {
            final ByteArrayInputStream tokenInputStream = new ByteArrayInputStream(
                    checkNotBlank(account.getPassword()).getBytes(
                            StandardCharsets.UTF_8));
            ServiceAccountCredentials credentials = (ServiceAccountCredentials) ServiceAccountCredentials.fromStream(
                    tokenInputStream).createDelegated(account.getUsername()).createScoped(
                    "https://mail.google.com/");
            credentials.refreshIfExpired();
            AccessToken accessToken = credentials.getAccessToken();
            enableXOAUTH2(properties);
            return new MyAuthenticator(account.getUsername(), accessToken.getTokenValue());
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    abstract protected void enableXOAUTH2(CmMapUtils.FluentMap<String, String> properties);

}
