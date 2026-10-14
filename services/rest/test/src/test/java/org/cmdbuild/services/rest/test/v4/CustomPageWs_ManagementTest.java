/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.service.rest.v4.command.CustomPageWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CustomPageWs_Management;
import org.cmdbuild.services.rest.test.common.TestHelper_Model;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListNames;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkName;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListUiComponentInfo;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class CustomPageWs_ManagementTest extends WsTestBase {

    private final CustomPageWs_Management instance;

    private final UiComponentInfo uiComponentInfo;
    private final List<UiComponentInfo> uiComponentInfoList;

    public CustomPageWs_ManagementTest() {
        CustomPageWsCommand command = new CustomPageWsCommand(customPageService);
        instance = new CustomPageWs_Management(customPageService, objectTranslationService, command);
        uiComponentInfo = TestHelper_Model.mockBuildUiComponentInfo(A_KNOWN_UI_COMPONENT_NAME1);
        uiComponentInfoList = mockBuildListUiComponentInfo();
    }

    @Test
    public void testList() {
        System.out.println("list");

        //arrange:
        List<String> expListNames = list(A_KNOWN_UI_COMPONENT_NAME1, A_KNOWN_UI_COMPONENT_NAME2, A_KNOWN_UI_COMPONENT_NAME3);
        when(customPageService.getActiveForCurrentUserAndDevice()).thenReturn(uiComponentInfoList);
        when(objectTranslationService.translateCustomPageDesciption(anyString(), anyString())).thenReturn("_description_translation");

        //act:
        Object resultObject = instance.list();

        //assert:
        verify(customPageService).getActiveForCurrentUserAndDevice();
        verify(objectTranslationService, times(expListNames.size())).translateCustomPageDesciption(anyString(), anyString());
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testGet() {
        System.out.println("get");

        //arrange:
        String expName = A_KNOWN_UI_COMPONENT_NAME1;
        when(customPageService.getForUser(A_RANDOM_ID)).thenReturn(uiComponentInfo);

        //act:
        Object resultObject = instance.get(A_RANDOM_ID);

        //assert:
        verify(customPageService).getForUser(A_RANDOM_ID);
        checkName(expName, resultObject);
    }
}
