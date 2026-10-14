/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.stylerules.GisStyleRuleset;
import org.cmdbuild.service.rest.v4.command.GeoStyleRulesWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.GeoStyleRulesWs;
import org.cmdbuild.service.rest.v4.model.WsRulesetData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsRulesetData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class GeoStyleRulesWsTest extends WsTestBase {

    private final GeoStyleRulesWs instance;

    private final List<GisStyleRuleset> gisStyleRulesetList;
    private final Classe classe;
    private final WsRulesetData wsRulesetData;
    private final GisAttribute gisAttribute;

    public GeoStyleRulesWsTest() {
        GeoStyleRulesWsCommand command = new GeoStyleRulesWsCommand(gisStyleRulesService, gisService, userClassService);
        this.instance = new GeoStyleRulesWs(command);

        this.gisStyleRulesetList = mockBuildGisStyleRulesetList();
        this.classe = mockBuildClasseWithGisPermissions(A_KNOWN_CLASS_NAME1, list(A_KNOWN_NAME1, A_KNOWN_NAME2, A_KNOWN_NAME3));
        this.wsRulesetData = mockBuildWsRulesetData(A_KNOWN_NAME1);
        this.gisAttribute = mockBuildGisAttribute(A_KNOWN_NAME1, true, ACTIVE, true);
    }

    @Test
    public void testReadAll() {
        System.out.println("readAll");

        //arrange:
        when(gisStyleRulesService.getAll()).thenReturn(gisStyleRulesetList);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.readAll(A_KNOWN_ANY_ID, A_TEST_EMPTY_FILTER, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(gisStyleRulesService).getAll();
        verify(userClassService, times(3)).getUserClass(anyString());
        checkResultList(list(A_KNOWN_CODE1, A_KNOWN_CODE2, A_KNOWN_CODE3), resultObject, "name");
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        when(gisStyleRulesService.getById(A_KNOWN_LONG_ID1)).thenReturn(gisStyleRulesetList.get(0));
        when(userClassService.getUserClass(A_KNOWN_CLASS_ID)).thenReturn(classe);

        //act:
        Object resultObject = instance.read(A_KNOWN_CLASS_ID, A_KNOWN_LONG_ID1);

        //assert:
        verify(gisStyleRulesService).getById(A_KNOWN_LONG_ID1);
        verify(userClassService).getUserClass(A_KNOWN_CLASS_ID);
        checkResult(A_KNOWN_CODE1, resultObject, "name");
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        when(gisStyleRulesService.create(any(GisStyleRuleset.class))).thenReturn(gisStyleRulesetList.get(0));
        when(gisService.getGisAttributeIncludeInherited(anyString(), anyString())).thenReturn(gisAttribute);

        //act:
        Object resultObject = instance.create(wsRulesetData);

        //assert:
        verify(gisStyleRulesService).create(any(GisStyleRuleset.class));
        verify(gisService).getGisAttributeIncludeInherited(anyString(), anyString());
        checkResult(A_KNOWN_CODE1, resultObject, "name");
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(gisStyleRulesService.update(any(GisStyleRuleset.class))).thenReturn(gisStyleRulesetList.get(0));
        when(gisService.getGisAttributeIncludeInherited(anyString(), anyString())).thenReturn(gisAttribute);

        //act:
        Object resultObject = instance.update(A_KNOWN_LONG_ID1, wsRulesetData);

        //assert:
        verify(gisStyleRulesService).update(any(GisStyleRuleset.class));
        verify(gisService).getGisAttributeIncludeInherited(anyString(), anyString());
        checkResult(A_KNOWN_CODE1, resultObject, "name");
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_LONG_ID1);

        //assert:
        verify(gisStyleRulesService).delete(A_KNOWN_LONG_ID1);
        checkSuccess(resultObject);
    }

    @Test
    public void testUpdateVisibility() {
        System.out.println("updateVisibility");

        //arrange:
        when(gisStyleRulesService.getById(A_KNOWN_LONG_ID1)).thenReturn(gisStyleRulesetList.get(0));
        when(gisStyleRulesService.getById(A_KNOWN_LONG_ID2)).thenReturn(gisStyleRulesetList.get(1));

        //act:
        Object resultObject = instance.updateVisibility(A_KNOWN_ANY_ID, map(A_KNOWN_LONG_ID1, true, A_KNOWN_LONG_ID2, false));

        //assert:
        verify(gisStyleRulesService).getById(A_KNOWN_LONG_ID1);
        verify(gisStyleRulesService).getById(A_KNOWN_LONG_ID2);
        verify(gisStyleRulesService).update(any(GisStyleRuleset.class));
    }

    @Test
    public void testApplyRules() {
        System.out.println("applyRules");

        //arrange:
        Map<Long, Map<String, Object>> map = emptyMap();
        when(gisStyleRulesService.applyRulesOnCards(eq(A_KNOWN_LONG_ID1), eq(set(1L, 2L, 3L)))).thenReturn(map);

        //act:
        instance.applyRules(A_KNOWN_LONG_ID1, "1,2,3");

        //assert:
        verify(gisStyleRulesService).applyRulesOnCards(eq(A_KNOWN_LONG_ID1), eq(set(1L, 2L, 3L)));
        // no check on returned object cause it's a service call and it gets mocked
    }

    @Test
    public void testTestRules() {
        System.out.println("testRules");

        //arrange:
        Map<Long, Map<String, Object>> map = emptyMap();
        when(gisStyleRulesService.applyRulesOnCards(eq(A_KNOWN_LONG_ID1), eq(set(1L, 2L, 3L)))).thenReturn(map);
        when(gisService.getGisAttributeIncludeInherited(anyString(), anyString())).thenReturn(gisAttribute);


        //act:
        Object resultObject = instance.testRules(wsRulesetData, "1,2,3");

        //assert:
        verify(gisStyleRulesService).applyRulesOnCards(any(GisStyleRuleset.class), eq(set(1L, 2L, 3L)));
        verify(gisService).getGisAttributeIncludeInherited(anyString(), anyString());
        // no check on returned object cause it's a service call and it gets mocked
    }

}
