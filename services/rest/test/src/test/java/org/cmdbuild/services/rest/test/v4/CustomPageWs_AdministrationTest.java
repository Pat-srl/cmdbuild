/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import jakarta.activation.DataHandler;
import org.cmdbuild.service.rest.v4.command.CustomPageWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CustomPageWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsCustomPageData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListUiComponentInfo;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildUiComponentInfo;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class CustomPageWs_AdministrationTest extends WsTestBase {


    private final CustomPageWs_Administration instance;

    private final UiComponentInfo uiComponentInfo;
    private final List<UiComponentInfo> uiComponentInfoList;
    private final List<DataHandler> dataHandlerList;
    private final WsCustomPageData wsCustomPageData;
    private final WsCustomPageData nullData;

    public CustomPageWs_AdministrationTest() {
        CustomPageWsCommand command = new CustomPageWsCommand(customPageService);
        instance = new CustomPageWs_Administration(customPageService, objectTranslationService, command);
        uiComponentInfo = mockBuildUiComponentInfo(A_KNOWN_UI_COMPONENT_NAME1);
        uiComponentInfoList = mockBuildListUiComponentInfo();
        dataHandlerList = list(new DataHandler("<some_xml/>", "text/xml"));
        wsCustomPageData = new WsCustomPageData("exampleDescription", true, null);
        nullData = null;
    }

    @Test
    public void testList() {
        System.out.println("list");

        //arrange:
        List<String> expListNames = list(A_KNOWN_UI_COMPONENT_NAME1, A_KNOWN_UI_COMPONENT_NAME2, A_KNOWN_UI_COMPONENT_NAME3);
        when(customPageService.getAll()).thenReturn(uiComponentInfoList);
        when(objectTranslationService.translateCustomPageDesciption(anyString(), anyString())).thenReturn("_description_translation");

        //act:
        Object resultObject = instance.list();

        //assert:
        verify(customPageService).getAll();
        verify(objectTranslationService, times(expListNames.size())).translateCustomPageDesciption(anyString(), anyString());
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testGet() {
        System.out.println("get");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(customPageService.get(A_RANDOM_ID)).thenReturn(uiComponentInfo);

        //act:
        Object resultObject = instance.get(A_RANDOM_ID);

        //assert:
        verify(customPageService).get(A_RANDOM_ID);
        checkName(expName, resultObject);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_RANDOM_ID);

        //assert:
        verify(customPageService).delete(A_RANDOM_ID);
        checkSuccess(resultObject);
    }

    @Test
    public void testDeleteForTargetDevice() {
        System.out.println("deleteForTargetDevice");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(customPageService.deleteForTargetDevice(A_RANDOM_ID, TD_DEFAULT)).thenReturn(uiComponentInfo);
        when(objectTranslationService.translateCustomPageDesciption(anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.deleteForTargetDevice(A_RANDOM_ID, TD_DEFAULT);

        //assert:
        verify(customPageService).deleteForTargetDevice(A_RANDOM_ID, TD_DEFAULT);
        verify(objectTranslationService).translateCustomPageDesciption(anyString(), anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testCreate_dataNotNull_MergeTrue() {
        System.out.println("create_dataNotNull_MergeTrue");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(customPageService.createOrUpdate(anyList())).thenReturn(uiComponentInfo);
        when(customPageService.update(any(UiComponentInfo.class))).thenReturn(uiComponentInfo);

        //act:
        Object resultObject = instance.create(dataHandlerList, wsCustomPageData, MERGE);

        //assert:
        verify(customPageService).createOrUpdate(anyList());
        verify(customPageService).update(any(UiComponentInfo.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testCreate_dataNull_MergeTrue() {
        System.out.println("create_dataNull_MergeTrue");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(customPageService.createOrUpdate(anyList())).thenReturn(uiComponentInfo);
        when(customPageService.update(any(UiComponentInfo.class))).thenReturn(uiComponentInfo);

        //act:
        Object resultObject = instance.create(dataHandlerList, nullData, MERGE);

        //assert:
        verify(customPageService).createOrUpdate(anyList());
        verify(customPageService).update(any(UiComponentInfo.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testCreate_dataNotNull_MergeFalse() {
        System.out.println("create_dataNotNull_MergeFalse");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(customPageService.create(anyList())).thenReturn(uiComponentInfo);
        when(customPageService.update(any(UiComponentInfo.class))).thenReturn(uiComponentInfo);

        //act:
        Object resultObject = instance.create(dataHandlerList, wsCustomPageData, NOT_MERGE);

        //assert:
        verify(customPageService).create(anyList());
        verify(customPageService).update(any(UiComponentInfo.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testCreate_dataNull_MergeFalse() {
        System.out.println("create_dataNull_MergeFalse");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(customPageService.create(anyList())).thenReturn(uiComponentInfo);
        when(customPageService.update(any(UiComponentInfo.class))).thenReturn(uiComponentInfo);

        //act:
        Object resultObject = instance.create(dataHandlerList, nullData, NOT_MERGE);

        //assert:
        verify(customPageService).create(anyList());
        verify(customPageService).update(any(UiComponentInfo.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate_dataNotNull() {
        System.out.println("update_dataNotNull");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(customPageService.get(A_RANDOM_ID)).thenReturn(uiComponentInfo);
        when(customPageService.update(eq(A_RANDOM_ID), anyList())).thenReturn(uiComponentInfo);
        when(customPageService.update(any(UiComponentInfo.class))).thenReturn(uiComponentInfo);

        //act:
        Object resultObject = instance.update(A_RANDOM_ID, dataHandlerList, wsCustomPageData);

        //assert:
        verify(customPageService).get(A_RANDOM_ID);
        verify(customPageService).update(eq(A_RANDOM_ID), anyList());
        verify(customPageService).update(any(UiComponentInfo.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate_dataNull() {
        System.out.println("update_dataNull");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(customPageService.get(A_RANDOM_ID)).thenReturn(uiComponentInfo);
        when(customPageService.update(eq(A_RANDOM_ID), anyList())).thenReturn(uiComponentInfo);

        //act:
        Object resultObject = instance.update(A_RANDOM_ID, dataHandlerList, nullData);

        //assert:
        verify(customPageService).get(A_RANDOM_ID);
        verify(customPageService).update(eq(A_RANDOM_ID), anyList());
        checkName(expName, resultObject);
    }

    @Test
    public void testDownload() {
        System.out.println("download");

        //arrange:
        when(customPageService.get(A_RANDOM_ID)).thenReturn(uiComponentInfo);
        when(customPageService.getCustomPageData(anyString(), any(TargetDevice.class))).thenReturn(new DataHandler("<some_xml/>", "text/xml"));

        //act:
        Object resultObject = instance.download(A_RANDOM_ID, TD_DEFAULT);

        //assert:
        verify(customPageService).get(A_RANDOM_ID);
        verify(customPageService).getCustomPageData(anyString(), any(TargetDevice.class));
        assertNotNull(resultObject);
    }
}
