/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task.scriptexecutors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.lang.String.format;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.script.CmScriptUtils.normalizeClassName;
import org.cmdbuild.utils.script.beanshell.BeanshellScriptExecutor;
import org.cmdbuild.utils.script.groovy.GroovyScriptService;
import org.cmdbuild.workflow.river.engine.RiverLiveTask;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.RiverTaskCompleted;
import org.cmdbuild.workflow.river.engine.RiverTaskType;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;
import org.cmdbuild.workflow.river.engine.core.CompletedTaskImpl;
import org.cmdbuild.workflow.river.engine.task.ScriptTaskExtraAttr;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR;
import org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.ScriptEngine;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.ScriptEngine.BEANSHELL;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.ScriptEngine.GROOVY;
import org.codehaus.groovy.control.ResolveVisitor;
import org.codehaus.groovy.vmplugin.VMPluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskScriptExecutorServiceImpl implements TaskScriptExecutorService {

    private final TaskClassLoaderSupplier classLoaderSupplier;
    private final GroovyScriptService groovyScriptService;

    public TaskScriptExecutorServiceImpl(GroovyScriptService groovyScriptService, TaskClassLoaderSupplier classLoaderSupplier) {
        this.groovyScriptService = checkNotNull(groovyScriptService);
        this.classLoaderSupplier = checkNotNull(classLoaderSupplier);
    }

    @Override
    public RiverTaskCompleted executeTask(RiverLiveTask liveTask, Map<String, Object> data) {
        return new TaskScriptExecutor(liveTask, data).executeTask();
    }

    private class TaskScriptExecutor {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final RiverPlan plan;
        private final RiverLiveTask liveTask;
        private final RiverTask task;
        private final Map<String, Object> data;

        public TaskScriptExecutor(RiverLiveTask liveTask, Map<String, Object> data) {
            this.liveTask = checkNotNull(liveTask);
            this.plan = checkNotNull(liveTask.getFlow().getPlan());
            this.task = liveTask.getTask();
            this.data = checkNotNull(data);
        }

        public RiverTaskCompleted executeTask() {
            String taskId = task.getId();
            checkArgument(EnumSet.of(RiverTaskType.SCRIPT_INLINE, RiverTaskType.SCRIPT_BATCH).contains(task.getTaskType()), "unable to process task %s", task);
            ScriptTaskExtraAttr scriptAttr = (ScriptTaskExtraAttr) task.getTaskTypeData();
            ScriptEngine scriptEngine = parseEnum(scriptAttr.getLanguage(), ScriptEngine.class);
            String scriptContent = scriptAttr.getScript();
            try {

                Map<String, Object> dataIn = map();
                data.forEach((key, value) -> {
                    RiverVariableInfo variableInfo = plan.getGlobalVariables().get(key);
                    if (variableInfo != null && variableInfo.isBasicType()) {
                        value = convertValue(value, variableInfo);
                    }
                    dataIn.put(key, value);
                });

                switch (scriptEngine) {
                    case GROOVY -> {
                        logger.trace("workaround for reserved words, defining them all at the beginning and end of the script");
                        scriptContent = serializeConflictVars(scriptContent, dataIn);
                    }
                    case BEANSHELL -> {
                        logger.trace("set to null global variables that where not explicitly set (this is required by beanshell)");
                        plan.getGlobalVariables().keySet().stream().filter(not(dataIn::containsKey)).forEach((key) -> {
                            dataIn.put(key, null);
                        });
                    }
                }

                logger.trace("set script input \n\n{}\n", mapToLoggableStringLazy(dataIn));

                logger.debug("executing workflow script for taskId = {} with engine = {}\n\n{}\n", taskId, scriptEngine, scriptContent);
                ClassLoader customClassLoader = classLoaderSupplier.getCustomClassLoader(liveTask);

                Map<String, Object> scriptOutput = switch (scriptEngine) {
                    case GROOVY ->
                        groovyScriptService.getScriptExecutor(format("org.cmdbuild.workflow.river.GroovyScript_%s", normalizeClassName(task.getId())), scriptContent, customClassLoader).execute(dataIn);
                    case BEANSHELL ->
                        new BeanshellScriptExecutor(scriptContent, customClassLoader).execute(dataIn);
                    default ->
                        throw new IllegalArgumentException("unsupported script engine = " + scriptEngine);
                };

                Map<String, Object> dataOut = map();
                Object activeFlagsHintVar = scriptOutput.get(NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR);
                if (activeFlagsHintVar != null && activeFlagsHintVar instanceof Iterable) {
                    Collection<String> activeFlagsHint = newLinkedHashSet((Iterable<String>) activeFlagsHintVar);
                    logger.debug("return completed task with flag hint = {}", activeFlagsHint);
                    dataOut.put(NEXT_FLAGS_TO_ACTIVATE_SCRIPT_VAR, activeFlagsHint);
                } else {
                    logger.debug("return simple completed task");
                }
                plan.getGlobalVariables().forEach((key, globalVarInfo) -> {
                    Object value = scriptOutput.get(key);
                    value = convertValue(value, globalVarInfo);
                    dataOut.put(key, value);
                });

                logger.trace("got script output \n\n{}\n", mapToLoggableStringLazy(dataOut));

                return new CompletedTaskImpl(liveTask, dataOut);
            } catch (Exception ex) {
                throw new WorkflowScriptProcessingException(ex, "error running workflow script for task = %s within flow = %s, plan = %s (%s)", taskId, liveTask.getFlowId(), liveTask.getPlanId(), liveTask.getFlow().getPlan().getName());
            }
        }

        @Nullable
        private Object convertValue(@Nullable Object rawValue, RiverVariableInfo varInfo) {
            try {
                return convert(rawValue, varInfo.getJavaType());
            } catch (Exception ex) {
                throw new WorkflowScriptProcessingException(ex, "error converting param = %s with value = '%s'", varInfo.getKey(), abbreviate(rawValue));
            }
        }

        private String serializeConflictVars(String scriptVal, Map<String, Object> dataIn) {
            String GROOVY_DEFAULT_IMPORTS_WORKAROUND_CODE_HEADER = "// default imports workaround code";
            Set<String> GROOVY_DEFAULT_IMPORTS = ImmutableSet.copyOf(VMPluginFactory.getPlugin().getDefaultImportClasses(ResolveVisitor.DEFAULT_IMPORTS).keySet());
            Set<String> importConflictVars = Sets.intersection(dataIn.keySet(), GROOVY_DEFAULT_IMPORTS);
            if (!importConflictVars.isEmpty() && !scriptVal.contains(GROOVY_DEFAULT_IMPORTS_WORKAROUND_CODE_HEADER)) {
                logger.debug("detected var/import conflict, apply workaround");
                scriptVal = GROOVY_DEFAULT_IMPORTS_WORKAROUND_CODE_HEADER + "\n"
                        + importConflictVars.stream().sorted().distinct().map(k -> "def %s = binding.variables.%s;".formatted(k, k)).collect(joining("\n")) + "\n"
                        + scriptVal + "\n"
                        + importConflictVars.stream().sorted().distinct().map(k -> "binding.variables.%s = %s;".formatted(k, k)).collect(joining("\n"));
            }
            return scriptVal;
        }

    }

}
