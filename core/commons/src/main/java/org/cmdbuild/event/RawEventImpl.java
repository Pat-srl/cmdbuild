/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.event;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

public class RawEventImpl implements RawEvent {

    private final EventDirection direction;
    private final String sessionId, messageId, eventCode, clientId;
    private final Map<String, Object> payload;
    private final ZonedDateTime timestamp;

    private RawEventImpl(EventImplBuilder builder) {
        this.direction = checkNotNull(builder.direction);
        this.sessionId = emptyToNull(builder.sessionId);
        this.clientId = emptyToNull(builder.clientId);
        this.messageId = firstNotBlank(builder.messageId, randomId());
        this.eventCode = checkNotBlank(builder.eventCode);
        this.payload = map(firstNotNull(builder.payload, emptyMap())).immutable();
        this.timestamp = firstNotNull(builder.timestamp, now());
    }

    @Override
    public EventDirection getDirection() {
        return direction;
    }

    @Override
    @Nullable
    public String getSessionIdOrNull() {
        return sessionId;
    }

    @Override
    @Nullable
    public String getClientIdOrNull() {
        return clientId;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getEventCode() {
        return eventCode;
    }

    @Override
    public Map<String, Object> getPayload() {
        return payload;
    }

    @Override
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "EventImpl{" + "direction=" + direction + ", sessionId=" + sessionId + ", messageId=" + messageId + ", eventCode=" + eventCode + '}';
    }

    public static EventImplBuilder builder() {
        return new EventImplBuilder();
    }

    public static EventImplBuilder copyOf(RawEventImpl source) {
        return new EventImplBuilder()
                .withDirection(source.getDirection())
                .withSessionId(source.getSessionIdOrNull())
                .withClientId(source.getClientIdOrNull())
                .withMessageId(source.getMessageId())
                .withEventCode(source.getEventCode())
                .withPayload(source.getPayload())
                .withTimestamp(source.getTimestamp());
    }

    public static class EventImplBuilder implements Builder<RawEventImpl, EventImplBuilder> {

        private EventDirection direction;
        private String sessionId, clientId;
        private String messageId;
        private String eventCode;
        private Map<String, Object> payload;
        private ZonedDateTime timestamp;

        public EventImplBuilder withDirection(EventDirection direction) {
            this.direction = direction;
            return this;
        }

        public EventImplBuilder withSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public EventImplBuilder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public EventImplBuilder withMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public EventImplBuilder withEventCode(String eventCode) {
            this.eventCode = eventCode;
            return this;
        }

        public EventImplBuilder withPayload(Object... payload) {
            return this.withPayload(map(payload));
        }

        public EventImplBuilder withPayload(Map<String, Object> payload) {
            this.payload = payload;
            return this;
        }

        public EventImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        @Override
        public RawEventImpl build() {
            return new RawEventImpl(this);
        }

    }
}
