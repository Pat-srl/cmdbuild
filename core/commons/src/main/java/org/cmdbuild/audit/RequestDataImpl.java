/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.xml.CmXmlUtils.isXml;

@CardMapping("_Request")
public class RequestDataImpl implements RequestData {

    private final Long id, payloadSize, responseSize;
    private final String client, userAgent, path, method, payload, response, responseContentType, payloadContentType, nodeId;
    private final Integer statusCode;
    private final ZonedDateTime timestamp;
    private final List<FaultEvent> events;
    private final String sessionId, requestId, actionId, user, query, soapActionOrMethod, logs;
    private final Integer elapsedTimeMillis;
    private final boolean isSoap;
    private final byte[] payloadBytes, responseBytes, tcpDumpBytes;
    private final Map<String, String> requestHeaders, responseHeaders;

    private RequestDataImpl(RequestDataImplBuilder builder) {
        this.nodeId = builder.nodeId;
        this.sessionId = builder.sessionId;
        this.id = builder.id;
        this.requestId = builder.requestId;
        this.actionId = builder.actionId;
        this.user = builder.user;
        this.query = builder.query;
        this.soapActionOrMethod = builder.soapActionOrMethod;
        this.isSoap = builder.isSoap;
        this.payloadBytes = builder.payloadBytes;
        this.responseBytes = builder.responseBytes;
        this.tcpDumpBytes = builder.tcpDumpBytes;
        this.elapsedTimeMillis = (builder.elapsedTimeMillis);
        this.client = checkNotNull(builder.client);
        this.userAgent = checkNotNull(builder.userAgent);
        this.path = checkNotNull(builder.path);
        this.method = checkNotNull(builder.method);
        this.payload = (builder.payload);
        this.payloadContentType = (builder.payloadContentType);
        this.payloadSize = checkNotNull(builder.payloadSize);
        this.response = (builder.response);
        this.responseContentType = (builder.responseContentType);
        this.responseSize = (builder.responseSize);
        this.statusCode = (builder.statusCode);
        this.logs = (builder.logs);
        this.timestamp = checkNotNull(builder.timestamp);
        this.events = ImmutableList.copyOf(firstNonNull(builder.events, emptyList()));

        this.requestHeaders = map(firstNotNull(builder.requestHeaders, emptyMap())).immutable();
        this.responseHeaders = map(firstNotNull(builder.responseHeaders, emptyMap())).immutable();

        checkArgument(payloadBytes == null || isBlank(payload), "cannot set both payload text and payload bytes");
        checkArgument(responseBytes == null || isBlank(response), "cannot set both response text and response bytes");
    }

    @Override
    @CardAttr
    @Nullable
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(value = "ElapsedTime", defaultValue = "0")
    public Integer getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    @Override
    @CardAttr
    public String getSessionId() {
        return sessionId;
    }

    @Override
    @CardAttr("NodeId")
    public String getNodeId() {
        return nodeId;
    }

    @Override
    @CardAttr("SessionUser")
    public String getUser() {
        return user;
    }

    @Override
    @CardAttr
    public String getRequestId() {
        return requestId;
    }

    @Override
    @CardAttr
    public String getActionId() {
        return actionId;
    }

    @Override
    @CardAttr
    public String getPath() {
        return path;
    }

    @Override
    @CardAttr
    public String getMethod() {
        return method;
    }

    @Override
    @CardAttr
    public String getQuery() {
        return query;
    }

    @Override
    public String getSoapActionOrMethod() {
        return soapActionOrMethod;
    }

    @Override
    public boolean isSoap() {
        return isSoap;
    }

    @Override
    @CardAttr
    public String getClient() {
        return client;
    }

    @Override
    @CardAttr
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    @Nullable
    @CardAttr("Payload")
    public String getPayloadText() {
        return payload;
    }

    @Override
    @CardAttr
    @Nullable
    public byte[] getPayloadBytes() {
        return payloadBytes;
    }

    @Override
    @Nullable
    @CardAttr("Response")
    public String getResponseText() {
        return response;
    }

    @Override
    @CardAttr
    @Nullable
    public Long getPayloadSize() {
        return payloadSize;
    }

    @Override
    @Nullable
    @CardAttr
    public String getPayloadContentType() {
        return payloadContentType;
    }

