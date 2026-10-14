/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.activitylog;

import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;

/**
 *
 * @author ataboga
 */
public interface ActivityLogRecord extends ActivityLogInfo {

    @Nullable
    Long getId();

    String getEventId();

    ZonedDateTime getTimestamp();

    @Nullable
    String getRequestId();

    @Nullable
    String getSessionId();

    @Nullable
    String getSessionUser();
}
