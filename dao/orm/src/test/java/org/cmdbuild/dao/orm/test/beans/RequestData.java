/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.test.beans;

import static com.google.common.base.Objects.equal;
import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * request data (for request tracking); classes implementing this interface are
 * supposed to be immutable
 */
public interface RequestData {

    static final String NO_ACTION_ID = "NO_ACTION_ID",
            NO_SESSION_ID = "NO_SESSION_ID",
            NO_USER_AGENT = "NO_USER_AGENT",
            PAYLOAD_TRACKING_DISABLED = "PAYLOAD_TRACKING_DISABLED",
            RESPONSE_TRACKING_DISABLED = "RESPONSE_TRACKING_DISABLED",
            NO_SESSION_USER = "NO_SESSION_USER";

    String getSessionId();

    String getUser();

    /**
     * return request id, as provided by client (this should be unique, but
     * since we cannot trust client, we have to handle the case where this is
     * duplicate or missing)
     *
     * @return request id
     */
    String getRequestId();

    /**
     * return tracking id: this is a request id generated on the server (thus
     * guaranteeed unique)
     *
     * @return tracking id
     */
    String getTrackingId();

    String getActionId();

    String getPath();

    MyEnum getMethod();

    String getQuery();

    @Nullable
    String getSoapActionOrMethod();

    @Nullable
    Integer getElapsedTimeMillis();

    boolean isSoap();

    default boolean isCompleted() {
        return getElapsedTimeMillis() != null;
    }

    String getClient();

    String getUserAgent();

    ZonedDateTime getTimestamp();

    JsonBeanInterface getErrorOrWarningEvents();

    @Nullable
    String getPayload();

    /**
     * return size of request payload
     *
     * @return
     */
    Integer getPayloadSize();

    @Nullable
    String getResponse();

    /**
     * return size of response payload
     *
     * @return
     */
    @Nullable
    Integer getResponseSize();

    @Nullable
    Integer getStatusCode();

//	default boolean isCompleted() {
//		return getPayload() != null && getResponse() != null && getStatusCode() != null && getElapsedTimeMillis() != null && getResponseSize() != null;
//	}
    default boolean hasSession() {
        return !equal(getSessionId(), NO_SESSION_ID) && !isBlank(getSessionId());
    }

    default boolean hasPayload() {
        return !isBlank(getPayload());
    }

}
