/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.storage;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.parseItemKey;
import org.cmdbuild.etl.waterway.WaterwayMessagesStats;
import org.cmdbuild.etl.waterway.message.MessageReference;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.toMessageReference;

public interface WaterwayStorageHandler {

    final String WY_STORAGE_TYPE = "provider";

    String getKey();

    WaterwayMessage createMessage(WaterwayMessage message);

    WaterwayMessage updateMessage(WaterwayMessage message);

    void deleteMessage(String messageId);

    @Nullable
    WaterwayMessage getMessageOrNull(MessageReference messageReference);

    WaterwayMessageAttachment getMessageAttachmentLoadData(MessageReference messageReference, String attachmentName);

    WaterwayMessagesStats getMessagesStats();

    default WaterwayMessageAttachment getMessageAttachmentLoadData(String messageReference, String attachmentName) {
        return getMessageAttachmentLoadData(toMessageReference(messageReference), attachmentName);
    }

    default String getCode() {
        return parseItemKey(getKey()).getCode();
    }

    default void deleteMessage(WaterwayMessage message) {
        deleteMessage(message.getMessageId());
    }

    @Nullable
    default WaterwayMessage getMessageOrNull(String messageReference) {
        return getMessageOrNull(toMessageReference(messageReference));
    }

    @Nullable
    default WaterwayMessage getMessageOrNull(String messageId, int transactionId) {
        return getMessageOrNull(toMessageReference(messageId, transactionId));
    }

    default WaterwayMessage getMessage(String messageReference) {
        return getMessage(toMessageReference(messageReference));
    }

    default WaterwayMessage getMessage(String messageId, int transactionId) {
        return getMessage(toMessageReference(messageId, transactionId));
    }

    default WaterwayMessage getMessage(MessageReference messageReference) {
        return checkNotNull(getMessageOrNull(messageReference), "message not found for id/key =< %s > in storage = %s", messageReference.hasTransactionId() ? messageReference.getMessageKey() : messageReference.getMessageId(), this);
    }

}
