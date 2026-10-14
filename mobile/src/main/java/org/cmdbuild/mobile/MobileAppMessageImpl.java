/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.mobile;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.time.ZonedDateTime;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessage;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageSourceType;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationDataImpl.MobileAppNotificationDataImplBuilder;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

@CardMapping("_MobileAppMessage")
public class MobileAppMessageImpl implements MobileAppMessage {

    private final Long id;
    private final String target, subject, content, sourceName, sourceDescription, messageId;
    private final ZonedDateTime timestamp;
    private final Map<String, String> meta;
    private final MobileAppMessageSourceType sourceType;
    private final MobileAppMessageStatus status;

    private MobileAppMessageImpl(MobileAppMessageImplBuilder builder) {
        this.id = builder.id;
        this.target = checkNotBlank(builder.target);
        this.subject = nullToEmpty(builder.subject);
        this.content = nullToEmpty(builder.content);
        this.sourceName = checkNotBlank(builder.sourceName);
        this.sourceDescription = firstNotBlank(builder.sourceDescription, sourceName);
        this.messageId = firstNotBlank(builder.messageId, randomId());
        this.timestamp = firstNotNull(builder.timestamp, now());
        this.meta = map(builder.meta).immutable();
        this.sourceType = checkNotNull(builder.sourceType);
        this.status = checkNotNull(builder.status);
    }

    @CardAttr(MOBILE_APP_MESSAGE_ATTR_STATUS)
    @Override
    public MobileAppMessageStatus getStatus() {
        return status;
    }

    @CardAttr(ATTR_ID)
    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    @CardAttr(ATTR_CODE)
    @Override
    public String getMessageId() {
        return messageId;
    }

    @CardAttr(MOBILE_APP_MESSAGE_ATTR_TARGET)
    @Override
    public String getTarget() {
        return target;
    }

    @CardAttr
    @Override
    public String getSubject() {
        return subject;
    }

    @CardAttr
    @Override
    public String getContent() {
        return content;
    }

    @CardAttr(MOBILE_APP_MESSAGE_ATTR_SOURCE_NAME)
    @Override
    public String getSourceName() {
        return sourceName;
    }

    @CardAttr
    @Override
    public String getSourceDescription() {
        return sourceDescription;
    }

    @CardAttr(MOBILE_APP_MESSAGE_ATTR_TIMESTAMP)
    @Override
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @CardAttr
    @JsonBean
    @Override
    public Map<String, String> getMeta() {
        return meta;
    }

    @CardAttr(MOBILE_APP_MESSAGE_ATTR_SOURCE_TYPE)
    @Override
    public MobileAppMessageSourceType getSourceType() {
        return sourceType;
    }

    @Override
    public String toString() {
        return "MobileAppMessage{" + "id=" + id + ", target=" + target + ", subject=" + subject + ", sourceName=" + sourceName + ", messageId=" + messageId + ", timestamp=" + timestamp + ", status=" + status + '}';
    }

    public static MobileAppMessageImplBuilder builder() {
        return new MobileAppMessageImplBuilder();
    }

    /**
     * Note: status is not valorized, because only {@link MobileAppMessage} has
     * status attribute.
     *
     * @param source
     * @return
     */
    public static MobileAppMessageImplBuilder copyOf(MobileAppMessageData source) {
        final MobileAppMessageImplBuilder result = new MobileAppMessageImplBuilder()
                .withTarget(source.getTarget())
                .withSubject(source.getSubject())
                .withContent(source.getContent())
                .withMeta(source.getMeta());

        if (source instanceof MobileAppMessage mobileAppMessage) {
            result.withStatus(mobileAppMessage.getStatus());
        }

        return result;
    }

    /**
     * Additional info to be stored
     *
     * @param source
     * @return
     */
    public static MobileAppMessageImplBuilder copyOf(MobileAppMessage source) {
        return copyOf((MobileAppMessageData) source)
                .withId(source.getId())
                .withSourceType(source.getSourceType())
                .withSourceName(source.getSourceName())
                .withSourceDescription(source.getSourceDescription())
                .withMessageId(source.getMessageId())
                .withTimestamp(source.getTimestamp())
                .withStatus(source.getStatus());
    }

    /**
     * From message to notification (builder)
     *
     * @param source
     * @return initialized data <b>but not</b>
     * <ul>
     * <li>status
     * <li>contentType
     * </ul>
     */
    public static MobileAppNotificationDataImplBuilder copyNotificationDataOf(MobileAppMessage source) {
        return new MobileAppNotificationDataImplBuilder()
                .withTopics(source.getTarget())
                .withSubject(source.getSubject())
                .withContent(source.getContent())
                .withMeta(source.getMeta())
                .withStatus(source.getStatus().toNotificationStatus());
    }

    /**
     * From notification to message (builder)
     *
     * @param source
     * @return initialized data
     */
    public static MobileAppMessageImplBuilder copyMessageDataOf(MobileAppNotificationData source) {
        return new MobileAppMessageImplBuilder()
                .withTarget(source.getTo())
                .withSubject(source.getSubject())
                .withContent(source.getContent())
                .withMeta(source.getMeta())
                .withStatus(MobileAppMessageStatus.toMessageStatus(source.getStatus()));
    }

    public static class MobileAppMessageImplBuilder implements Builder<MobileAppMessageImpl, MobileAppMessageImplBuilder> {

        private Long id;
        private String target;
        private String subject;
        private String content;
        private String sourceName;
        private String sourceDescription;
        private String messageId;
        private ZonedDateTime timestamp;
        private final Map<String, String> meta = map();
        private MobileAppMessageSourceType sourceType;
        private MobileAppMessageStatus status;

        public MobileAppMessageImplBuilder withStatus(MobileAppMessageStatus status) {
            this.status = status;
            return this;
        }

        public MobileAppMessageImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public MobileAppMessageImplBuilder withTarget(String target) {
            this.target = target;
            return this;
        }

        public MobileAppMessageImplBuilder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public MobileAppMessageImplBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public MobileAppMessageImplBuilder withSourceName(String sourceName) {
            this.sourceName = sourceName;
            return this;
        }

        public MobileAppMessageImplBuilder withSourceDescription(String sourceDescription) {
            this.sourceDescription = sourceDescription;
            return this;
        }

        public MobileAppMessageImplBuilder withMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public MobileAppMessageImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public MobileAppMessageImplBuilder withMeta(String key, String value) {
            this.meta.put(key, value);
            return this;
        }

        public MobileAppMessageImplBuilder withMeta(Map<String, String> meta) {
            this.meta.putAll(meta);
            return this;
        }

        public MobileAppMessageImplBuilder withSourceType(MobileAppMessageSourceType sourceType) {
            this.sourceType = sourceType;
            return this;
        }

        @Override
        public MobileAppMessageImpl build() {
            return new MobileAppMessageImpl(this);
        }

    }
}
