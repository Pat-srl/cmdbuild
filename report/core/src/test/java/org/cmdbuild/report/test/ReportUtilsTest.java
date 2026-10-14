/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.test;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.dao.ReportDataImpl;
import org.cmdbuild.report.inner.ReportDataExt;
import static org.cmdbuild.report.inner.utils.ReportUtils.exportReportTemplatesAsZip;
import static org.cmdbuild.report.inner.utils.ReportUtils.loadReport;
import static org.cmdbuild.report.inner.utils.ReportUtils.updateReportData;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmZipUtils.unzipDataAsMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.xml.CmXmlUtils.applyXpath;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ReportUtilsTest {

    private final static Map<String, String> JRXML_NAMESPACE = ImmutableMap.of("jr", "http://jasperreports.sourceforge.net/jasperreports");

    @Test
    public void testReportReadWrite() throws IOException {
        Map<String, byte[]> files = unzipDataAsMap(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/report/test/report_1.zip")));

        ReportData reportData = ReportDataImpl.builder().withCode("test").accept(updateReportData(files)).build();

        assertEquals(2, reportData.getCompiledSubReports().size());
        assertEquals(3, reportData.getCompiledReports().size());
        assertNotNull(reportData.getCompiledMasterReport());
        assertTrue(reportData.hasCompiledReports());
        assertFalse(reportData.hasSourceReports());
        assertEquals(0, reportData.getSourceReports().size());
        assertEquals(5, reportData.getImageNames().size());
        assertEquals(5, reportData.getImages().size());

        ReportDataExt reportDataExt = loadReport(reportData);

        assertEquals(2, reportDataExt.getCompiledSubReports().size());
        assertEquals(3, reportDataExt.getCompiledReports().size());
        assertNotNull(reportDataExt.getCompiledMasterReport());
        assertTrue(reportDataExt.hasCompiledReports());
        assertTrue(reportDataExt.hasSourceReports());
        assertEquals(3, reportDataExt.getSourceReports().size());
        assertEquals(5, reportDataExt.getImageNames().size());
        assertEquals(5, reportDataExt.getImages().size());

//        assertEquals(2, reportDataExt.getSubJasperDesigns().size());
//        assertEquals(3, reportDataExt.getJasperDesigns().size());
        assertEquals(2, reportDataExt.getSubJasperReports().size());
        assertEquals(3, reportDataExt.getJasperReports().size());
//        assertNotNull(reportDataExt.getMasterJasperDesign());
        assertNotNull(reportDataExt.getMasterJasperReport());

        ReportDataExt reportDataExt2 = loadReport(reportData);

        assertEquals(2, reportDataExt2.getCompiledSubReports().size());
        assertEquals(3, reportDataExt2.getCompiledReports().size());
        assertNotNull(reportDataExt2.getCompiledMasterReport());
        assertTrue(reportDataExt2.hasCompiledReports());
        assertTrue(reportDataExt2.hasSourceReports());
        assertEquals(3, reportDataExt2.getSourceReports().size());
        assertEquals(5, reportDataExt2.getImageNames().size());
        assertEquals(5, reportDataExt2.getImages().size());

//        assertEquals(2, reportDataExt2.getSubJasperDesigns().size());
//        assertEquals(3, reportDataExt2.getJasperDesigns().size());
        assertEquals(2, reportDataExt2.getSubJasperReports().size());
        assertEquals(3, reportDataExt2.getJasperReports().size());
//        assertNotNull(reportDataExt2.getMasterJasperDesign());
        assertNotNull(reportDataExt2.getMasterJasperReport());

        for (int i = 0; i < 3; i++) {
            assertEquals(reportDataExt.getSourceReports().get(i), reportDataExt2.getSourceReports().get(i));
        }

        Map<String, byte[]> exportFiles = unzipDataAsMap(toByteArray(exportReportTemplatesAsZip(reportDataExt2)));

        String masterReportOrig = checkNotBlank(new String(files.get("AssessmentCheck.jrxml"), StandardCharsets.UTF_8)),
                masterReportLoaded = reportDataExt.getSourceReports().get(0),
                masterReportExport = checkNotBlank(new String(exportFiles.get("AssessmentCheck.jrxml"), StandardCharsets.UTF_8));

        assertEquals("\"PrepAnalysis.jasper\"", applyXpath(masterReportOrig, JRXML_NAMESPACE, "(//jr:subreport)[1]/jr:subreportExpression"));
//        assertEquals("PrepAnalysis", reportDataExt.getSubJasperDesigns().get(0).getName());
        assertEquals("PrepAnalysis", reportDataExt.getSubJasperReports().get(0).getName());
        assertEquals("$P{REPORT_PARAMETERS_MAP}.get(\"SUBREPORT1\")", applyXpath(masterReportLoaded, JRXML_NAMESPACE, "(//jr:subreport)[1]/jr:subreportExpression"));
        assertEquals("\"PrepAnalysis.jasper\"", applyXpath(masterReportExport, JRXML_NAMESPACE, "(//jr:subreport)[1]/jr:subreportExpression"));

        assertEquals("\"InstanceCheck.jasper\"", applyXpath(masterReportOrig, JRXML_NAMESPACE, "(//jr:subreport)[2]/jr:subreportExpression"));
//        assertEquals("InstanceCheck", reportDataExt.getSubJasperDesigns().get(1).getName());
        assertEquals("InstanceCheck", reportDataExt.getSubJasperReports().get(1).getName());
        assertEquals("$P{REPORT_PARAMETERS_MAP}.get(\"SUBREPORT2\")", applyXpath(masterReportLoaded, JRXML_NAMESPACE, "(//jr:subreport)[2]/jr:subreportExpression"));
        assertEquals("\"InstanceCheck.jasper\"", applyXpath(masterReportExport, JRXML_NAMESPACE, "(//jr:subreport)[2]/jr:subreportExpression"));

        assertEquals("\"Logo_Snam_2018.png\"", applyXpath(masterReportOrig, JRXML_NAMESPACE, "(//jr:image)[1]/jr:imageExpression"));
        assertEquals("Logo_Snam_2018.png", reportData.getImageNames().get(0));
        assertArrayEquals(reportData.getImages().get(0), files.get("Logo_Snam_2018.png"));
        assertArrayEquals(reportData.getImages().get(0), exportFiles.get("Logo_Snam_2018.png"));
        assertEquals("$P{REPORT_PARAMETERS_MAP}.get(\"IMAGE0\")", applyXpath(masterReportLoaded, JRXML_NAMESPACE, "(//jr:image)[1]/jr:imageExpression"));
        assertEquals("\"Logo_Snam_2018.png\"", applyXpath(masterReportExport, JRXML_NAMESPACE, "(//jr:image)[1]/jr:imageExpression"));

        String subReportOrig1 = checkNotBlank(new String(files.get("PrepAnalysis.jrxml"), StandardCharsets.UTF_8)),
                subReportLoaded1 = reportDataExt.getSourceReports().get(1),
                subReportExport1 = checkNotBlank(new String(exportFiles.get("PrepAnalysis.jrxml"), StandardCharsets.UTF_8));

        assertEquals("\"conforme.png\"", applyXpath(subReportOrig1, JRXML_NAMESPACE, "(//jr:image)[1]/jr:imageExpression"));
        assertEquals("conforme.png", reportData.getImageNames().get(3));
        assertArrayEquals(reportData.getImages().get(3), files.get("conforme.png"));
        assertArrayEquals(reportData.getImages().get(3), exportFiles.get("conforme.png"));
        assertEquals("$P{REPORT_PARAMETERS_MAP}.get(\"IMAGE3\")", applyXpath(subReportLoaded1, JRXML_NAMESPACE, "(//jr:image)[1]/jr:imageExpression"));
        assertEquals("\"conforme.png\"", applyXpath(subReportExport1, JRXML_NAMESPACE, "(//jr:image)[1]/jr:imageExpression"));

        String subReportOrig2 = checkNotBlank(new String(files.get("InstanceCheck.jrxml"), StandardCharsets.UTF_8)),
                subReportLoaded2 = reportDataExt.getSourceReports().get(2),
                subReportExport2 = checkNotBlank(new String(exportFiles.get("InstanceCheck.jrxml"), StandardCharsets.UTF_8));

        assertEquals("\"check-o-square_512x512.png\"", applyXpath(subReportOrig2, JRXML_NAMESPACE, "(//jr:image)[2]/jr:imageExpression"));
        assertEquals("check-o-square_512x512.png", reportData.getImageNames().get(2));
        assertArrayEquals(reportData.getImages().get(2), files.get("check-o-square_512x512.png"));
        assertArrayEquals(reportData.getImages().get(2), exportFiles.get("check-o-square_512x512.png"));
        assertEquals("$P{REPORT_PARAMETERS_MAP}.get(\"IMAGE2\")", applyXpath(subReportLoaded2, JRXML_NAMESPACE, "(//jr:image)[2]/jr:imageExpression"));
        assertEquals("\"check-o-square_512x512.png\"", applyXpath(subReportExport2, JRXML_NAMESPACE, "(//jr:image)[2]/jr:imageExpression"));
    }

}
