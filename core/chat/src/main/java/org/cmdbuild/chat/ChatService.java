/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.chat;

import java.util.List;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;

public interface ChatService {

    final String CHAT_NOTIFICATION_SOURCE_TYPE = "cm_chat_sourceType";

    PagedElements<ChatMessage> getMessagesForCurrentUser(DaoQueryOptions options);

    ChatMessage sendMessage(ChatMessageData message);

    ChatMessage sendMessageAs(ChatMessageData messageData, ChatMessageSourceType sourceType, String sourceName, String sourceDescription);

    ChatMessage sendMessageAs(ChatMessageData messageData, ChatMessageSourceType sourceType, String sourceName, String sourceDescription, List<Session> sessions);

    void archiveMessagesForCurrentUser(List<Long> recordIds);

    void deleteMessagesForCurrentUser(List<Long> recordIds);

}
