/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.python;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.addLineNumbers;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonScriptExecutorImpl implements PythonScriptExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String scriptContent;

    public PythonScriptExecutorImpl(String scriptContent) {
        try {
            logger.debug("create python script executor for script = \n\n{}\n\n", scriptContent);
            this.scriptContent = checkNotNull(scriptContent);
        } catch (Exception ex) {
            logger.error("error preparing script = \n\n{}\n", scriptContent);
            throw new PythonScriptException(ex, "error preparing python script");
        }
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> dataIn) {
        logger.trace("execute python script (actual) =\n\n{}\n\nwith data = \n\n{}\n", scriptContent, mapToLoggableStringLazy(dataIn));
        try ( PythonInterpreter pyInterp = new PythonInterpreter()) {
            dataIn.forEach(pyInterp::set);
            pyInterp.exec(scriptContent);

            Map<String, Object> map = map();
            pyInterp.getLocals().asIterable().forEach(a -> {
                String key = a.asString();
                PyObject value = pyInterp.get(key);
                map.put(key, pythonToSystem(value));
            });
            logger.trace("script output =\n\n{}\n", mapToLoggableStringLazy(map));
            return map;
        } catch (Exception ex) {
            throw new PythonScriptException(ex, "error executing script = \n\n%s\n", addLineNumbers(scriptContent));
        }
    }

    @Nullable
    private static Object pythonToSystem(@Nullable PyObject value) {
        return value.__tojava__(Object.class);
    }
}
