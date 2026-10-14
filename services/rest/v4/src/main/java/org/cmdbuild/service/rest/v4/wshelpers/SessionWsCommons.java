package org.cmdbuild.service.rest.v4.wshelpers;

import jakarta.annotation.Nullable;
import org.cmdbuild.auth.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class SessionWsCommons {

    public static final String CURRENT = "current";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final SessionService sessionService;

    protected SessionWsCommons(SessionService sessionService) {
        this.sessionService = checkNotNull(sessionService);
    }

    @Nullable
    protected String sessionIdOrCurrent(String sessionId) {
        if (CURRENT.equalsIgnoreCase(sessionId)) {
            return sessionService.getCurrentSessionIdOrNull();
        } else {
            return sessionId;
        }
    }

    protected void checkIsCurrent(String sessionId) {
        checkArgument(equal(sessionId, CURRENT) || equal(sessionId, sessionService.getCurrentSessionIdOrNull()), "session id param must be equal to current session id");
    }
}
