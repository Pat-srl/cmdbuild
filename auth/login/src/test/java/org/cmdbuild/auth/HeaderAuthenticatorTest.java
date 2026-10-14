package org.cmdbuild.auth;

import org.cmdbuild.auth.login.AuthRequestInfo;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.RequestAuthenticatorResponse;
import static org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl.login;
import org.cmdbuild.auth.login.header.HeaderAuthenticator;
import org.cmdbuild.auth.login.header.HeaderAuthenticatorConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HeaderAuthenticatorTest {

    private static final String USER_HEADER_NAME = "X-Username";
    private static final String USER_HEADER_VALUE = "MyUser";

    private final HeaderAuthenticator headerAuthenticator = mock(HeaderAuthenticator.class);
    private final HeaderAuthenticatorConfiguration authenticatorConfiguration = mock(HeaderAuthenticatorConfiguration.class);

    @Test
    public void doesNotAuthenticateIfTheHeaderIsNotPresent() {
        AuthRequestInfo request = mock(AuthRequestInfo.class);

        when(request.getMethod()).thenReturn("GET");

        when(authenticatorConfiguration.isHeaderEnabled()).thenReturn(true);
        when(authenticatorConfiguration.getHeaderAttributeName()).thenReturn(USER_HEADER_NAME);

        when(request.getRequestPath()).thenReturn("/services/rest/v4/sessions/current");

        RequestAuthenticatorResponse<LoginUserIdentity> response = headerAuthenticator.authenticate(request);
        assertNull(response);
    }

    @Test
    public void testHeaderAuth1() {
        AuthRequestInfo request = mock(AuthRequestInfo.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestPath()).thenReturn("something");
        when(request.getHeader(USER_HEADER_NAME)).thenReturn(USER_HEADER_VALUE);

        RequestAuthenticatorResponse<LoginUserIdentity> response = headerAuthenticator.authenticate(request);

        assertNull(response);
    }

    @Test
    public void testHeaderAuth2() {
        AuthRequestInfo request = mock(AuthRequestInfo.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestPath()).thenReturn("/services/rest/v4/sessions/current");
        when(request.getHeader(USER_HEADER_NAME)).thenReturn(USER_HEADER_VALUE);
        
        RequestAuthenticatorResponse<LoginUserIdentity> login = login(LoginUserIdentity.build(USER_HEADER_VALUE));
        
        when(headerAuthenticator.authenticate(request)).thenReturn(login);

        RequestAuthenticatorResponse<LoginUserIdentity> response = headerAuthenticator.authenticate(request);

        assertTrue(response.hasLogin());
        assertFalse(response.hasRedirectUrl());
        assertEquals(response.getLogin().getValue(), USER_HEADER_VALUE);
    }
}
