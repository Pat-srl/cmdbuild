/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.security.PrivateKey;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.client.rest.api.LoginApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.model.Session;
import static org.cmdbuild.utils.crypto.CmRsaUtils.createToken;
import static org.cmdbuild.utils.crypto.CmRsaUtils.parsePrivateKey;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class LoginApiImpl extends AbstractServiceClientImpl implements LoginApi {

    public LoginApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public RestClient doLogin(String username, String password) {
        return doLogin(username, password, false);
    }

    @Override
    public RestClient doLoginWithAnyGroup(String username, String password) {
        return doLogin(username, password, true);
    }

    private RestClient doLogin(String username, String password, boolean ensureGroup) {
        logger.debug("doLogin");

        if (password.startsWith(RSA_KEY_PASSWORD_PREFIX)) {
            PrivateKey privateKey = parsePrivateKey(password.substring(RSA_KEY_PASSWORD_PREFIX.length()));
            password = format("rsa:%s", createToken(privateKey));
        }

        JsonNode response = post("sessions?scope=service&returnId=true", map()
                .with("username", checkNotNull(username, "username cannot be null"))
                .with("password", checkNotNull(password, "password cannot be null"))).asJackson().get("data");
        Session session = fromJson(response, Session.class);
        String sessionToken = checkNotNull(session.getSessionId(), "cannot find session token in response");
        RestClient restClient = restClient().withSessionToken(sessionToken);
        if (ensureGroup && isBlank(session.getRole())) {
            checkArgument(!isNullOrEmpty(session.getAvailableRoles()), "no roles available for user %s", session.getUsername());
            String candidateRole;
            if (session.getAvailableRoles().contains("SuperUser")) {
                candidateRole = "SuperUser";
            } else {
                candidateRole = session.getAvailableRoles().iterator().next();
            }
            session = Session.copyOf(session).withRole(candidateRole).build();
            session = restClient.session().updateSession(session).getSession();
            checkNotBlank(session.getRole());
        }
        return restClient;
    }

    @Override
    protected boolean isSessionTokenRequired() {
        return false;
    }

    @Override
    public boolean isLoggedIn() {
        if (isBlank(restClient().getSessionToken())) {
            return false;
        } else {
            return get("sessions/current?if_exists=true").asJackson().get("data").get("exists").asBoolean();
        }
    }

}
