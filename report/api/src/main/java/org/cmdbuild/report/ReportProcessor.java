/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report;

import jakarta.activation.DataSource;

public interface ReportProcessor {

	ReportFormat getReportExtension();

	DataSource executeReport();

	String getContentType();
 
}
