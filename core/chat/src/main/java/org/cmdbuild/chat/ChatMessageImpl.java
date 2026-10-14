/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.chat;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.time.ZonedDateTime;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

@CardMapping("_ChatMessage")
public class ChatMessageImpl implements ChatMessage {

    private final Long id;
    private final String target, subject, content, thread, sourceName, sourceDescription, messageId;
    private final ZonedDateTime timestamp;
    private final Map<String, String> meta;
    private final ChatMessageSourceType sourceType;
    private final ChatMessageStatus status;
    private final ChatMessageType type;

    private ChatMessageImpl(ChatMessageImplBuilder builder) {
        this.id = builder.id;
        this.target = checkNotBlank(builder.target);
        this.subject = nullToEmpty(builder.subject);
        this.content = nullToEmpty(builder.content);
        this.thread = builder.thread;
        this.sourceName = checkNotBlank(builder.sourceName);
        this.sourceDescription = firstNotBlank(builder.sourceDescription, sourceName);
        this.messageId = firstNotBlank(builder.messageId, randomId());
        this.timestamp = firstNotNull(builder.timestamp, now());
        this.meta = map(builder.meta).immutable();
        this.sourceType = checkNotNull(builder.sourceType);
        this.status = checkNotNull(builder.status);
        this.type = checkNotNull(builder.type);
    }

    @CardAttr(CHAT_MESSAGE_ATTR_STATUS)
    @Override
    public ChatMessageStatus getStatus() {
        return status;
    }

    @CardAttr(CHAT_MESSAGE_ATTR_TYPE)
    @Override
    public ChatMessageType getType() {
        return type;
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

    @CardAttr(CHAT_MESSAGE_ATTR_TARGET)
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

    @CardAttr(CHAT_MESSAGE_ATTR_THREAD)
    @Override
    public String getThread() {
        return thread;
    }

    @CardAttr(CHAT_MESSAGE_ATTR_SOURCE_NAME)
    @Override
    public String getSourceName() {
        return sourceName;
    }

    @CardAttr
    @Override
    public String getSourceDescription() {
        return sourceDescription;
    }

    @CardAttr(CHAT_MESSAGE_ATTR_TIMESTAMP)
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

    @CardAttr(CHAT_MESSAGE_ATTR_SOURCE_TYPE)
    @Override
    public ChatMessageSourceType getSourceType() {
        return sourceType;
    }

    @Override
    public String toString() {
        return "ChatMessage{" + "id=" + id + ", target=" + target + ", subject=" + subject + ", sourceName=" + sourceName + ", messageId=" + messageId + ", timestamp=" + timestamp + ", type=" + type + ", status=" + status + '}';
    }

    public static ChatMessageImplBuilder builder() {
        return new ChatMessageImplBuilder();
    }

    public static ChatMessageImplBuilder copyOf(ChatMessageData source) {
        return new ChatMessageImplBuilder()
                .withTarget(source.getTarget())
                .withSubject(source.getSubject())
                .withContent(source.getContent())
                .withThread(source.getThread())
                .withMeta(source.getMeta());
    }

    public static ChatMessageImplBuilder copyOf(ChatMessage source) {
        return copyOf((ChatMessageData) source)
                .withId(source.getId())
                .withSourceName(source.getSourceName())
                .withSourceDescription(source.getSourceDescription())
                .withMessageId(source.getMessageId())
                .withTimestamp(source.getTimestamp())
                .withSourceType(source.getSourceType())
                .withType(source.getType())
                .withStatus(source.getStatus());
    }

    public static class ChatMessageImplBuilder implements Builder<ChatMessageImpl, ChatMessageImplBuilder> {

        private Long id;
        private String target;
        private String subject;
        private String content;
        private String thread;
        private String sourceName;
        private String sourceDescription;
        private String messageId;
        private ZonedDateTime timestamp;
        private final Map<String, String> meta = map();
        private ChatMessageSourceType sourceType;
        private ChatMessageStatus status;
        private ChatMessageType type;

        public ChatMessageImplBuilder withType(ChatMessageType type) {
            this.type = type;
            return this;
        }

        public ChatMessageImplBuilder withStatus(ChatMessageStatus status) {
            this.status = status;
            return this;
        }

        public ChatMessageImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ChatMessageImplBuilder withTarget(String target) {
            this.target = target;
            return this;
        }

        public ChatMessageImplBuilder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public ChatMessageImplBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public ChatMessageImplBuilder withThread(String thread) {
            this.thread = thread;
            return this;
        }

        public ChatMessageImplBuilder withSourceName(String sourceName) {
            this.sourceName = sourceName;
            return this;
        }

        public ChatMessageImplBuilder withSourceDescription(String sourceDescription) {
            this.sourceDescription = sourceDescription;
            return this;
        }

        public ChatMessageImplBuilder withMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public ChatMessageImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ChatMessageImplBuilder withMeta(String key, String value) {
            this.meta.put(key, value);
            return this;
        }

        public ChatMessageImplBuilder withMeta(Map<String, String> meta) {
            this.meta.putAll(meta);
            return this;
        }

        public ChatMessageImplBuilder withSourceType(ChatMessageSourceType sourceType) {
            this.sourceType = sourceType;
            return this;
        }

        @Override
        public ChatMessageImpl build() {
            return new ChatMessageImpl(this);
        }

    }
}