    @Override
    @Nullable
    @CardAttr
    public String getResponseContentType() {
        return responseContentType;
    }

    @Override
    @CardAttr
    @Nullable
    public byte[] getResponseBytes() {
        return responseBytes;
    }

    @Override
    @CardAttr("TcpDump")
    @Nullable
    public byte[] getTcpDumpBytes() {
        return tcpDumpBytes;
    }

    @Override
    @CardAttr
    @Nullable
    public Long getResponseSize() {
        return responseSize;
    }

    @Override
    @CardAttr("StatusCode")
    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    @CardAttr
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    @CardAttr(readFromDb = false)
    public boolean isCompleted() {
        return isNotNullAndGtZero(getStatusCode()) && getElapsedTimeMillis() != null;
    }

    @CardAttr("Errors")
    @JsonBean
    @Override
    public List<FaultEvent> getFaultEvents() {
        return events;
    }

    @Override
    @CardAttr
    @Nullable
    public String getLogs() {
        return logs;
    }

    @CardAttr
    @JsonBean
    @Override
    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    @CardAttr
    @JsonBean
    @Override
    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    @Override
    public String toString() {
        String str = "RequestDataImpl{" + "requestId=" + getRequestId() + ", path=" + method + " " + path;
        if (getElapsedTimeMillis() != null) {
            str += format(", elapsed=%.3fs", getElapsedTimeMillis() / 1000d);
        }
        return str + "}";
    }

    public static RequestDataImplBuilder builder() {
        return new RequestDataImplBuilder();
    }

    public static RequestDataImplBuilder copyOf(RequestData data) {
        return new RequestDataImplBuilder()
                .withActionId(data.getActionId())
                .withClient(data.getClient())
                .withUserAgent(data.getUserAgent())
                .withElapsedTimeMillis(data.getElapsedTimeMillis())
                .withPath(data.getPath())
                .withMethod(data.getMethod())
                .withPayloadText(data.getPayloadText())
                .withPayloadContentType(data.getPayloadContentType())
                .withPayloadBytes(data.getPayloadBytes())
                .withQuery(data.getQuery())
                .withRequestId(data.getRequestId())
                .withResponseText(data.getResponseText())
                .withResponseContentType(data.getResponseContentType())
                .withResponseBytes(data.getResponseBytes())
                .withSessionId(data.getSessionId())
                .withUser(data.getUser())
                .withStatusCode(data.getStatusCode())
                .withPayloadSize(data.getPayloadSize())
                .withResponseSize(data.getResponseSize())
                .withTimestamp(data.getTimestamp())
                .withNodeId(data.getNodeId())
                .withFaultEvents(data.getFaultEvents())
                .withId(data.getId())
                .withLogs(data.getLogs())
                .withSoap(data.isSoap(), data.getSoapActionOrMethod())
                .withTcpDumpBytes(data.getTcpDumpBytes())
                .withRequestHeaders(data.getRequestHeaders())
                .withResponseHeaders(data.getResponseHeaders());
    }

    public static class RequestDataImplBuilder implements Builder<RequestDataImpl, RequestDataImplBuilder> {

        private Long id, payloadSize, responseSize;
        private String client, userAgent, payload, response, payloadContentType, responseContentType, nodeId, logs;
        private Integer statusCode;
        private ZonedDateTime timestamp;
        private List<FaultEvent> events;
        private String sessionId, requestId, actionId, user, path, method, query, soapActionOrMethod;
        private Integer elapsedTimeMillis;
        private boolean isSoap = false;
        private byte[] payloadBytes, responseBytes, tcpDumpBytes;
        private Map<String, String> requestHeaders, responseHeaders;

        private RequestDataImplBuilder() {
        }

        public RequestDataImplBuilder withNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public RequestDataImplBuilder withLogs(String logs) {
            this.logs = logs;
            return this;
        }

        public RequestDataImplBuilder withId(@Nullable Long id) {
            this.id = id;
            return this;
        }

        public RequestDataImplBuilder withSessionId(String sessionId) {
            this.sessionId = checkNotNull(sessionId);
            return this;
        }

        public RequestDataImplBuilder withUser(String user) {
            this.user = user;
            return this;
        }

        public RequestDataImplBuilder withRequestId(String requestId) {
            this.requestId = checkNotNull(requestId);
            return this;
        }

