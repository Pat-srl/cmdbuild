/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.message;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import java.time.ZonedDateTime;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import jakarta.annotation.Nullable;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class WaterwayMessageImpl implements WaterwayMessage {

    private final String messageId, storage, queue, nodeId;
    private final int transactionId;
    private final ZonedDateTime timestamp;
    private final WaterwayMessageStatus status;
    private final WaterwayMessageData data;
    private final List<String> history;

    private WaterwayMessageImpl(WaterwayMessageImplBuilder builder) {
        this.messageId = checkNotBlank(builder.messageId);
        this.storage = builder.storage;
        this.queue = checkNotBlank(builder.queue);
        this.nodeId = checkNotBlank(builder.nodeId);
        this.transactionId = firstNotNull(builder.transactionId, 0);
        this.timestamp = checkNotNull(builder.timestamp);
        this.status = checkNotNull(builder.status);
        this.history = ImmutableList.copyOf(firstNotNull(builder.history, emptyList()));
        this.data = builder.data.build();
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    @Nullable
    public String getStorage() {
        return storage;
    }

    @Override
    public String getQueue() {
        return queue;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public int getTransactionId() {
        return transactionId;
    }

    @Override
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public WaterwayMessageStatus getStatus() {
        return status;
    }

    @Override
    public Map<String, String> getMeta() {
        return data.getMeta();
    }

    @Override
    public Map<String, WaterwayMessageAttachment> getAttachmentMap() {
        return data.getAttachmentMap();
    }

    @Nullable
    @Override
    public String getLogs() {
        return data.getLogs();
    }

    @Override
    public List<FaultEvent> getErrors() {
        return data.getErrors();
    }

    @Override
    public List<String> getHistory() {
        return history;
    }

    @Override
    public List<FaultEvent> getFaultTolerantErrors() {
        return data.getFaultTolerantErrors();
    }

    @Override
    public String toString() {
        return "WaterwayMessage{" + "messageId=" + messageId + "-" + transactionId + ", queue=" + getQueue() + ", storage=" + getStorage() + ", status=" + serializeEnum(status) + ", attachments=" + Joiner.on(",").join(data.getAttachmentMap().keySet()) + '}';
    }

    public static WaterwayMessageImplBuilder builder() {
        return new WaterwayMessageImplBuilder();
    }

    public static WaterwayMessageImplBuilder copyOf(WaterwayMessageData source) {
        return new WaterwayMessageImplBuilder()
                .withData(source);
    }

    public static WaterwayMessageImplBuilder copyOf(WaterwayMessage source) {
        return new WaterwayMessageImplBuilder()
                .withData(source)
                .withMessageId(source.getMessageId())
                .withStorage(source.getStorage())
                .withQueue(source.getQueue())
                .withNodeId(source.getNodeId())
                .withTransactionId(source.getTransactionId())
                .withTimestamp(source.getTimestamp())
                .withStatus(source.getStatus())
                .withHistory(source.getHistory());
    }

    public static class WaterwayMessageImplBuilder implements Builder<WaterwayMessageImpl, WaterwayMessageImplBuilder> {

        private String messageId;
        private String storage;
        private String queue;
        private String nodeId;
        private Integer transactionId;
        private ZonedDateTime timestamp;
        private WaterwayMessageStatus status;
        private WaterwayMessageDataImpl.WaterwayMessageDataImplBuilder data = WaterwayMessageDataImpl.builder();
        private List<String> history;

        public WaterwayMessageImplBuilder withHistory(List<String> history) {
            this.history = history;
            return this;
        }

        public WaterwayMessageImplBuilder withMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public WaterwayMessageImplBuilder withData(WaterwayMessageData data) {
            this.data.withData(data);
            return this;
        }

        public WaterwayMessageImplBuilder clearAttachments() {
            this.data.clearAttachments();
            return this;
        }

        public WaterwayMessageImplBuilder clearMeta() {
            this.data.clearMeta();
            return this;
        }

        public WaterwayMessageImplBuilder withLogs(String logs) {
            this.data.withLogs(logs);
            return this;
        }

        public WaterwayMessageImplBuilder withErrors(List<FaultEvent> errors) {
            this.data.withErrors(errors);
            return this;
        }

        public WaterwayMessageImplBuilder withFaultTolerantErrors(List<FaultEvent> faultTolerantErrors) {
            this.data.withFaultTolerantErrors(faultTolerantErrors);
            return this;
        }

        public WaterwayMessageImplBuilder withStorage(String storage) {
            this.storage = storage;
            return this;
        }

        public WaterwayMessageImplBuilder withQueue(String queue) {
            this.queue = queue;
            return this;
        }

        public WaterwayMessageImplBuilder withNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public WaterwayMessageImplBuilder withTransactionId(Integer transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public WaterwayMessageImplBuilder bumpTransactionId() {
            transactionId++;
            return this;
        }

        public WaterwayMessageImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public WaterwayMessageImplBuilder withStatus(WaterwayMessageStatus status) {
            this.status = status;
            return this;
        }

        public WaterwayMessageImplBuilder withMeta(Map<String, String> meta) {
            this.data.withMeta(meta);
            return this;
        }

        public WaterwayMessageImplBuilder withMeta(String key, String value) {
            this.data.withMeta(key, value);
            return this;
        }

        public WaterwayMessageImplBuilder withMeta(Object... items) {
            this.data.withMeta(items);
            return this;
        }

        public WaterwayMessageImplBuilder withAttachments(Collection<WaterwayMessageAttachment> attachments) {
            return withAttachments(map(attachments, WaterwayMessageAttachment::getName, identity()));
        }

        public WaterwayMessageImplBuilder withAttachments(Map<String, WaterwayMessageAttachment> attachments) {
            this.data.withAttachments(attachments);
            return this;
        }

        public WaterwayMessageImplBuilder withoutMeta(String... keys) {
            this.data.withoutMeta(keys);
            return this;
        }

        @Override
        public WaterwayMessageImpl build() {
            return new WaterwayMessageImpl(this);
        }

    }
}
