package org.cmdbuild.webapp.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.cmdbuild.config.UiFilterConfiguration;
import static org.cmdbuild.webapp.utils.ProxyUtils.setNoCacheHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UiCacheControlFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UiFilterConfiguration config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing; this init method is not invoked by spring configured filters
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.debug("ui cache control filter doFilter BEGIN");
        try {
            if (!config.isResourceCachingEnabled()) {
                setNoCacheHeaders((HttpServletResponse) response);
            }
            filterChain.doFilter(request, response);
            logger.debug("ui cache control filter doFilter END");
        } catch (Exception ex) {
            logger.error("error in ui cache control filter", ex);
            throw ex;
        }
    }

}
