/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.groovy;

import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;

public interface GroovyScriptService {

    GroovyScriptExecutor getScriptExecutor(String className, String scriptContent, @Nullable ClassLoader customClassLoader);

    GroovyScriptExecutor getScriptExecutor(String scriptContent, @Nullable ClassLoader customClassLoader);

    default GroovyScriptExecutor getScriptExecutor(String className, String scriptContent) {
        return getScriptExecutor(className, scriptContent, null);
    }

    default GroovyScriptExecutor getScriptExecutor(String scriptContent) {
        return getScriptExecutor(scriptContent, (ClassLoader) null);
    }

    default Map<String, Object> executeScript(String script, Map<String, Object> dataIn) {
        return executeScript(script, null, dataIn);
    }

    default Map<String, Object> executeScript(String script) {
        return executeScript(script, emptyMap());
    }

    default Map<String, Object> executeScript(String script, @Nullable ClassLoader customClassLoader, Map<String, Object> dataIn) {
        return getScriptExecutor(script, customClassLoader).execute(dataIn);
    }

}
