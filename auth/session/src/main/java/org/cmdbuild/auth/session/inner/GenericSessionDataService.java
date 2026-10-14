/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.function.Function;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.session.model.SessionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class GenericSessionDataService implements SessionDataService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Function<String,Session> sessionLoader;
    private final CurrentSessionHolder currentSessionIdHolder;

    public GenericSessionDataService(Function<String,Session> sessionLoader, CurrentSessionHolder currentSessionIdHolder) {
        this.sessionLoader = checkNotNull(sessionLoader);
        this.currentSessionIdHolder = checkNotNull(currentSessionIdHolder);
    }

    @Override
    public SessionData getCurrentSessionDataSafe() {
        return new SessionData() {

            @Override
            public Map<String, Object> getSessionData() {
                Session session = getSession();
                if (session == null) {
                    logger.warn("no session available, using dummy session data");
                    return emptyMap();
                } else {
                    return session.getSessionData();
                }
            }

            @Nullable
            private Session getSession() {
                String sessionId = currentSessionIdHolder.getOrNull();
                if (isBlank(sessionId)) {
                    return null;
                } else {
                    return sessionLoader.apply(sessionId);
                }
            }
        };
    }
}
