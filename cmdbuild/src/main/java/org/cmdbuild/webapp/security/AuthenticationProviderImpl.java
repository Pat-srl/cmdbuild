/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.security;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.dao.beans.AuthenticationToken;
import org.cmdbuild.auth.user.OperationUserStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SessionService sessionService;

    public AuthenticationProviderImpl(SessionService sessionService) {
        this.sessionService = checkNotNull(sessionService);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OperationUserStack operationUser = sessionService.getCurrentSession().getOperationUser();
        logger.trace("operationUser from session = {}", operationUser);
        return new AuthenticatedUser(operationUser); // this will be stored in session
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AuthenticationToken.class.isAssignableFrom(authentication);
    }
}
