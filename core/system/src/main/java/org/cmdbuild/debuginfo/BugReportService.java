/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;

public interface BugReportService {

    DataSource generateBugReport(@Nullable String password);

    BugReportInfo sendBugReport(@Nullable String message, @Nullable String password);

    default DataSource generateBugReport() {
        return generateBugReport(null);
    }

    default BugReportInfo sendBugReport(@Nullable String message) {
        return sendBugReport(message, null);
    }

    default BugReportInfo sendBugReport() {
        return sendBugReport(null, null);
    }

}
