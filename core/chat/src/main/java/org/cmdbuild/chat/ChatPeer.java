package org.cmdbuild.chat;

import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;

public interface ChatPeer {

    String getUsername();

    String getDescription();

    boolean hasMessages();

    long getNewMessageCount();

    @Nullable
    ZonedDateTime getLastMessageTimestamp();

    default boolean hasNewMessages() {
        return getNewMessageCount() > 0;
    }

}
