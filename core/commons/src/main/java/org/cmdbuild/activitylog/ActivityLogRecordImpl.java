/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.activitylog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Map;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ataboga
 */
@CardMapping("_ActivityLog")
public class ActivityLogRecordImpl implements ActivityLogRecord {

    private final Long id;
    private final ActivityLogAction code;
    private final String eventId, requestId, sessionId, sessionUser;
    private final Map<String, Object> metadata;
    private final ZonedDateTime timestamp;

    private ActivityLogRecordImpl(ActivityLogRecordImplBuilder builder) {
        this.id = builder.id;
        this.eventId = checkNotBlank(builder.eventId);
        this.code = checkNotNull(builder.code);
        this.timestamp = checkNotNull(builder.timestamp);
        this.requestId = builder.requestId;
        this.sessionId = builder.sessionId;
        this.sessionUser = builder.sessionUser;
        this.metadata = Map.copyOf(checkNotNull(builder.metadata));
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
    public String getCodeAsString() {
        return code.getActionName();
    }

    @JsonIgnore
    @Override
    public ActivityLogAction getCode() {
        return code;
    }

    @Override
    @CardAttr
    public ZonedDateTime getTimestamp() {
        return timestamp;
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
    @CardAttr("SessionUser")
    @Override
    public String getSessionUser() {
        return sessionUser;
    }

    @CardAttr
    @Override
    @JsonBean
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "ActivityLogRecordImpl{" + "id=" + id + ", code=" + code + '}';
    }

    public static ActivityLogRecordImplBuilder builder() {
        return new ActivityLogRecordImplBuilder();
    }

    public static ActivityLogRecordImplBuilder copyOf(ActivityLogRecord source) {
        return new ActivityLogRecordImplBuilder()
                .withId(source.getId())
                .withEventId(source.getEventId())
                .withCode(source.getCode())
                .withRequestId(source.getRequestId())
                .withSessionId(source.getSessionId())
                .withSessionUser(source.getSessionUser())
                .withMetadata(source.getMetadata())
                .withTimestamp(source.getTimestamp());
    }

    public static class ActivityLogRecordImplBuilder implements Builder<ActivityLogRecordImpl, ActivityLogRecordImplBuilder> {

        private Long id;
        private ActivityLogAction code;
        private String eventId, requestId, sessionId, sessionUser;
        private Map<String, Object> metadata;
        private ZonedDateTime timestamp;

        public ActivityLogRecordImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ActivityLogRecordImplBuilder withCode(ActivityLogAction code) {
            this.code = code;
            return this;
        }

        public ActivityLogRecordImplBuilder withCodeAsString(String code) {
            this.code = parseEnum(code.replaceFirst("action_", ""), ActivityLogAction.class);
            return this;
        }

        public ActivityLogRecordImplBuilder withEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public ActivityLogRecordImplBuilder withRequestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public ActivityLogRecordImplBuilder withSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public ActivityLogRecordImplBuilder withSessionUser(String sessionUser) {
            this.sessionUser = sessionUser;
            return this;
        }

        public ActivityLogRecordImplBuilder withMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public ActivityLogRecordImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        @Override
        public ActivityLogRecordImpl build() {
            return new ActivityLogRecordImpl(this);
        }

    }
}
