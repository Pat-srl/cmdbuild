/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.filters;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cmdbuild.config.GisConfiguration;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.webapp.utils.ProxyUtils.proxyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoserverProxyFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GisConfiguration configuration;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing; this init method is not invoked by spring configured filters
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.debug("geoserver proxy filter doFilter BEGIN");
        try {
            checkArgument(configuration.isGeoServerEnabled(), "geoserver is not enabled");
            proxyRequest(checkNotBlank(configuration.getGeoServerUrl(), "geoserver url not configured"), "/services/geoserver/?", (HttpServletRequest) request, (HttpServletResponse) response);
            logger.debug("geoserver proxy filter doFilter END");
        } catch (Exception ex) {
            logger.error("error in geoserver proxy filter", ex);
            throw runtime(ex, "error processing geoserver proxy filter");//TODO return properly formatted json response
        }

    }

}
