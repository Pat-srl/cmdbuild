/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.beanshell;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.Map;
import static java.util.function.Function.identity;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmException;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.addLineNumbers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanshellScriptExecutor {//TODO fix exceptions

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String scriptContent;
    private final ClassLoader customClassLoader;

    public BeanshellScriptExecutor(String scriptContent) {
        this(scriptContent, null);
    }

    public BeanshellScriptExecutor(String scriptContent, @Nullable ClassLoader classLoader) {
        this.scriptContent = checkNotBlank(scriptContent);
        this.customClassLoader = classLoader;
    }

    public Map<String, Object> execute(Map<String, Object> dataIn) {
        Interpreter interpreter = new Interpreter();
        dataIn.forEach((key, value) -> {
            try {
                interpreter.set(key, value);
            } catch (EvalError ex) {
                throw new CmException(ex, "error setting var %s = %s", key, value);
            }
        });
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (customClassLoader != null) {
                interpreter.setClassLoader(customClassLoader);
                Thread.currentThread().setContextClassLoader(customClassLoader);
            }
            try {
                interpreter.eval(scriptContent);
            } finally {
                if (customClassLoader != null) {
                    Thread.currentThread().setContextClassLoader(contextClassLoader);
                }
            }
            return map(list(interpreter.getNameSpace().getVariableNames()), identity(), (key) -> {
                try {
                    return interpreter.get((String) key);
                } catch (EvalError ex) {
                    throw new CmException(ex, "error getting var = %s", key);
                }
            });
        } catch (EvalError ex) {
            logger.warn("error executing beanshell script = \n\n{}\n", CmStringUtils.addLineNumbers(scriptContent));
            String message;
            try {
                logger.warn("error executing beanshell script at line {}: {} {} {}\n\n{}\n", ex.getErrorLineNumber(), ex.getMessage(), ex.getErrorText(), ex.getScriptStackTrace(), addLineNumbers(scriptContent));
                message = format("beanshell script error =< %s > at line = %s", ex.getErrorText(), ex.getErrorLineNumber());
            } catch (Exception exx) {
                logger.error("error processing beanshell script error (!)", exx);
                message = "beanshell script error";
            }
            Throwable inner = ex;
            while (inner instanceof TargetError targetError && targetError.getTarget() != null) {
                inner = targetError.getTarget();
            }
            throw new CmException(inner, message);
        }
    }

}
