/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import static java.lang.String.format;

public interface JobSessionService {

    JobSessionContext createJobSessionContextWithUser(String user, String reqCtxId);

    JobSessionContext createJobSessionContextWithExistingSession(String sessionId, String reqCtxId);

    void destroyJobSessionContext();

    default JobSessionContext createJobSessionContextWithUser(String user, String reqCtxId, Object... params) {
        return createJobSessionContextWithUser(user, format(reqCtxId, params));
    }

    default JobSessionContext createJobSessionContextWithExistingSession(String sessionId, String reqCtxId, Object... params) {
        return createJobSessionContextWithExistingSession(sessionId, format(reqCtxId, params));
    }

    interface JobSessionContext {

        void destroyJobSessionContext();
    }
}
