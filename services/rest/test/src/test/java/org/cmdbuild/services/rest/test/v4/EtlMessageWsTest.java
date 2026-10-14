/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.etl.waterway.WaterwayMessagesStats;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.EtlMessageWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.EtlMessageWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.Map;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsQueryOptions;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class EtlMessageWsTest extends WsTestBase {

    private final EtlMessageWs instance;

    private final WsQueryOptions wsQueryOptions;
    private final PagedElements<WaterwayMessage> waterwayMessagePagedElements;
    private final Classe classe;
    private final WaterwayMessageAttachment waterwayMessageAttachment;
    private final WaterwayMessagesStats waterwayMessagesStats;

    public EtlMessageWsTest() {
        EtlMessageWsCommand command = new EtlMessageWsCommand(waterwayService, cardMapperService);
        this.instance = new EtlMessageWs(command);

        this.wsQueryOptions = mockBuildWsQueryOptions(DETAILED);
        this.waterwayMessagePagedElements = mockBuildEtlMessagePagedElements();
        this.classe = mockBuildClasseWithAttr(A_KNOWN_CLASS_NAME1);
        this.waterwayMessageAttachment = mockBuildWaterwayMessageAttachment(A_KNOWN_NAME1);
        this.waterwayMessagesStats = mockBuildWaterwayMessagesStats();
    }

    @Test
    public void testReadMessages() {
        System.out.println("readMessages");

        //arrange:
        when(waterwayService.getMessages(any(DaoQueryOptions.class))).thenReturn(waterwayMessagePagedElements);
        when(cardMapperService.getClasseForModelOrBuilder(any())).thenReturn(classe);

        //act:
        Object resultObject = instance.readMessages(wsQueryOptions);

        //assert:
        verify(waterwayService).getMessages(any(DaoQueryOptions.class));
        verify(cardMapperService).getClasseForModelOrBuilder(any());
        checkResultList(list(A_KNOWN_GENERIC_ID1, A_KNOWN_GENERIC_ID2, A_KNOWN_GENERIC_ID3), resultObject, "messageId");
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        when(waterwayService.getMessage(A_KNOWN_NAME1)).thenReturn(waterwayMessagePagedElements.elements().get(0));

        //act:
        Object resultObject = instance.read(A_KNOWN_NAME1);

        //assert:
        verify(waterwayService).getMessage(A_KNOWN_NAME1);
        checkResult(A_KNOWN_GENERIC_ID1, resultObject, "messageId");
    }

    @Test
    public void testReadAttachment() {
        System.out.println("readAttachment");

        //arrange:
        when(waterwayService.getMessageAttachmentLoadData(A_KNOWN_NAME1, A_KNOWN_ATTACHMENT_ID)).thenReturn(waterwayMessageAttachment);

        //act:
        Object resultObject = instance.readAttachment(A_KNOWN_NAME1, A_KNOWN_ATTACHMENT_ID);

        //assert:
        verify(waterwayService).getMessageAttachmentLoadData(A_KNOWN_NAME1, A_KNOWN_ATTACHMENT_ID);
        checkDataSource("text/plain; charset=UTF-8", resultObject);
    }

    @Test
    public void testReadMessageStats() {
        System.out.println("readMessageStats");

        //arrange:
        Map<String, Object> expResultMap = map(
                "draft", 10L,
                "queued", 15L,
                "processing", 20L,
                "standby", 0L,
                "processed", 0L,
                "completed", 0L,
                "forwarded", 0L,
                "fault_tolerant_error", 0L,
                "error", 0L,
                "failed", 0L
        );
        when(waterwayService.getMessagesStats()).thenReturn(waterwayMessagesStats);

        //act:
        Object resultObject = instance.readMessagesStats();

        //assert:
        verify(waterwayService).getMessagesStats();
        checkResponse(expResultMap, resultObject);
    }

}
