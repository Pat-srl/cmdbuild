/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner;

import jakarta.activation.DataHandler;
import net.sf.jasperreports.engine.JasperPrint;
import org.cmdbuild.report.ReportFormat;

public interface ReportHelper {

    String getContentTypeForReportFormat(ReportFormat reportExtension);

    DataHandler exportReport(JasperPrint reportOutput, String basename, ReportFormat format);
}
