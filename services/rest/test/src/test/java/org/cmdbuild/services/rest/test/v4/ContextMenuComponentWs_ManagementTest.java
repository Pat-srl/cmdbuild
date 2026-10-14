/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.service.rest.v4.command.ContextMenuComponentWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ContextMenuComponentWs_Administration;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.junit.Test;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResult;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildUiComponentInfo;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.A_KNOWN_LONG_ID1;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.A_KNOWN_UI_COMPONENT_NAME1;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class ContextMenuComponentWs_ManagementTest extends WsTestBase {

    private final ContextMenuComponentWs_Administration instance;

    private final UiComponentInfo uiComponentInfo;

    public ContextMenuComponentWs_ManagementTest() {
        ContextMenuComponentWsCommand command = new ContextMenuComponentWsCommand(contextMenuComponentService);
        this.instance = new ContextMenuComponentWs_Administration(command);

        this.uiComponentInfo = mockBuildUiComponentInfo(A_KNOWN_UI_COMPONENT_NAME1);
    }

    @Test
    public void testGet() {
        System.out.println("get");

        //arrange:
        when(contextMenuComponentService.get(A_KNOWN_LONG_ID1)).thenReturn(uiComponentInfo);

        //act:
        Object resultObject = instance.get(A_KNOWN_LONG_ID1);

        //assert:
        verify(contextMenuComponentService).get(A_KNOWN_LONG_ID1);
        checkResult(A_KNOWN_UI_COMPONENT_NAME1, resultObject, "name");
    }
}
