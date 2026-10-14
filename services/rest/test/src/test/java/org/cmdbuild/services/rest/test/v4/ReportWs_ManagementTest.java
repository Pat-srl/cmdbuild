/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import jakarta.activation.DataHandler;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.report.BatchReportInfo;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportInfoImpl;
import org.cmdbuild.report.dao.ReportDataImpl;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ReportSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ReportWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ReportWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.cmdbuild.report.ReportFormat.CSV;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author ldare
 */
public class ReportWs_ManagementTest extends WsTestBase {

    private final ReportWs_Management instance;

    private final ReportDataImpl expReportDataImpl1;
    private final ReportDataImpl expReportDataImpl2;
    private final List<ReportInfo> expReportInfoList;
    private final ReportInfoImpl reportInfoImpl;
    private final Classe testClasse;
    private final Attribute testAttr1;
    private final Attribute testAttr2;
    private final Attribute testAttr3;
    private final List<Attribute> listAttributes;
    private final DataHandler dataHandler;
    private final MultivaluedMap<String, String> headers;

    public ReportWs_ManagementTest() throws IOException {

        AttributeTypeConversionService conversionService = mockBuildAttributeTypeConversionService();
        ReportSerializationHelper reportSerializationHelper = mockBuildReportSerializationHelper();
        ReportWsCommand command = new ReportWsCommand(reportService);
        instance = new ReportWs_Management(reportService, conversionService, reportSerializationHelper, command);
        // Raises IOException when constructor fails reading the string
        dataHandler = new DataHandler(new ByteArrayDataSource(
                "{\"code\":\"testCode\",\"description\":\"testDescription\",\"active\":true}",
                A_JSON_MIME_TYPE));
        expReportDataImpl1 = mockBuildReportData(A_KNOWN_REPORT_ID1);
        expReportDataImpl2 = mockBuildReportData(A_KNOWN_REPORT_ID2);
        expReportInfoList = list(expReportDataImpl1, expReportDataImpl2);
        reportInfoImpl = ReportInfoImpl.builder()
                .withId(1L)
                .withCode("testCode")
                .withDescription("test description")
                .withActive(true)
                .build();

        List<AttributeWithoutOwner> listAttributesWO = list(
                mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME1),
                mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME2),
                mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME3)
        );
        testClasse = mockBuildClasse(A_KNOWN_CLASS_ID, emptyList(), listAttributesWO);
        testAttr1 = mockBuildAttribute(A_KNOWN_ATTR_NAME1, testClasse);
        testAttr2 = mockBuildAttribute(A_KNOWN_ATTR_NAME2, testClasse);
        testAttr3 = mockBuildAttribute(A_KNOWN_ATTR_NAME3, testClasse);
        listAttributes = list(testAttr1, testAttr2, testAttr3);
        headers = new MultivaluedHashMap<>();
        headers.add("Content-Disposition", "attachment; filename=\"testReport.json\"");
    }

    @Test
    public void testReadAll() {
        System.out.println("readAll");

        //arrange:
        List<Long> expListIds = list(A_KNOWN_REPORT_ID1, A_KNOWN_REPORT_ID2);
        when(reportService.getForCurrentUser()).thenReturn(expReportInfoList);

        //act:
        Object resultObject = instance.readAll(A_TEST_EMPTY_FILTER, A_TEST_LIMIT, A_TEST_OFFSET, DETAILED);

        //assert:
        verify(reportService).getForCurrentUser();
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testRead() {
        System.out.println("testReadManagement");

        //arrange:
        when(reportService.getForUserByIdOrCode(A_KNOWN_REPORT_INFO_NAME1)).thenReturn(expReportDataImpl1);

        //act:
        Object resultObject = instance.read(A_KNOWN_REPORT_INFO_NAME1);

        //assert:
        verify(reportService).getForUserByIdOrCode(A_KNOWN_REPORT_INFO_NAME1);
        checkId(A_KNOWN_REPORT_ID1, resultObject);
    }

    @Test
    public void testReadAllAttributes() {
        System.out.println("readAllAttributes");

        //arrange:
        List<String> expListNames = list(A_KNOWN_ATTR_NAME1, A_KNOWN_ATTR_NAME2, A_KNOWN_ATTR_NAME3);
        when(reportService.getForUserByIdOrCode(A_KNOWN_REPORT_INFO_NAME1)).thenReturn(reportInfoImpl);
        when(reportService.getParamsById(A_KNOWN_REPORT_ID1)).thenReturn(listAttributes);

        //act:
        Object resultObject = instance.readAllAttributes(A_KNOWN_REPORT_INFO_NAME1, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(reportService).getForUserByIdOrCode(A_KNOWN_REPORT_INFO_NAME1);
        verify(reportService).getParamsById(A_KNOWN_REPORT_ID1);
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testExecuteBatchReport() {
        System.out.println("executeBatchReport");

        //arrange:
        when(reportService.getForUserByIdOrCode(A_KNOWN_REPORT_INFO_NAME1)).thenReturn(reportInfoImpl);
        BatchReportInfo batchReportInfo = new BatchReportInfo() {
            @Override
            public String getBatchId() {
                return A_KNOWN_REPORT_INFO_NAME1;
            }

            @Override
            public String toString() {
                return "BatchReportInfo{batchId=%s}".formatted(A_KNOWN_REPORT_INFO_NAME1);
            }
        };
        when(reportService.executeBatchReport(A_KNOWN_REPORT_ID1, CSV, emptyMap())).thenReturn(batchReportInfo);

        //act:
        Object resultObject = instance.executeBatchReport(A_KNOWN_REPORT_INFO_NAME1, A_CSV_REPORT_EXTENSION, "");

        //assert:
        verify(reportService).getForUserByIdOrCode(A_KNOWN_REPORT_INFO_NAME1);
        verify(reportService).executeBatchReport(A_KNOWN_REPORT_ID1, CSV, emptyMap());
        checkSuccess(resultObject);
        assertEquals(A_KNOWN_REPORT_INFO_NAME1, ((Map) ((Map) resultObject).get("data")).get("batchId"));
    }

    @Test
    public void testDownload() {
        System.out.println("download");

        //arrange:
        when(reportService.getForUserByIdOrCode(A_KNOWN_REPORT_INFO_NAME1)).thenReturn(reportInfoImpl);
        when(reportService.executeReportAndDownload(A_KNOWN_REPORT_ID1, reportExtFromString(A_CSV_REPORT_EXTENSION), emptyMap())).thenReturn(dataHandler);

        //act:
        DataHandler result = instance.download(A_KNOWN_REPORT_INFO_NAME1, A_CSV_REPORT_EXTENSION, "");

        //assert:
        verify(reportService).getForUserByIdOrCode(A_KNOWN_REPORT_INFO_NAME1);
        verify(reportService).executeReportAndDownload(A_KNOWN_REPORT_ID1, reportExtFromString(A_CSV_REPORT_EXTENSION), emptyMap());
        assertEquals(A_JSON_MIME_TYPE, result.getDataSource().getContentType());
    }
}
