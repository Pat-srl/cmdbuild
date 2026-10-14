/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.eventlog;

import java.time.ZonedDateTime;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.fault.FaultEventsData;
import org.cmdbuild.fault.FaultEvent;

public interface EventLogRecord extends EventLogInfo {

    final static String EVENT_LOG_ATTR_CARD = "Card";

    @Nullable
    Long getId();
    
    String getEventId();

    ZonedDateTime getTimestamp();

    @Nullable
    String getRequestId();

    @Nullable
    String getSessionId();

    @Nullable
    String getUsername();

    FaultEventsData getErrorsData();

    default List<FaultEvent> getErrors() {
        return getErrorsData().getData();
    }
}
