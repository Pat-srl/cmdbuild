/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.python;

import static java.util.Collections.emptyMap;
import java.util.Map;

public interface PythonScriptService {

    PythonScriptExecutor getScriptExecutor(String scriptContent);

    default Map<String, Object> executeScript(String script) {
        return executeScript(script, emptyMap());
    }

    default Map<String, Object> executeScript(String script, Map<String, Object> dataIn) {
        return getScriptExecutor(script).execute(dataIn);
    }

}
