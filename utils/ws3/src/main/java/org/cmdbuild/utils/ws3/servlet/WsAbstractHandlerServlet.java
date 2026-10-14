/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.servlet;

import static com.google.common.base.Preconditions.checkArgument;
import jakarta.activation.DataSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import java.io.IOException;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.function.Function.identity;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaFileCleaner;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletRequestContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmMultipartUtils.isPlaintext;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.cmdbuild.utils.ws3.api.WsResourceRepository;
import org.cmdbuild.utils.ws3.api.WsWarningSource;
import org.cmdbuild.utils.ws3.inner.*;
import org.cmdbuild.utils.ws3.inner.WsRestRequest;
import org.cmdbuild.utils.ws3.utils.Ws3Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class WsAbstractHandlerServlet extends HttpServlet {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private WsRequestHandler handlerV3;
    private WsRequestHandler handlerV4;
    private ExceptionMapper exceptionHandler;

    private DiskFileItemFactory diskFileItemFactory;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            WebApplicationContext applicationContext = checkNotNull(
                    WebApplicationContextUtils.getWebApplicationContext(getServletContext()),
                    "application context not available"
            );
            Map<String, WsResourceRepository> repositories = applicationContext.getBeansOfType(WsResourceRepository.class);

            WsResourceRepository repoV3 = checkNotNull(repositories.get("ws3Loader"),
                    "WsResourceRepository bean 'ws3Loader' not found (v3 endpoints)");
            WsResourceRepository repoV4 = checkNotNull(repositories.get("ws4Loader"),
                    "WsResourceRepository bean 'ws4Loader' not found (v4 endpoints)");

            exceptionHandler = applicationContext.getBean(ExceptionMapper.class);//TODO improve this
            WsWarningSource warningSource = applicationContext.getBean(WsWarningSource.class);//TODO improve this

            handlerV3 = new WsRequestHandlerImpl(repoV3, warningSource);
            handlerV4 = new WsRequestHandlerImpl(repoV4, warningSource);

            diskFileItemFactory = DiskFileItemFactory.builder()
                    .setFileCleaningTracker(JakartaFileCleaner.getFileCleaningTracker(getServletContext()))
                    .get();
        } catch (Exception ex) {
            logger.error("error starting ws rest servlet", ex);
            throw ex;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WsResponseHandler responseHelper;
        WsResponsePrinter responsePrinter;
        try {

            responseHelper = handleRequest(request, response);
            responsePrinter = responseHelper.prepareResponse();
        } catch (Exception ex) {
            logger.debug("error processing ws rest request", ex);
            handleError(request, response, ex);
            return;
        }
        try {
            responsePrinter.printResponse(response);
        } catch (IOException ex) {
            logger.warn("write error printing ws rest response: {}", ex.toString());
            logger.debug("write error printing ws rest response", ex);
        } catch (Exception ex) {
            logger.error("error printing ws rest response", ex);
        }
    }

    protected JakartaServletFileUpload getMultipartHelper() {
        return new JakartaServletFileUpload(checkNotNull(diskFileItemFactory, "file upload handler not ready"));
    }

    protected abstract WsResponseHandler handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

    protected WsRestRequest buildWsRestRequest(HttpServletRequest request, String resourceUri) throws IOException {
        try {
            Map<String, String> headers = list(request.getHeaderNames()).collect(toMap(identity(), request::getHeader));

            Map<String, List<String>> params = map(request.getParameterMap()).mapValues(v -> asList(v));

            String payload;
            Map<String, WsPart> parts = map();
            if (isPlaintext(request.getContentType())) {
                payload = IOUtils.toString(request.getReader());
                parts.put(WsPart.DEFAULT_PART, new WsPart(newDataSource(payload, request.getContentType()), WsPart.DEFAULT_PART, emptyMap()));
            } else {
                if (JakartaServletFileUpload.isMultipartContent(new JakartaServletRequestContext(request))) {
                    logger.trace("processing multipart request");
                    AtomicInteger i = new AtomicInteger(0);
                    List<FileItem> items = getMultipartHelper().parseRequest(request);
                    items.forEach(part -> {
                        DataSource data = newDataSource(part::getInputStream, part.getContentType(), part.getName());
                        String partName = part.getFieldName();
                        if (isBlank(partName)) {
                            partName = format("part_%s", i.getAndIncrement());
                        }
                        logger.trace("processing multipart element =< {} > ( {} {} )", partName, FileUtils.byteCountToDisplaySize(part.getSize()), part.getContentType());
                        Map<String, String> partHeaders = list(part.getHeaders().getHeaderNames()).collect(toMap(identity(), h -> part.getHeaders().getHeader(h)));
                        parts.put(partName, new WsPart(data, partName, partHeaders));
                    });
                    payload = parts.values().stream().filter(p -> isPlaintext(p.getDataSource().getContentType())).findFirst().map(p -> readToString(p.getDataSource())).orElse(null);
                } else {
                    ServletInputStream in = request.getInputStream();
                    DataSource dataSource;
                    if (in == null) {
                        dataSource = newDataSource(in, request.getContentType());
                    } else {
                        dataSource = newDataSource(new byte[]{}, request.getContentType());
                    }
                    parts.put(WsPart.DEFAULT_PART, new WsPart(dataSource, WsPart.DEFAULT_PART, headers));
                    payload = null;
                }
            }

            return new WsRestRequestImpl(request, resourceUri, params, parts, headers, payload);

        } catch (FileUploadException ex) {
            throw new Ws3Exception(ex);
        }
    }

    protected WsRequestHandler getHandler(HttpServletRequest request) {
        String servletPath = request == null ? null : request.getServletPath();
        boolean isV3 = servletPath != null && servletPath.matches(".*/v3/?$");
        return checkNotNull(isV3 ? handlerV3 : handlerV4, "handler not ready");
    }

    protected WsRequestHandler getHandler() {
        return checkNotNull(handlerV4, "handler not ready");
    }

    protected void handleError(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {
        try {
            if (exceptionHandler != null) {
                Response resp = exceptionHandler.toResponse(ex);
                checkArgument(isContentType(resp.getMediaType().toString(), "application/json"));
                response.setContentType("application/json");
                response.setCharacterEncoding(UTF_8.name());
                response.setStatus(resp.getStatus());
                String payload = toJson(resp.getEntity());
                response.getWriter().write(payload);
            } else {
                response.setStatus(500);
                //TODO
            }
        } catch (Exception exx) {
            logger.error("error handling ws error", exx);
        }
    }
}
