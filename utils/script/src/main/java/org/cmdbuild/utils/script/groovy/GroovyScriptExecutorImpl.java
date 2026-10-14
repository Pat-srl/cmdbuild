/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.groovy;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.collect.Sets;
import groovy.lang.Binding;
import groovy.lang.GString;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.addLineNumbers;
import static org.cmdbuild.utils.lang.CmStringUtils.getLine;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.script.CmScriptUtils.SCRIPT_OUTPUT_VAR;
import org.codehaus.groovy.control.ResolveVisitor;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.vmplugin.VMPluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyScriptExecutorImpl implements GroovyScriptExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String GROOVY_DEFAULT_IMPORTS_WORKAROUND_CODE_HEADER = "// default imports workaround code";
    private final static int SCRIPT_LINE_OFFSET = -2;

    private final String scriptContentOriginal, scriptContentActual;
    private final ClassLoader customClassLoader;
    private final Class groovyClass;

    private final static Set<String> GROOVY_DEFAULT_IMPORTS = ImmutableSet.copyOf(VMPluginFactory.getPlugin().getDefaultImportClasses(ResolveVisitor.DEFAULT_IMPORTS).keySet());

    public GroovyScriptExecutorImpl(String className, String scriptContent, @Nullable ClassLoader customClassLoader) {
        try {
            logger.debug("create groovy script executor for script = \n\n{}\n\n", scriptContent);
            checkNotBlank(className);
            scriptContentOriginal = checkNotNull(scriptContent);
            if (!className.contains(".")) {
                className = format("org.cmdbuild.utils.groovy.script.%s", className);
            }
            Matcher matcher = Pattern.compile("(.+)[.]([^.]+)").matcher(className);
            checkArgument(matcher.matches());
            String packageName = checkNotBlank(matcher.group(1));
            className = checkNotBlank(matcher.group(2));
            scriptContentActual = format("package %s;\n\n", packageName) + scriptContentOriginal;
            this.customClassLoader = customClassLoader;
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (customClassLoader != null) {
                Thread.currentThread().setContextClassLoader(customClassLoader);
            }
            try {
                GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
                groovyClass = checkNotNull(groovyClassLoader.parseClass(scriptContentActual, className));
            } finally {
                if (customClassLoader != null) {
                    Thread.currentThread().setContextClassLoader(contextClassLoader);
                }
            }
        } catch (Exception ex) {
            logger.error("error preparing script = \n\n{}\n", scriptContent);
            throw new GroovyScriptException(ex, "error preparing groovy script");
        }
    }

    public Class getGroovyClass() {
        return groovyClass;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> dataIn) {
        Set<String> importConflictVars = Sets.intersection(dataIn.keySet(), GROOVY_DEFAULT_IMPORTS);
        if (!importConflictVars.isEmpty() && !scriptContentActual.contains(GROOVY_DEFAULT_IMPORTS_WORKAROUND_CODE_HEADER)) {
            logger.debug("detected var/import conflict, apply workaround");
            String newScriptContent = GROOVY_DEFAULT_IMPORTS_WORKAROUND_CODE_HEADER + "\n" + importConflictVars.stream().sorted().distinct().map(k -> "def %s = binding.variables.%s;".formatted(k, k)).collect(joining("\n")) + "\n" + scriptContentOriginal;
            return new GroovyScriptExecutorImpl(groovyClass.getName(), newScriptContent, customClassLoader).execute(dataIn);//TODO cache ??
        } else {
            logger.debug("execute groovy script class =< {} >", groovyClass);
            logger.trace("execute groovy script (actual) =\n\n{}\n\nwith data = \n\n{}\n", scriptContentActual, mapToLoggableStringLazy(dataIn));
            Binding binding = new Binding();
            dataIn.forEach(binding::setVariable);
            Object output;
            try {
                ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                if (customClassLoader != null) {
                    Thread.currentThread().setContextClassLoader(customClassLoader);
                }
                try {
                    Script script = InvokerHelper.newScript(groovyClass, binding);
                    output = script.run();
                } finally {
                    if (customClassLoader != null) {
                        Thread.currentThread().setContextClassLoader(contextClassLoader);
                    }
                }
            } catch (Exception ex) {
                StackTraceElement element = list(ex.getStackTrace()).stream().filter(e -> equal(e.getClassName(), groovyClass.getName())).limit(1).collect(toOptional()).orElse(null);
                logger.error("error executing script at line = {} script = \n\n{}\n", Optional.ofNullable(element).map(e -> element.getLineNumber() + SCRIPT_LINE_OFFSET).orElse(null), addLineNumbers(scriptContentOriginal));
                if (element == null) {
                    throw new GroovyScriptException(ex, "error executing groovy script");
                } else {
                    throw new GroovyScriptException(ex, "error executing groovy script at line = %s, near < %s >", element.getLineNumber() + SCRIPT_LINE_OFFSET, abbreviate(safe(() -> getLine(element.getLineNumber() - 1, scriptContentActual))));
                }
            }
            Map<String, Object> map = map((Map<String, Object>) binding.getVariables()).accept(m -> {
                if (!m.containsKey(SCRIPT_OUTPUT_VAR) && output != null) {
                    m.put(SCRIPT_OUTPUT_VAR, output);
                }
            }).mapValues(GroovyScriptExecutorImpl::groovyToSystem);
            logger.trace("script output =\n\n{}\n", mapToLoggableStringLazy(map));
            return map;
        }
    }

    @Override
    public Map<String, Object> executeQuietly(Map<String, Object> dataIn) {
        Set<String> importConflictVars = Sets.intersection(dataIn.keySet(), GROOVY_DEFAULT_IMPORTS);
        if (!importConflictVars.isEmpty() && !scriptContentActual.contains(GROOVY_DEFAULT_IMPORTS_WORKAROUND_CODE_HEADER)) {
            String newScriptContent = GROOVY_DEFAULT_IMPORTS_WORKAROUND_CODE_HEADER + "\n" + importConflictVars.stream().sorted().distinct().map(k -> "def %s = binding.variables.%s;".formatted(k, k)).collect(joining("\n")) + "\n" + scriptContentOriginal;
            return new GroovyScriptExecutorImpl(groovyClass.getName(), newScriptContent, customClassLoader).execute(dataIn);//TODO cache ??
        } else {
            Binding binding = new Binding();
            dataIn.forEach(binding::setVariable);
            Object output;
            try {
                ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                if (customClassLoader != null) {
                    Thread.currentThread().setContextClassLoader(customClassLoader);
                }
                try {
                    Script script = InvokerHelper.newScript(groovyClass, binding);
                    output = script.run();
                } finally {
                    if (customClassLoader != null) {
                        Thread.currentThread().setContextClassLoader(contextClassLoader);
                    }
                }
            } catch (Exception ex) {
                return null;
            }
            Map<String, Object> map = map((Map<String, Object>) binding.getVariables()).accept(m -> {
                if (!m.containsKey(SCRIPT_OUTPUT_VAR) && output != null) {
                    m.put(SCRIPT_OUTPUT_VAR, output);
                }
            }).mapValues(GroovyScriptExecutorImpl::groovyToSystem);
            return map;
        }
    }

    @Nullable
    private static Object groovyToSystem(@Nullable Object value) {
        if (value instanceof GString gString) {//TODO apply recursive
            return gString.toString();
        } else {
            return value;
        }
    }
}
