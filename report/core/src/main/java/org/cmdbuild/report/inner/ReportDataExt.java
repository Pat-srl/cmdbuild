/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner;

import java.util.List;
import net.sf.jasperreports.engine.JasperReport;
import org.cmdbuild.report.ReportData;

public interface ReportDataExt extends ReportData {

    List<JasperReport> getJasperReports();

    default JasperReport getMasterJasperReport() {
        return getJasperReports().get(0);
    }

    default List<JasperReport> getSubJasperReports() {
        return getJasperReports().subList(1, getJasperReports().size());
    }

}