        public RequestDataImplBuilder withRequestHeaders(Map<String, String> requestHeaders) {
            this.requestHeaders = requestHeaders;
            return this;
        }

        public RequestDataImplBuilder withResponseHeaders(Map<String, String> responseHeaders) {
            this.responseHeaders = responseHeaders;
            return this;
        }

        public RequestDataImplBuilder withActionId(String actionId) {
            this.actionId = checkNotNull(actionId);
            return this;
        }

        public RequestDataImplBuilder withPath(String path) {
            this.path = checkNotNull(path);
            return this;
        }

        public RequestDataImplBuilder withMethod(String method) {
            this.method = checkNotNull(method);
            return this;
        }

        public RequestDataImplBuilder withQuery(String query) {
            this.query = query;
            return this;
        }

        public RequestDataImplBuilder withSoap(@Nullable String soapActionOrMethod) {
            this.isSoap = true;
            this.soapActionOrMethod = soapActionOrMethod;
            return this;
        }

        public RequestDataImplBuilder withSoapActionOrMethod(String soapActionOrMethod) {
            this.isSoap = true;
            this.soapActionOrMethod = soapActionOrMethod;
            return this;
        }

        public RequestDataImplBuilder withSoap(boolean isSoap, @Nullable String soapActionOrMethod) {
            this.isSoap = isSoap;
            this.soapActionOrMethod = soapActionOrMethod;
            return this;
        }

        public RequestDataImplBuilder withElapsedTimeMillis(Integer elapsedTimeMillis) {
            this.elapsedTimeMillis = (elapsedTimeMillis);
            return this;
        }

        public RequestDataImplBuilder withClient(String client) {
            this.client = checkNotNull(client);
            return this;
        }

        public RequestDataImplBuilder withUserAgent(String userAgent) {
            this.userAgent = checkNotNull(userAgent);
            return this;
        }

        public RequestDataImplBuilder withPayloadText(String payload) {
            this.payload = (payload);
            return this;
        }

        public RequestDataImplBuilder withPayloadBytes(byte[] payloadBytes) {
            this.payloadBytes = payloadBytes;
            return this;
        }

        public RequestDataImplBuilder withPayloadContentType(String payloadContentType) {
            this.payloadContentType = (payloadContentType);
            return this;
        }

        public RequestDataImplBuilder withResponseText(String response) {
            this.response = (response);
            return this;
        }

        public RequestDataImplBuilder withResponseBytes(byte[] responseBytes) {
            this.responseBytes = responseBytes;
            return this;
        }

        public RequestDataImplBuilder withResponseContentType(String responseContentType) {
            this.responseContentType = (responseContentType);
            return this;
        }

        public RequestDataImplBuilder withTcpDumpBytes(byte[] tcpDumpBytes) {
            this.tcpDumpBytes = tcpDumpBytes;
            return this;
        }

        public RequestDataImplBuilder withStatusCode(Integer statusCode) {
            this.statusCode = (statusCode);
            return this;
        }

        public RequestDataImplBuilder withPayloadSize(Long payloadSize) {
            this.payloadSize = checkNotNull(payloadSize);
            return this;
        }

        public RequestDataImplBuilder withResponseSize(Long responseSize) {
            this.responseSize = (responseSize);
            return this;
        }

        public RequestDataImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = checkNotNull(timestamp);
            return this;
        }

        public RequestDataImplBuilder withFaultEvents(List<FaultEvent> events) {
            this.events = events;
            return this;
        }

        @Override
        public RequestDataImpl build() {
            isSoap = isSoapRequest(path, payload);
            if (isSoap) {
                soapActionOrMethod = getSoapActionOrMethod(payload);
            }
            return new RequestDataImpl(this);
        }

        //TODO only for orm tool, refactor and remove
        public void setCompleted(String param) {
            //do nothig
        }

        private boolean isSoapRequest(String path, @Nullable String payload) {
            return path.contains("soap") && isXml(payload);
        }

        private @Nullable
        String getSoapActionOrMethod(String payload) {
            Matcher matcher = Pattern.compile("[<][^:]+[:]Body[>][<][^:]+[:]([a-zA-Z0-9]+)").matcher(payload);
            if (matcher.find()) {
                return trimToNull(matcher.group(1));
            } else {
                return null;
            }
        }
    }
}
