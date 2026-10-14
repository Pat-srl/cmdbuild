/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import jakarta.activation.DataHandler;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.report.BatchReportInfo;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportInfoImpl;
import org.cmdbuild.report.dao.ReportDataImpl;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ReportSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ReportWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ReportWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsReportData;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class ReportWs_AdministrationTest extends WsTestBase {

    private final ReportWs_Administration instance;

    private final ReportDataImpl reportDataImpl1;
    private final ReportDataImpl reportDataImpl2;
    private final List<ReportInfo> reportInfoList;
    private final ReportInfoImpl reportInfoImpl;
    private final Classe classe;
    private final Attribute attr1;
    private final Attribute attr2;
    private final Attribute attr3;
    private final List<Attribute> listAttributes;
    private final DataHandler dataHandler;
    private final MultivaluedMap<String, String> headers;
    private final Attachment attachment;
    private final List<Attachment> listAttachment;

    public ReportWs_AdministrationTest() throws IOException {

        AttributeTypeConversionService attributeTypeConversionService = mockBuildAttributeTypeConversionService();
        ReportSerializationHelper reportSerializationHelper = mockBuildReportSerializationHelper();
        ReportWsCommand command = new ReportWsCommand(reportService);
        instance = new ReportWs_Administration(reportService, attributeTypeConversionService, reportSerializationHelper, command);

        // Raises IOException when constructor fails reading the string
        dataHandler = new DataHandler(new ByteArrayDataSource(
                "{\"code\":\"testCode\",\"description\":\"testDescription\",\"active\":true}",
                A_JSON_MIME_TYPE));
        reportDataImpl1 = mockBuildReportData(A_KNOWN_REPORT_ID1);
        reportDataImpl2 = mockBuildReportData(A_KNOWN_REPORT_ID2);
        reportInfoList = list(reportDataImpl1, reportDataImpl2);
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
        classe = mockBuildClasse(A_KNOWN_CLASS_ID, emptyList(), listAttributesWO);
        attr1 = mockBuildAttribute(A_KNOWN_ATTR_NAME1, classe);
        attr2 = mockBuildAttribute(A_KNOWN_ATTR_NAME2, classe);
        attr3 = mockBuildAttribute(A_KNOWN_ATTR_NAME3, classe);
        listAttributes = list(attr1, attr2, attr3);
        headers = new MultivaluedHashMap<>();
        headers.add("Content-Disposition", "attachment; filename=\"testReport.json\"");
        attachment = new Attachment(A_KNOWN_GENERIC_ID1, dataHandler, headers);
        listAttachment = list(attachment);
    }

    @Test
    public void testReadAll() {
        System.out.println("readAll");

        //arrange:
        List<Long> expListIds = list(A_KNOWN_REPORT_ID1, A_KNOWN_REPORT_ID2);
        when(reportService.getAll()).thenReturn(reportInfoList);

        //act:
        Object resultObject = instance.readAll(A_TEST_EMPTY_FILTER, A_TEST_LIMIT, A_TEST_OFFSET, DETAILED);

        //assert:
        verify(reportService).getAll();
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        when(reportService.getByIdOrCode(A_KNOWN_REPORT_INFO_NAME1)).thenReturn(reportDataImpl1);

        //act:
        Object resultObject = instance.read(A_KNOWN_REPORT_INFO_NAME1);

        //assert:
        verify(reportService).getByIdOrCode(A_KNOWN_REPORT_INFO_NAME1);
        checkId(A_KNOWN_REPORT_ID1, resultObject);
    }

    @Test
    public void testReadAllAttributes() {
        System.out.println("readAllAttributes");

        //arrange:
        List<String> expListNames = list(A_KNOWN_ATTR_NAME1, A_KNOWN_ATTR_NAME2, A_KNOWN_ATTR_NAME3);
        when(reportService.getByIdOrCode(A_KNOWN_REPORT_INFO_NAME1)).thenReturn(reportInfoImpl);
        when(reportService.getParamsById(A_KNOWN_REPORT_ID1)).thenReturn(listAttributes);

        //act:
        Object resultObject = instance.readAllAttributes(A_KNOWN_REPORT_INFO_NAME1, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(reportService).getByIdOrCode(A_KNOWN_REPORT_INFO_NAME1);
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

    @Test
    public void testCreateReport() {
        System.out.println("createReport");

        //arrange:
        WsReportData repData = new WsReportData(
                "test description",
                "testCode",
                true,
                emptyMap(),
                null);
        when(reportService.createReport(any(), any())).thenReturn(reportDataImpl1);

        //act:
        Object resultObject = instance.createReport(repData, listAttachment);

        //assert:
        verify(reportService).createReport(any(), any());
        checkId(A_KNOWN_REPORT_ID1, resultObject);
    }

    @Test
    public void testUpdateReport() {
        System.out.println("updateReport");

        //arrange:
        when(reportService.getByIdOrCode(A_KNOWN_GENERIC_ID1)).thenReturn(reportInfoImpl);
        when(reportService.updateReportInfo(any())).thenReturn(reportDataImpl1);
        when(reportService.updateReport(any(), any())).thenReturn(reportDataImpl1);

        //act:
        Object resultObject = instance.updateReport(A_KNOWN_GENERIC_ID1, listAttachment);

        //assert:
        verify(reportService).getByIdOrCode(A_KNOWN_GENERIC_ID1);
        verify(reportService, atMost(1)).updateReportInfo(any());
        verify(reportService, atMost(1)).updateReport(any(), any());
        checkId(A_KNOWN_REPORT_ID1, resultObject);
    }

    @Test
    public void testUpdateReportTemplate() {
        System.out.println("updateReportTemplate");

        //arrange:
        when(reportService.getByIdOrCode(A_KNOWN_GENERIC_ID1)).thenReturn(reportInfoImpl);
        when(reportService.updateReportTemplate(anyLong(), any())).thenReturn(reportDataImpl1);

        //act:
        Object resultObject = instance.updateReportTemplate(A_KNOWN_GENERIC_ID1, listAttachment);

        //assert:
        verify(reportService).getByIdOrCode(A_KNOWN_GENERIC_ID1);
        verify(reportService).updateReportTemplate(anyLong(), any());
        checkId(A_KNOWN_REPORT_ID1, resultObject);
    }

    @Test
    public void testDownloadTemplateFiles() {
        System.out.println("downloadTemplateFiles");

        //arrange:
        when(reportService.executeReportAndDownload(A_KNOWN_REPORT_ID1.toString(), ReportFormat.ZIP)).thenReturn(dataHandler);

        //act:
        DataHandler result = instance.downloadTemplateFiles(A_KNOWN_REPORT_ID1);

        //assert:
        verify(reportService).executeReportAndDownload(A_KNOWN_REPORT_ID1.toString(), ReportFormat.ZIP);
        assertEquals(A_JSON_MIME_TYPE, result.getDataSource().getContentType());
    }

    @Test
    public void testDownloadTemplateFilesWithFilename() {
        System.out.println("downloadTemplateFilesWithFilename");

        //arrange:
        when(reportService.executeReportAndDownload(A_KNOWN_REPORT_ID1.toString(), ReportFormat.ZIP)).thenReturn(dataHandler);

        //act:
        DataHandler result = instance.downloadTemplateFilesWithFilename(A_KNOWN_REPORT_ID1);

        //assert:
        verify(reportService).executeReportAndDownload(A_KNOWN_REPORT_ID1.toString(), ReportFormat.ZIP);
        assertEquals(A_JSON_MIME_TYPE, result.getDataSource().getContentType());
    }

    @Test
    public void testDeleteReport() {
        System.out.println("deleteReport");

        //act:
        Object resultObject = instance.deleteReport(A_KNOWN_REPORT_ID1);

        //assert:
        verify(reportService).deleteReport(A_KNOWN_REPORT_ID1);
        assertTrue((boolean) ((Map) resultObject).get("success"));
    }

}
