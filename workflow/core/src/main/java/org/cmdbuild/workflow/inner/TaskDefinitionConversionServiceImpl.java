/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.MoreCollectors.toOptional;
import jakarta.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import static org.cmdbuild.auth.role.RolePrivilege.RP_PROCESS_ALL_EXEC;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.utils.script.groovy.GroovyScriptService;
import org.cmdbuild.widget.model.WidgetData;
import static org.cmdbuild.widget.utils.WidgetUtils.isWorkflowWidgetType;
import static org.cmdbuild.widget.utils.WidgetUtils.toWidgetData;
import org.cmdbuild.workflow.model.TaskAttribute;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.TaskDefinitionImpl;
import org.cmdbuild.workflow.model.TaskMetadata;
import org.cmdbuild.workflow.model.TaskPerformer;
import static org.cmdbuild.workflow.model.TaskPerformer.newAdminPerformer;
import static org.cmdbuild.workflow.model.TaskPerformer.newRolePerformer;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.river.engine.RiverTask;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TASK_ATTR_DESCRIPTION;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TASK_ATTR_NAME;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TASK_PERFORMER_TYPE_EXPR;
import static org.cmdbuild.workflow.river.engine.xpdl.XpdlConst.TASK_PERFORMER_TYPE_ROLE;
import org.cmdbuild.workflow.utils.TaskPerformerExpressionProcessorUtils;
import org.cmdbuild.workflow.xpdl.XpdlTaskUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskDefinitionConversionServiceImpl implements TaskDefinitionConversionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EasytemplateProcessor templateResolver;
    private final GroovyScriptService groovyScriptService;
    private final CmCache<TaskDefinition> taskDefinitionCacheByTaskId;

    public TaskDefinitionConversionServiceImpl(EasytemplateProcessor templateResolver, GroovyScriptService groovyScriptService, CacheService cacheService) {
        this.templateResolver = checkNotNull(templateResolver);
        this.groovyScriptService = checkNotNull(groovyScriptService);
        taskDefinitionCacheByTaskId = cacheService.newCache("river_wf_task_definition_cache_by_task_def_id", CacheConfig.SYSTEM_OBJECTS);
    }

    @Override
    public TaskDefinition toTaskDefinition(RiverTask task) {
        return taskDefinitionCacheByTaskId.get(key(task.getPlanId(), task.getId()), () -> doToTaskDefinition(task)); //warning: will cache processed taskPerformers, for example with data from templateResolver
    }

    private TaskDefinition doToTaskDefinition(RiverTask task) {
        logger.trace("convert river task = {} to task definition", task);
        return new TaskConverter(task).toTaskDefinition();
    }

    @Nullable
    public static TaskPerformer getFirstNonAdminPerformerOrNull(List<TaskPerformer> taskPerformers) {//TODO check this
        return taskPerformers.stream()
                .filter(not(TaskPerformer::isAdmin))
                .findFirst().orElse(null);
    }

    @Nullable
    public static TaskPerformer getSingleNonAdminPerformerOrNull(List<TaskPerformer> taskPerformers) {
        return taskPerformers.stream()
                .filter(not(TaskPerformer::isAdmin))
                .collect(toOptional()).orElse(null);
    }

    public static TaskPerformer getSingleNonAdminPerformer(List<TaskPerformer> taskPerformers) {
        return checkNotNull(getSingleNonAdminPerformerOrNull(taskPerformers));
    }

    @Override
    public List<TaskPerformer> getProcessedTaskPerformersForTask(RiverTask task, Map<String, Object> flowData) {
        if (task.isUser()) {
            logger.trace("evaluating task performers for task = {}", task);
            List<TaskPerformer> taskPerformers = getTaskPerformers(task, flowData);
            logger.trace("task performers for task = {} are = {}", task, taskPerformers);
            return taskPerformers;
        } else {
            return emptyList();
        }
    }

    public static TaskPerformer getTaskPerformerForUser(List<TaskPerformer> taskPerformers, OperationUser currentUser) {
        return checkNotNull(getTaskPerformerForUserOrNull(taskPerformers, currentUser), "task performer not found for current user = %s with available performers = %s", currentUser, taskPerformers);
    }

    @Nullable
    public static TaskPerformer getTaskPerformerForUserOrNull(List<TaskPerformer> taskPerformers, OperationUser currentUser) {
        TaskPerformer taskPerformer;
        try {
            if (currentUser.hasDefaultGroup()) {
                taskPerformer = taskPerformers.stream().filter(t -> t.isRole(currentUser.getDefaultGroupNameOrNull())).collect(toOptional()).orElse(null);
                if (taskPerformer != null) {
                    return taskPerformer;
                }
            }
            if (currentUser.isMultiGroup()) {
                taskPerformer = taskPerformers.stream().filter(t -> currentUser.getActiveGroupNames().stream().anyMatch(t::isRole)).collect(toOptional()).orElse(null);
                if (taskPerformer != null) {
                    LOGGER.debug("user is multigroup and default group {} can't execute the task, selecting {}", currentUser.getDefaultGroupNameOrNull(), taskPerformer.getValue());
                    return taskPerformer;
                }
            }
            if (currentUser.hasPrivileges(RP_PROCESS_ALL_EXEC)) {
                taskPerformer = taskPerformers.stream().filter(TaskPerformer::isAdmin).collect(toOptional()).orElse(null);
                if (taskPerformer != null) {
                    return taskPerformer;
                }
            }
            return null;
        } catch (Exception ex) {
            throw new WorkflowException(ex, "error processing task performer for current user = %s with available performers = %s", currentUser, taskPerformers);
        }
    }

    private List<TaskPerformer> getTaskPerformers(RiverTask task, Map<String, Object> flowData) {
        List<TaskPerformer> performers = list();
        switch (task.getPerformerType()) {
            case TASK_PERFORMER_TYPE_ROLE ->
                performers.add(newRolePerformer(task.getPerformerValue()));
            case TASK_PERFORMER_TYPE_EXPR -> {
                logger.trace("evaluating task performer expression = {}", task.getPerformerValue());
                TaskPerformerExpressionProcessorUtils.getPerformersFromExpression(templateResolver, groovyScriptService, flowData, task.getPerformerValue()).stream()
                        .map(TaskPerformer::newRolePerformer)
                        .forEach(performers::add);
            }
            default ->
                throw unsupported("unsupported task performer type = %s", task.getPerformerType());
        }
        if (toBooleanOrDefault(task.getExtendedAttribute("adminStart"), false)) {
            performers.add(newAdminPerformer());
        }
        return performers;
    }

    private class TaskConverter {

        private final RiverTask task;

        public TaskConverter(RiverTask task) {
            this.task = checkNotNull(task);
        }

        public TaskDefinition toTaskDefinition() {
            List<TaskPerformer> taskPerformers = getProcessedTaskPerformersForTask(task, emptyMap());
            TaskDefinitionImpl taskDefinition = TaskDefinitionImpl.builder()
                    .withId(task.getId())
                    .withDescription(task.getAttr(TASK_ATTR_NAME))
                    .withInstructions(task.getAttr(TASK_ATTR_DESCRIPTION))
                    .withVariables(getTaskVariables())
                    .withMetadata(getTaskMetadata())
                    .withWidgets(getTaskWidgets())
                    .withPerformers(taskPerformers)//TODO check this
                    .withFirstNonAdminPerformer(getFirstNonAdminPerformerOrNull(taskPerformers))//TODO check this
                    //TODO extra data
                    .build();
            return taskDefinition;
        }

        private List<TaskAttribute> getTaskVariables() {
            return entriesForTask().map((entry) -> XpdlTaskUtils.taskVariableFromXpdlKeyValue(entry.getKey(), entry.getValue())).filter(notNull()).collect(toList());
        }

        private List<TaskMetadata> getTaskMetadata() {
            return entriesForTask().map((entry) -> XpdlTaskUtils.taskMetadataFromXpdlKeyValue(entry.getKey(), entry.getValue())).filter(notNull()).collect(toList());
        }

        private List<WidgetData> getTaskWidgets() {
            return entriesForTask().filter((e) -> isWorkflowWidgetType(e.getKey()) || nullToEmpty(e.getKey()).startsWith("Widget_")).map(this::parseTaskWidget).collect(toList());
        }

        private WidgetData parseTaskWidget(Entry<String, String> entry) {
            String widgetId = entry.getKey().replaceFirst("^Widget_", ""), widgetData = entry.getValue();
            try {
                return toWidgetData(widgetId, widgetData);
            } catch (Exception ex) {
                logger.debug("error processing widget id =< {} > for plan = {} task = {} with data = \n\n{}\n", widgetId, task.getPlanId(), task.getId(), widgetData);
                throw new WorkflowException(ex, "error processing widget for key =< %s > value =< %s >", widgetId, abbreviate(widgetData));
            }
        }

        private Stream<Map.Entry<String, String>> entriesForTask() {
            return task.getExtendedAttributes().entries().stream();
        }
    }

}
