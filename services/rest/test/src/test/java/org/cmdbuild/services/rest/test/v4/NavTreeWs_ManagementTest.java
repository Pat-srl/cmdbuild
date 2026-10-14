/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import java.util.List;
import org.cmdbuild.domain.tree.DomainPathFinder;
import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.NavTreeWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.NavTreeWs_Management;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkId;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListIds;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListNavTree;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class NavTreeWs_ManagementTest extends WsTestBase {

    private final NavTreeWs_Management instance;

    private final String expId = A_KNOWN_NAVTREE_NAME1;
    private final List<String> expListIds = list(A_KNOWN_NAVTREE_NAME1, A_KNOWN_NAVTREE_NAME2, A_KNOWN_NAVTREE_NAME3);

    private final List<NavTree> listNavTree;

    private final DomainPathFinder domainPathFinder = mock(DomainPathFinder.class);
    private final CardWsSerializationHelperv3 cardWsSerializationHelperv3 = mock(CardWsSerializationHelperv3.class);

    public NavTreeWs_ManagementTest() {
        NavTreeWsCommand command = new NavTreeWsCommand(navTreeService);
        instance = new NavTreeWs_Management(navTreeService, objectTranslationService, domainPathFinder, cardWsSerializationHelperv3, command);
        listNavTree = mockBuildListNavTree();
    }

    @Test
    public void testReadAll() {
        System.out.println("readAll");

        //arrange:
        when(navTreeService.getUserAll()).thenReturn(listNavTree);
        when(objectTranslationService.translateNavtreeDescription(anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.readAll("flat", A_TEST_EMPTY_FILTER, A_TEST_LIMIT, A_TEST_OFFSET, false);

        //assert:
        verify(navTreeService).getUserAll();
        verify(objectTranslationService, times(listNavTree.size())).translateNavtreeDescription(anyString(), anyString());
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        when(navTreeService.getUserTree(anyString())).thenReturn(listNavTree.get(0));
        when(objectTranslationService.translateNavtreeDescription(anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.read(A_KNOWN_TREE_ID, A_KNOWN_TREE_MODE);

        //assert:
        verify(navTreeService).getUserTree(anyString());
        verify(objectTranslationService).translateNavtreeDescription(anyString(), anyString());
        checkId(expId, resultObject);
    }
}
