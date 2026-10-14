/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.utils;

import org.cmdbuild.report.ReportFormat;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;

public class ReportExtUtils {

    public static ReportFormat reportExtFromString(String ext) {
        return ReportFormat.valueOf(trimAndCheckNotBlank(ext).toUpperCase());
    }

}
