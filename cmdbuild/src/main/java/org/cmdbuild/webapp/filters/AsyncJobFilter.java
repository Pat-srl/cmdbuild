/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.util.regex.Pattern;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.cmdbuild.asyncjob.AsyncRequestJobService;
import org.cmdbuild.asyncjob.AsyncRequestJob;
import static org.cmdbuild.asyncjob.AsyncRequestJobService.ASYNC_JOB_REQUEST_HEADER;
import static org.cmdbuild.asyncjob.AsyncRequestJobService.ASYNC_JOB_REQUEST_PARAM;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;

@Configuration
public class AsyncJobFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AsyncRequestJobService service;

    public AsyncJobFilter(AsyncRequestJobService service) {
        this.service = checkNotNull(service);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (toBooleanOrDefault(firstNotBlankOrNull(request.getParameter(ASYNC_JOB_REQUEST_PARAM), request.getHeader(ASYNC_JOB_REQUEST_HEADER)), false) == true) {
            String requestPath = request.getRequestURI().replaceFirst(Pattern.quote((request).getContextPath()), "");

            AsyncRequestJob job = service.createAsyncRequest(requestPath, request, response);

            response.setContentType("application/json");
            response.getWriter().write(toJson(map("success", true, "data", map("_id", job.getId()))));
        } else {
            filterChain.doFilter(request, response);
        }
    }

}
