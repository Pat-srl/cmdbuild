package org.cmdbuild.etl.waterway.message;

import jakarta.annotation.Nullable;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.buildMessageKey;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public interface MessageReference {

    final String MESSAGE_SKIP_STORAGE = "wy_skip_storage";

    @Nullable
    String getStorage();

    String getMessageId();

    @Nullable
    Integer getTransactionId();

    default boolean hasTransactionId() {
        return getTransactionId() != null;
    }

    default boolean hasStorage() {
        return isNotBlank(getStorage()) && !getStorage().equals(MESSAGE_SKIP_STORAGE);
    }

    default String getMessageKey() {
        return buildMessageKey(getMessageId(), getTransactionId());
    }

}
