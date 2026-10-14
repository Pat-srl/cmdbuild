/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.client.rest.core.RestServiceClient;

public interface LoginApi extends RestServiceClient {

    static final String RSA_KEY_PASSWORD_PREFIX = "pk:";

    RestClient doLogin(String username, String password);

    RestClient doLoginWithAnyGroup(String username, String password);

    boolean isLoggedIn();

    default RestClient checkLogin(Runnable callbackIfNotLogged) {
        if (!isLoggedIn()) {
            callbackIfNotLogged.run();
        }
        return restClient();
    }
}
