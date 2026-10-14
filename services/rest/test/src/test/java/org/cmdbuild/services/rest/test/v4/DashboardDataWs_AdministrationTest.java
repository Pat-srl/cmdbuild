/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.service.rest.v4.command.DashboardWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.DashboardWs_Administration;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildDashboardData;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListDashBoardData;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsDashboardData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class DashboardDataWs_AdministrationTest extends WsTestBase {

    private final DashboardWs_Administration instance;

    private final DashboardData dashboardData;
    private final List<DashboardData> dashboardDataList;

    public DashboardDataWs_AdministrationTest() {
        DashboardWsCommand command = new DashboardWsCommand(dashboardService);
        instance = new DashboardWs_Administration(objectTranslationService, dashboardService, command);
        dashboardData = mockBuildDashboardData(A_KNOWN_DASHBOARD_NAME1);
        dashboardDataList = mockBuildListDashBoardData();
    }

    @Test
    public void testGetAll_Detailed() {
        System.out.println("getAll_Detailed");

        //arrange:
        List<String> expListNames = list(A_KNOWN_DASHBOARD_NAME1, A_KNOWN_DASHBOARD_NAME2, A_KNOWN_DASHBOARD_NAME3);
        when(dashboardService.getAll()).thenReturn(dashboardDataList);
        when(objectTranslationService.translateDashboardComponentDescription(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateDashboardDescription(anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateDashboardParametersDescription(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.getAll(DETAILED, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(dashboardService).getAll();
        verify(objectTranslationService, times(dashboardDataList.size())).translateDashboardComponentDescription(anyString(), anyString(), anyString());
        verify(objectTranslationService, times(dashboardDataList.size())).translateDashboardDescription(anyString(), anyString());
        verify(objectTranslationService, times(dashboardDataList.size())).translateDashboardParametersDescription(anyString(), anyString(), anyString());
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testGetAll_NotDetailed() {
        System.out.println("getAll_NotDetailed");

        //arrange:
        List<String> expListNames = list(A_KNOWN_DASHBOARD_NAME1, A_KNOWN_DASHBOARD_NAME2, A_KNOWN_DASHBOARD_NAME3);
        when(dashboardService.getAll()).thenReturn(dashboardDataList);
        when(objectTranslationService.translateDashboardDescription(anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.getAll(NOT_DETAILED, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(dashboardService).getAll();
        verify(objectTranslationService, times(dashboardDataList.size())).translateDashboardDescription(anyString(), anyString());
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadOne() {
        System.out.println("readOne");

        //arrange:
        String expName = A_KNOWN_DASHBOARD_NAME1;
        when(dashboardService.getByIdOrCode(A_KNOWN_DASHBOARD_NAME1)).thenReturn(dashboardData);
        when(objectTranslationService.translateDashboardComponentDescription(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateDashboardDescription(anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateDashboardParametersDescription(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.readOne(A_KNOWN_DASHBOARD_NAME1);

        //assert:
        verify(dashboardService).getByIdOrCode(A_KNOWN_DASHBOARD_NAME1);
        verify(objectTranslationService).translateDashboardComponentDescription(anyString(), anyString(), anyString());
        verify(objectTranslationService).translateDashboardDescription(anyString(), anyString());
        verify(objectTranslationService).translateDashboardParametersDescription(anyString(), anyString(), anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testCreate() throws JsonProcessingException {
        System.out.println("create");

        //arrange:
        String expName = A_KNOWN_DASHBOARD_NAME1;
        when(dashboardService.create(any(DashboardData.class))).thenReturn(dashboardData);
        when(objectTranslationService.translateDashboardComponentDescription(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateDashboardDescription(anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateDashboardParametersDescription(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.create(mockBuildWsDashboardData());

        //assert:
        verify(dashboardService).create(any(DashboardData.class));
        verify(objectTranslationService).translateDashboardComponentDescription(anyString(), anyString(), anyString());
        verify(objectTranslationService).translateDashboardDescription(anyString(), anyString());
        verify(objectTranslationService).translateDashboardParametersDescription(anyString(), anyString(), anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate() throws JsonProcessingException {
        System.out.println("update");

        //arrange:
        String expName = A_KNOWN_DASHBOARD_NAME1;
        when(dashboardService.update(any(DashboardData.class))).thenReturn(dashboardData);
        when(objectTranslationService.translateDashboardComponentDescription(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateDashboardDescription(anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateDashboardParametersDescription(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.update(3L, mockBuildWsDashboardData());

        //assert:
        verify(dashboardService).update(any(DashboardData.class));
        verify(objectTranslationService).translateDashboardComponentDescription(anyString(), anyString(), anyString());
        verify(objectTranslationService).translateDashboardDescription(anyString(), anyString());
        verify(objectTranslationService).translateDashboardParametersDescription(anyString(), anyString(), anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testDelete() throws JsonProcessingException {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(3L);

        //assert:
        verify(dashboardService).delete(anyLong());
        checkSuccess(resultObject);
    }
}
