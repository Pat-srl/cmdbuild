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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.UiFilterConfiguration;
import static org.cmdbuild.easytemplate.FtlUtils.getDefaultConfiguration;
import static org.cmdbuild.easytemplate.FtlUtils.processToString;
import org.cmdbuild.ui.UiBaseUrlService;
import static org.cmdbuild.utils.json.CmJsonUtils.toPrettyJson;

/**
 * this filters is used to embed global configuration inside main javascript
 * file; it is mostly used to set the 'baseUrl' param, that javascript code will
 * use to call rest ws.
 * <br><br>
 * TODO: cache response (or put this whole filter behind a caching filter)
 */
@Component
public class UiFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UiFilterConfiguration config;
    @Autowired
    private UiBaseUrlService baseUrlService;

    private final Template template;

    public UiFilter() throws IOException {
        template = new Template("config_template.js.ftl", new InputStreamReader(getClass().getResourceAsStream("/org/cmdbuild/webapp/filters/config_template.js.ftl")), getDefaultConfiguration());
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
        logger.debug("ui filter doFilter BEGIN");
        try {
            String uiManifest = checkNotBlank(config.getUiManifest(), "missing config ui manifest");
            String baseRequestUrl = baseUrlService.getBaseUrl(request);

            String configJsonStr = toPrettyJson(map(
                    "baseUrl", baseRequestUrl + "/services/rest/v4",
                    "geoserverBaseUrl", baseRequestUrl + "/services/geoserver",
                    "socketUrl", baseRequestUrl.replaceFirst("http", "ws") + "/services/websocket/v1/main",
                    "manifest", uiManifest
            ));
            logger.debug("return ui config = {}", configJsonStr);

            byte[] responseData = processToString(template, map("configJsonStr", configJsonStr)).getBytes(StandardCharsets.UTF_8);

            response.setContentType("application/javascript");
            response.setContentLength(responseData.length);
            response.getOutputStream().write(responseData);

            logger.debug("ui filter doFilter END");
        } catch (Exception ex) {
            logger.error("error in ui filter", ex);
            throw ex;
        }
    }

}
