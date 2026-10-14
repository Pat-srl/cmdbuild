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
import org.cmdbuild.uicomponents.widget.WidgetComponentService;
import static org.cmdbuild.utils.io.CmIoUtils.TIKA;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WidgetFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WidgetComponentService service;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing; this init method is not invoked by spring configured filters
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {//note: duplicate code from custom page filter
        logger.debug("widget filter doFilter BEGIN");
        String requestUrl = "<undefined>";
        try {

            requestUrl = ((HttpServletRequest) request).getRequestURI()
                    .replaceFirst(Pattern.quote(((HttpServletRequest) request).getContextPath()) + "/ui/app/view/widgets/custom/?", "");

            logger.debug("widget filter filter, processing request = {}", requestUrl);

            Matcher matcher = Pattern.compile("([^/]+)/(.+)").matcher(requestUrl);
            checkArgument(matcher.matches());

            String widgetName = checkNotBlank(matcher.group(1)),
                    filePath = checkNotBlank(matcher.group(2));

            byte[] responseData = service.getWidgetFile(widgetName, filePath); 
            
            ((HttpServletResponse) response).setHeader("CMDBuild-ProcessedByWidgetFilter", Boolean.TRUE.toString());
            response.setContentType(TIKA.detect(responseData, FilenameUtils.getName(filePath)));
            response.setContentLength(responseData.length);
            response.getOutputStream().write(responseData);

            logger.debug("widget filter doFilter END");
        } catch (Exception ex) {
            logger.error("error in widget filter", ex);
            throw runtime(ex, "error widget menu filter, request path = %s", requestUrl);
        }
    }

}
