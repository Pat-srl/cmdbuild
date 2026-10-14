/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import org.cmdbuild.service.rest.v4.command.WidgetWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.WidgetWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsWidgetComponentData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListUiComponentInfo;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class WidgetWs_AdministrationTest extends WsTestBase {

    private final WidgetWs_Administration instance;
    private final List<UiComponentInfo> listUiComponentInfo;
    private final DataHandler dataHandler;
    private final WsWidgetComponentData wsWidgetComponentData;

    public WidgetWs_AdministrationTest() {
        WidgetWsCommand command = new WidgetWsCommand(widgetComponentService);
        instance = new WidgetWs_Administration(widgetComponentService, command);
        listUiComponentInfo = mockBuildListUiComponentInfo();
        dataHandler = new DataHandler("<some_xml/>", "text/xml");
        wsWidgetComponentData = new WsWidgetComponentData("description", ACTIVE);
    }

    @Test
    public void testList() {
        System.out.println("list");

        //arrange:
        List<String> expListNames = list(A_KNOWN_UI_COMPONENT_NAME1, A_KNOWN_UI_COMPONENT_NAME2, A_KNOWN_UI_COMPONENT_NAME3);
        when(widgetComponentService.getAll()).thenReturn(listUiComponentInfo);

        //act:
        Object resultObject = instance.list();

        //assert:
        verify(widgetComponentService).getAll();
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

    @Test
    public void testDeleteForTargetDevice() {
        System.out.println("deleteForTargetDevice");

        //act:
        Object resultObject = instance.deleteForTargetDevice(A_RANDOM_ID, TD_DEFAULT);

        //assert:
        verify(widgetComponentService).deleteForTargetDevice(A_RANDOM_ID, TD_DEFAULT);
        checkSuccess(resultObject);
    }


    @Test
    public void testCreate_Merge() {
        System.out.println("create");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(widgetComponentService.createOrUpdate(anyList())).thenReturn(listUiComponentInfo.get(0));
        when(widgetComponentService.update(any(UiComponentInfo.class))).thenReturn(listUiComponentInfo.get(0));

        //act:
        Object resultObject = instance.create(list(dataHandler), wsWidgetComponentData, MERGE);

        //assert:
        verify(widgetComponentService).createOrUpdate(anyList());
        verify(widgetComponentService).update(any(UiComponentInfo.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testCreate_NoMerge() {
        System.out.println("create");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(widgetComponentService.create(anyList())).thenReturn(listUiComponentInfo.get(0));
        when(widgetComponentService.update(any(UiComponentInfo.class))).thenReturn(listUiComponentInfo.get(0));

        //act:
        Object resultObject = instance.create(list(dataHandler), wsWidgetComponentData, NOT_MERGE);

        //assert:
        verify(widgetComponentService).create(anyList());
        verify(widgetComponentService).update(any(UiComponentInfo.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate_Versions_Data() {
        System.out.println("update");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(widgetComponentService.get(A_RANDOM_ID)).thenReturn(listUiComponentInfo.get(0));
        when(widgetComponentService.update(eq(A_RANDOM_ID), anyList())).thenReturn(listUiComponentInfo.get(0));
        when(widgetComponentService.update(any(UiComponentInfo.class))).thenReturn(listUiComponentInfo.get(0));

        //act:
        Object resultObject = instance.update(A_RANDOM_ID, list(dataHandler), wsWidgetComponentData);

        //assert:
        verify(widgetComponentService).get(A_RANDOM_ID);
        verify(widgetComponentService).update(eq(A_RANDOM_ID), anyList());
        verify(widgetComponentService).update(any(UiComponentInfo.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate_NoVersions_Data() {
        System.out.println("update");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(widgetComponentService.get(A_RANDOM_ID)).thenReturn(listUiComponentInfo.get(0));
        when(widgetComponentService.update(any(UiComponentInfo.class))).thenReturn(listUiComponentInfo.get(0));

        //act:
        Object resultObject = instance.update(A_RANDOM_ID, emptyList(), wsWidgetComponentData);

        //assert:
        verify(widgetComponentService).get(A_RANDOM_ID);
        verify(widgetComponentService).update(any(UiComponentInfo.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate_Versions_NoData() {
        System.out.println("update");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(widgetComponentService.get(A_RANDOM_ID)).thenReturn(listUiComponentInfo.get(0));
        when(widgetComponentService.update(eq(A_RANDOM_ID), anyList())).thenReturn(listUiComponentInfo.get(0));

        //act:
        Object resultObject = instance.update(A_RANDOM_ID, list(dataHandler), null);

        //assert:
        verify(widgetComponentService).get(A_RANDOM_ID);
        verify(widgetComponentService).update(eq(A_RANDOM_ID), anyList());
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate_NoVersions_NoData() {
        System.out.println("update");

        //arrange:
        when(widgetComponentService.get(A_RANDOM_ID)).thenReturn(listUiComponentInfo.get(0));

        //act:
        try {
            instance.update(A_RANDOM_ID, emptyList(), null);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly thrown and cought exception");
        }

        //assert:
        verify(widgetComponentService).get(A_RANDOM_ID);
    }
}
