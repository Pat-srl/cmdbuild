/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.etl.gate.inner.EtlGate;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.EtlGateWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.EtlGateWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsImportExportGateData;
import org.cmdbuild.services.rest.test.common.TestHelper_Variable;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildEtlGate;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildEtlTemplate;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsImportExportGateData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class EtlGateWs_AdministrationTest extends WsTestBase {

    private final List<String> expListIds = list(A_KNOWN_ETL_GATE_NAME1, A_KNOWN_ETL_GATE_NAME2, A_KNOWN_ETL_GATE_NAME3);

    private final EtlGateWs_Administration instance;

    private final EtlGate etlGate1;
    private final EtlGate etlGate2;
    private final EtlGate etlGate3;
    private final List<EtlGate> listEtlGate;
    private final WsQueryOptions wsQueryOptions;
    private final EtlTemplate etlTemplate;
    private final WsImportExportGateData wsImportExportGateData = mockBuildWsImportExportGateData();

    public EtlGateWs_AdministrationTest() {
        EtlGateWsCommand command = new EtlGateWsCommand(etlGateService);
        instance = new EtlGateWs_Administration(etlTemplateService, command);

        etlGate1 = mockBuildEtlGate(A_KNOWN_ETL_GATE_NAME1);
        etlGate2 = mockBuildEtlGate(A_KNOWN_ETL_GATE_NAME2);
        etlGate3 = mockBuildEtlGate(A_KNOWN_ETL_GATE_NAME3);
        listEtlGate = list(etlGate1, etlGate2, etlGate3);
        wsQueryOptions = TestHelper_WSData.mockBuildWsQueryOptions2();
        etlTemplate = mockBuildEtlTemplate(A_KNOWN_ETL_TEMPLATE_NAME1, ACTIVE);
    }

    @Test
    public void testReadAll() {
        System.out.println("readAll");

        //arrange:
        when(etlGateService.getAllForCurrentUser()).thenReturn(listEtlGate);
        when(etlTemplateService.getTemplateByName(A_KNOWN_ETL_TEMPLATE_NAME1)).thenReturn(etlTemplate);

        //act:
        Object resultObject = instance.readAll(wsQueryOptions, TestHelper_Variable.INCLUDE);

        //assert:
        verify(etlGateService).getAllForCurrentUser();
        verify(etlTemplateService, times(3)).getTemplateByName(A_KNOWN_ETL_TEMPLATE_NAME1);
        checkListIds(expListIds, resultObject);
        checkListEtlGateTemplates(list(A_KNOWN_ETL_TEMPLATE_NAME1), resultObject);
    }

    @Test
    public void testReadAll_notIncludeEtlTemplates() {
        System.out.println("readAll_notIncludeEtlTemplates");

        //arrange:
        when(etlGateService.getAllForCurrentUser()).thenReturn(listEtlGate);

        //act:
        Object resultObject = instance.readAll(wsQueryOptions, TestHelper_Variable.DONT_INCLUDE);

        //assert:
        verify(etlGateService).getAllForCurrentUser();
        verify(etlTemplateService, times(0)).getTemplateByName(anyString());
        checkListIds(expListIds, resultObject);
        checkNoTemplatesOnListEtlGate(resultObject);
    }

    @Test
    public void testReadAllForClass() {
        System.out.println("readAllForClass");

        //arrange:
        when(etlGateService.getAllForCurrentUser()).thenReturn(listEtlGate);
        when(etlTemplateService.getTemplateByName(A_KNOWN_ETL_TEMPLATE_NAME1)).thenReturn(etlTemplate);

        //act:
        Object resultObject = instance.readAllForClass(A_KNOWN_CLASS_ID, wsQueryOptions, TestHelper_Variable.INCLUDE);

        //assert:
        verify(etlGateService).getAllForCurrentUser();
        verify(etlTemplateService, times(3)).getTemplateByName(A_KNOWN_ETL_TEMPLATE_NAME1);
        checkListIds(expListIds, resultObject);
        checkListEtlGateTemplates(list(A_KNOWN_ETL_TEMPLATE_NAME1), resultObject);
    }

    @Test
    public void testReadAllForClass_notIncludeEtlTemplates() {
        System.out.println("readAllForClass_notIncludeEtlTemplates");

        //arrange:
        when(etlGateService.getAllForCurrentUser()).thenReturn(listEtlGate);

        //act:
        Object resultObject = instance.readAllForClass(A_KNOWN_CLASS_ID, wsQueryOptions, TestHelper_Variable.DONT_INCLUDE);

        //assert:
        verify(etlGateService).getAllForCurrentUser();
        verify(etlTemplateService, times(0)).getTemplateByName(anyString());
        checkListIds(expListIds, resultObject);
        checkNoTemplatesOnListEtlGate(resultObject);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        when(etlGateService.getByCodeForCurrentUser(A_KNOWN_ETL_GATE_NAME1)).thenReturn(etlGate1);
        when(etlTemplateService.getTemplateByName(A_KNOWN_ETL_TEMPLATE_NAME1)).thenReturn(etlTemplate);

        //act;
        Object resultObject = instance.read(A_KNOWN_ETL_GATE_NAME1, TestHelper_Variable.INCLUDE);

        //assert:
        verify(etlGateService).getByCodeForCurrentUser(A_KNOWN_ETL_GATE_NAME1);
        verify(etlTemplateService).getTemplateByName(A_KNOWN_ETL_TEMPLATE_NAME1);
        checkId(A_KNOWN_ETL_GATE_NAME1, resultObject);
        checkEtlGateTemplates(list(A_KNOWN_ETL_TEMPLATE_NAME1), resultObject);
    }

    @Test
    public void testRead_notIncludeEtlTemplates() {
        System.out.println("read_notIncludeEtlTemplates");

        //arrange:
        when(etlGateService.getByCodeForCurrentUser(A_KNOWN_ETL_GATE_NAME1)).thenReturn(etlGate1);

        //act;
        Object resultObject = instance.read(A_KNOWN_ETL_GATE_NAME1, TestHelper_Variable.DONT_INCLUDE);

        //assert:
        verify(etlGateService).getByCodeForCurrentUser(A_KNOWN_ETL_GATE_NAME1);
        verify(etlTemplateService, times(0)).getTemplateByName(anyString());
        checkId(A_KNOWN_ETL_GATE_NAME1, resultObject);
        checkNoTemplatesOnEtlGate(resultObject);
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        when(etlGateService.create(any())).thenReturn(etlGate1);

        //act:
        Object resultObject = instance.create(wsImportExportGateData);

        //assert:
        verify(etlGateService).create(any());
        checkId(A_KNOWN_ETL_GATE_NAME1, resultObject);
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(etlGateService.update(any())).thenReturn(etlGate1);

        //act:
        Object resultObject = instance.update(A_KNOWN_ETL_GATE_NAME1, wsImportExportGateData);

        //assert:
        verify(etlGateService).update(any());
        checkId(A_KNOWN_ETL_GATE_NAME1, resultObject);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_ETL_GATE_NAME1);

        //assert:
        verify(etlGateService).delete(A_KNOWN_ETL_GATE_NAME1);
        checkSuccess(resultObject);
    }
}
