/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.groovy;

import static java.util.Collections.emptyMap;
import java.util.Map;

public interface GroovyScriptExecutor {

    Map<String, Object> execute(Map<String, Object> dataIn);

    Map<String, Object> executeQuietly(Map<String, Object> dataIn);

    default Map<String, Object> execute() {
        return execute(emptyMap());
    }
}
