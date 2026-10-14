package org.cmdbuild.services.rest.test.v4;

import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessage;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildMobileAppMessageList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsMobileAppMessageData;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsQueryOptions;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.MobileAppMessageWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.MobileAppMessageWs;
import org.cmdbuild.service.rest.v4.model.WsMobileAppMessageData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

/**
 * @author ldare
 */
public class MobileAppMessageWsTest extends WsTestBase {

    private MobileAppMessageWs instance;

    private final WsQueryOptions wsQueryOptions;
    private final WsMobileAppMessageData wsMobileAppMessageData;
    private final PagedElements<MobileAppMessage> mobileAppMessagePagedElements;

    public MobileAppMessageWsTest() {
        MobileAppMessageWsCommand command = new MobileAppMessageWsCommand(mobileAppService, mobileConfiguration);
        this.instance = new MobileAppMessageWs(command);

        this.wsQueryOptions = mockBuildWsQueryOptions(DETAILED);
        this.wsMobileAppMessageData = mockBuildWsMobileAppMessageData("target");
        this.mobileAppMessagePagedElements = mockBuildMobileAppMessageList();
    }

    @Test
    public void testGetMessages() {
        System.out.println("testGetMessages");

        //arrange
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileAppService.getMessagesForCurrentUser(any())).thenReturn(mobileAppMessagePagedElements);

        //act:
        Object resultObject = instance.getMessages(wsQueryOptions);

        //assert:
        verify(mobileConfiguration).isMobileEnabled();
        verify(mobileAppService).getMessagesForCurrentUser(any());
        checkResultList(list(A_KNOWN_NAME1, A_KNOWN_NAME2, A_KNOWN_NAME3), resultObject, "sourceName");
    }

    @Test
    public void testGetMessages_exception() {
        System.out.println("testGetMessages_exception");

        //arrange:
        when(mobileConfiguration.isMobileEnabled()).thenThrow(new UnsupportedOperationException());

        //act:
        Object resultObject = instance.getMessages(wsQueryOptions);

        //assert:
        assertEquals(400, ((ResponseImpl) resultObject).getStatus());
    }

    @Test
    public void testGetMessages_dummyService() {
        System.out.println("testGetMessages_dummyService");

        //act:
        Object resultObject = instance.getMessages(wsQueryOptions);

        //assert:
        verify(mobileConfiguration).isMobileEnabled();
        assertEquals("trying to get mobile app notification msg, but mobile is disabled", ((Map) ((ResponseImpl) resultObject).getActualEntity()).get("messages"));
    }

    @Test
    public void testSendMessage() {
        System.out.println("testSendMessage");

        //arrange:
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileAppService.sendMessage(any(MobileAppMessageData.class))).thenReturn(mobileAppMessagePagedElements.elements().get(0));

        //act:
        Object resultObject = instance.sendMessage(wsMobileAppMessageData);

        //assert:
        verify(mobileConfiguration).isMobileEnabled();
        verify(mobileAppService).sendMessage(any(MobileAppMessageData.class));
        checkResult(A_KNOWN_NAME1, resultObject, "sourceName");
    }

    @Test
    public void testSendMessage_exception() {
        System.out.println("testSendMessage_exception");

        //arrange:
        when(mobileConfiguration.isMobileEnabled()).thenThrow(new UnsupportedOperationException());

        //act:
        Object resultObject = instance.sendMessage(wsMobileAppMessageData);

        //assert:
        assertEquals(400, ((ResponseImpl) resultObject).getStatus());
    }

    @Test
    public void testSendMessage_dummyService() {
        System.out.println("testSendMessage_dummyService");

        //act:
        Object resultObject = instance.sendMessage(wsMobileAppMessageData);

        //assert:
        verify(mobileConfiguration).isMobileEnabled();
        assertEquals("trying to send mobile app notification msg, but mobile is disabled", ((Map) ((ResponseImpl) resultObject).getActualEntity()).get("messages"));
    }

    @Test
    public void testUpdateMessage() {
        System.out.println("testUpdateMessage");

        //arrange:
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);

        //act:
        Object resultObject = instance.updateMessage(A_KNOWN_LONG_ID1, wsMobileAppMessageData);

        //assert:
        verify(mobileConfiguration).isMobileEnabled();
        verify(mobileAppService).archiveMessagesForCurrentUser(list(A_KNOWN_LONG_ID1));
        checkSuccess(resultObject);
    }

    @Test
    public void testUpdateMessage_exception() {
        System.out.println("testUpdateMessage_exception");

        //arrange:
        when(mobileConfiguration.isMobileEnabled()).thenThrow(new UnsupportedOperationException());

        //act:
        Object resultObject = instance.updateMessage(A_KNOWN_LONG_ID1, wsMobileAppMessageData);

        //assert:
        assertEquals(400, ((ResponseImpl) resultObject).getStatus());
    }

    @Test
    public void testUpdateMessage_dummyService() {
        System.out.println("testUpdateMessage_dummyService");


        //act:
        Object resultObject = instance.updateMessage(A_KNOWN_LONG_ID1, wsMobileAppMessageData);

        //assert:
        verify(mobileConfiguration).isMobileEnabled();
        assertEquals("trying to archive mobile app notification message, but mobile is disabled", ((Map) ((ResponseImpl) resultObject).getActualEntity()).get("messages"));
    }

    @Test
    public void testDeleteMessage() {
        System.out.println("testDeleteMessage");

        //arrange:
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);

        //act:
        Object resultObject = instance.deleteMessage(A_KNOWN_LONG_ID1);

        //assert:
        verify(mobileConfiguration).isMobileEnabled();
        verify(mobileAppService).deleteMessagesForCurrentUser(list(A_KNOWN_LONG_ID1));
        checkSuccess(resultObject);
    }

    @Test
    public void testDeleteMessage_exception() {
        System.out.println("testDeleteMessage_exception");

        //arrange:
        when(mobileConfiguration.isMobileEnabled()).thenThrow(new UnsupportedOperationException());

        //act:
        Object resultObject = instance.deleteMessage(A_KNOWN_LONG_ID1);

        //assert:
        assertEquals(400, ((ResponseImpl) resultObject).getStatus());
    }

    @Test
    public void testDeleteMessage_dummyService() {
        System.out.println("testDeleteMessage_dummyService");

        //act:
        Object resultObject = instance.deleteMessage(A_KNOWN_LONG_ID1);

        //assert:
        verify(mobileConfiguration).isMobileEnabled();
        assertEquals("trying to delete mobile app notification message, but mobile is disabled", ((Map) ((ResponseImpl) resultObject).getActualEntity()).get("messages"));
    }
}

