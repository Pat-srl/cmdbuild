/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.eventlog;

import org.cmdbuild.dao.orm.annotations.CardMapping;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.fault.FaultEventsData;
import static org.cmdbuild.fault.FaultEventsData.emptyErrorMessagesData;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.json.JsonBean;

@CardMapping("_EventLog")
public class EventLogRecordImpl implements EventLogRecord {

    private final Long id, card;
    private final String code, requestId, sessionId, username, eventId;
    private final Map<String, Object> data;
    private final FaultEventsData errors;
    private final ZonedDateTime timestamp;

    private EventLogRecordImpl(EventLogRecordImplBuilder builder) {
        this.id = builder.id;
        this.card = builder.card;
        this.eventId = checkNotBlank(builder.eventId);
        this.code = checkNotBlank(builder.code);
        this.requestId = builder.requestId;
        this.sessionId = builder.sessionId;
        this.username = builder.username;
        this.data = map(checkNotNull(builder.data)).immutable();
        this.errors = firstNonNull(builder.errors, emptyErrorMessagesData());
        this.timestamp = checkNotNull(builder.timestamp);
    }

    @Nullable
    @CardAttr(ATTR_ID)
    @Override
    public Long getId() {
        return id;
    }

    @CardAttr
    @Override
    public String getEventId() {
        return eventId;
    }

    @CardAttr(ATTR_CODE)
    @Override
    public String getCode() {
        return code;
    }

    @Nullable
    @CardAttr(EVENT_LOG_ATTR_CARD)
    @Override
    public Long getCard() {
        return card;
    }

    @Nullable
    @CardAttr
    @Override
    public String getRequestId() {
        return requestId;
    }

    @Nullable
    @CardAttr
    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Nullable
    @CardAttr("SessionUsername")
    @Override
    public String getUsername() {
        return username;
    }

    @CardAttr
    @Override
    @JsonBean
    public Map<String, Object> getData() {
        return data;
    }

    @CardAttr("Errors")
    @Override
    public FaultEventsData getErrorsData() {
        return errors;
    }

    @Override
    @CardAttr
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "EventLogRecordImpl{" + "id=" + id + ", code=" + code + '}';
    }

    public static EventLogRecordImplBuilder builder() {
        return new EventLogRecordImplBuilder();
    }

    public static EventLogRecordImplBuilder copyOf(EventLogRecord source) {
        return new EventLogRecordImplBuilder()
                .withId(source.getId())
                .withEventId(source.getEventId())
                .withCard(source.getCard())
                .withCode(source.getCode())
                .withRequestId(source.getRequestId())
                .withSessionId(source.getSessionId())
                .withUsername(source.getUsername())
                .withData(source.getData())
                .withTimestamp(source.getTimestamp())
                .withErrorsData(source.getErrorsData());
    }

    public static class EventLogRecordImplBuilder implements Builder<EventLogRecordImpl, EventLogRecordImplBuilder> {

        private ZonedDateTime timestamp;
        private Long id;
        private Long card;
        private String code, eventId;
        private String requestId;
        private String sessionId;
        private String username;
        private Map<String, Object> data;
        private FaultEventsData errors;

        public EventLogRecordImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EventLogRecordImplBuilder withCard(Long card) {
            this.card = card;
            return this;
        }

        public EventLogRecordImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public EventLogRecordImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public EventLogRecordImplBuilder withEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public EventLogRecordImplBuilder withRequestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public EventLogRecordImplBuilder withSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public EventLogRecordImplBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public EventLogRecordImplBuilder withData(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public EventLogRecordImplBuilder withErrorsData(FaultEventsData errors) {
            this.errors = errors;
            return this;
        }

        @Override
        public EventLogRecordImpl build() {
            return new EventLogRecordImpl(this);
        }

    }
}
