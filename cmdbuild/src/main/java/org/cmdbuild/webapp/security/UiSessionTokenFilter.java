/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.security;

import org.cmdbuild.auth.login.AuthenticationService;
import org.cmdbuild.auth.login.custom.CustomLoginService;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.config.RestConfiguration;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.webapp.services.FilterHelperService;
import org.springframework.stereotype.Component;

@Component
public class UiSessionTokenFilter extends SessionTokenFilter {

    public UiSessionTokenFilter(FilterHelperService helper, SessionService sessionService, AuthenticationService authenticationService, CustomLoginService customLoginService, FaultEventCollectorService faultEventCollectorService, RestConfiguration restConfiguration) {
        super(helper, sessionService, authenticationService, customLoginService, faultEventCollectorService, restConfiguration);
    }

    @Override
    protected boolean allowSessionsWithoutGroup() {
        return true;
    }

    @Override
    protected boolean enableRedirectToLoginForIncompleteSession() {
        return true;
    }

}
