/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.notification.mobileapp.beans;

import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import org.cmdbuild.notification.NotificationStatus;
import org.cmdbuild.utils.date.CmDateUtils;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class MobileAppNotificationDataImpl implements MobileAppNotificationData {

    private final String topics, subject, content, contentType;
    private final String authInfo;
    private final String notificationProvider;

    private final NotificationStatus status;
    private final ZonedDateTime sentDate, beginDate;
    private final int errorCount;
    private final Map<String, String> meta;

    private MobileAppNotificationDataImpl(MobileAppNotificationDataImplBuilder builder) {
        this.topics = trimToEmpty(builder.topics);
        this.subject = nullToEmpty(builder.subject);
        this.content = nullToEmpty(builder.content);
        this.contentType = builder.contentType;

        this.authInfo = builder.authInfo;
        this.notificationProvider = builder.notificationProvider;


        this.status = firstNotNull(builder.status, NotificationStatus.NS_OUTGOING);
        this.errorCount = firstNotNull(builder.errorCount, 0);
        this.beginDate = firstNotNull(builder.beginDate, CmDateUtils.now());
        switch (status) {
            case NS_SENT ->
                this.sentDate = firstNotNull(builder.sentDate, beginDate);
            default ->
                this.sentDate = null;
        }

        this.meta = map(builder.meta).immutable();
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public String getTo() {
        return topics;
    }

    @Nullable
    @Override
    public String getSubject() {
        return subject;
    }

    @Nullable
    @Override
    public String getContent() {
        return content;
    }

    @Nullable
    @Override
    public String getContentType() {
        return contentType;
    }

    @Nullable
    @Override
    public String getAuthInfo() {
        return authInfo;
    }

    @Override
    public NotificationStatus getStatus() {
        return status;
    }

    @Override
    public String getStatusAsString() {
        return serializeNotificationStatus(getStatus());
    }

    @Nullable
    @Override
    public ZonedDateTime getSentDate() {
        return sentDate;
    }

    @Nullable
    @Override
    public ZonedDateTime getBeginDate() {
        return beginDate;
    }

    @Override
    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public String getNotificationProvider() {
        return notificationProvider;
    }

    @Override
    public Map<String, String> getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return format("MobileAppNotification{subject=<%s>}", abbreviate(subject, 40));
    }

    public static MobileAppNotificationDataImplBuilder builder() {
        return new MobileAppNotificationDataImplBuilder();
    }

    public static MobileAppNotificationDataImplBuilder copyOf(MobileAppNotificationData source) {
        return new MobileAppNotificationDataImplBuilder()
                .withTopics(source.getTo())
                .withSubject(source.getSubject())
                .withContent(source.getContent())
                .withContentType(source.getContentType())
                .withAuthInfo(source.getAuthInfo())
                .withSentDate(source.getSentDate())
                .withBeginDate(source.getBeginDate())
                .withStatus(source.getStatus())
                .withErrorCount(source.getErrorCount())
                .withNotificationProvider(source.getNotificationProvider())
                .withMeta(source.getMeta());
    }

    public static class MobileAppNotificationDataImplBuilder implements Builder<MobileAppNotificationDataImpl, MobileAppNotificationDataImplBuilder> {

        private String topics, subject, content, contentType;
        private String notificationProvider = MobileAppNotificationHelper.NOTIFICATION_PROVIDER_MOBILE_APP;
        private String authInfo;

        private NotificationStatus status;
        private ZonedDateTime sentDate, beginDate;
        private Integer errorCount;
        private final Map<String, String> meta = map();

        public MobileAppNotificationDataImplBuilder withMeta(Map<String, String> meta) {
            this.meta.putAll(firstNotNull(meta, emptyMap()));
            return this;
        }

        public MobileAppNotificationDataImplBuilder withNotificationProvider(String notificationProvider) {
            this.notificationProvider = notificationProvider;
            return this;
        }

        public MobileAppNotificationDataImplBuilder withTopics(String topics) {
            this.topics = topics;
            return this;
        }

        public MobileAppNotificationDataImplBuilder withTopics(List<String> topicIds) {
            this.topics = MobileAppNotificationData.topicListToString(topicIds);
            return this;
        }
        public MobileAppNotificationDataImplBuilder addTopic(String topicId) {
            return this.withTopics(list(MobileAppNotificationData.parseTopicListAsStrings(topics)).with(topicId));
        }

        public MobileAppNotificationDataImplBuilder addTopic(List<String> topics) {
            topics.forEach(this::addTopic);
            return this;
        }

        public MobileAppNotificationDataImplBuilder withAuthInfo(String authInfo) {
            this.authInfo = authInfo;
            return this;
        }

        public MobileAppNotificationDataImplBuilder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public MobileAppNotificationDataImplBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public MobileAppNotificationDataImplBuilder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public MobileAppNotificationDataImplBuilder withBeginDate(ZonedDateTime date) {
            this.beginDate = date;
            return this;
        }

        public MobileAppNotificationDataImplBuilder withSentDate(ZonedDateTime date) {
            this.sentDate = date;
            return this;
        }

        public MobileAppNotificationDataImplBuilder withStatus(NotificationStatus status) {
            this.status = status;
            return this;
        }

        public MobileAppNotificationDataImplBuilder withErrorCount(Integer errorCount) {
            this.errorCount = errorCount;
            return this;
        }

        public MobileAppNotificationDataImplBuilder withStatusAsString(String status) {
            return this.withStatus(MobileAppNotificationData.parseNotificationStatus(status));
        }

        @Override
        public MobileAppNotificationDataImpl build() {
            return new MobileAppNotificationDataImpl(this);
        }

    }
}
