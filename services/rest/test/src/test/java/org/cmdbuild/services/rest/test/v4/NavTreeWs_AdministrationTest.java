/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.service.rest.v4.command.NavTreeWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.NavTreeWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsTreeData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListNavTree;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsTreeData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class NavTreeWs_AdministrationTest extends WsTestBase {

    private final NavTreeWs_Administration instance;

    private final String expId = A_KNOWN_NAVTREE_NAME1;
    private final List<String> expListIds = list(A_KNOWN_NAVTREE_NAME1, A_KNOWN_NAVTREE_NAME2, A_KNOWN_NAVTREE_NAME3);

    private final List<NavTree> listNavTree;
    private final WsTreeData wsTreeData;

    public NavTreeWs_AdministrationTest() {
        NavTreeWsCommand command = new NavTreeWsCommand(navTreeService);
        instance = new NavTreeWs_Administration(navTreeService, objectTranslationService, command);
        listNavTree = mockBuildListNavTree();
        wsTreeData = mockBuildWsTreeData();
    }

    @Test
    public void testReadAll() {
        System.out.println("readAll");

        //arrange:
        when(navTreeService.getAll()).thenReturn(listNavTree);
        when(objectTranslationService.translateNavtreeDescription(anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.readAll("flat", A_TEST_EMPTY_FILTER, A_TEST_LIMIT, A_TEST_OFFSET, false);

        //assert:
        verify(navTreeService).getAll();
        verify(objectTranslationService, times(listNavTree.size())).translateNavtreeDescription(anyString(), anyString());
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        when(navTreeService.getTree(anyString())).thenReturn(listNavTree.get(0));
        when(objectTranslationService.translateNavtreeDescription(anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.read(A_KNOWN_TREE_ID, A_KNOWN_TREE_MODE);

        //assert:
        verify(navTreeService).getTree(anyString());
        verify(objectTranslationService).translateNavtreeDescription(anyString(), anyString());
        checkId(expId, resultObject);
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        when(navTreeService.create(any(NavTree.class))).thenReturn(listNavTree.get(0));
        when(objectTranslationService.translateNavtreeDescription(anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.create(wsTreeData);

        //assert:
        verify(navTreeService).create(any(NavTree.class));
        verify(objectTranslationService).translateNavtreeDescription(anyString(), anyString());
        checkId(expId, resultObject);
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(navTreeService.update(any(NavTree.class))).thenReturn(listNavTree.get(0));
        when(objectTranslationService.translateNavtreeDescription(anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.update(A_KNOWN_TREE_ID, wsTreeData);

        //assert:
        verify(navTreeService).update(any(NavTree.class));
        verify(objectTranslationService).translateNavtreeDescription(anyString(), anyString());
        checkId(expId, resultObject);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_TREE_ID);

        //assert:
        verify(navTreeService).removeTree(A_KNOWN_TREE_ID);
        checkSuccess(resultObject);
    }

    @Test
    public void testFixNavtreeDirections() {
        System.out.println("fixNavtreeDirections");

        //act:
        Object resultObject = instance.fixNavtreeDirections(A_KNOWN_TREE_ID);

        //assert:
        verify(navTreeService).fixDirections(anyString());
        checkSuccess(resultObject);
    }
}
