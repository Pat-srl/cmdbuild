/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.CardRelationWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CardRelationWs;
import org.cmdbuild.service.rest.v4.model.WsRelationData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsRelationData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class CardRelationWsTest extends WsTestBase {

    private final CardRelationWs instance;

    private final WsRelationData wsRelationData;
    private final PagedElements<CMRelation> cmRelationPagedElements;
    private final Classe sourceClass;
    private final Classe targetClass;
    private final Domain domain;
    private final Card card1;
    private final Card card2;

    public CardRelationWsTest() {

        CardWsSerializationHelperv3 cardWsSerializationHelperv3 = mockBuildCardWsSerializationHelperv3();
        CardRelationWsCommand command = new CardRelationWsCommand(userDomainService, daoService);
        this.instance = new CardRelationWs(cardWsSerializationHelperv3, command);

        this.wsRelationData = mockBuildWsRelationData(A_KNOWN_LONG_ID1, true);
        this.cmRelationPagedElements = new PagedElements<>(list(
                mockBuildCMRelation(A_KNOWN_LONG_ID1),
                mockBuildCMRelation(A_KNOWN_LONG_ID2),
                mockBuildCMRelation(A_KNOWN_LONG_ID3)
        ));
        this.sourceClass = mockBuildClasse(A_KNOWN_CLASS_NAME1);
        this.targetClass = mockBuildClasse(A_KNOWN_CLASS_NAME2);
        this.domain = mockBuildDomainDirect(A_KNOWN_DOMAIN_NAME1, sourceClass, targetClass);
        this.card1 = mockBuildCard(A_KNOWN_CARD_ID1, sourceClass, map(A_KNOWN_ATTR_NAME1, 1));
        this.card2 = mockBuildCard(A_KNOWN_CARD_ID1, targetClass, map(A_KNOWN_ATTR_NAME2, 2));
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        when(userDomainService.getUserRelationsForCard(eq(A_KNOWN_CLASS_NAME1), eq(A_KNOWN_CARD_ID1), any(DaoQueryOptions.class))).thenReturn(cmRelationPagedElements);

        //act:
        Object resultObject = instance.read(A_KNOWN_CLASS_NAME1, A_KNOWN_CARD_ID1, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, DETAILED, A_TEST_FILTER, A_TEST_SORT);

        //assert:
        verify(userDomainService).getUserRelationsForCard(eq(A_KNOWN_CLASS_NAME1), eq(A_KNOWN_CARD_ID1), any(DaoQueryOptions.class));
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        when(daoService.getDomain("domain")).thenReturn(domain);
        when(daoService.getCard("ClassA", 10L)).thenReturn(card1);
        when(daoService.getCard("ClassB", 20L)).thenReturn(card2);
        when(daoService.create(any(CMRelation.class))).thenReturn(cmRelationPagedElements.elements().get(0));

        //act:
        Object resultObject = instance.create(A_KNOWN_LONG_ID1, wsRelationData);

        //assert:
        verify(daoService).getDomain("domain");
        verify(daoService).getCard("ClassA", 10L);
        verify(daoService).getCard("ClassB", 20L);
        verify(daoService).create(any(CMRelation.class));
        checkResult(A_KNOWN_CLASS_NAME1, resultObject, "_sourceType");
        checkResult(A_KNOWN_CLASS_NAME2, resultObject, "_destinationType");
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(daoService.getRelation("domain", A_KNOWN_LONG_ID2)).thenReturn(cmRelationPagedElements.elements().get(0));
        when(daoService.getCard("ClassA", 10L)).thenReturn(card1);
        when(daoService.getCard("ClassB", 20L)).thenReturn(card2);
        when(daoService.update(any(CMRelation.class))).thenReturn(cmRelationPagedElements.elements().get(0));

        //act:
        Object resultObject = instance.update(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, wsRelationData);

        //assert:
        verify(daoService).getRelation("domain", A_KNOWN_LONG_ID2);
        verify(daoService).getCard("ClassA", 10L);
        verify(daoService).getCard("ClassB", 20L);
        verify(daoService).update(any(CMRelation.class));
        checkResult(A_KNOWN_CLASS_NAME1, resultObject, "_sourceType");
        checkResult(A_KNOWN_CLASS_NAME2, resultObject, "_destinationType");
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //arrange:
        when(daoService.getRelation(A_KNOWN_LONG_ID1)).thenReturn(cmRelationPagedElements.elements().get(0));

        //act:
        Object resultObject = instance.delete(A_KNOWN_LONG_ID1);

        //assert:
        verify(daoService).getRelation(A_KNOWN_LONG_ID1);
        verify(daoService).delete(any(CMRelation.class));
        checkSuccess(resultObject);
    }


}
