/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.chat.ChatMessage;
import org.cmdbuild.chat.ChatMessageData;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.ChatMessageWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ChatMessageWs;
import org.cmdbuild.service.rest.v4.model.WsChatMessageData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildChatMessageList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsChatMessageData;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsQueryOptions;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class ChatMessageWsTest extends WsTestBase {

    private final ChatMessageWs instance;

    private final WsQueryOptions wsQueryOptionsActive;
    private final WsQueryOptions wsQueryOptionsNonActive;
    private final WsChatMessageData wsChatMessageData;
    private final PagedElements<ChatMessage> chatMessagePagedElements;

    public ChatMessageWsTest() {
        ChatMessageWsCommand command = new ChatMessageWsCommand(chatService);
        this.instance = new ChatMessageWs(command);

        this.wsQueryOptionsActive = mockBuildWsQueryOptions(DETAILED);
        this.wsQueryOptionsNonActive = mockBuildWsQueryOptions(NOT_DETAILED);
        this.wsChatMessageData = mockBuildWsChatMessageData();
        this.chatMessagePagedElements = new PagedElements<>(mockBuildChatMessageList());
    }

    @Test
    public void testGetMessages_detailed() {
        System.out.println("getMessages_detailed");

        //arrange:
        when(chatService.getMessagesForCurrentUser(any(DaoQueryOptions.class))).thenReturn(chatMessagePagedElements);

        //act:
        Object resultObject = instance.getMessages(wsQueryOptionsActive);

        //assert:
        verify(chatService).getMessagesForCurrentUser(any(DaoQueryOptions.class));
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
        checkDetailedList(resultObject, "content");
    }

    @Test
    public void testGetMessages_notDetailed() {
        System.out.println("getMessages_notDetailed");

        //arrange:
        when(chatService.getMessagesForCurrentUser(any(DaoQueryOptions.class))).thenReturn(chatMessagePagedElements);

        //act:
        Object resultObject = instance.getMessages(wsQueryOptionsNonActive);

        //assert:
        verify(chatService).getMessagesForCurrentUser(any(DaoQueryOptions.class));
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
        checkNotDetailedList(resultObject, "content");
    }

    @Test
    public void testUpdateMessage() {
        System.out.println("updateMessage");

        //act:
        Object resultObject = instance.updateMessage(A_KNOWN_LONG_ID1, wsChatMessageData);

        //assert:
        verify(chatService).archiveMessagesForCurrentUser(anyList());
        checkSuccess(resultObject);
    }

    @Test
    public void testSendMessage() {
        System.out.println("sendMessage");

        //arrange:
        when(chatService.sendMessage(any(ChatMessageData.class))).thenReturn(chatMessagePagedElements.elements().get(0));

        //act:
        Object resultObject = instance.sendMessage(wsChatMessageData);

        //assert:
        verify(chatService).sendMessage(any(ChatMessageData.class));
        checkResult(A_KNOWN_LONG_ID1, resultObject, "_id");
    }

    @Test
    public void testDeleteMessage() {
        System.out.println("deleteMessage");

        //act:
        Object resultObject = instance.deleteMessage(A_KNOWN_LONG_ID1);

        //assert:
        verify(chatService).deleteMessagesForCurrentUser(anyList());
        checkSuccess(resultObject);
    }

}
