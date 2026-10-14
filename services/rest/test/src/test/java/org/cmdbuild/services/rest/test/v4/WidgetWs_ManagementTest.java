/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import org.cmdbuild.service.rest.v4.command.WidgetWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.WidgetWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListNames;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkName;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListUiComponentInfo;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class WidgetWs_ManagementTest extends WsTestBase {

    private final WidgetWs_Management instance;
    private final List<UiComponentInfo> listUiComponentInfo;

    public WidgetWs_ManagementTest() {
        WidgetWsCommand command = new WidgetWsCommand(widgetComponentService);
        instance = new WidgetWs_Management(widgetComponentService, command);
        listUiComponentInfo = mockBuildListUiComponentInfo();
    }

    @Test
    public void testList() {
        System.out.println("list");

        //arrange:
        List<String> expListNames = list(A_KNOWN_UI_COMPONENT_NAME1, A_KNOWN_UI_COMPONENT_NAME2, A_KNOWN_UI_COMPONENT_NAME3);
        when(widgetComponentService.getActiveForCurrentUserAndDevice()).thenReturn(listUiComponentInfo);

        //act:
        Object resultObject = instance.list();

        //assert:
        verify(widgetComponentService).getActiveForCurrentUserAndDevice();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testGet() {
        System.out.println("get");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(widgetComponentService.get(A_RANDOM_ID)).thenReturn(listUiComponentInfo.get(0));

        //act:
        Object resultObject = instance.get(A_RANDOM_ID);

        //assert:
        verify(widgetComponentService).get(A_RANDOM_ID);
        checkName(expName, resultObject);
    }

    @Test
    public void testDownload() {
        System.out.println("download");

        //arrange:
        when(widgetComponentService.get(A_RANDOM_ID)).thenReturn(listUiComponentInfo.get(0));
        // following code mocks the service in return statement of method tested -> no check on return
        when(widgetComponentService.getWidgetData(A_KNOWN_UI_COMPONENT_NAME1, TD_DEFAULT)).thenReturn(new DataHandler("<some_xml/>", "text/xml"));

        //act:
        instance.download(A_RANDOM_ID, TD_DEFAULT);

        //assert:
        verify(widgetComponentService).get(A_RANDOM_ID);
        verify(widgetComponentService).getWidgetData(A_KNOWN_UI_COMPONENT_NAME1, TD_DEFAULT);
    }
}
