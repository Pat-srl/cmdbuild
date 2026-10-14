/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easytemplate;

import static java.util.Collections.emptyMap;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;

public interface EasytemplateService {

	@Nullable
	Object evalJavascriptCode(String expr);

	EasytemplateProcessor getDefaultProcessorWithJsContext(String jsContext);

	default EasytemplateProcessor getDefaultProcessor() {
		return getDefaultProcessorWithJsContext(toJson(emptyMap()));
	}
}
