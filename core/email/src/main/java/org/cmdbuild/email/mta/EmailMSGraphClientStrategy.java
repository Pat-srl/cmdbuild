/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import java.util.Set;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailException;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import org.json.JSONException;
import org.json.JSONObject;
import static java.lang.String.format;

/**
 * Models different types of authentication for MSGraph client:
 * <ol>
 * <li>with application (delegated permissions), with username and password;
 * <li>with application client secret;
 * <li>with application client certificate.
 * </ol>
 *
 * @author afelice
 */
public class EmailMSGraphClientStrategy {
    public final static Set<String> RECOGNIZED_MS_AUTH_MODES = Set.of(EmailMSGraphClient_Password.PASSWORD_KEY, EmailMSGraphClient_ClientSecret.CLIENT_SECRET_KEY, EmailMSGraphClient_ClientCertificate.CLIENT_CERTIFICATE_KEY);

    public EmailMSGraphClientProvider buildMSGraphClientProvider(EmailAccount emailAccount) {
        JSONObject msConfig = fetchMSConf(emailAccount);

        String authMode = RECOGNIZED_MS_AUTH_MODES.stream()
                .filter(toFind -> isNotBlank(matchConfig(msConfig, toFind)))
                .findFirst()
                .get();

        return switch (authMode) {
            case EmailMSGraphClient_Password.PASSWORD_KEY -> {
                yield new EmailMSGraphClient_Password(emailAccount);
            }
            case EmailMSGraphClient_ClientSecret.CLIENT_SECRET_KEY -> {
                yield new EmailMSGraphClient_ClientSecret(emailAccount);
            }
            case EmailMSGraphClient_ClientCertificate.CLIENT_CERTIFICATE_KEY -> {
                yield new EmailMSGraphClient_ClientCertificate(emailAccount);
            }
            default ->
                throw new EmailException(format("unsupported MS authentication mode for =< %s >; handled only =< %s >", msConfig.toString(), RECOGNIZED_MS_AUTH_MODES));
        };
    }

    protected JSONObject fetchMSConf(EmailAccount emailAccount) throws JSONException {
        return new JSONObject(emailAccount.getPassword());
    }

    private String matchConfig(JSONObject msConfig, String propertyConf) {
        return msConfig.optString(propertyConf);
    }
}
