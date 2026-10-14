/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisValue;
import org.cmdbuild.service.rest.common.serializationhelpers.GisValueSerializationHelper;
import org.cmdbuild.service.rest.v4.command.CardGeoValueWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CardGeoValueWs;
import org.cmdbuild.service.rest.v4.model.WsGisValue;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsGisValue;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class CardGeoValueWsTest extends WsTestBase {

    private final CardGeoValueWs instance;

    private final List<GisValue> gisValueList;
    private final GisAttribute gisAttribute1;
    private final GisAttribute gisAttribute2;
    private final GisAttribute gisAttribute3;
    private final Classe classe;
    private final Card card;
    private final WsGisValue wsGisValue;

    public CardGeoValueWsTest() {

        GisValueSerializationHelper gisValueSerializationHelper = mockBuildGisValueSerializationHelper();
        CardGeoValueWsCommand command = new CardGeoValueWsCommand(gisService);
        this.instance = new CardGeoValueWs(gisService, gisValueSerializationHelper, command);

        this.gisValueList = mockBuildGisValueList();
        this.gisAttribute1 = mockBuildGisAttribute(A_KNOWN_NAME1, true, ACTIVE, true);
        this.gisAttribute2 = mockBuildGisAttribute(A_KNOWN_NAME2, true, ACTIVE, true);
        this.gisAttribute3 = mockBuildGisAttribute(A_KNOWN_NAME3, true, ACTIVE, true);
        this.classe = mockBuildClasse(A_KNOWN_CLASS_NAME1);
        this.card = mockBuildCard(A_KNOWN_CARD_ID1, classe, map("attr1", 1, "attr2", 2, "attr3", 3));
        this.wsGisValue = mockBuildWsGisValue();
    }

    @Test
    public void testGetAllForCard() {
        System.out.println("getAllForCard");

        //arrange:
        when(gisService.getGisValuesForCurrentUser(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1)).thenReturn(gisValueList);
        when(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_NAME1)).thenReturn(gisAttribute1);
        when(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_NAME2)).thenReturn(gisAttribute2);
        when(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_NAME3)).thenReturn(gisAttribute3);
        when(daoService.getCard(A_KNOWN_CARD_ID1)).thenReturn(card);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.getAllForCard(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1);

        //assert:
        verify(gisService).getGisValuesForCurrentUser(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1);
        verify(gisService).getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_NAME1);
        verify(gisService).getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_NAME2);
        verify(gisService).getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_NAME3);
        verify(daoService, times(3)).getCard(A_KNOWN_CARD_ID1);
        verify(userClassService, times(3)).getUserClass(anyString());
        checkResultList(list(A_KNOWN_NAME1, A_KNOWN_NAME2, A_KNOWN_NAME3), resultObject, "_attr");
    }

    @Test
    public void testGet() {
        System.out.println("get");

        //arrange:
        when(gisService.getGisValueForCurrentUser(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, A_KNOWN_ATTR_ID)).thenReturn(gisValueList.get(0));
        when(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID)).thenReturn(gisAttribute1);
        when(daoService.getCard(A_KNOWN_CARD_ID1)).thenReturn(card);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.get(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, A_KNOWN_ATTR_ID);

        //assert:
        verify(gisService).getGisValueForCurrentUser(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, A_KNOWN_ATTR_ID);
        verify(gisService).getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);
        verify(daoService, times(1)).getCard(A_KNOWN_CARD_ID1);
        verify(userClassService, times(1)).getUserClass(anyString());
        checkResult(A_KNOWN_NAME1, resultObject, "_attr");
    }

    @Test
    public void testSet() {
        System.out.println("set");

        //arrange:
        when(gisService.setGisValueWithCurrentUser(any(GisValue.class))).thenReturn(gisValueList.get(0));
        when(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID)).thenReturn(gisAttribute1);
        when(daoService.getCard(A_KNOWN_CARD_ID1)).thenReturn(card);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.set(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, A_KNOWN_ATTR_ID, wsGisValue);

        //assert:
        verify(gisService).setGisValueWithCurrentUser(any(GisValue.class));
        verify(gisService).getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);
        verify(daoService, times(1)).getCard(A_KNOWN_CARD_ID1);
        verify(userClassService, times(1)).getUserClass(anyString());
        checkResult(A_KNOWN_NAME1, resultObject, "_attr");
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, A_KNOWN_ATTR_ID);

        //assert:
        verify(gisService).deleteGisValueWithCurrentUser(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1, A_KNOWN_ATTR_ID);
        checkSuccess(resultObject);
    }


}
