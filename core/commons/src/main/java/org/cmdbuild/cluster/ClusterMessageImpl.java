package org.cmdbuild.cluster;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.copyOf;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.builder.Builder;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

public class ClusterMessageImpl implements ClusterMessage {

    private final String sourceInstanceId, messageType, messageId, targetNodeId;
    private final Map<String, Object> messageData;
    private final ZonedDateTime timestamp;

    private ClusterMessageImpl(String sourceInstanceId, @Nullable String targetNodeId, String messageType, String messageId, Map<String, Object> messageData, ZonedDateTime timestamp) {
        this.sourceInstanceId = checkNotBlank(sourceInstanceId);
        this.messageType = checkNotBlank(messageType);
        this.messageId = checkNotBlank(messageId);
        this.messageData = firstNotNull(messageData, emptyMap());
        this.timestamp = checkNotNull(timestamp);
        this.targetNodeId = targetNodeId;
    }

    @Override
    public String getSourceInstanceId() {
        return sourceInstanceId;
    }

    @Override
    public String getMessageType() {
        return messageType;
    }

    @Override
    public Map<String, Object> getMessageData() {
        return messageData;
    }

    @Override
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Nullable
    @Override
    public String getTargetNodeId() {
        return targetNodeId;
    }

    @Override
    public String toString() {
        return "ClusterMessage{" + "source=" + sourceInstanceId + ", messageType=" + messageType + ", messageId=" + messageId + ", messageData=" + messageData + ", timestamp=" + timestamp + '}';
    }

    public static ClusterMessageBuilder builder() {
        return new ClusterMessageBuilder();
    }

    public static class ClusterMessageBuilder implements Builder<ClusterMessage> {

        private String sourceInstanceId = THIS_INSTANCE_ID, messageType, targetNodeId, messageId = randomId();
        private Map<String, Object> messageData;
        private ZonedDateTime timestamp = now();

        public ClusterMessageBuilder withSourceInstanceId(String sourceInstanceId) {
            this.sourceInstanceId = checkNotNull(sourceInstanceId);
            return this;
        }

        public ClusterMessageBuilder withTargetNodeId(String targetNodeId) {
            this.targetNodeId = checkNotNull(targetNodeId);
            return this;
        }

        public ClusterMessageBuilder withMessageType(String messageType) {
            this.messageType = checkNotNull(messageType);
            return this;
        }

        public ClusterMessageBuilder withMessageId(String messageId) {
            this.messageId = checkNotNull(messageId);
            return this;
        }

        public ClusterMessageBuilder withMessageData(Map<String, Object> messageData) {
            this.messageData = copyOf(checkNotNull(messageData));
            return this;
        }

        public ClusterMessageBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = checkNotNull(timestamp);
            return this;
        }

        @Override
        public ClusterMessage build() {
            return new ClusterMessageImpl(sourceInstanceId, targetNodeId, messageType, messageId, messageData, timestamp);
        }
    }
}
