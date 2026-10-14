/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import org.cmdbuild.service.rest.v4.command.ContextMenuComponentWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ContextMenuComponentWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsContextMenuComponentData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsContextMenuComponentData;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class ContextMenuComponentWs_AdministrationTest extends WsTestBase {

    private final ContextMenuComponentWs_Administration instance;

    private final List<UiComponentInfo> uiComponentInfoList;
    private final DataHandler dataHandler;
    private final List<DataHandler> dataHandlerList;
    private final WsContextMenuComponentData wsContextMenuComponentData;

    public ContextMenuComponentWs_AdministrationTest() {
        ContextMenuComponentWsCommand command = new ContextMenuComponentWsCommand(contextMenuComponentService);
        this.instance = new ContextMenuComponentWs_Administration(command);

        this.uiComponentInfoList = mockBuildListUiComponentInfo();
        this.dataHandler = mockBuildDataHandler(A_TEST_DATAHANDLER_TYPE);
        this.dataHandlerList = mockBuildDataHandlerList();
        this.wsContextMenuComponentData = mockBuildWsContextMenuComponentData();
    }

    @Test
    public void testList() {
        System.out.println("list");

        //arrange:
        when(contextMenuComponentService.getAll()).thenReturn(uiComponentInfoList);

        //act:
        Object resultObject = instance.list();

        //assert:
        verify(contextMenuComponentService).getAll();
        checkResultList(list(A_KNOWN_UI_COMPONENT_NAME1, A_KNOWN_UI_COMPONENT_NAME2, A_KNOWN_UI_COMPONENT_NAME3), resultObject, "name");
    }

    @Test
    public void testGet() {
        System.out.println("get");

        //arrange:
        when(contextMenuComponentService.get(A_KNOWN_LONG_ID1)).thenReturn(uiComponentInfoList.get(0));

        //act:
        Object resultObject = instance.get(A_KNOWN_LONG_ID1);

        //assert:
        verify(contextMenuComponentService).get(A_KNOWN_LONG_ID1);
        checkResult(A_KNOWN_UI_COMPONENT_NAME1, resultObject, "name");
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_LONG_ID1);

        //assert:
        verify(contextMenuComponentService).delete(A_KNOWN_LONG_ID1);
        checkSuccess(resultObject);
    }

    @Test
    public void testDeleteForTargetDevice() {
        System.out.println("deleteForTargetDevice");

        //act:
        Object resultObject = instance.deleteForTargetDevice(A_KNOWN_LONG_ID1, TD_DEFAULT);

        //assert:
        verify(contextMenuComponentService).deleteForTargetDevice(A_KNOWN_LONG_ID1, TD_DEFAULT);
        checkSuccess(resultObject);
    }

    @Test
    public void testDownload() {
        System.out.println("download");

        //arrange:
        when(contextMenuComponentService.get(A_KNOWN_LONG_ID1)).thenReturn(uiComponentInfoList.get(0));
        when(contextMenuComponentService.getContextMenuData(anyString(), eq(TD_DEFAULT))).thenReturn(dataHandler);

        //act:
        Object resutlObject = instance.download(A_KNOWN_LONG_ID1, TD_DEFAULT);

        //assert:
        verify(contextMenuComponentService).get(A_KNOWN_LONG_ID1);
        verify(contextMenuComponentService).getContextMenuData(anyString(), eq(TD_DEFAULT));
        checkDataHandler("text/plain", resutlObject);
    }

    @Test
    public void testCreate_merge() {
        System.out.println("create_merge");

        //arrange:
        when(contextMenuComponentService.createOrUpdate(anyList())).thenReturn(uiComponentInfoList.get(0));
        when(contextMenuComponentService.update(any(UiComponentInfo.class))).thenReturn(uiComponentInfoList.get(0));

        //act:
        Object resultObject = instance.create(dataHandlerList, MERGE, wsContextMenuComponentData);

        //assert:
        verify(contextMenuComponentService).createOrUpdate(anyList());
        verify(contextMenuComponentService).update(any(UiComponentInfo.class));
        checkResult(A_KNOWN_UI_COMPONENT_NAME1, resultObject, "name");
    }

    @Test
    public void testCreate_noMerge() {
        System.out.println("create_noMerge");

        //arrange:
        when(contextMenuComponentService.create(anyList())).thenReturn(uiComponentInfoList.get(0));
        when(contextMenuComponentService.update(any(UiComponentInfo.class))).thenReturn(uiComponentInfoList.get(0));

        //act:
        Object resultObject = instance.create(dataHandlerList, NOT_MERGE, wsContextMenuComponentData);

        //assert:
        verify(contextMenuComponentService).create(anyList());
        verify(contextMenuComponentService).update(any(UiComponentInfo.class));
        checkResult(A_KNOWN_UI_COMPONENT_NAME1, resultObject, "name");
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(contextMenuComponentService.get(A_KNOWN_LONG_ID1)).thenReturn(uiComponentInfoList.get(0));
        when(contextMenuComponentService.update(eq(A_KNOWN_LONG_ID1), anyList())).thenReturn(uiComponentInfoList.get(0));
        when(contextMenuComponentService.update(any(UiComponentInfo.class))).thenReturn(uiComponentInfoList.get(0));

        //act:
        Object resultObject = instance.update(A_KNOWN_LONG_ID1, dataHandlerList, wsContextMenuComponentData);

        //assert:
        verify(contextMenuComponentService).get(A_KNOWN_LONG_ID1);
        verify(contextMenuComponentService).update(eq(A_KNOWN_LONG_ID1), anyList());
        verify(contextMenuComponentService).update(any(UiComponentInfo.class));
    }
}
