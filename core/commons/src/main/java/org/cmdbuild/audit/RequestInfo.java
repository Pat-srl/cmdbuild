/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface RequestInfo {

    static final String NO_ACTION_ID = "NO_ACTION_ID",
            NO_SESSION_ID = "NO_SESSION_ID",
            NO_USER_AGENT = "NO_USER_AGENT",
            PAYLOAD_TRACKING_DISABLED = "PAYLOAD_TRACKING_DISABLED",
            RESPONSE_TRACKING_DISABLED = "RESPONSE_TRACKING_DISABLED",
            NO_SESSION_USER = "NO_SESSION_USER";

    String getSessionId();

    ZonedDateTime getTimestamp();

    String getUser();

    String getNodeId();

    String getRequestId();

    String getActionId();

    String getPath();

    String getMethod();

    @Nullable
    String getQuery();

    @Nullable
    String getSoapActionOrMethod();

    @Nullable
    Integer getElapsedTimeMillis();

    @Nullable
    Integer getStatusCode();

    boolean isSoap();

    default boolean hasMethod(String method) {
        return checkNotBlank(method).equalsIgnoreCase(getMethod());
    }

    default String getPathWithQuery() {
        return getPath() + getQueryPartOrEmpty();
    }

    default String getQueryPartOrEmpty() {
        if (isBlank(getQuery())) {
            return "";
        } else {
            return "?" + getQuery();
        }
    }

    default boolean isCompleted() {
        return getElapsedTimeMillis() != null;
    }

    default boolean hasError() {
        Integer statusCode = getStatusCode();
        return statusCode != null && !statusCode.toString().matches("[123]..");
    }
}
