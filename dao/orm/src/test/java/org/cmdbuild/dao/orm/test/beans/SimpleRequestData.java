/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.test.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import org.cmdbuild.utils.json.JsonBean;
import static org.junit.Assert.fail;

/**
 *
 */
@CardMapping("Request")
public class SimpleRequestData implements RequestData {

    private final String client, userAgent, path, payload, response;
    private final Integer statusCode, payloadSize, responseSize;
    private final ZonedDateTime timestamp;
    private final MyEnum method;
    private final JsonBeanInterface events;
    private final String sessionId, requestId, trackingId, actionId, user, query, soapActionOrMethod;
    private final Integer elapsedTimeMillis;
    private final boolean isSoap;

    private SimpleRequestData(RequestDataBuilder builder) {
        this.sessionId = builder.sessionId;
        this.requestId = builder.requestId;
        this.trackingId = builder.trackingId;
        this.actionId = builder.actionId;
        this.user = builder.user;
        this.path = builder.path;
        this.method = builder.method;
        this.query = builder.query;
        this.soapActionOrMethod = builder.soapActionOrMethod;
        this.isSoap = builder.isSoap;
        this.elapsedTimeMillis = (builder.elapsedTimeMillis);
        this.client = (builder.client);
        this.userAgent = (builder.userAgent);
        this.payload = (builder.payload);
        this.payloadSize = (builder.payloadSize);
        this.response = (builder.response);
        this.statusCode = (builder.statusCode);
        this.responseSize = (builder.responseSize);
        this.timestamp = (builder.timestamp);
        this.events = builder.events;
    }

    @Override
//	@CardAttr
    public Integer getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    @Override
//	@CardAttr
    public String getSessionId() {
        return sessionId;
    }

    @Override
//	@CardAttr
    public String getUser() {
        return user;
    }

    @Override
//	@CardAttr
    public String getRequestId() {
        return requestId;
    }

    @Override
//	@CardAttr
    public String getTrackingId() {
        return trackingId;
    }

    @Override
    @CardAttr
    public String getActionId() {
        return actionId;
    }

    @Override
//	@CardAttr
    public String getPath() {
        return path;
    }

    @Override
	@CardAttr
    public MyEnum getMethod() {
        return method;
    }

    @Override
//	@CardAttr
    public String getQuery() {
        return query;
    }

    @Override
//	@CardAttr
    public String getSoapActionOrMethod() {
        return soapActionOrMethod;
    }

    @Override
//	@CardAttr
    public boolean isSoap() {
        return isSoap;
    }

    @Override
//	@CardAttr
    public String getClient() {
        return client;
    }

    @Override
//	@CardAttr
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    @CardAttr("Payla")
    public String getPayload() {
        return payload;
    }

    @Override
    @CardAttr
    public String getResponse() {
        return response;
    }

    @Override
    @CardAttr
    public Integer getPayloadSize() {
        return payloadSize;
    }

    @Override
//	@CardAttr
    public Integer getResponseSize() {
        return responseSize;
    }

    @Override
//	@CardAttr
    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
//	@CardAttr
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    @CardAttr("Bean")
    @JsonBean(JsonBeanImpl.class)
    public JsonBeanInterface getErrorOrWarningEvents() {
        return events;
    }

    @Override
    public String toString() {
        String str = "RequestDataImpl{" + "requestId=" + getRequestId() + ", path=" + method + " " + path;
        if (getElapsedTimeMillis() != null) {
            str += format(", elapsed=%.3fs", getElapsedTimeMillis() / 1000d);
        }
        return str + "}";
    }

    public static RequestDataBuilder builder() {
        return new RequestDataBuilder();
    }

    public static RequestDataBuilder copyOf(RequestData data) {
        return new RequestDataBuilder().
                withActionId(data.getActionId()).
                withClient(data.getClient()).
                withUserAgent(data.getUserAgent()).
                withElapsedTimeMillis(data.getElapsedTimeMillis()).
                withPath(data.getPath()).
                withMethod(data.getMethod()).
                withPayload(data.getPayload()).
                withQuery(data.getQuery()).
                withRequestId(data.getRequestId()).
                withTrackingId(data.getTrackingId()).
                withResponse(data.getResponse()).
                withSessionId(data.getSessionId()).
                withUser(data.getUser()).
                withStatusCode(data.getStatusCode()).
                withPayloadSize(data.getPayloadSize()).
                withResponseSize(data.getResponseSize()).
                withTimestamp(data.getTimestamp()).withErrorOrWarningEvents(data.getErrorOrWarningEvents())
                .withSoap(data.isSoap(), data.getSoapActionOrMethod());
    }

