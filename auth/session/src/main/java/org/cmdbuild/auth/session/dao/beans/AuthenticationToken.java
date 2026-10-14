/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.dao.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class AuthenticationToken extends AbstractAuthenticationToken {

    private final String token;

    public AuthenticationToken(String token) {
        this(token, null);
    }

    public AuthenticationToken(String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = checkNotNull(trimToNull(token));
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }
}
