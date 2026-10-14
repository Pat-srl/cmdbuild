/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.script;

import jakarta.annotation.Nullable;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;

public interface ScriptService {

    ScriptServiceHelper helper();

    default ScriptServiceHelper helper(@Nullable Class parentLoggerClass) {
        return helper().withLoggerClass(parentLoggerClass);
    }

    default ScriptServiceHelper helper(@Nullable Class parentLoggerClass, String script) {
        return helper().withLoggerClass(parentLoggerClass).withScript(script);
    }

    default ScriptServiceHelper helper(@Nullable Class parentLoggerClass, String script, @Nullable ScriptType language) {
        return helper().withLoggerClass(parentLoggerClass).withScript(script, language);
    }

    public interface ScriptServiceHelper {

        ScriptServiceHelper withLogger(@Nullable Logger logger);

        ScriptServiceHelper withLoggerClass(@Nullable Class parentLoggerClass);

        ScriptServiceHelper withScript(String script);

        ScriptServiceHelper withLanguage(@Nullable ScriptType language);

        ScriptServiceHelper withClassLoader(@Nullable ClassLoader customClassLoader);

        ScriptServiceHelper withClassLoader(@Nullable String customClassLoader);

        ScriptServiceHelper withData(@Nullable Map<String, Object> dataIn);

        ScriptServiceHelper withData(String key, Object value);

        Map<String, Object> execute();

        Object executeForOutput();

        default ScriptServiceHelper withScript(String script, @Nullable ScriptType language) {
            return withScript(script).withLanguage(language);
        }

        default Object executeForOutput(@Nullable Map<String, Object> dataIn) {
            return withData(dataIn).executeForOutput();
        }

        default Object executeForOutput(Object... dataIn) {
            return withData(dataIn).executeForOutput();
        }

        default Object execute(Object... dataIn) {
            return withData(dataIn).execute();
        }

        default Map<String, Object> execute(@Nullable Map<String, Object> dataIn) {
            return withData(dataIn).execute();
        }

        default ScriptServiceHelper withData(Object... values) {
            return withData(map(values));
        }
    }

}
