/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.auth.role.Role;
import org.cmdbuild.cardfilter.StoredFilter;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.etl.gate.inner.EtlGate;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.endpoint.SearchWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.uicomponents.data.UiComponentData;
import org.cmdbuild.view.View;
import org.cmdbuild.workflow.model.Process;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.dao.entrytype.ClassMetadata.ClassSpeciality.CS_DMSMODEL;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ClassSpeciality.CS_PROCESS;
import static org.cmdbuild.lookup.LookupSpeciality.LS_DEFAULT;
import static org.cmdbuild.lookup.LookupSpeciality.LS_DMSCATEGORY;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.uicomponents.data.UiComponentType.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class SearchWs_Test extends WsTestBase {


    private final String A_KNOWN_SEARCH_TYPE1 = "classes";
    private final String A_KNOWN_SEARCH_TYPE2 = "processes";
    private final String A_KNOWN_SEARCH_TYPE3 = "dms/models";
    private final String A_KNOWN_SEARCH_TYPE4 = "domains";
    private final String A_KNOWN_SEARCH_TYPE5 = "lookup/types";
    private final String A_KNOWN_SEARCH_TYPE6 = "dms/categories";
    private final String A_KNOWN_SEARCH_TYPE7 = "dashboards";
    private final String A_KNOWN_SEARCH_TYPE8 = "reports";
    private final String A_KNOWN_SEARCH_TYPE9 = "custompages";
    private final String A_KNOWN_SEARCH_TYPE10 = "components/contextmenu";
    private final String A_KNOWN_SEARCH_TYPE11 = "components/widget";
    private final String A_KNOWN_SEARCH_TYPE12 = "roles";
    private final String A_KNOWN_SEARCH_TYPE13 = "jobs";
    private final String A_KNOWN_SEARCH_TYPE14 = "etl/templates";
    private final String A_KNOWN_SEARCH_TYPE15 = "etl/gates";
    private final String A_KNOWN_SEARCH_TYPE16 = "filters";
    private final String A_KNOWN_SEARCH_TYPE17 = "views";
    private final String A_KNOWN_SEARCH_TYPE18 = "busdescriptors";
    private final String A_KNOWN_SEARCH_TYPE19 = "DEFAULT";

    private final SearchWs instance;
    private final WsQueryOptions queryOptions;
    private final EtlTemplate etlTemplate;

    public SearchWs_Test() {

        instance = new SearchWs(userClassService, daoService, lookupService, dashboardService, reportService, uiComponentRepository, roleRepository, jobService, etlGateService, etlTemplateService, cardFilterService, viewService, workflowService, userDomainService, waterwayDescriptorService);

        queryOptions = new WsQueryOptions(list(""), Boolean.TRUE, "", "", "", 5L, 0L, Boolean.TRUE, Long.MIN_VALUE, Boolean.TRUE);

        etlTemplate = mockBuildEtlTemplate(A_KNOWN_ETL_TEMPLATE_NAME1, ACTIVE);
    }

    @Test
    public void testSearchFail_Management() {
        System.out.println("searchFail_Management");

        //act:
        try {
            instance.search(A_KNOWN_SEARCH_TYPE1, queryOptions);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }
    }

    @Test
    public void testSearch_switchClasses() {
        System.out.println("search_switchClasses");

        //arrange:
        Classe expClasse1 = mockBuildClasseWithAttr("classeName1");
        Classe expClasse2 = mockBuildClasseWithAttr("classeName2");
        Classe expClasse3 = mockBuildClasseWithAttr("classeName3");
        List<Classe> expListClasse = list(expClasse1, expClasse2, expClasse3);
        List<String> expListClasseNames = list("classeName1", "classeName2", "classeName3");
        when(userClassService.getAllUserClasses()).thenReturn(expListClasse);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE1, queryOptions);

        //assert:
        verify(userClassService).getAllUserClasses();
        checkListNames(expListClasseNames, resultObject);
    }

    @Test
    public void testSearch_switchProcesses() {
        System.out.println("search_switchProcesses");

        //arrange:
        List<String> expListClasseNames = list(A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2, A_KNOWN_CLASS_NAME3);
        Classe classe1 = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME1, CS_PROCESS);
        Classe classe2 = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME2, CS_PROCESS);
        Classe classe3 = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME3, CS_PROCESS);
        org.cmdbuild.workflow.model.Process process1 = mockBuildProcess(classe1);
        org.cmdbuild.workflow.model.Process process2 = mockBuildProcess(classe2);
        org.cmdbuild.workflow.model.Process process3 = mockBuildProcess(classe3);
        List<Process> listProcess = list(process1, process2, process3);
        when(workflowService.getAllProcessClasses()).thenReturn(listProcess);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE2, queryOptions);

        //assert:
        verify(workflowService).getAllProcessClasses();
        checkListNames(expListClasseNames, resultObject);
    }

    @Test
    public void testSearch_switchDmsModels() {
        System.out.println("search_switchDmsModels");

        //arrange:
        List<String> expListClasseNames = list(A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2, A_KNOWN_CLASS_NAME3);
        Classe classe1 = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME1, CS_DMSMODEL);
        Classe classe2 = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME2, CS_DMSMODEL);
        Classe classe3 = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME3, CS_DMSMODEL);
        List<Classe> listClasse = list(classe1, classe2, classe3);
        when(daoService.getAllClasses()).thenReturn(listClasse);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE3, queryOptions);

        //assert:
        verify(daoService).getAllClasses();
        checkListNames(expListClasseNames, resultObject);
    }

    @Test
    public void testSearch_switchDomains() {
        System.out.println("search_switchDomains");

        //arrange:
        List<String> expListDomainNames = list("sourceClass1targetClass1", "sourceClass2targetClass2");
        List<Domain> listDomain = mockBuildDomainsList();
        when(userDomainService.getUserDomains()).thenReturn(listDomain);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE4, queryOptions);

        //assert:
        checkListNames(expListDomainNames, resultObject);
    }

    @Test
    public void testSearch_switchLookupTypes() {
        System.out.println("search_switchLookupTypes");

        //arrange:
        List<String> expListLookupTypeNames = list(A_KNOWN_LOOKUP_NAME1, A_KNOWN_LOOKUP_NAME2, A_KNOWN_LOOKUP_NAME3);
        PagedElements<LookupType> pagedElementsLookupType = mockBuildPagedElementsLookupType(LS_DEFAULT);
        when(lookupService.getAllTypes()).thenReturn(pagedElementsLookupType);
        PagedElements<LookupValue> listLookupValue = mockBuildPagedElementsLookupValue2();
        when(lookupService.getAllLookup(any(LookupType.class))).thenReturn(listLookupValue);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE5, queryOptions);

        //assert:
        verify(lookupService).getAllTypes();
        verify(lookupService, atLeastOnce()).getAllLookup(any(LookupType.class));
        checkListNames(expListLookupTypeNames, resultObject);
    }

    @Test
    public void testSearch_switchDmsCategories() {
        System.out.println("search_switchDmsCategories");

        //arrange:
        List<String> expListNames = list(A_KNOWN_LOOKUP_NAME1, A_KNOWN_LOOKUP_NAME2, A_KNOWN_LOOKUP_NAME3);
        PagedElements<LookupType> pagedElementsLookupType = mockBuildPagedElementsLookupType(LS_DMSCATEGORY);
        when(lookupService.getAllTypes()).thenReturn(pagedElementsLookupType);
        PagedElements<LookupValue> listLookupValue = mockBuildPagedElementsLookupValue2();
        when(lookupService.getAllLookup(any(LookupType.class))).thenReturn(listLookupValue);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE6, queryOptions);

        //assert:
        verify(lookupService).getAllTypes();
        verify(lookupService, atLeastOnce()).getAllLookup(any(LookupType.class));
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testSearch_switchDashboards() {
        System.out.println("search_switchDashboards");

        //arrange:
        List<String> expListDashboardDataNames = list(A_KNOWN_DASHBOARD_NAME1, A_KNOWN_DASHBOARD_NAME2, A_KNOWN_DASHBOARD_NAME3);
        List<DashboardData> listDashboardData = mockBuildListDashBoardData();
        when(dashboardService.getAll()).thenReturn(listDashboardData);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE7, queryOptions);

        //assert:
        verify(dashboardService).getAll();
        checkListNames(expListDashboardDataNames, resultObject);
    }

    @Test
    public void testSearch_switchReports() {
        System.out.println("search_switchReports");

        //arrange:
        List<String> expListReportInfoNames = list("reportInfoName1", "reportInfoName2", "reportInfoName3");
        List<ReportInfo> listReportInfo = mockBuildReportInfoList();
        when(reportService.getAll()).thenReturn(listReportInfo);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE8, queryOptions);

        //assert:
        verify(reportService).getAll();
        checkListNames(expListReportInfoNames, resultObject);
    }

    //TODO sistemare mockBuildListUiComponent
    @Test
    public void testSearch_switchCustompages() {
        System.out.println("search_switchCustompages");

        //arrange:
        List<String> expListUiComponentDataNames = list(A_KNOWN_UI_COMPONENT_DATA_NAME1, A_KNOWN_UI_COMPONENT_DATA_NAME2, A_KNOWN_UI_COMPONENT_DATA_NAME3);

        // have to create uicomponents with "data" or throws error
        List<UiComponentData> listUiComponentData = mockBuildListUiComponent(UCT_CUSTOMPAGE);
        when(uiComponentRepository.getAllByType(UCT_CUSTOMPAGE)).thenReturn(listUiComponentData);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE9, queryOptions);

        //assert:
        verify(uiComponentRepository).getAllByType(UCT_CUSTOMPAGE);
        checkListNames(expListUiComponentDataNames, resultObject);
    }

    //TODO sistemare mockBuildListUiComponent
    @Test
    public void testSearch_switchContextmenu() {
        System.out.println("search_switchContextmenu");

        //arrange:
        List<String> expListUiComponentDataNames = list(A_KNOWN_UI_COMPONENT_DATA_NAME1, A_KNOWN_UI_COMPONENT_DATA_NAME2, A_KNOWN_UI_COMPONENT_DATA_NAME3);
        List<UiComponentData> listUiComponentData = mockBuildListUiComponent(UCT_CONTEXTMENU);
        when(uiComponentRepository.getAllByType(UCT_CONTEXTMENU)).thenReturn(listUiComponentData);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE10, queryOptions);

        //assert:
        verify(uiComponentRepository).getAllByType(UCT_CONTEXTMENU);
        checkListNames(expListUiComponentDataNames, resultObject);
    }

    //TODO sistemare mockBuildListUiComponent
    @Test
    public void testSearch_switchWidget() {
        System.out.println("search_switchWidget");

        //arrange:
        List<String> expListUiComponentDataNames = list(A_KNOWN_UI_COMPONENT_DATA_NAME1, A_KNOWN_UI_COMPONENT_DATA_NAME2, A_KNOWN_UI_COMPONENT_DATA_NAME3);
        List<UiComponentData> listUiComponentData = mockBuildListUiComponent(UCT_WIDGET);
        when(uiComponentRepository.getAllByType(UCT_WIDGET)).thenReturn(listUiComponentData);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE11, queryOptions);

        //assert:
        verify(uiComponentRepository).getAllByType(UCT_WIDGET);
        checkListNames(expListUiComponentDataNames, resultObject);
    }

    @Test
    public void testSearch_switchRoles() {
        System.out.println("search_switchRoles");

        //arrange:
        List<Long> expListIds = list(A_KNOWN_ROLE_ID1, A_KNOWN_ROLE_ID2, A_KNOWN_ROLE_ID3);
        List<Role> listRole = mockBuildRoleList();
        when(roleRepository.getAllGroups()).thenReturn(listRole);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE12, queryOptions);

        //assert:
        verify(roleRepository).getAllGroups();
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testSearch_switchJobs() {
        System.out.println("search_switchJobs");

        //arrange:
        List<Long> expListIds = list(A_KNOWN_JOBDATA_ID1, A_KNOWN_JOBDATA_ID2, A_KNOWN_JOBDATA_ID3);
        List<JobData> listJobData = mockBuildJobDataList();
        when(jobService.getAllJobs()).thenReturn(listJobData);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE13, queryOptions);

        //assert:
        verify(jobService).getAllJobs();
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testSearch_switchEtlTemplates() {
        System.out.println("search_switchEtlTemplates");

        //arrange:
        List<String> expListNames = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        List<EtlTemplate> listEtlTemplate = mockBuildListEtlTemplate();
        when(etlTemplateService.getTemplates()).thenReturn(listEtlTemplate);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE14, queryOptions);

        //assert:
        verify(etlTemplateService).getTemplates();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testSearch_switchEtlGates() {
        System.out.println("search_switchEtlGates");

        //arrange:
        List<String> expListNames = list(A_KNOWN_ETL_GATE_NAME1, A_KNOWN_ETL_GATE_NAME2, A_KNOWN_ETL_GATE_NAME3);
        // PROBLEMA NELL'INSTANZIARE ETLGATES
        List<EtlGate> listEtlGate = mockBuildListEtlGate();
        when(etlGateService.getAll()).thenReturn(listEtlGate);
        when(etlTemplateService.getTemplateByName(A_KNOWN_ETL_TEMPLATE_NAME1)).thenReturn(etlTemplate);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE15, queryOptions);

        //assert:
        verify(etlGateService).getAll();
        verify(etlTemplateService, times(3)).getTemplateByName(A_KNOWN_ETL_TEMPLATE_NAME1);
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testSearch_switchFilters() {
        System.out.println("search_switchFilters");

        //arrange:
        List<Long> expIds = list(A_KNOWN_STORED_FILTER_ID1, A_KNOWN_STORED_FILTER_ID2);
        List<StoredFilter> listStoredFilter = mockBuildTwoStoredFilters(A_KNOWN_STORED_FILTER_ID1, A_KNOWN_STORED_FILTER_ID2);
        when(cardFilterService.readAllSharedFilters()).thenReturn(listStoredFilter);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE16, queryOptions);

        //assert:
        verify(cardFilterService).readAllSharedFilters();
        checkListIds(expIds, resultObject);
    }

    @Test
    public void testSearch_switchViews() {
        System.out.println("search_switchViews");

        //arrange:
        List<String> expListNames = list(A_KNOWN_VIEW_NAME1, A_KNOWN_VIEW_NAME2, A_KNOWN_VIEW_NAME3);
        List<View> listView = mockBuildListView();
        when(viewService.getAllSharedViews()).thenReturn(listView);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE17, queryOptions);

        //assert:
        verify(viewService).getAllSharedViews();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testSearch_switchBusdescriptors() {
        System.out.println("search_switchBusdescriptors");

        //arrange:
        List<String> expIds = list(A_KNOWN_CODE1, A_KNOWN_CODE2, A_KNOWN_CODE3);
        List<WaterwayDescriptorRecord> listWaterwayDescriptorRecord = mockBuildWaterwayDescriptorRecordList();
        when(waterwayDescriptorService.getAllDescriptors()).thenReturn(listWaterwayDescriptorRecord);

        //act:
        Object resultObject = instance.search(A_KNOWN_SEARCH_TYPE18, queryOptions);

        //assert:
        verify(waterwayDescriptorService).getAllDescriptors();
        checkResultList(expIds, resultObject, "name");
    }

    @Test
    public void testSearch_switchDefault() {
        System.out.println("search_switchDefault");

        //act:
        try {
            instance.search(A_KNOWN_SEARCH_TYPE19, queryOptions);
        } catch (RuntimeException ex) {
            System.out.println("Operation failed successfully");
        }
    }
}
