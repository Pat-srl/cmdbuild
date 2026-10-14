package org.cmdbuild.webapp.filters;

import freemarker.template.Template;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.session.SessionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.stereotype.Component;
import static org.cmdbuild.easytemplate.FtlUtils.getDefaultConfiguration;
import static org.cmdbuild.easytemplate.FtlUtils.processToString;

@Component
public class MobileLoginFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SessionService sessionService;

    private final Template template;

    public MobileLoginFilter() throws IOException {
        template = new Template("mobile_login_template.html.ftl", new InputStreamReader(getClass().getResourceAsStream("/org/cmdbuild/webapp/filters/mobile_login_template.html.ftl")), getDefaultConfiguration());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing; this init method is not invoked by spring configured filters
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.debug("mobile login filter doFilter BEGIN");
        try {
            String sessionToken = sessionService.getCurrentSessionIdOrNull();

            byte[] responseData = processToString(template, map("sessionOk", isNotBlank(sessionToken), "sessionToken", sessionToken)).getBytes(StandardCharsets.UTF_8);

            response.setContentType("text/html");
            response.setContentLength(responseData.length);
            response.getOutputStream().write(responseData);

            logger.debug("mobile login filter doFilter END");
        } catch (Exception ex) {
            logger.error("error in mobile login filter", ex);
            throw ex;
        }
    }

}
