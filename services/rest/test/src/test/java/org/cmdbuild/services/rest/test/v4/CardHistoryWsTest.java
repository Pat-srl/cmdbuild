/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.HistorySerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.CardHistoryWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CardHistoryWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.classe.access.CardHistoryService.HistoryElement.HE_CARDS;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResult;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildCard;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildClasseWithAttr;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsQueryOptions;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsQueryOptions3;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class CardHistoryWsTest extends WsTestBase {

    private final CardHistoryWs instance;

    private final WsQueryOptions wsQueryOptionsDetailed;
    private final WsQueryOptions wsQueryOptionsNotDetailed;
    private final List<Card> cardList;
    private final PagedElements<Card> cardPagedElements;

    public CardHistoryWsTest() {

        CardWsSerializationHelperv3 cardWsSerializationHelperv3 = mockBuildCardWsSerializationHelperv3();
        HistorySerializationHelper historySerializationHelper = mockBuildHistorySerializationHelper();
        CardHistoryWsCommand command = new CardHistoryWsCommand(cardHistoryService);
        this.instance = new CardHistoryWs(cardWsSerializationHelperv3, historySerializationHelper, command);


        this.wsQueryOptionsDetailed = mockBuildWsQueryOptions3(DETAILED);
        this.wsQueryOptionsNotDetailed = mockBuildWsQueryOptions3(NOT_DETAILED);
        Classe classe = mockBuildClasseWithAttr(A_KNOWN_CLASS_NAME1);
        this.cardList = list(
                mockBuildCard(A_KNOWN_CARD_ID1, classe, map(A_KNOWN_ATTR_NAME1, 1, A_KNOWN_ATTR_NAME2, 2, A_KNOWN_ATTR_NAME3, 3))
        );
        this.cardPagedElements = new PagedElements<>(cardList);
    }

    @Test
    public void testGetHistory_detailed() {
        System.out.println("getHistory_detailed");

        //arrange:
        when(cardHistoryService.fetchHistoryTypes(HE_CARDS.toString())).thenReturn(list(HE_CARDS));
        when(cardHistoryService.getHistoryElements(eq(A_KNOWN_CLASS_ID), eq(A_KNOWN_CARD_ID1), any(DaoQueryOptions.class), anyList())).thenReturn(cardPagedElements);
        when(cardHistoryService.getHistoryRecord(A_KNOWN_CLASS_ID, cardList.get(0).getId())).thenReturn(cardList.get(0));


        //act:
        Object resultObject = instance.getHistory(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, wsQueryOptionsDetailed, HE_CARDS.toString());

        //assert:
        verify(cardHistoryService).fetchHistoryTypes(HE_CARDS.toString());
        verify(cardHistoryService).getHistoryElements(eq(A_KNOWN_CLASS_ID), eq(A_KNOWN_CARD_ID1), any(DaoQueryOptions.class), anyList());
        verify(cardHistoryService).getHistoryRecord(A_KNOWN_CLASS_ID, cardList.get(0).getId());
        checkResultList(list(A_KNOWN_CLASS_NAME1), resultObject, "_type");
    }

    @Test
    public void testGetHistory_notDetailed() {
        System.out.println("getHistory_notDetailed");

        //arrange:
        when(cardHistoryService.fetchHistoryTypes(HE_CARDS.toString())).thenReturn(list(HE_CARDS));
        when(cardHistoryService.getHistoryElements(eq(A_KNOWN_CLASS_ID), eq(A_KNOWN_CARD_ID1), any(DaoQueryOptions.class), anyList())).thenReturn(cardPagedElements);


        //act:
        Object resultObject = instance.getHistory(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, wsQueryOptionsNotDetailed, HE_CARDS.toString());

        //assert:
        verify(cardHistoryService).fetchHistoryTypes(HE_CARDS.toString());
        verify(cardHistoryService).getHistoryElements(eq(A_KNOWN_CLASS_ID), eq(A_KNOWN_CARD_ID1), any(DaoQueryOptions.class), anyList());
        verify(cardHistoryService, never()).getHistoryRecord(anyString(), anyLong());
        checkResultList(list(A_KNOWN_CLASS_NAME1), resultObject, "_type");
    }

    @Test
    public void testGetHistoryOnlyChanges() {
        System.out.println("getHistoryOnlyChanges");

        //arrange:
        when(cardHistoryService.fetchHistoryTypes(HE_CARDS.toString())).thenReturn(list(HE_CARDS));
        when(cardHistoryService.getHistoryElementsOnlyChanges(eq(A_KNOWN_CLASS_ID), eq(A_KNOWN_CARD_ID1), any(DaoQueryOptions.class), anyList())).thenReturn(cardList);

        //act:
        Object resultObject = instance.getHistoryOnlyChanges(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, wsQueryOptionsDetailed, HE_CARDS.toString());

        //assert:
        verify(cardHistoryService).fetchHistoryTypes(HE_CARDS.toString());
        verify(cardHistoryService).getHistoryElementsOnlyChanges(eq(A_KNOWN_CLASS_ID), eq(A_KNOWN_CARD_ID1), any(DaoQueryOptions.class), anyList());
        checkResultList(list(A_KNOWN_CARD_ID1), resultObject, "_id");
    }

    @Test
    public void testGetHistoryRecord() {
        System.out.println("getHistoryRecord");

        //arrange:
        when(cardHistoryService.getHistoryRecord(A_KNOWN_CLASS_ID, cardList.get(0).getId())).thenReturn(cardList.get(0));

        //act:
        Object resultObject = instance.getHistoryRecord(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, cardList.get(0).getId());

        //assert:
        verify(cardHistoryService).getHistoryRecord(A_KNOWN_CLASS_ID, cardList.get(0).getId());
        checkResult(A_KNOWN_CLASS_NAME1, resultObject, "_type");
    }
}