    public static class RequestDataBuilder implements Builder<SimpleRequestData, RequestDataBuilder> {

        private String client, userAgent, payload, response;
        private Integer statusCode, payloadSize, responseSize;
        private ZonedDateTime timestamp;
        private JsonBeanInterface events;
        private MyEnum method;
        protected String sessionId, requestId, trackingId, actionId, user, path, query, soapActionOrMethod;
        protected Integer elapsedTimeMillis;
        protected boolean isSoap = false;

        private RequestDataBuilder() {
        }

//		@Override
//		public <B extends Builder<SimpleRequestData>> B accept(Visitor<B> visitor) {
//			visitor.visit((B) this);
//			return Builder.super.accept(visitor);
//		}
//		@Override
//		public RequestDataBuilder accept(Visitor<RequestDataBuilder> visitor) {
//			visitor.visit(this);
//			return this;
//		}
        public RequestDataBuilder withSessionId(String sessionId) {
            this.sessionId = checkNotNull(sessionId);
            return this;
        }

        public RequestDataBuilder withUser(String user) {
            this.user = user;
            return this;
        }

        public RequestDataBuilder withRequestId(String requestId) {
            this.requestId = checkNotNull(requestId);
            return this;
        }

        public RequestDataBuilder withTrackingId(String trackingId) {
            this.trackingId = checkNotNull(trackingId);
            return this;
        }

        public RequestDataBuilder withActionId(String actionId) {
            this.actionId = checkNotNull(actionId);
            return this;
        }

        public RequestDataBuilder withPath(String path) {
            this.path = checkNotNull(path);
            return this;
        }

        public RequestDataBuilder withMethod(MyEnum method) {
            this.method = checkNotNull(method);
            return this;
        }

        public RequestDataBuilder withQuery(String query) {
            this.query = query;
            return this;
        }

        public RequestDataBuilder withSoap(@Nullable String soapActionOrMethod) {
            this.isSoap = true;
            this.soapActionOrMethod = soapActionOrMethod;
            return this;
        }

        public RequestDataBuilder withSoapActionOrMethod(String soapActionOrMethod) {
            this.isSoap = true;
            this.soapActionOrMethod = soapActionOrMethod;
            return this;
        }

        public RequestDataBuilder withSoap(boolean isSoap, @Nullable String soapActionOrMethod) {
            this.isSoap = isSoap;
            this.soapActionOrMethod = soapActionOrMethod;
            return this;
        }

        public RequestDataBuilder withElapsedTimeMillis(Integer elapsedTimeMillis) {
            this.elapsedTimeMillis = (elapsedTimeMillis);
            return this;
        }

        public RequestDataBuilder withClient(String client) {
            this.client = checkNotNull(client);
            return this;
        }

        public RequestDataBuilder withUserAgent(String userAgent) {
            this.userAgent = checkNotNull(userAgent);
            return this;
        }

        public RequestDataBuilder withPayload(String payload) {
            this.payload = (payload);
            return this;
        }

        public RequestDataBuilder withPayla(String payload) {
            fail();
            return this;
        }

        public RequestDataBuilder withResponse(String response) {
            this.response = (response);
            return this;
        }

        public RequestDataBuilder withStatusCode(Integer statusCode) {
            this.statusCode = (statusCode);
            return this;
        }

        public RequestDataBuilder withPayloadSize(Integer payloadSize) {
            this.payloadSize = checkNotNull(payloadSize);
            return this;
        }

        public RequestDataBuilder withResponseSize(Integer responseSize) {
            this.responseSize = (responseSize);
            return this;
        }

        public RequestDataBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = checkNotNull(timestamp);
            return this;
        }

        public RequestDataBuilder withErrorOrWarningEvents(JsonBeanInterface events) {
            this.events = events;
            return this;
        }

        @Override
        public SimpleRequestData build() {
            return new SimpleRequestData(this);
        }

    }
}
