/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.chat.ChatPeer;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.ChatPeerSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ChatPeersWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ChatPeersWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildChatPeerList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsQueryOptions;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class ChatPeersWsTest extends WsTestBase {

    private final ChatPeersWs instance;

    private final List<ChatPeer> chatPeerList;
    private final WsQueryOptions wsQueryOptions;

    public ChatPeersWsTest() {
        ChatPeerSerializationHelper chatPeerSerializationHelper = mockBuildChatPeerSerializationHelper();
        ChatPeersWsCommand command = new ChatPeersWsCommand(peerService);
        this.instance = new ChatPeersWs(chatPeerSerializationHelper, command);

        this.chatPeerList = mockBuildChatPeerList();
        this.wsQueryOptions = mockBuildWsQueryOptions(DETAILED);
    }

    @Test
    public void testGetPeers() {
        System.out.println("getPeers");

        //arrange:
        when(peerService.getPeersForCurrentSession()).thenReturn(chatPeerList);

        //act:
        Object resultObject = instance.getPeers(wsQueryOptions);

        //assert:
        verify(peerService).getPeersForCurrentSession();
        checkResultList(list(A_KNOWN_NAME1, A_KNOWN_NAME2, A_KNOWN_NAME3), resultObject, "_id");
    }
}
