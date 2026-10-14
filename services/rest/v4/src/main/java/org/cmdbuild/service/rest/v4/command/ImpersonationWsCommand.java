/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.auth.session.SessionService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class ImpersonationWsCommand {

    private final SessionService sessionService;

    public ImpersonationWsCommand(SessionService sessionLogic) {
        this.sessionService = checkNotNull(sessionLogic);
    }

    public void doImpersonate(String username) {
        sessionService.impersonate(username);
    }

    public void doDeimpersonate() {
        sessionService.deimpersonate();
    }
}
