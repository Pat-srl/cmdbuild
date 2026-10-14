/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;

public interface ReportConfiguration {

    List<Map<String, String>> getFontConfigs();

    @Nullable
    Boolean ignoreMissingFont();

    @Nullable
    String getDefaultPdfEncoding();

    @Nullable
    Boolean getDefaultPdfEmbedded();

    @Nullable
    String getDefaultPdfFont();

    Map<String, String> getOtherReportConfigs();

}
