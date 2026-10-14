/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.gis.GeoserverLayer;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.service.rest.v4.command.GeoserverLayerWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.GeoserverLayerWs_Administration;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListNames;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class GeoserverLayerWs_AdministrationTest extends WsTestBase {

    private final GeoserverLayerWs_Administration instance;

    private final String expName = A_KNOWN_LAYER_NAME;
    private final List<String> expListNames = list(expName);

    private final GeoserverLayer geoserverLayer;
    private final GisAttribute gisAttribute;
    private final Card card;
    private final Classe classe1;
    private final Classe classe2;

    public GeoserverLayerWs_AdministrationTest() {
        GeoserverLayerWsCommand command = new GeoserverLayerWsCommand(operationUserSupplier, gisService, daoService, userClassService);
        instance = new GeoserverLayerWs_Administration(gisService, daoService, command);

        geoserverLayer = mockBuildGeoserverLayer();
        gisAttribute = mockBuildGisAttribute(A_KNOWN_LAYER_NAME, true, ACTIVE, false);
        classe1 = mockBuildClasse(A_KNOWN_CLASS_ID1, A_KNOWN_CLASS_NAME2);
        classe2 = mockBuildClasse(A_KNOWN_CLASS_ID2, A_KNOWN_CLASS_NAME3);
        card = mockBuildCard(A_KNOWN_CARD_ID1, classe2,
                map(A_KNOWN_ATTR_NAME1, mockBuildAttribute(A_KNOWN_CLASS_ID, classe2)));
    }

    @Test
    public void testGetMany() {
        System.out.println("getMany");

        //arrange:
        when(daoService.getClasse(anyString())).thenReturn(classe1);
        when(gisService.getGeoServerLayersForCard(anyString(), anyLong())).thenReturn(list(geoserverLayer));
        when(gisService.getGisAttributeIncludeInherited(anyString(), anyString())).thenReturn(gisAttribute);
        when(daoService.getInfo(any(Classe.class), anyLong())).thenReturn(card);

        //act:
        Object resultObject = instance.getMany(A_KNOWN_CLASS_ID, A_KNOWN_CARD_ID1.toString(), A_TEST_EMPTY_FILTER);

        //assert:
        verify(daoService).getClasse(anyString());
        verify(gisService).getGeoServerLayersForCard(anyString(), anyLong());
        verify(gisService).getGisAttributeIncludeInherited(anyString(), anyString());
        verify(daoService).getInfo(any(Classe.class), anyLong());
        checkListNames(expListNames, resultObject);
    }
}
