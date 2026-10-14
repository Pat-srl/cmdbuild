/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.core;

import org.cmdbuild.utils.io.StreamProgressListener;

public interface InnerRestClient {

    void setSessionToken(String sessionToken);

    /**
     *
     * @return session token (never null)
     * @throws RuntimeException if session token is not available
     */
    String getSessionToken();

    void setActionId(String actionId);

    void setHeader(String key, String value);

    void allowInsecureSsl(boolean ignoreSslValidation);

    void addUploadProgressListener(StreamProgressListener listener);
}
