/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.filters;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SamlLogoutFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

//    @Autowired
//    private SamlAuthenticator samlAuthenticator; TODO
    @Autowired
    private AuthenticationConfiguration config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing; this init method is not invoked by spring configured filters
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//        checkArgument(config.isAuthenticatorEnabled(samlAuthenticator.getName()), "saml service is not active");  TODO
//        if (         
//                nullToEmpty(((HttpServletRequest) request).getPathInfo()).matches("(?i)/services/saml/(SLO|SingleLogout|logout)/?")) {
//            handleLogoutRequest((HttpServletRequest) request, (HttpServletResponse) response);
//        } else {
//            filterChain.doFilter(request, response);
//        }
    }

    private void handleLogoutRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        //TODO
        throw new UnsupportedOperationException("not supported yet");
    }

}
