/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner;

import java.util.Map;
import jakarta.activation.DataHandler;
import org.cmdbuild.report.ReportFormat;

public interface ReportProcessorService {

    DataHandler executeReport(ReportDataExt report, ReportFormat reportExtension, Map<String, Object> parameters);

}
