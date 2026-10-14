/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.message;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class WaterwayMessageHistoryRecordImpl implements WaterwayMessageHistoryRecord {

    private final ZonedDateTime timestamp;
    private final WaterwayMessageStatus status;
    private final int transactionId;
    private final String messageId;
    private final String queueKey, storageKey, nodeId;

    private WaterwayMessageHistoryRecordImpl(WaterwayMessageHistoryRecordImplBuilder builder) {
        this.timestamp = checkNotNull(builder.timestamp);
        this.status = checkNotNull(builder.status);
        this.transactionId = builder.transactionId;
        this.messageId = checkNotBlank(builder.messageId);
        this.queueKey = checkNotBlank(builder.queueKey);
        this.storageKey = checkNotBlank(builder.storageKey);
        this.nodeId = checkNotBlank(builder.nodeId);
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
    public int getTransactionId() {
        return transactionId;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getQueueKey() {
        return queueKey;
    }

    @Override
    public String getStorageKey() {
        return storageKey;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return "WaterwayMessageHistoryRecord{" + "timestamp=" + timestamp + ", status=" + status + ", messageId=" + messageId + "/" + transactionId + ", queue=" + queueKey + '}';
    }

    public static WaterwayMessageHistoryRecordImplBuilder builder() {
        return new WaterwayMessageHistoryRecordImplBuilder();
    }

    public static WaterwayMessageHistoryRecordImplBuilder copyOf(WaterwayMessageHistoryRecord source) {
        return new WaterwayMessageHistoryRecordImplBuilder()
                .withTimestamp(source.getTimestamp())
                .withStatus(source.getStatus())
                .withTransactionId(source.getTransactionId())
                .withMessageId(source.getMessageId())
                .withQueueKey(source.getQueueKey())
                .withStorageKey(source.getStorageKey())
                .withNodeId(source.getNodeId());
    }

    public static class WaterwayMessageHistoryRecordImplBuilder implements Builder<WaterwayMessageHistoryRecordImpl, WaterwayMessageHistoryRecordImplBuilder> {

        private ZonedDateTime timestamp;
        private WaterwayMessageStatus status;
        private Integer transactionId;
        private String messageId;
        private String queueKey;
        private String storageKey;
        private String nodeId;

        public WaterwayMessageHistoryRecordImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public WaterwayMessageHistoryRecordImplBuilder withStatus(WaterwayMessageStatus status) {
            this.status = status;
            return this;
        }

        public WaterwayMessageHistoryRecordImplBuilder withTransactionId(Integer transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public WaterwayMessageHistoryRecordImplBuilder withMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public WaterwayMessageHistoryRecordImplBuilder withQueueKey(String queueKey) {
            this.queueKey = queueKey;
            return this;
        }

        public WaterwayMessageHistoryRecordImplBuilder withStorageKey(String storageKey) {
            this.storageKey = storageKey;
            return this;
        }

        public WaterwayMessageHistoryRecordImplBuilder withNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        @Override
        public WaterwayMessageHistoryRecordImpl build() {
            return new WaterwayMessageHistoryRecordImpl(this);
        }

    }
}
