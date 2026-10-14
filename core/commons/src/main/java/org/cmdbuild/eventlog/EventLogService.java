/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.eventlog;

import java.time.ZonedDateTime;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface EventLogService extends EventLogCollectorService {

    EventLogRecord store(EventLogRecord event);

    EventLogRecord store(String code, @Nullable Long cardId, Map<String, Object> data);

    boolean hasEvent(String code, long cardId);

    List<EventLogRecord> getEvents(String code, ZonedDateTime since);

    default EventLogRecord store(String code, @Nullable Long cardId, Object... data) {
        return store(code, cardId, map(data));
    }

    default EventLogRecord store(String code, long cardId) {
        return store(code, (Long) cardId, emptyMap());
    }

    default EventLogRecord store(String code) {
        return store(code, null);
    }

    default EventLogRecord store(String code, Map<String, Object> data) {
        return store(code, null, data);
    }

}
