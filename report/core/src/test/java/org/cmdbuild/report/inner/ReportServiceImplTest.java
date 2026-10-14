/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.report.inner;

import static java.lang.String.format;
import java.util.Map;
import java.util.Objects;
import net.sf.jasperreports.engine.JasperReport;
import org.cmdbuild.auth.user.OperationUserStore;
import org.cmdbuild.customclassloader.CustomClassloaderService;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.notification.NotificationCommonData;
import org.cmdbuild.report.BatchReportInfo;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.report.dao.ReportDataImpl;
import org.cmdbuild.report.dao.ReportDataImpl.ReportDataImplBuilder;
import org.cmdbuild.report.dao.ReportRepository;
import org.cmdbuild.report.inner.ReportDataExtImpl.ReportDataExtImplBuilder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.hamcrest.Description;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class ReportServiceImplTest {
    
    private final ReportRepository reportRepository = mock(ReportRepository.class);
    private final OperationUserStore operationUser = mock(OperationUserStore.class);
    private final ReportProcessorService processor = mock(ReportProcessorService.class);
    private final CustomClassloaderService customClassloaderService = mock(CustomClassloaderService.class);
    private final WaterwayService service = mock(WaterwayService.class);

    private final ReportService instance = new ReportServiceImpl(service, reportRepository, operationUser, processor, customClassloaderService);
    
    /**
     * Test of executeReportAndDownload method, of class ReportServiceImpl.
     */
    @Test
    public void testExecuteReportAndDownload() {
        System.out.println("executeReportAndDownload");
        
        //arrange:
        Map<String, Object> dynamicPparameters = map("CustomProp", "valueX");        
        long reportId = 1l;
        final ReportFormat aReportFormat = ReportFormat.PDF;
        ReportDataExt report = new ReportDataExtImplBuilder()
                .withInner(
               mockReportDataImplBuilder()
                       .withConfig("GlobalProp", "valueA")
                       .withConfig("CustomProp", "valueB")
                    .build())
                .withJasperReports(list(mock(JasperReport.class)))
                .build();
        when(reportRepository.getById(reportId)).thenReturn(report);
        
        //act:
        instance.executeReportAndDownload(reportId, aReportFormat, dynamicPparameters);

        //assert:
        verify(processor, times(1)).executeReport(eq(report), eq(aReportFormat), 
                                                    eq(map("GlobalProp", "valueA", "CustomProp", "valueX")));
    }
 
    private ReportDataImplBuilder mockReportDataImplBuilder() {
        return ReportDataImpl.builder()
                       .withCode("R1")
                       .withSourceReports(list("aReport.pdf"))
                       .withCompiledReports(list(new byte[0]));
    }
    
}