/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report;

import java.util.Map;
import org.cmdbuild.utils.json.JsonBean;

@JsonBean(ReportConfigImpl.class)
public interface ReportConfig {

    String getCode();

    ReportFormat getFormat();

    Map<String, Object> getParams();

}
