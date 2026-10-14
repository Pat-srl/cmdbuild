/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.webapp.filters;

import com.google.common.base.Joiner;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.base.Supplier;
import static com.google.common.base.Suppliers.memoize;
import com.google.common.eventbus.EventBus;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import static java.util.function.Function.identity;
import java.util.regex.Pattern;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.apache.tika.Tika;
import org.cmdbuild.audit.RequestBeginEventImpl;
import org.cmdbuild.audit.RequestCompleteEventImpl;
import org.cmdbuild.audit.RequestData;
import org.cmdbuild.audit.RequestDataImpl;
import static org.cmdbuild.audit.RequestInfo.NO_ACTION_ID;
import static org.cmdbuild.audit.RequestInfo.NO_SESSION_ID;
import static org.cmdbuild.audit.RequestInfo.NO_USER_AGENT;
import static org.cmdbuild.audit.RequestInfo.PAYLOAD_TRACKING_DISABLED;
import static org.cmdbuild.audit.RequestInfo.RESPONSE_TRACKING_DISABLED;
import org.cmdbuild.audit.RequestTrackingService;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_ACTION_ID_HEADER;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_REQUEST_ID_HEADER;
import org.cmdbuild.config.RequestTrackingConfiguration;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.fault.FaultEventCollector;
import org.cmdbuild.fault.FaultEventCollectorService;
import static org.cmdbuild.requestcontext.RequestContext.REQUEST_ID;
import org.cmdbuild.requestcontext.RequestContextService;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.encode.CmEncodeUtils.sanitizeStringForId;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmMultipartUtils.isPlaintext;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.truncate;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.webapp.beans.MyContentCachingRequestWrapper;
import org.cmdbuild.webapp.beans.MyContentCachingResponseWrapper;
import static org.cmdbuild.webapp.security.SessionTokenFilter.getSessionTokenFromRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@Configuration("RequestTrackingFilter")
public class RequestTrackingFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RequestContextService requestContextService;
    private final RequestTrackingConfiguration config;
    private final RequestTrackingService service;
    private final EventBus eventBus;
    private final FaultEventCollectorService errorAndWarningCollectorService;
    private final Tika tika = new Tika();

    public RequestTrackingFilter(RequestContextService requestContextService, EventBusService eventService, RequestTrackingConfiguration config, RequestTrackingService service, FaultEventCollectorService errorAndWarningCollectorService) {
        this.config = checkNotNull(config);
        this.service = checkNotNull(service);
        this.errorAndWarningCollectorService = checkNotNull(errorAndWarningCollectorService);
        this.eventBus = eventService.getRequestEventBus();
        this.requestContextService = checkNotNull(requestContextService);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestIdFromHeader = sanitizeStringForId(request.getHeader(CMDBUILD_REQUEST_ID_HEADER)),
                requestId;

        if (isBlank(requestIdFromHeader)) {
            requestId = randomId();
            logger.trace("processing request with null requestId (using generated requestId = {})", requestId);
        } else {
            requestId = truncate(requestIdFromHeader, 16) + randomId(8);
        }

        MDC.put("cm_type", "req");
        MDC.put("cm_id", format("req:%s", requestId.substring(0, 6)));
        request.setAttribute(CMDBUILD_REQUEST_ID_HEADER, requestId);
        requestContextService.initCurrentRequestContext(format("req_%s", requestId), REQUEST_ID, requestId);
        if (config.enableLogTracking()) {
            errorAndWarningCollectorService.getCurrentRequestEventCollector().enableFullLogCollection();
        }
        eventBus.post(new RequestBeginEventImpl(requestId));

        String requestPath = request.getServletPath() + nullToEmpty(request.getPathInfo());

        boolean trackRequest = config.isRequestTrackingEnabled();
        if (trackRequest && isNotBlank(config.getRegexForPathsToInclude())) {
            trackRequest = Pattern.compile(config.getRegexForPathsToInclude()).matcher(requestPath).find();
        }
        if (trackRequest && isNotBlank(config.getRegexForPathsToExclude())) {
            trackRequest = !Pattern.compile(config.getRegexForPathsToExclude()).matcher(requestPath).find();
        }

        logger.trace("request BEGIN, track request = {}", trackRequest);

        Map<String, String> requestHeaders = Collections.list(request.getHeaderNames()).stream().distinct().sorted().collect(toMap(identity(), h -> Joiner.on(", ").join(Collections.list(request.getHeaders(h)))));

        if (logger.isTraceEnabled()) {
            logger.trace("request is from =< {} > method =< {} > path =< {} > query =< {} >", request.getRemoteAddr(), request.getMethod(), requestPath, nullToEmpty(request.getQueryString()));
            logger.trace("request headers =\n\n{}\n", mapToLoggableString(requestHeaders));
            logger.trace("request cookies =\n\n{}\n", mapToLoggableString(map(list(firstNonNull(request.getCookies(), new Cookie[]{})).sorted(Cookie::getName), Cookie::getName, Cookie::getValue)));
        }

        HttpServletRequest requestToUse;
        HttpServletResponse responseToUse;

        boolean isFirstRequest = !isAsyncDispatch(request);
        if (isFirstRequest && !(request instanceof MyContentCachingRequestWrapper)) {
            requestToUse = new MyContentCachingRequestWrapper(request);
        } else {
            requestToUse = request;
        }

        if (isFirstRequest && !(response instanceof MyContentCachingResponseWrapper)) {
            responseToUse = new MyContentCachingResponseWrapper(response);
        } else {
            responseToUse = response;
        }

        ZonedDateTime beginTimestamp = CmDateUtils.now();

        Supplier<RequestData> requestBeginDataSupplier = memoize(() -> {
            return RequestDataImpl.builder()
                    .withActionId(firstNonNull(trimToNull(request.getHeader(CMDBUILD_ACTION_ID_HEADER)), NO_ACTION_ID))
                    .withRequestId(requestId)
                    .withSessionId(firstNonNull(getSessionTokenFromRequest(request), NO_SESSION_ID))
                    .withClient(request.getRemoteAddr())
                    .withUserAgent(firstNonNull(trimToNull(request.getHeader("user-agent")), NO_USER_AGENT))
                    .withMethod(request.getMethod())
                    .withPath(requestPath)
                    .withQuery(nullToEmpty(request.getQueryString()))
                    .withTimestamp(beginTimestamp)
                    .withPayloadSize((long) request.getContentLength()) // request size from header
                    .withPayloadContentType(request.getContentType())
                    .accept(b -> {
                        if (config.includeHeaders()) {
                            b.withRequestHeaders(requestHeaders);
                        }
                    })
                    .build();
        });

        //try to track request before processing
        if (trackRequest) {
            try {
                service.requestBegin(requestBeginDataSupplier.get());
            } catch (Exception ex) {
                logger.warn("error tracking request begin", ex);
            }
        }

        try {
            response.setHeader("CMDBuild-RequestId", requestId);
            filterChain.doFilter(requestToUse, responseToUse);
        } catch (Exception ex) {
            logger.error(marker(), "error processing request = {}", requestId, ex);
            throw new RequestTrackingFilterException(ex);
        } finally {

            logger.trace("request END, track request = {}", trackRequest);

            boolean includeRequestPayload;
            boolean includeResponsePayload;

            if (errorAndWarningCollectorService.getCurrentRequestEventCollector().hasErrors()) {
                logger.trace("response has error, turn on full tracking for request requestId = {}", requestId);
                trackRequest = true;
                includeRequestPayload = true;
                includeResponsePayload = true;
            } else {
                includeRequestPayload = config.includeRequestPayload();
                includeResponsePayload = config.includeResponsePayload();
            }

            MyContentCachingResponseWrapper responseWrapper = getResponseWrapper(responseToUse);
            Map<String, String> responseHeaders = responseWrapper.getHeaderNames().stream().distinct().sorted().collect(toMap(identity(), h -> Joiner.on(", ").join(responseWrapper.getHeaders(h))));

            //try to track request after processing
            if (trackRequest) {
                try {
                    long elapsed = beginTimestamp.until(CmDateUtils.now(), ChronoUnit.MILLIS);
                    FaultEventCollector currentRequestEventCollector = errorAndWarningCollectorService.getCurrentRequestEventCollector();
                    RequestData requestComplete = RequestDataImpl.copyOf(requestBeginDataSupplier.get())
                            .withElapsedTimeMillis(toIntExact(elapsed))
                            .withStatusCode(response.getStatus())
                            .withResponseSize(getContentLength(response)) //response size from header
                            .accept((builder) -> {
                                String payloadContentType = request.getContentType();
                                try {
                                    if (includeRequestPayload) {
                                        MyContentCachingRequestWrapper requestWrapper = checkNotNull(WebUtils.getNativeRequest(requestToUse, MyContentCachingRequestWrapper.class), "unable to get request wrapper");
                                        BigByteArray data = requestWrapper.getContentAsBigByteArray();
                                        payloadContentType = requestWrapper.getContentType();
                                        if (isBlank(payloadContentType) && data.length() > 0) {
                                            payloadContentType = tika.detect(data.toInputStream());
                                        }
                                        builder
                                                .withPayloadSize(data.length())
                                                .withPayloadContentType(payloadContentType);
                                        if (isPlaintext(payloadContentType) && data.length() < Integer.MAX_VALUE) {
                                            builder.withPayloadBytes(null).withPayloadText(trimPayload(fixCharset(data.toByteArray(), requestWrapper.getCharacterEncoding())));
                                        } else {
                                            builder.withPayloadText(null).withPayloadBytes((data.length() > Integer.MAX_VALUE || (config.trimPayload() && data.length() > config.getMaxPayloadLength())) ? null : data.toByteArray());
                                        }
                                    } else {
                                        builder
                                                .withPayloadContentType(payloadContentType)
                                                .withPayloadBytes(null).withPayloadText(PAYLOAD_TRACKING_DISABLED);
                                    }
                                } catch (Exception ex) {
                                    logger.warn("error retrieving request payload for tracking", ex);
                                    builder
                                            .withPayloadContentType(payloadContentType)
                                            .withPayloadBytes(null).withPayloadText("PAYLOAD_TRACKING_ERROR: error retrieving request payload: " + ex.toString());
                                }
                                String responseContentType = responseWrapper.getContentType();
                                try {
                                    if (includeResponsePayload) {
                                        BigByteArray data = responseWrapper.getContentBytes();
                                        if (isBlank(responseContentType) && data.length() > 0) {
                                            responseContentType = tika.detect(data.toInputStream());
                                        }
                                        builder
                                                .withResponseSize(data.length())
                                                .withResponseContentType(responseContentType);
                                        if (isPlaintext(responseContentType) && data.length() < Integer.MAX_VALUE) {
                                            builder.withResponseBytes(null).withResponseText(trimPayload(fixCharset(data.toByteArray(), responseWrapper.getCharacterEncoding())));
                                        } else {
                                            builder.withResponseText(null).withResponseBytes((data.length() > Integer.MAX_VALUE || (config.trimPayload() && data.length() > config.getMaxPayloadLength())) ? null : data.toByteArray());
                                        }
                                    } else {
                                        builder.withResponseContentType(responseContentType).withResponseText(RESPONSE_TRACKING_DISABLED);
                                    }
                                } catch (Exception ex) {
                                    logger.warn("error retrieving response payload for tracking", ex);
                                    builder
                                            .withResponseContentType(responseContentType)
                                            .withResponseBytes(null).withResponseText("RESPONSE_TRACKING_ERROR: error retrieving response payload: " + ex.toString());
                                }

                                boolean attachFullLogs = switch (config.getLogTrackingMode()) {
                                    case LTM_ALWAYS ->
                                        true;
                                    case LTM_NEVER ->
                                        false;
                                    case LTM_ON_ERROR ->
                                        currentRequestEventCollector.hasErrors();
                                };
                                if (attachFullLogs) {
                                    builder.withLogs(currentRequestEventCollector.getLogs());
                                }

                                if (config.includeHeaders()) {
                                    builder.withResponseHeaders(responseHeaders);
                                }
//                                if (config.includeTcpDump()) {
//                                    if (tomcatPlus.isAvailable()) {//TODO improve this
//                                        BigByteArray dump = tomcatPlus.getRequestResponseTcpDump(requestId);
//                                        if (dump.length() > Integer.MAX_VALUE || (config.trimPayload() && dump.length() > config.getMaxPayloadLength())) {
//                                            logger.trace("skip tcp dump, size is too big");
//                                        } else {
//                                            logger.trace("raw request dump = \n\n{}\n", dump);
//                                            builder.withTcpDumpBytes(dump.toByteArray());
//                                        }
//                                    } else {
//                                        logger.debug("cannot attach tcp dump to request: tomcat plus service is not available");
//                                    }
//                                }
                            })
                            .withFaultEvents(currentRequestEventCollector.getCollectedEvents())
                            .build();

                    service.requestComplete(requestComplete);
                    if (requestComplete.hasError()) {
                        logger.warn("processed request, returned error for requestId = {}", requestId);
                    }
                } catch (Exception ex) {
                    logger.warn("error tracking request completion", ex);
                }
            }

            if (logger.isTraceEnabled()) {
                logger.trace("response status =< {} > content = {}", response.getStatus(), getContentLength(response) > 0 ? byteCountToDisplaySize(getContentLength(response)) + " " : "", firstNotBlank(response.getContentType(), "N/A"));
                logger.trace("response headers =\n\n{}\n", mapToLoggableString(responseHeaders));
            }

            eventBus.post(new RequestCompleteEventImpl(requestId));
            requestContextService.destroyCurrentRequestContext();
            MDC.clear();
        }

    }

    private static long getContentLength(HttpServletResponse response) {
        return Long.parseLong(firstNonNull(trimToNull(response.getHeader("Content-Length")), "-1"));
    }

    private String fixCharset(byte[] data, @Nullable String characterEncoding) throws UnsupportedEncodingException {
        if (isBlank(characterEncoding)) {
            //TODO this is not really correct. Http specs say that default character encoding should be ISO-something (see WebUtils.DEFAULT_CHARACTER_ENCODING). defaultCharset here is probably UTF8
            // BUT, utf8 is generally correct even if not explicitly set
            characterEncoding = Charset.defaultCharset().name();
        }
        return new String(data, characterEncoding);
    }

    private String trimPayload(String payload) {
        if (config.trimPayload() && payload.length() > config.getMaxPayloadLength()) {
            String suffix = " PAYLOAD_TRIMMED_TO_" + config.getMaxPayloadLength() + "_BYTES";
            int maxLenMinusSuffix = config.getMaxPayloadLength() - suffix.length();
            return maxLenMinusSuffix > 0
                    ? (abbreviate(payload, maxLenMinusSuffix) + suffix)
                    : abbreviate(payload, config.getMaxPayloadLength());
        } else {
            return payload;
        }
    }

    private MyContentCachingResponseWrapper getResponseWrapper(HttpServletResponse responseToUse) {
        return checkNotNull(WebUtils.getNativeResponse(responseToUse, MyContentCachingResponseWrapper.class), "unable to get response wrapper");
    }
}
