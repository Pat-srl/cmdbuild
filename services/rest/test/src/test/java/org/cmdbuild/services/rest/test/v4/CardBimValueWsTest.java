/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.bim.BimObject;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.service.rest.v4.command.CardBimValueWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CardBimValueWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.Map;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResult;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultStructure;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class CardBimValueWsTest extends WsTestBase {

    private final CardBimValueWs instance;

    private final Card card;
    private final BimObject bimObject;
    private final Map<String, Object> expStructure;

    public CardBimValueWsTest() {
        CardBimValueWsCommand command = new CardBimValueWsCommand(daoService, bimService);
        this.instance = new CardBimValueWs(command);

        Classe classe = mockBuildClasse(A_KNOWN_CLASS_ID);
        this.card = mockBuildCard(A_KNOWN_CARD_ID1, classe, map("attr1", 1, "attr2", 2, "attr3", 3));
        this.bimObject = mockBuildBimObject(A_KNOWN_LONG_ID1);
        this.expStructure = map(
                "_id", null,
                "projectId", null,
                "globalId", null,
                "_owner_type", null,
                "_owner_id", null,
                "exists", null
        );
    }

    @Test
    public void testGetAllForCard_includeRelated() {
        System.out.println("getAllForCard_includeRelated");

        //arrange:
        when(daoService.getCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1)).thenReturn(card);
        when(bimService.getBimObjectForCardOrViaNavTreeOrNull(card)).thenReturn(bimObject);

        //act:
        Object resultObject = instance.getAllForCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, true, INCLUDE);

        //assert:
        verify(daoService).getCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1);
        verify(bimService).getBimObjectForCardOrViaNavTreeOrNull(card);
        verify(bimService, never()).getBimObjectForCardOrNull(any());
        checkResult(A_KNOWN_LONG_ID1, resultObject, "_id");
        checkResultStructure(this.expStructure, (Map<String, Object>) ((Map<String, Object>) resultObject).get("data"));
    }

    @Test
    public void testGetAllForCard_dontIncludeRelated() {
        System.out.println("getAllForCard_dontIncludeRelated");

        //arrange:
        when(daoService.getCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1)).thenReturn(card);
        when(bimService.getBimObjectForCardOrNull(card)).thenReturn(bimObject);

        //act:
        Object resultObject = instance.getAllForCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, true, DONT_INCLUDE);

        //assert:
        verify(daoService).getCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1);
        verify(bimService, never()).getBimObjectForCardOrViaNavTreeOrNull(any());
        verify(bimService).getBimObjectForCardOrNull(card);
        checkResult(A_KNOWN_LONG_ID1, resultObject, "_id");
        checkResultStructure(this.expStructure, (Map<String, Object>) ((Map<String, Object>) resultObject).get("data"));
    }

    @Test
    public void testGetAllForCard_dontCheckIfExists() {
        System.out.println("getAllForCard_includeRelated");

        //arrange:
        Map<String, Object> expStructure = map(
                "_id", null,
                "projectId", null,
                "globalId", null,
                "_owner_type", null,
                "_owner_id", null
        );
        when(daoService.getCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1)).thenReturn(card);
        when(bimService.getBimObjectForCardOrViaNavTreeOrNull(card)).thenReturn(bimObject);

        //act:
        Object resultObject = instance.getAllForCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, false, INCLUDE);

        //assert:
        verify(daoService).getCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1);
        verify(bimService).getBimObjectForCardOrViaNavTreeOrNull(card);
        verify(bimService, never()).getBimObjectForCardOrNull(any());
        checkResult(A_KNOWN_LONG_ID1, resultObject, "_id");
        checkResultStructure(expStructure, (Map<String, Object>) ((Map<String, Object>) resultObject).get("data"));
    }
}
