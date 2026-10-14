package org.cmdbuild.webapp.filters;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.cmdbuild.uicomponents.contextmenu.ContextMenuComponentService;
import static org.cmdbuild.utils.io.CmIoUtils.TIKA;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextMenuFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ContextMenuComponentService service;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing; this init method is not invoked by spring configured filters
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {//note: duplicate code from custom page filter
        logger.debug("context menu filter doFilter BEGIN");
        String requestUrl = "<undefined>";
        try {

            requestUrl = ((HttpServletRequest) request).getRequestURI()
                    .replaceFirst(Pattern.quote(((HttpServletRequest) request).getContextPath()) + "/ui/app/view/contextmenucomponents/?", "");

            logger.debug("context menu filter filter, processing request = {}", requestUrl);

            Matcher matcher = Pattern.compile("([^/]+)/(.+)").matcher(requestUrl);
            checkArgument(matcher.matches());

            String contextMenuName = checkNotBlank(matcher.group(1)),
                    filePath = checkNotBlank(matcher.group(2));

            byte[] responseData = service.getContextMenuFile(contextMenuName, filePath);

            ((HttpServletResponse) response).setHeader("CMDBuild-ProcessedByContextMenuFilter", Boolean.TRUE.toString());
            response.setContentType(TIKA.detect(responseData, FilenameUtils.getName(filePath)));
            response.setContentLength(responseData.length);
            response.getOutputStream().write(responseData);

            logger.debug("custom page filter doFilter END");
        } catch (Exception ex) {
            logger.error("error in context menu filter", ex);
            throw runtime(ex, "error processing context menu filter, request path = %s", requestUrl);
        }
    }

}
