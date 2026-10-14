/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.message;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import javax.annotation.Nullable;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.getItemCodeFromKeyOrCode;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.isItemKey;
import org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.hasMessageParent;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.parseHistoryRecords;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface WaterwayMessage extends WaterwayMessageData, WaterwayMessageHistoryRecord {

    final String MESSAGE_SKIP_QUEUE = "wy_skip_queue", MESSAGE_SESSION_ID = "sessionId", MESSAGE_CONTEXT_ID = "contextId", MESSAGE_REQUEST_ID = "requestId";

    String getQueue();

    @Nullable
    String getStorage();

    List<String> getHistory();

    default List<WaterwayMessageHistoryRecord> getHistoryRecords() {
        return parseHistoryRecords(getHistory());
    }

    @Override
    default String getQueueCode() {
        return getItemCodeFromKeyOrCode(getQueue());
    }

    @Nullable
    @Override
    default String getStorageCode() {
        return hasStorage() ? getItemCodeFromKeyOrCode(getStorage()) : null;
    }

    @Override
    @Nullable
    default String getQueueKey() {
        return isItemKey(getQueue()) ? getQueue() : null;
    }

    @Override
    @Nullable
    default String getStorageKey() {
        return isItemKey(getStorage()) ? getStorage() : null;
    }

    default boolean hasStorage() {
        return isNotBlank(getStorage());
    }

    default boolean hasStatus(WaterwayMessageStatus... status) {
        return set(status).contains(getStatus());
    }

    default boolean hasParent() {
        return hasMessageParent(getMessageId());
    }

    default String getParentMessageId() {
        return WaterwayMessageUtils.getMessageParent(getMessageId());
    }

    default void checkHasStatus(WaterwayMessageStatus... status) {
        checkArgument(hasStatus(status), "invalid message status, expected = %s but found = %s", status, getStatus());
    }

    default void checkHasQueueKey(String key) {
        checkArgument(equal(checkNotBlank(key), getQueueKey()), "message queue key mismatch: expected key =< %s > but found key =< %s >", key, getQueueKey());
    }

    default void checkHasQueueCode(String code) {
        checkArgument(equal(checkNotBlank(code), getQueueCode()), "message queue code mismatch: expected code =< %s > but found code =< %s >", code, getQueueCode());
    }

    default void checkHasStorageKey(String key) {
        checkArgument(equal(checkNotBlank(key), getStorageKey()), "message storage key mismatch: expected key =< %s > but found key =< %s >", key, getStorageKey());
    }

}
