/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.utils.lang.Builder;

public class RequestInfoImpl implements RequestInfo {

    private final String sessionId, requestId, actionId, user, path, method, query, soapActionOrMethod, nodeId;
    private final Integer statusCode, elapsedTimeMillis;
    private final boolean isSoap;
    private final ZonedDateTime timestamp;

    private RequestInfoImpl(SimpleRequestInfoBuilder builder) {
        this.nodeId = builder.nodeId;
        this.sessionId = builder.sessionId;
        this.requestId = builder.requestId;
        this.actionId = builder.actionId;
        this.user = builder.user;
        this.path = builder.path;
        this.method = builder.method;
        this.query = builder.query;
        this.soapActionOrMethod = builder.soapActionOrMethod;
        this.isSoap = builder.isSoap;
        this.elapsedTimeMillis = (builder.elapsedTimeMillis);
        this.statusCode = (builder.statusCode);
        this.timestamp = checkNotNull(builder.timestamp);
    }

    @Override
    public Integer getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public String getActionId() {
        return actionId;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public String getSoapActionOrMethod() {
        return soapActionOrMethod;
    }

    @Override
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean isSoap() {
        return isSoap;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return "SimpleRequestInfo{" + "requestId=" + requestId + '}';
    }

    @Override
    public Integer getStatusCode() {
        return statusCode;
    }

    public static SimpleRequestInfoBuilder builder() {
        return new SimpleRequestInfoBuilder();
    }

    public static class SimpleRequestInfoBuilder implements Builder<RequestInfoImpl, SimpleRequestInfoBuilder> {

        protected String sessionId, requestId, actionId, user, path, method, query, soapActionOrMethod, nodeId;
        protected Integer statusCode, elapsedTimeMillis;
        protected boolean isSoap = false;
        private ZonedDateTime timestamp;

        public SimpleRequestInfoBuilder withSessionId(String sessionId) {
            this.sessionId = checkNotNull(sessionId);
            return this;
        }

        public SimpleRequestInfoBuilder withUser(String user) {
            this.user = user;
            return this;
        }

        public SimpleRequestInfoBuilder withNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public SimpleRequestInfoBuilder withRequestId(String requestId) {
            this.requestId = checkNotNull(requestId);
            return this;
        }

        public SimpleRequestInfoBuilder withActionId(String actionId) {
            this.actionId = checkNotNull(actionId);
            return this;
        }

        public SimpleRequestInfoBuilder withPath(String path) {
            this.path = checkNotNull(path);
            return this;
        }

        public SimpleRequestInfoBuilder withMethod(String method) {
            this.method = checkNotNull(method);
            return this;
        }

        public SimpleRequestInfoBuilder withQuery(String query) {
            this.query = query;
            return this;
        }

        public SimpleRequestInfoBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = checkNotNull(timestamp);
            return this;
        }

        public SimpleRequestInfoBuilder withSoapActionOrMethod(@Nullable String soapActionOrMethod) {
            this.isSoap = !isBlank(soapActionOrMethod);
            this.soapActionOrMethod = soapActionOrMethod;
            return this;
        }

        public SimpleRequestInfoBuilder withSoap(boolean isSoap, @Nullable String soapActionOrMethod) {
            this.isSoap = isSoap;
            this.soapActionOrMethod = soapActionOrMethod;
            return this;
        }

        public SimpleRequestInfoBuilder withElapsedTimeMillis(Integer elapsedTimeMillis) {
            this.elapsedTimeMillis = (elapsedTimeMillis);
            return this;
        }

        public SimpleRequestInfoBuilder withStatusCode(Integer statusCode) {
            this.statusCode = (statusCode);
            return this;
        }

        @Override
        public RequestInfoImpl build() {
            return new RequestInfoImpl(this);
        }
    }

}
