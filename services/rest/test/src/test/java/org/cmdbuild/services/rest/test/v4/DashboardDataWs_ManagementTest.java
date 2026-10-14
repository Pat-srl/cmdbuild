/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.service.rest.v4.command.DashboardWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.DashboardWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListNames;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkName;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildDashboardData;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListDashBoardData;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class DashboardDataWs_ManagementTest extends WsTestBase {

    private final DashboardWs_Management instance;

    private final DashboardData dashboardData;
    private final List<DashboardData> dashboardDataList;

    public DashboardDataWs_ManagementTest() {
        DashboardWsCommand command = new DashboardWsCommand(dashboardService);
        instance = new DashboardWs_Management(objectTranslationService, dashboardService, command);
        dashboardData = mockBuildDashboardData(A_KNOWN_DASHBOARD_NAME1);
        dashboardDataList = mockBuildListDashBoardData();
    }

    @Test
    public void testGetAll_Detailed() {
        System.out.println("getAll_Detailed");

        //arrange:
        List<String> expListNames = list(A_KNOWN_DASHBOARD_NAME1, A_KNOWN_DASHBOARD_NAME2, A_KNOWN_DASHBOARD_NAME3);
        when(dashboardService.getActiveForCurrentUser()).thenReturn(dashboardDataList);
        when(objectTranslationService.translateDashboardComponentDescription(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateDashboardDescription(anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.getAll(DETAILED, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(dashboardService).getActiveForCurrentUser();
        verify(objectTranslationService, times(dashboardDataList.size())).translateDashboardComponentDescription(anyString(), anyString(), anyString());
        verify(objectTranslationService, times(dashboardDataList.size())).translateDashboardDescription(anyString(), anyString());
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testGetAll_NotDetailed() {
        System.out.println("getAll_NotDetailed");

        //arrange:
        List<String> expListNames = list(A_KNOWN_DASHBOARD_NAME1, A_KNOWN_DASHBOARD_NAME2, A_KNOWN_DASHBOARD_NAME3);
        when(dashboardService.getActiveForCurrentUser()).thenReturn(dashboardDataList);
        when(objectTranslationService.translateDashboardDescription(anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.getAll(NOT_DETAILED, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(dashboardService).getActiveForCurrentUser();
        verify(objectTranslationService, times(dashboardDataList.size())).translateDashboardDescription(anyString(), anyString());
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadOne() {
        System.out.println("readOne");

        //arrange:
        String expName = A_KNOWN_DASHBOARD_NAME1;
        when(dashboardService.getForUserByIdOrCode(A_KNOWN_DASHBOARD_NAME1)).thenReturn(dashboardData);
        when(objectTranslationService.translateDashboardComponentDescription(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");
        when(objectTranslationService.translateDashboardDescription(anyString(), anyString())).thenReturn("examp_description_translation");

        //act:
        Object resultObject = instance.readOne(A_KNOWN_DASHBOARD_NAME1);

        //assert:
        verify(dashboardService).getForUserByIdOrCode(A_KNOWN_DASHBOARD_NAME1);
        verify(objectTranslationService).translateDashboardComponentDescription(anyString(), anyString(), anyString());
        verify(objectTranslationService).translateDashboardDescription(anyString(), anyString());
        checkName(expName, resultObject);
    }
}
