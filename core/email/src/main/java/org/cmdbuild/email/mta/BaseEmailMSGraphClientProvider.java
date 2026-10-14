/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.azure.core.credential.TokenCredential;
import com.microsoft.graph.core.requests.GraphClientFactory;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.authentication.AzureIdentityAuthenticationProvider;
import okhttp3.OkHttpClient;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailException;
import org.json.JSONObject;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Base class to create a session for a <code>GraphServiceClient</code>
 *
 * @author afelice
 */
abstract class BaseEmailMSGraphClientProvider implements EmailMSGraphClientProvider {

    protected final EmailAccount emailAccount;
    protected GraphServiceClient msGraphClient;
    protected static EmailMSGraphClientStrategy emailMSGraphClientStrategy = new EmailMSGraphClientStrategy();
    protected JSONObject msConfig;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected BaseEmailMSGraphClientProvider(EmailAccount account) {
        this.emailAccount = account;
    }

    /**
     * MS Graph has not an idempotent test for connection of sender. So only a
     * test on receiver is performed, reading the number of new email
     */
    @Override
    public void testConnection() {
        logger.info("test ms graph connection for account = {}", emailAccount);
        EmailMSGraphClientProvider msGraphClientProvider = emailMSGraphClientStrategy.buildMSGraphClientProvider(emailAccount);

        GraphServiceClient currentMsGraphClient = msGraphClientProvider.create();
        checkArgument(currentMsGraphClient.users().byUserId(emailAccount.getAddress()).messages().count().get() != null, "ms graph cannot connect");
        logger.info("ms graph connection ok");
    }

    @Override
    public void close() {
        if (msGraphClient != null) {
            logger.debug("destroying ms graph context");
            msGraphClient = null;
        }
    }

    @Override
    public GraphServiceClient create() {
        try {
            // used in buildCredential implementations
            msConfig = new JSONObject(emailAccount.getPassword());
            
            TokenCredential credential = buildCredential();
            String scope = getScope();
            
            AzureIdentityAuthenticationProvider authProvider = new AzureIdentityAuthenticationProvider(credential, null, scope);
            OkHttpClient httpClient = createHttpClient();
            
            return new GraphServiceClient(authProvider, httpClient);
        } catch (ApiException ex) {
            throw new EmailException(ex, "error creating imap session for account = %s", emailAccount);
        }
    }

    protected OkHttpClient createHttpClient() {
        return GraphClientFactory.create()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .build();
    }

    protected abstract TokenCredential buildCredential();

    protected String getScope() {
        return msConfig.optString("scope", "https://graph.microsoft.com/.default");
    }
}
