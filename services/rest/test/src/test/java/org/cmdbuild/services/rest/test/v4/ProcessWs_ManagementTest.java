/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ProcessWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ProcessWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ProcessWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.XpdlInfo;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class ProcessWs_ManagementTest extends WsTestBase {

    private final ProcessWs_Management instance;

    private final String A_PARAM_TO_CHECK = "widgets";

    private final org.cmdbuild.workflow.model.Process process;
    private final TaskDefinition taskDefinition;
    private final ExtendedClass extendedClass;
    private final Classe classe;
    private final XpdlInfo xpdlInfo;
    private final Widget widget;
    private final PagedElements<LookupValue> lookupValuePagedElements;

    public ProcessWs_ManagementTest() {

        ClassSerializationHelper classSerializationHelper = mockBuildClassSerializationHelper();
        ProcessWsSerializationHelper processWsSerializationHelper = mockBuildProcessWsSerializationHelper();
        ProcessWsCommand command = new ProcessWsCommand(workflowService, userClassService, classSerializationHelper);
        instance = new ProcessWs_Management(workflowService, userClassService, classSerializationHelper, processWsSerializationHelper, command);

        process = mockBuildProcess();
        taskDefinition = mockBuildTaskDefinition();
        extendedClass = mockBuildExtendedClass(A_KNOWN_CLASS_NAME1);
        classe = mockBuildClasse(A_KNOWN_CLASS_NAME1);
        xpdlInfo = mockBuildXpdlInfo();
        widget = mockBuildWidget();
        lookupValuePagedElements = mockBuildPagedElementsLookupValue();
    }

    @Test
    public void testReadAll_Detailed() {
        System.out.println("readAll_Detailed");

        //arrange:
        List<String> expListNames = list("class1");
        when(workflowService.getActiveProcessClasses()).thenReturn(list(process));
        when(userClassService.getExtendedClass(anyString(), any(UserClassService.ClassQueryFeatures.class), any(UserClassService.ClassQueryFeatures.class))).thenReturn(extendedClass);
        when(workflowConfiguration.enableSaveButton()).thenReturn(true);
        when(workflowService.getTaskDefinitions(anyString())).thenReturn(list(taskDefinition));
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(lookupValuePagedElements);

        //act:
        Object resultObject = instance.readAll(A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, DETAILED);

        //assert:
        verify(workflowService).getActiveProcessClasses();
        verify(userClassService).getExtendedClass(anyString(), any(UserClassService.ClassQueryFeatures.class), any(UserClassService.ClassQueryFeatures.class));
        verify(workflowConfiguration).enableSaveButton();
        verify(workflowService).getTaskDefinitions(anyString());
        verify(widgetService).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService).getAllLookup(anyString());

        checkDetailedList(resultObject, A_PARAM_TO_CHECK);
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_NotDetailed() {
        System.out.println("readAll_NotDetailed");

        //arrange:
        List<String> expListNames = list(A_KNOWN_CLASS_NAME1);
        when(workflowService.getActiveProcessClasses()).thenReturn(list(process));
        when(userClassService.getUserClass(anyString())).thenReturn(classe);
        when(workflowConfiguration.enableSaveButton()).thenReturn(true);

        //act:
        Object resultObject = instance.readAll(A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, NOT_DETAILED);

        //assert:
        verify(workflowService).getActiveProcessClasses();
        verify(userClassService).getUserClass(anyString());
        verify(workflowConfiguration).enableSaveButton();
        checkListNames(expListNames, resultObject);
        checkNotDetailedList(resultObject, A_PARAM_TO_CHECK);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        String expName = A_KNOWN_CLASS_NAME1;
        when(workflowService.getProcess(A_KNOWN_PROCESS_ID)).thenReturn(process);
        when(userClassService.getExtendedClass(anyString(), any(UserClassService.ClassQueryFeatures.class), any(UserClassService.ClassQueryFeatures.class))).thenReturn(extendedClass);
        when(workflowConfiguration.enableSaveButton()).thenReturn(true);
        when(workflowService.getTaskDefinitions(anyString())).thenReturn(list(taskDefinition));
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(lookupValuePagedElements);

        //act:
        Object resultObject = instance.read(A_KNOWN_PROCESS_ID);

        //assert:
        verify(workflowService).getProcess(A_KNOWN_PROCESS_ID);
        verify(userClassService).getExtendedClass(anyString(), any(UserClassService.ClassQueryFeatures.class), any(UserClassService.ClassQueryFeatures.class));
        verify(workflowConfiguration).enableSaveButton();
        verify(workflowService).getTaskDefinitions(anyString());
        verify(widgetService).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService).getAllLookup(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testUploadNewXpdlVersion_Replace() {
        System.out.println("uploadNewXpdlVersion_Replace");

        //arrange:
        String expId = A_KNOWN_XPDL_ID;
        when(workflowService.addXpdlReplaceCurrent(anyString(), any(DataSource.class))).thenReturn(xpdlInfo);
        DataHandler dataHandler = mock(DataHandler.class);

        //act:
        Object resultObject = instance.uploadNewXpdlVersion(A_KNOWN_XPDL_ID, dataHandler, true);

        //assert:
        verify(workflowService).addXpdlReplaceCurrent(anyString(), any(DataSource.class));
        checkId(expId, resultObject);
    }

    @Test
    public void testUploadNewXpdlVersion_NotReplace() {
        System.out.println("uploadNewXpdlVersion_NotReplace");

        //arrange:
        String expId = A_KNOWN_XPDL_ID;
        when(workflowService.addXpdl(anyString(), any(DataSource.class))).thenReturn(xpdlInfo);
        DataHandler dataHandler = mock(DataHandler.class);

        //act:
        Object resultObject = instance.uploadNewXpdlVersion(A_KNOWN_XPDL_ID, dataHandler, false);

        //assert:
        verify(workflowService).addXpdl(anyString(), any(DataSource.class));
        checkId(expId, resultObject);
    }

    @Test
    public void testGetAllXpdlVersions_WorkflowEnabled() {
        System.out.println("getAllXpdlVersions_WorkflowEnabled");

        //arrange:
        List<String> expListIds = list(A_KNOWN_XPDL_ID);
        when(workflowService.isWorkflowEnabled()).thenReturn(true);
        when(workflowService.getXpdlInfosOrderByVersionDesc(anyString())).thenReturn(list(xpdlInfo));

        //act:
        Object resultObject = instance.getAllXpdlVersions(A_KNOWN_XPDL_ID);

        //assert:
        verify(workflowService).isWorkflowEnabled();
        verify(workflowService).getXpdlInfosOrderByVersionDesc(anyString());
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testGetAllXpdlVersions_WorkflowNotEnabled() {
        System.out.println("getAllXpdlVersions_WorkflowNotEnabled");

        //arrange:
        when(workflowService.isWorkflowEnabled()).thenReturn(false);

        //act:
        Object resultObject = instance.getAllXpdlVersions(A_KNOWN_PROCESS_ID);

        //assert:
        verify(workflowService).isWorkflowEnabled();
        checkListIds(list(), resultObject);
    }

    @Test
    public void testGetXpdlVersionFile() {
        System.out.println("getXpdlVersionFile");

        //arrange:
        DataSource dataSource = null;
        when(workflowService.getXpdlByClasseIdAndPlanId(anyString(), anyString())).thenReturn(dataSource);

        //act:
        DataHandler resultDataHandler = instance.getXpdlVersionFile(A_KNOWN_PROCESS_ID, A_KNOWN_PLAN_ID);

        //assert:
        verify(workflowService).getXpdlByClasseIdAndPlanId(anyString(), anyString());
        assertNotNull(resultDataHandler);
    }

    @Test
    public void testGetXpdlTemplateFile() {
        System.out.println("getXpdlTemplateFile");

        //arrange:
        DataSource dataSource = null;
        when(workflowService.getXpdlTemplate(anyString())).thenReturn(dataSource);

        //act:
        DataHandler resultDataHandler = instance.getXpdlTemplateFile(A_KNOWN_PROCESS_ID);

        //assert:
        verify(workflowService).getXpdlTemplate(anyString());
        assertNotNull(resultDataHandler);
    }

}
