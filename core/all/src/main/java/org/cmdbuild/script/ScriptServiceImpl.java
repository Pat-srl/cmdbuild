/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.script;

import jakarta.annotation.Nullable;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.api.CmApiServiceExt;
import org.cmdbuild.customclassloader.CustomClassloaderService;
import static org.cmdbuild.script.ScriptType.ST_BEANSHELL;
import static org.cmdbuild.script.ScriptType.ST_GROOVY;
import static org.cmdbuild.script.ScriptType.ST_PYTHON;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPacked;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmStringUtils.nullToEmpty;
import static org.cmdbuild.utils.script.CmScriptUtils.SCRIPT_OUTPUT_VAR;
import org.cmdbuild.utils.script.beanshell.BeanshellScriptExecutor;
import org.cmdbuild.utils.script.groovy.GroovyScriptService;
import org.cmdbuild.utils.script.python.PythonScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ScriptServiceImpl implements ScriptService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CmApiServiceExt apiService;
    private final GroovyScriptService groovyScriptService;
    private final PythonScriptService pythonScriptService;
    private final CustomClassloaderService customClassloaderService;

    public ScriptServiceImpl(CmApiServiceExt apiService, GroovyScriptService groovyScriptService, PythonScriptService pythonScriptService, CustomClassloaderService customClassloaderService) {
        this.apiService = checkNotNull(apiService);
        this.groovyScriptService = checkNotNull(groovyScriptService);
        this.pythonScriptService = checkNotNull(pythonScriptService);
        this.customClassloaderService = checkNotNull(customClassloaderService);
    }

    @Override
    public ScriptServiceHelper helper() {
        return new ScriptServiceHelperImpl();
    }

    private Map<String, Object> doExecute(ScriptServiceHelperImpl helper) {
        ScriptType language = firstNotNull(helper.language, ST_GROOVY);
        String script = unpackIfPacked(nullToEmpty(helper.script));
        logger.debug("execute {} script = \n\n{}\n", language, script);
        Map<String, Object> input = map(helper.dataIn)
                .with("logger", firstNotNull(helper.logger, LoggerFactory.getLogger(format("%s.EVAL", getClass().getName()))))
                .with(apiService.getCmApiAsDataMap());
        return switch (language) {
            case ST_BEANSHELL ->
                new BeanshellScriptExecutor(script, helper.customClassLoader).execute(input);
            case ST_GROOVY ->
                groovyScriptService.executeScript(script, helper.customClassLoader, input);
            case ST_PYTHON ->
                pythonScriptService.executeScript(script, input);
        };
    }

    private class ScriptServiceHelperImpl implements ScriptServiceHelper {

        private String script;
        private ScriptType language;
        private ClassLoader customClassLoader;
        private final Map<String, Object> dataIn = map();
        private Logger logger;

        @Override
        public ScriptServiceHelper withLoggerClass(@Nullable Class parentLoggerClass) {
            logger = parentLoggerClass != null ? LoggerFactory.getLogger(format("%s.EVAL", parentLoggerClass.getName())) : null;
            return this;
        }

        @Override
        public ScriptServiceHelper withLogger(@Nullable Logger logger) {
            this.logger = logger;
            return this;
        }

        @Override
        public ScriptServiceHelper withScript(String script) {
            this.script = script;
            return this;
        }

        @Override
        public ScriptServiceHelper withLanguage(@Nullable ScriptType language) {
            this.language = language;
            return this;
        }

        @Override
        public ScriptServiceHelper withClassLoader(@Nullable ClassLoader customClassLoader) {
            this.customClassLoader = customClassLoader;
            return this;
        }

        @Override
        public ScriptServiceHelper withClassLoader(@Nullable String customClassLoader) {
            return this.withClassLoader(customClassloaderService.getCustomClassLoaderOrNull(customClassLoader));
        }

        @Override
        public ScriptServiceHelper withData(@Nullable Map<String, Object> dataIn) {
            this.dataIn.putAll(firstNotNull(dataIn, emptyMap()));
            return this;
        }

        @Override
        public ScriptServiceHelper withData(String key, Object value) {
            this.dataIn.put(key, value);
            return this;
        }

        @Override
        public Object executeForOutput() {
            return execute().get(SCRIPT_OUTPUT_VAR);
        }

        @Override
        public Map<String, Object> execute() {
            return doExecute(this);
        }

    }

}
