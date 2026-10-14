/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.EtlTemplateWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.EtlTemplateWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsEtlTemplateData;
import org.cmdbuild.services.rest.test.common.TestHelper_Variable;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.etl.loader.EtlTemplateTarget.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildClasseWithAttr;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListEtlTemplate;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsEtlTemplateData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class EtlTemplateWs_AdministrationTest extends WsTestBase {

    private final EtlTemplateWs_Administration instance;

    private final String paramToCheck1 = "fileFormat";

    private final WsQueryOptions wsQueryOptionsDetailed;
    private final WsQueryOptions wsQueryOptionsNotDetailed;
    private final List<EtlTemplate> listEtlTemplate;
    private final WsEtlTemplateData wsEtlTemplateData;
    private final Classe classe;


    public EtlTemplateWs_AdministrationTest() {

        EtlTemplateWsCommand command = new EtlTemplateWsCommand(etlTemplateService, etlTemplateInlineProcessorService);
        instance = new EtlTemplateWs_Administration(command);

        wsQueryOptionsDetailed = TestHelper_WSData.mockBuildWsQueryOptions(DETAILED);
        wsQueryOptionsNotDetailed = TestHelper_WSData.mockBuildWsQueryOptions(NOT_DETAILED);
        listEtlTemplate = mockBuildListEtlTemplate();
        wsEtlTemplateData = mockBuildWsEtlTemplateData();
        classe = mockBuildClasseWithAttr(A_KNOWN_CLASS_NAME1);
    }

    @Test
    public void testReadAll_Detailed() {
        System.out.println("readAll_Detailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        when(etlTemplateService.getAllForUser()).thenReturn(listEtlTemplate);
        //act:
        Object resultObject = instance.readAll(wsQueryOptionsDetailed);

        //assert:
        verify(etlTemplateService).getAllForUser();
        checkListCodes(expCodes, resultObject);
        checkDetailedList(resultObject, paramToCheck1);
    }

    @Test
    public void testReadAll_NotDetailed() {
        System.out.println("readAll_NotDetailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        when(etlTemplateService.getAllForUser()).thenReturn(listEtlTemplate);
        //act:
        Object resultObject = instance.readAll(wsQueryOptionsNotDetailed);

        //assert:
        verify(etlTemplateService).getAllForUser();
        checkListCodes(expCodes, resultObject);
        checkNotDetailedList(resultObject, paramToCheck1);
    }

    @Test
    public void testReadAllForClass_IncludeDomains_Detailed() {
        System.out.println("readAllForClass_IncludeDomains_Detailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        when(etlTemplateService.getForUserForTargetClassAndRelatedDomains(A_KNOWN_CLASS_ID)).thenReturn(listEtlTemplate);

        //act:
        Object resultObject = instance.readAllForClass(A_KNOWN_CLASS_ID, wsQueryOptionsDetailed, TestHelper_Variable.INCLUDE);

        //assert:
        verify(etlTemplateService).getForUserForTargetClassAndRelatedDomains(A_KNOWN_CLASS_ID);
        verify(etlTemplateService, times(0)).getForUserForTarget(any(), any());
        checkListCodes(expCodes, resultObject);
        checkDetailedList(resultObject, paramToCheck1);
    }

    @Test
    public void testReadAllForClass_IncludeDomains_NotDetailed() {
        System.out.println("readAllForClass_IncludeDomains_Detailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        when(etlTemplateService.getForUserForTargetClassAndRelatedDomains(A_KNOWN_CLASS_ID)).thenReturn(listEtlTemplate);

        //act:
        Object resultObject = instance.readAllForClass(A_KNOWN_CLASS_ID, wsQueryOptionsNotDetailed, TestHelper_Variable.INCLUDE);

        //assert:
        verify(etlTemplateService).getForUserForTargetClassAndRelatedDomains(A_KNOWN_CLASS_ID);
        verify(etlTemplateService, times(0)).getForUserForTarget(any(), any());
        checkListCodes(expCodes, resultObject);
        checkNotDetailedList(resultObject, paramToCheck1);
    }

    @Test
    public void testReadAllForClass_NotIncludeDomains_Detailed() {
        System.out.println("readAllForClass_IncludeDomains_Detailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        when(etlTemplateService.getForUserForTarget(ET_CLASS, A_KNOWN_CLASS_ID)).thenReturn(listEtlTemplate);

        //act:
        Object resultObject = instance.readAllForClass(A_KNOWN_CLASS_ID, wsQueryOptionsDetailed, TestHelper_Variable.DONT_INCLUDE);

        //assert:
        verify(etlTemplateService, times(0)).getForUserForTargetClassAndRelatedDomains(A_KNOWN_CLASS_ID);
        verify(etlTemplateService).getForUserForTarget(ET_CLASS, A_KNOWN_CLASS_ID);
        checkListCodes(expCodes, resultObject);
        checkDetailedList(resultObject, paramToCheck1);
    }

    @Test
    public void testReadAllForClass_NotIncludeDomains_NotDetailed() {
        System.out.println("readAllForClass_IncludeDomains_Detailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        when(etlTemplateService.getForUserForTarget(ET_CLASS, A_KNOWN_CLASS_ID)).thenReturn(listEtlTemplate);

        //act:
        Object resultObject = instance.readAllForClass(A_KNOWN_CLASS_ID, wsQueryOptionsNotDetailed, TestHelper_Variable.DONT_INCLUDE);

        //assert:
        verify(etlTemplateService, times(0)).getForUserForTargetClassAndRelatedDomains(A_KNOWN_CLASS_ID);
        verify(etlTemplateService).getForUserForTarget(ET_CLASS, A_KNOWN_CLASS_ID);
        checkListCodes(expCodes, resultObject);
        checkNotDetailedList(resultObject, paramToCheck1);
    }

    @Test
    public void testReadAllForProcess_IncludeDomains_Detailed() {
        System.out.println("readAllForProcess_IncludeDomains_Detailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        when(etlTemplateService.getForUserForTargetClassAndRelatedDomains(A_KNOWN_CLASS_ID)).thenReturn(listEtlTemplate);

        //act:
        Object resultObject = instance.readAllForProcess(A_KNOWN_CLASS_ID, wsQueryOptionsDetailed, TestHelper_Variable.INCLUDE);

        //assert:
        verify(etlTemplateService).getForUserForTargetClassAndRelatedDomains(A_KNOWN_CLASS_ID);
        verify(etlTemplateService, times(0)).getForUserForTarget(any(), any());
        checkListCodes(expCodes, resultObject);
        checkDetailedList(resultObject, paramToCheck1);
    }

    @Test
    public void testReadAllForProcess_IncludeDomains_NotDetailed() {
        System.out.println("readAllForProcess_IncludeDomains_NotDetailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        when(etlTemplateService.getForUserForTargetClassAndRelatedDomains(A_KNOWN_CLASS_ID)).thenReturn(listEtlTemplate);

        //act:
        Object resultObject = instance.readAllForProcess(A_KNOWN_CLASS_ID, wsQueryOptionsNotDetailed, TestHelper_Variable.INCLUDE);

        //assert:
        verify(etlTemplateService).getForUserForTargetClassAndRelatedDomains(A_KNOWN_CLASS_ID);
        verify(etlTemplateService, times(0)).getForUserForTarget(any(), any());
        checkListCodes(expCodes, resultObject);
        checkNotDetailedList(resultObject, paramToCheck1);
    }

    @Test
    public void testReadAllForProcess_NotIncludeDomains_Detailed() {
        System.out.println("readAllForProcess_IncludeDomains_Detailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        when(etlTemplateService.getForUserForTarget(ET_PROCESS, A_KNOWN_CLASS_ID)).thenReturn(listEtlTemplate);

        //act:
        Object resultObject = instance.readAllForProcess(A_KNOWN_CLASS_ID, wsQueryOptionsDetailed, TestHelper_Variable.DONT_INCLUDE);

        //assert:
        verify(etlTemplateService, times(0)).getForUserForTargetClassAndRelatedDomains(A_KNOWN_CLASS_ID);
        verify(etlTemplateService).getForUserForTarget(ET_PROCESS, A_KNOWN_CLASS_ID);
        checkListCodes(expCodes, resultObject);
        checkDetailedList(resultObject, paramToCheck1);
    }

    @Test
    public void testReadAllForProcess_NotIncludeDomains_NotDetailed() {
        System.out.println("readAllForProcess_IncludeDomains_Detailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        when(etlTemplateService.getForUserForTarget(ET_PROCESS, A_KNOWN_CLASS_ID)).thenReturn(listEtlTemplate);

        //act:
        Object resultObject = instance.readAllForProcess(A_KNOWN_CLASS_ID, wsQueryOptionsNotDetailed, TestHelper_Variable.DONT_INCLUDE);

        //assert:
        verify(etlTemplateService, times(0)).getForUserForTargetClassAndRelatedDomains(A_KNOWN_CLASS_ID);
        verify(etlTemplateService).getForUserForTarget(ET_PROCESS, A_KNOWN_CLASS_ID);
        checkListCodes(expCodes, resultObject);
        checkNotDetailedList(resultObject, paramToCheck1);
    }

    @Test
    public void testReadAllForView_Detailed() {
        System.out.println("readAllForView_Detailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        when(etlTemplateService.getForUserForTarget(ET_VIEW, A_KNOWN_VIEW_ID)).thenReturn(listEtlTemplate);

        //act:
        Object resultObject = instance.readAllForView(A_KNOWN_VIEW_ID, wsQueryOptionsDetailed);

        //assert:
        verify(etlTemplateService).getForUserForTarget(ET_VIEW, A_KNOWN_VIEW_ID);
        checkListCodes(expCodes, resultObject);
        checkDetailedList(resultObject, paramToCheck1);
    }

    @Test
    public void testReadAllForView_NotDetailed() {
        System.out.println("readAllForView_Detailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2, A_KNOWN_ETL_TEMPLATE_NAME3);
        when(etlTemplateService.getForUserForTarget(ET_VIEW, A_KNOWN_VIEW_ID)).thenReturn(listEtlTemplate);

        //act:
        Object resultObject = instance.readAllForView(A_KNOWN_VIEW_ID, wsQueryOptionsNotDetailed);

        //assert:
        verify(etlTemplateService).getForUserForTarget(ET_VIEW, A_KNOWN_VIEW_ID);
        checkListCodes(expCodes, resultObject);
        checkNotDetailedList(resultObject, paramToCheck1);
    }

    @Test
    public void testReadOne() {
        System.out.println("readOne");

        //arrange:
        String expCode = A_KNOWN_ETL_TEMPLATE_NAME1;
        when(etlTemplateService.getForUserByCode(A_KNOWN_GENERIC_ID1)).thenReturn(listEtlTemplate.get(0));

        //act:
        Object resultObject = instance.readOne(A_KNOWN_GENERIC_ID1);

        //assert:
        verify(etlTemplateService).getForUserByCode(A_KNOWN_GENERIC_ID1);
        checkCode(expCode, resultObject);
        checkDetailed(resultObject, paramToCheck1);
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        when(etlTemplateService.create(any(EtlTemplate.class))).thenReturn(listEtlTemplate.get(0));

        //act:
        Object resultObject = instance.create(wsEtlTemplateData);

        //assert:
        verify(etlTemplateService).create((any(EtlTemplate.class)));
        checkCode(A_KNOWN_ETL_TEMPLATE_NAME1, resultObject);
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(etlTemplateService.update(any(EtlTemplate.class))).thenReturn(listEtlTemplate.get(0));

        //act:
        Object resultObject = instance.update(A_KNOWN_ETL_TEMPLATE_NAME1, wsEtlTemplateData);

        //assert:
        verify(etlTemplateService).update(any(EtlTemplate.class));
        checkCode(A_KNOWN_ETL_TEMPLATE_NAME1, resultObject);

    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_ETL_TEMPLATE_NAME1);

        //assert:
        verify(etlTemplateService).delete(A_KNOWN_ETL_TEMPLATE_NAME1);
        checkSuccess(resultObject);
    }
}
