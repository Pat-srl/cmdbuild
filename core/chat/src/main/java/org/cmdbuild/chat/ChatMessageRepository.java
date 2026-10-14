/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.chat;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;

public interface ChatMessageRepository {

    PagedElements<ChatMessage> getMessagesForUser(String username, DaoQueryOptions options);

    ChatMessage getMessageByRecordId(long id);

    ChatMessage getMessageByMessageIdAndType(String messageId, ChatMessageType type);

    ChatMessage createMessage(ChatMessage message);

    ChatMessage updateMessage(ChatMessage message);

    void deleteMessage(ChatMessage message);

}
