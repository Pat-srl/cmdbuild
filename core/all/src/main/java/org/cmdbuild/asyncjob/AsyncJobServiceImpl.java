/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.asyncjob;

import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_AUTHORIZATION_HEADER;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class AsyncJobServiceImpl implements AsyncRequestJobService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ExecutorService executorService = Executors.newCachedThreadPool(namedThreadFactory(getClass()));
    private final Map<Long, AsyncRequestJob> jobs = new ConcurrentHashMap<>();

    private final SessionService sessionService;
    private final RequestContextService requestContextService;

    public AsyncJobServiceImpl(SessionService sessionService, RequestContextService requestContextService) {
        this.sessionService = checkNotNull(sessionService);
        this.requestContextService = checkNotNull(requestContextService);
    }

    @Override
    public AsyncRequestJob createAsyncRequest(String requestPath, HttpServletRequest request, HttpServletResponse response) {
        try {
            String method = request.getMethod().toUpperCase();
            logger.debug("starting async request job for method = {} path =< {} >", method, requestPath);

            byte[] payload = toByteArray(request.getInputStream());

            String contentType = request.getContentType(),
                    baseUrl = request.getRequestURL().toString();

            Set<String> headersToCopyFromOriginalRequest = list("TODO").stream().map(String::toLowerCase).collect(toSet());//TODO

            Map<String, String> params = map(request.getParameterMap()).filterKeys(k -> !ASYNC_JOB_REQUEST_PARAM.equalsIgnoreCase(k)).mapValues(v -> getOnlyElement(list(v))),
                    headers = list(request.getHeaderNames()).filter(k -> headersToCopyFromOriginalRequest.contains(k.toLowerCase())).collect(toMap(identity(), request::getHeader));

            String sessionId = sessionService.getCurrentSessionId();
            headers.put(CMDBUILD_AUTHORIZATION_HEADER, sessionId);

            AsyncRequestJob job = new AsyncRequestJobImpl(ThreadLocalRandom.current().nextLong(Integer.MAX_VALUE)); // limit random to integer length
            jobs.put(job.getId(), job);

            executorService.submit(() -> {
                try {

                    //TODO init request thread context, etc
                    MDC.put("cm_type", "req_async");//TODO improve this
                    MDC.put("cm_id", format("req_async:%s", job.getId()));//TODO improve this, add original request id (?)
                    requestContextService.initCurrentRequestContext(format("req_async_%s", job.getId()));//TODO improve this, add original request id (?)

                    logger.debug("execute async request job = {}", job);

                    CloseableHttpClient httpClient = HttpClients.custom()
                            .setDefaultHeaders(headers.entrySet().stream().map(e -> new BasicHeader(e.getKey(), e.getValue())).collect(toList()))
                            .build();

                    URIBuilder builder = new URIBuilder(baseUrl);
                    params.forEach(builder::addParameter);
                    String requestUrl = builder.build().toString();

                    logger.debug("request url = {}", requestUrl);
                    logger.debug("request params = \n\n{}\n", mapToLoggableStringLazy(params));
                    logger.debug("request headers = \n\n{}\n", mapToLoggableStringLazy(headers));

                    HttpUriRequest clientRequest = switch (method) {
                        case HttpGet.METHOD_NAME ->
                            new HttpGet(requestUrl);
                        case HttpDelete.METHOD_NAME ->
                            new HttpDelete(requestUrl);
                        case HttpPost.METHOD_NAME -> {
                            HttpPost post = new HttpPost(requestUrl);
                            post.setEntity(new ByteArrayEntity(payload, ContentType.parse(contentType)));//TODO charset (?)
                            yield post;
                        }
                        case HttpPut.METHOD_NAME -> {
                            HttpPut put = new HttpPut(requestUrl);
                            put.setEntity(new ByteArrayEntity(payload, ContentType.parse(contentType)));//TODO charset (?)
                            yield put;
                        }
                        default ->
                            throw unsupported("unsupported method = %s", method);
                    };
                    HttpResponse clientResponse = httpClient.execute(clientRequest);

                    int statusCode = clientResponse.getStatusLine().getStatusCode();
                    byte[] responseContent = clientResponse.getEntity() == null ? ArrayUtils.EMPTY_BYTE_ARRAY : toByteArray(clientResponse.getEntity().getContent());
                    Map<String, String> responseHeaders = list(clientResponse.getAllHeaders()).stream().collect(toMap(Header::getName, Header::getValue, (v1, v2) -> format("%s,%s", v1, v2)));

                    AsyncRequestJob completedJob = new AsyncRequestJobImpl(job, responseHeaders, responseContent, statusCode);
                    logger.debug("completed job = {} with response status code = {}", completedJob, clientResponse.getStatusLine());
                    logger.debug("response content = \n\n{}\n", new String(responseContent));//TODO improve this
                    jobs.put(completedJob.getId(), completedJob);
                } catch (Exception ex) {
                    logger.error("failed job = {}", job, ex);
                    AsyncRequestJob failedJob = new AsyncRequestJobImpl(job, emptyMap(), toJson(map("success", false, "message", ex.toString())).getBytes(), 500);//TODO improve error response
                    jobs.put(failedJob.getId(), failedJob);
                }
            });

            return job;
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    @Override
    public AsyncRequestJob getJobForCurrentUserById(long jobId) {
        AsyncRequestJob job = checkNotNull(jobs.get(jobId), "job not found for id = %s", jobId);
        //TODO validate job user
        return job;
    }

    @Override
    public void deleteJob(long jobId) {
        jobs.remove(jobId);
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executorService);
    }

    private static class AsyncRequestJobImpl implements AsyncRequestJob {

        private final long id;

        private final boolean completed;

        private final Integer statusCode;
        private final Map<String, String> responseHeaders;
        private final byte[] responseContent;

        public AsyncRequestJobImpl(Long id) {
            this.id = id;
            this.completed = false;
            this.responseContent = null;
            this.responseHeaders = emptyMap();
            this.statusCode = null;
        }

        public AsyncRequestJobImpl(AsyncRequestJob copyOf, Map<String, String> responseHeaders, byte[] responseContent, int statusCode) {
            this.id = copyOf.getId();
            this.completed = true;
            this.responseHeaders = map(responseHeaders).immutable();
            this.responseContent = checkNotNull(responseContent);
            this.statusCode = statusCode;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public boolean isCompleted() {
            return completed;
        }

        @Override
        public int getStatusCode() {
            return statusCode;
        }

        @Override
        public Map<String, String> getResponseHeaders() {
            return responseHeaders;
        }

        @Override
        public byte[] getResponseContent() {
            return checkNotNull(responseContent);
        }

        @Override
        public String toString() {
            return "AsyncRequestJob{" + "id=" + id + '}';
        }

    }

}
