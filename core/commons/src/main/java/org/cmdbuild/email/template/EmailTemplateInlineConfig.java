/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.template;

import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.report.ReportConfig;

public interface EmailTemplateInlineConfig {

    String getId();

    String getTemplate();

    List<ReportConfig> getReportList();

    @Nullable
    String getContent();

    @Nullable
    Long getDelay();
}
