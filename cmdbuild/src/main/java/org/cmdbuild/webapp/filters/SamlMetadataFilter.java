/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.filters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import org.cmdbuild.auth.login.saml.SamlAuthenticator;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.xml.CmXmlUtils.prettifyXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SamlMetadataFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

//    @Autowired TODO
//    private SamlAuthenticator samlAuthenticator;
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
//        try { TODO
////            checkArgument(config.isAuthenticatorEnabled(samlAuthenticator.getName()), "saml service is not active");
//            String xml = prettifyXml(samlAuthenticator.getSamlSettings(request).getSPMetadata());
//            response.setContentType("text/xml");
//            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//            response.getWriter().write(xml);
//        } catch (CertificateEncodingException ex) {
//            throw runtime(ex);
//        }
    }

}
