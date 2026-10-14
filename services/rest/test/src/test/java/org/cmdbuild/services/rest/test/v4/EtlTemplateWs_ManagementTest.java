/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import org.cmdbuild.common.utils.TempDataSource;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.EtlTemplateWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.EtlTemplateWs_Management;
import org.cmdbuild.service.rest.v4.model.WsEtlTemplateData;
import org.cmdbuild.services.rest.test.common.TestHelper_Variable;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.cmdbuild.etl.loader.EtlTemplateTarget.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsEtlTemplateData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class EtlTemplateWs_ManagementTest extends WsTestBase {

    private final EtlTemplateWs_Management instance;

    private final String paramToCheck1 = "fileFormat";
    private final String paramToCheck2 = "created_records";

    private final WsQueryOptions wsQueryOptionsDetailed;
    private final WsQueryOptions wsQueryOptionsNotDetailed;
    private final List<EtlTemplate> listEtlTemplate;
    private final TempDataSource tempDataSource;
    private final DataHandler dataHandler;
    private final EtlProcessingResult etlProcessingResult;
    private final WsEtlTemplateData wsEtlTemplateData;
    private final Classe classe;
    private final Card card1;
    private final Card card2;
    private final Card card3;


    public EtlTemplateWs_ManagementTest() {

        CardWsSerializationHelperv3 cardWsSerializationHelperv3 = mockBuildCardWsSerializationHelperv3();
        EtlTemplateWsCommand command = new EtlTemplateWsCommand(etlTemplateService, etlTemplateInlineProcessorService);
        instance = new EtlTemplateWs_Management(tempService, cardWsSerializationHelperv3, command);

        wsQueryOptionsDetailed = TestHelper_WSData.mockBuildWsQueryOptions(DETAILED);
        wsQueryOptionsNotDetailed = TestHelper_WSData.mockBuildWsQueryOptions(NOT_DETAILED);
        listEtlTemplate = mockBuildListEtlTemplate();
        tempDataSource = mockBuildTempDataSource();
        dataHandler = new DataHandler(tempDataSource);
        etlProcessingResult = mockBuildEtlProcessingResult();
        wsEtlTemplateData = mockBuildWsEtlTemplateData();
        classe = mockBuildClasseWithAttr(A_KNOWN_CLASS_NAME1);

        Map<String, Object> attributes = map(
                "attr1", "val1",
                "attr2", 1L
        );
        card1 = mockBuildCard(A_KNOWN_CARD_ID1, classe, attributes);
        card2 = mockBuildCard(A_KNOWN_CARD_ID2, classe, attributes);
        card3 = mockBuildCard(A_KNOWN_CARD_ID3, classe, attributes);
    }

    @Test
    public void testReadAll_Detailed() {
        System.out.println("readAll_Detailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2);
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
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2);
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
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2);
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
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2);
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
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2);
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
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2);
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
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2);
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
        System.out.println("readAllForProcess_IncludeDomains_Detailed");

        //arrange:
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2);
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
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2);
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
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2);
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
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2);
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
        List<String> expCodes = list(A_KNOWN_ETL_TEMPLATE_NAME1, A_KNOWN_ETL_TEMPLATE_NAME2);
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
    public void testExecuteExportTemplate() {
        System.out.println("executeExportTemplate");

        //arrange:
        when(etlTemplateService.exportForUserDataWithTemplateAndFilter(A_KNOWN_GENERIC_ID1, A_TEST_EMPTY_FILTER)).thenReturn(tempDataSource);

        //act:
        DataHandler resultDH = instance.executeExportTemplate(A_KNOWN_GENERIC_ID1, A_TEST_EMPTY_FILTER);

        //assert:
        verify(etlTemplateService).exportForUserDataWithTemplateAndFilter(A_KNOWN_GENERIC_ID1, A_TEST_EMPTY_FILTER);
        assertEquals(A_KNOWN_DATASOURCE_NAME, resultDH.getName());
    }

    @Test
    public void testExecuteImportTemplate_Detailed() {
        System.out.println("executeImportTemplate_Detailed");

        //arrange:
        when(etlTemplateService.getForUserByCode(A_KNOWN_GENERIC_ID1)).thenReturn(listEtlTemplate.get(0));
        when(etlTemplateService.importForUserDataWithTemplate(any(DataSource.class), eq(listEtlTemplate.get(0)))).thenReturn(etlProcessingResult);
        when(etlTemplateService.buildImportResultReport(etlProcessingResult, listEtlTemplate.get(0))).thenReturn(tempDataSource);

        //act:
        Object resultObject = instance.executeImportTemplate(A_KNOWN_GENERIC_ID1, dataHandler, DETAILED);

        //assert:
        verify(etlTemplateService).getForUserByCode(A_KNOWN_GENERIC_ID1);
        verify(etlTemplateService).importForUserDataWithTemplate(any(DataSource.class), eq(listEtlTemplate.get(0)));
        verify(etlTemplateService).buildImportResultReport(etlProcessingResult, listEtlTemplate.get(0));
        assertEquals(100L, ((Map<String, Object>) ((Map<String, Object>) resultObject).get("data")).get("created"));
        checkDetailed(resultObject, paramToCheck2);
    }

    @Test
    public void testExecuteImportTemplate_NotDetailed() {
        System.out.println("executeImportTemplate_NotDetailed");

        //arrange:
        when(etlTemplateService.getForUserByCode(A_KNOWN_GENERIC_ID1)).thenReturn(listEtlTemplate.get(0));
        when(etlTemplateService.importForUserDataWithTemplate(any(DataSource.class), eq(listEtlTemplate.get(0)))).thenReturn(etlProcessingResult);
        when(etlTemplateService.buildImportResultReport(etlProcessingResult, listEtlTemplate.get(0))).thenReturn(tempDataSource);

        //act:
        Object resultObject = instance.executeImportTemplate(A_KNOWN_GENERIC_ID1, dataHandler, NOT_DETAILED);

        //assert:
        verify(etlTemplateService).getForUserByCode(A_KNOWN_GENERIC_ID1);
        verify(etlTemplateService).importForUserDataWithTemplate(any(DataSource.class), eq(listEtlTemplate.get(0)));
        verify(etlTemplateService).buildImportResultReport(etlProcessingResult, listEtlTemplate.get(0));
        assertEquals(100L, ((Map<String, Object>) ((Map<String, Object>) resultObject).get("data")).get("created"));
        checkNotDetailed(resultObject, paramToCheck2);
    }

    @Test
    public void testExecuteInlineExportTemplate() {
        System.out.println("executeInlineExportTemplate");

        //arrange:
        String data = """
                [
                  {
                    "name": "Alice",
                    "age": 30,
                    "active": true
                  },
                  {
                    "name": "Bob",
                    "age": 25,
                    "active": false
                  }
                ]
                """;
        when(etlTemplateInlineProcessorService.exportDataInline(any(List.class), any(Classe.class), any(EtlTemplate.class))).thenReturn(tempDataSource);

        //act:
        DataHandler resultDH = instance.executeInlineExportTemplate(data, wsEtlTemplateData);

        //assert:
        verify(etlTemplateInlineProcessorService).exportDataInline(any(List.class), any(Classe.class), any(EtlTemplate.class));
        assertEquals(A_KNOWN_DATASOURCE_NAME, resultDH.getName());
    }

    @Test
    public void testExecuteInlineImportTemplate() {
        System.out.println("executeInlineImportTemplate");

        //arrange:
        List<Long> expListId = list(A_KNOWN_CARD_ID1, A_KNOWN_CARD_ID2, A_KNOWN_CARD_ID3);
        when(etlTemplateInlineProcessorService.importDataInline(any(DataSource.class), any(Classe.class), any(EtlTemplate.class))).thenReturn(list(card1, card2, card3));

        //act:
        Object resultObject = instance.executeInlineImportTemplate(dataHandler, wsEtlTemplateData);

        //assert:
        verify(etlTemplateInlineProcessorService).importDataInline(any(DataSource.class), any(Classe.class), any(EtlTemplate.class));
        checkListIds(expListId, resultObject);
    }
}
