/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.core;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Streams.stream;
import com.google.common.net.UrlEscapers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.nio.charset.Charset;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.time.ZonedDateTime;
import java.util.Optional;
import static java.util.stream.Collectors.joining;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;
import org.apache.http.entity.StringEntity;
import org.cmdbuild.utils.date.CmDateUtils;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.listenToStreamProgress;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.isJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServiceClientImpl implements RestServiceClient {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestWsClient restClient;

    public AbstractServiceClientImpl(RestWsClient restClient) {
        this.restClient = checkNotNull(restClient);
    }

    @Override
    public RestWsClient restClient() {
        return restClient;
    }

    protected String buildRestV3Url(String path) {
        if (path.startsWith("/services/")) {
            return restClient().getServerUrl() + path;
        } else {
            return restClient().getServerUrl() + "/services/rest/v3/" + path;
        }
    }

    protected String buildRestV4Url(String path) {
        if (path.startsWith("/services/")) {
            return restClient().getServerUrl() + path;
        } else {
            return restClient().getServerUrl() + "/services/rest/v4/" + path;
        }
    }

    protected HttpClient httpClient() {
        return restClient().getHttpClient();
    }

    protected boolean isSessionTokenRequired() {
        return true;
    }

    protected Response post(String service, Object data) {
        return new PostRequestRunner(service, data).execute();
    }

    protected Response put(String service, Object data) {
        return new PutRequestRunner(service, data).execute();
    }

    protected Response get(String service) {
        return new GetRequestRunner(service).execute();
    }

    protected byte[] getBytes(String service) {
        return new GetBytesRequestRunner(service).executeForBytes();
    }

    protected DataSource getDataSource(String service) {
        return new GetBytesRequestRunner(service).executeForDataSource();
    }

    protected BigByteArray getBigBytes(String service) {
        return new GetBytesRequestRunner(service).executeForBigBytes();
    }

    protected Response delete(String service) {
        return new DeleteRequestRunner(service).execute();
    }

    @Nullable
    protected static String toString(@Nullable JsonElement value) {
        return CmJsonUtils.toString(value);
    }

    @Nullable
    protected static Boolean toBoolean(JsonElement value) {
        String stringValue = toString(value);
        return isBlank(stringValue) ? null : Boolean.valueOf(stringValue);
    }

    @Nullable
    protected static Integer toInteger(JsonElement value) {
        String stringValue = toString(value);
        return isBlank(stringValue) ? null : Integer.valueOf(stringValue);
    }

    @Nullable
    protected static Long toLong(JsonElement value) {
        String stringValue = toString(value);
        return isBlank(stringValue) ? null : Long.valueOf(stringValue);
    }

    @Nullable
    protected static ZonedDateTime toDateTime(JsonElement value) {
        return CmDateUtils.toDateTime(toString(value));
    }

    protected static String encodeUrlPath(String part) {
        return UrlEscapers.urlPathSegmentEscaper().escape(part);
    }

    protected static String encodeUrlQuery(String part) {
        return UrlEscapers.urlFormParameterEscaper().escape(part);
    }

    protected InputStream listenUpload(String description, InputStream in) {
        return listenUpload(randomId(), description, in);
    }

    protected InputStream listenUpload(String id, String description, InputStream in) {
        return listenToStreamProgress(id, description, in, restClient.getEventBus()::post);
    }

    protected class RequestRunner {

        protected final HttpUriRequest request;
        protected HttpResponse response;
        protected String responseContent, responseContentType;
        boolean hasError = false;
        protected JsonElement parsedResponse;

        public RequestRunner(HttpUriRequest request) {
            this.request = request;
        }

        public String executeForString() {
            execute();
            return responseContent;
        }

        public Response execute() {
            try {
                logger.debug("execute request {} {}", request.getMethod(), request.getURI());
                if (isSessionTokenRequired()) {
                    logger.debug("set session token = {}", restClient.getSessionToken());
                    request.setHeader("CMDBuild-Authorization", restClient.getSessionToken());//TODO use const for custom header name
                } else {
                    logger.debug("session not required, skipping session token");
                }
                if (!isBlank(restClient().getActionId())) {
                    logger.debug("set action id = {}", restClient.getActionId());
                    request.setHeader("CMDBuild-Actionid", restClient.getActionId());//TODO use const for custom header name
                }
                String requestId = randomId();
                logger.debug("set request id = {}", requestId);
                request.setHeader("CMDBuild-RequestId", requestId);//TODO use const for custom header name
                setEntity();
                response = httpClient().execute(request);
                readResponse();
                checkResponse();
                parseResponse();
                return new Response(responseContent, responseContentType, parsedResponse);
            } catch (IOException ex) {
                throw new RestClientException("IO exception", ex);
            }
        }

        protected void setEntity() {
        }

        protected void readResponse() {
            try {
                if (response.getEntity() == null) {
                    responseContent = responseContentType = null;
                } else {
                    responseContent = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
                    responseContentType = Optional.ofNullable(response.getEntity().getContentType()).map(h -> h.getValue()).orElseGet(() -> getContentType(responseContent.getBytes(UTF_8)));
                }
                logger.debug("response = {} ( {} )", abbreviate(responseContent), responseContentType);
            } catch (Exception ex) {
                logger.error("unable to read response content", ex);
                responseContent = null;
            }
        }

        protected boolean isStatusCodeGood(int statusCode) {
            return String.valueOf(statusCode).startsWith("20");
        }

        protected void checkResponse() {
            if (!isStatusCodeGood(response.getStatusLine().getStatusCode())) {
                hasError = true;
            }
            String errorMessageFromResponse = hasError ? getErrorMessageFromResponseSafe() : "<not needed>";
            checkArgument(!hasError, "error: response status code = %s, error message = %s", response.getStatusLine(), errorMessageFromResponse);
        }

        protected void parseResponse() {
            if (isBlank(responseContent)) {
                parsedResponse = null;
            } else if (isJson(responseContent)) {
                parsedResponse = new JsonParser().parse(responseContent);
            } else {
                parsedResponse = new JsonPrimitive(responseContent);
            }
        }

        private String getErrorMessageFromResponseSafe() {
            try {
                String message;
                if (isBlank(responseContent)) {
                    message = null;
                } else if (responseContent.startsWith("{")) {
                    JsonObject jsonObject = new JsonParser().parse(responseContent).getAsJsonObject();
                    if (jsonObject.has("messages")) {
                        message = stream(jsonObject.getAsJsonArray("messages")).map(JsonElement::getAsJsonObject).map((o) -> format("%s: %s", o.getAsJsonPrimitive("level").getAsString(), o.getAsJsonPrimitive("message").getAsString())).collect(joining(", "));
                    } else {
                        message = null;
                    }
                } else {
                    message = responseContent;
                }
                return firstNonNull(message, "no error message in response");
            } catch (Exception ex) {
                logger.warn("error extracting error message from response content = " + responseContent, ex);
                return "error extracting error message: " + ex.toString();
            }
        }

    }

    protected class Response {

        private final String json, contentType;
        private final JsonElement jsonElement;

        public Response(String json, String contentType, JsonElement jsonElement) {
            this.json = json;
            this.jsonElement = jsonElement;
            this.contentType = contentType;
        }

        @Nullable
        public String getContentType() {
            return contentType;
        }

        public String asString() {
            return checkNotNull(json);
        }

        public JsonElement asJson() {
            return checkNotNull(jsonElement);
        }

        public JsonNode asJackson() {
            return fromJson(json, JsonNode.class);
        }

        public boolean hasContent() {
            return isNotBlank(json);
        }

    }

    private class GetRequestRunner extends RequestRunner {

        public GetRequestRunner(String service) {
            super(new HttpGet(buildRestV4Url(service)));
        }

        @Override
        protected boolean isStatusCodeGood(int statusCode) {
            return statusCode == 200;
        }

    }

    private class GetBytesRequestRunner extends GetRequestRunner {

        private BigByteArray data;

        public GetBytesRequestRunner(String service) {
            super(service);
        }

        public byte[] executeForBytes() {
            return executeForBigBytes().toByteArray();
        }

        public DataSource executeForDataSource() {
            execute();
            String mimeType = response.getEntity().getContentType().getValue();
            return CmIoUtils.newDataSource(data, mimeType, "file." + CmIoUtils.getExtFromContentType(mimeType));
        }

        public BigByteArray executeForBigBytes() {
            execute();
            return data;
        }

        @Override
        protected void readResponse() {
            try {
                data = toBigByteArray(response.getEntity().getContent());
            } catch (IOException | UnsupportedOperationException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        protected void parseResponse() {
        }

    }

    private class DeleteRequestRunner extends RequestRunner {

        public DeleteRequestRunner(String service) {
            super(new HttpDelete(buildRestV4Url(service)));
        }

    }

    private class PostRequestRunner extends DataRequestRunner {

        public PostRequestRunner(String service, Object data) {
            super(new HttpPost(buildRestV4Url(service)), data);
        }

    }

    private class PutRequestRunner extends DataRequestRunner {

        public PutRequestRunner(String service, Object data) {
            super(new HttpPut(buildRestV4Url(service)), data);
        }

    }

    protected abstract class DataRequestRunner extends RequestRunner {

        private final Object data;

        public DataRequestRunner(HttpUriRequest request, Object data) {
            super(request);
            this.data = checkNotNull(data);
        }

        @Override
        protected void setEntity() {
            HttpEntity entity;
            if (data instanceof HttpEntity httpEntity) {
                entity = httpEntity;
                logger.debug("send request data entity= {}", entity);
            } else if (data instanceof DataSource dataSource) {
                entity = new ByteArrayEntity(toByteArray(dataSource), ContentType.parse(getContentType(dataSource)));
            } else {
                String stringPayload;
                ContentType contentType;
                if (data instanceof String string) {
                    stringPayload = string;
                    contentType = isJson(stringPayload) ? APPLICATION_JSON : TEXT_PLAIN;
                } else {
                    stringPayload = toJson(data);
                    contentType = APPLICATION_JSON;
                }
                entity = new StringEntity(stringPayload, contentType);
                logger.debug("send {} request data = {}", contentType, stringPayload);
            }
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }

    }
}
