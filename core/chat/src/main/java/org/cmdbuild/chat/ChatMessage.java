/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.chat;

import static com.google.common.base.Objects.equal;
import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import static org.cmdbuild.chat.ChatMessageStatus.CMS_NEW;
import static org.cmdbuild.chat.ChatMessageType.CMT_INCOMING;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;

public interface ChatMessage extends ChatMessageData {

    final String CHAT_MESSAGE_ATTR_TARGET = "Target",
            CHAT_MESSAGE_ATTR_SOURCE_NAME = "SourceName",
            CHAT_MESSAGE_ATTR_SOURCE_TYPE = "SourceType",
            CHAT_MESSAGE_ATTR_TIMESTAMP = "Timestamp",
            CHAT_MESSAGE_ATTR_STATUS = "MessageStatus",
            CHAT_MESSAGE_ATTR_TYPE = "MessageType",
            CHAT_MESSAGE_ATTR_THREAD = "Thread",
            CHAT_MESSAGE_META_VOLATILE = "cm_chat_message_volatile";

    @Nullable
    Long getId();

    String getMessageId();

    ZonedDateTime getTimestamp();

    ChatMessageSourceType getSourceType();

    String getSourceName();

    String getSourceDescription();

    ChatMessageStatus getStatus();

    ChatMessageType getType();

    default boolean isIncoming() {
        return equal(getType(), CMT_INCOMING);
    }

    default boolean isNewMessage() {
        return equal(getStatus(), CMS_NEW);
    }

    default boolean isVolatile() {
        return toBooleanOrDefault(getMeta(CHAT_MESSAGE_META_VOLATILE), false);
    }

}
