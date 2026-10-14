package org.cmdbuild.etl.waterway.storage;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.etl.waterway.message.MessageReference;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.toMessageReference;

public interface WaterwayStorageCommons {

    @Nullable
    WaterwayMessage getMessageOrNull(MessageReference messageReference);

    WaterwayMessageAttachment getMessageAttachmentLoadData(MessageReference messageReference, String attachmentName);

    default WaterwayMessage getMessage(MessageReference messageReference) {
        return checkNotNull(getMessageOrNull(messageReference), "message not found for reference =< %s >", messageReference);
    }

    default WaterwayMessageAttachment getMessageAttachmentLoadData(String messageReference, String attachmentName) {
        return getMessageAttachmentLoadData(toMessageReference(messageReference), attachmentName);
    }

    default WaterwayMessage getMessage(String messageReference) {
        return getMessage(toMessageReference(messageReference));
    }

    default WaterwayMessage getMessageOrNull(String messageReference) {
        return getMessageOrNull(toMessageReference(messageReference));
    }

    default WaterwayMessage getMessage(String storageCode, String messageIdOrKey) {
        return getMessage(toMessageReference(storageCode, messageIdOrKey));
    }

}
