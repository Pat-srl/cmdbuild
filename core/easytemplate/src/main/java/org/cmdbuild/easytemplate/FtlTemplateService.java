/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easytemplate;

import java.util.Map;
import static org.cmdbuild.easytemplate.FtlTemplateService.FtlTemplateMode.FTM_AUTO;

public interface FtlTemplateService {

    boolean isFtlTemplate(String template);

    String executeFtlTemplate(String template, FtlTemplateMode mode, Map data);

    default String executeFtlTemplate(String template, Map data) {
        return executeFtlTemplate(template, FTM_AUTO, data);
    }

    enum FtlTemplateMode {
        FTM_PLAIN, FTM_AUTO, FTM_HTML
    }
}
