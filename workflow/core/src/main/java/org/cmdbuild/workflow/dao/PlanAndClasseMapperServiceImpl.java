/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ArrayListMultimap;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.dao.driver.repository.ClassStructureChangedEvent;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.utils.script.groovy.GroovyScriptService;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.inner.PlanService;
import org.cmdbuild.workflow.inner.PlanUpdatedEvent;
import org.cmdbuild.workflow.model.Process;
import static org.cmdbuild.workflow.model.Process.ADMIN_PERFORMER_AS_GROUP;
import static org.cmdbuild.workflow.model.Process.UNKNOWN_PERFORMER_AS_GROUP;
import org.cmdbuild.workflow.model.ProcessImpl;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.TaskPerformer;
import org.cmdbuild.workflow.model.WorkflowException;
import static org.cmdbuild.workflow.utils.TaskPerformerExpressionProcessorUtils.getPerformersFromExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PlanAndClasseMapperServiceImpl implements PlanAndClasseMapperService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PlanService planService;
    private final EasytemplateProcessor easytemplateProcessor;
    private final GroovyScriptService groovyScriptService;
    private final WorkflowConfiguration configuration;

    private final CmCache<Process> classesByClasseIdAndPlanId;

    public PlanAndClasseMapperServiceImpl(PlanService planService, EasytemplateProcessor easytemplateProcessor, GroovyScriptService groovyScriptService, WorkflowConfiguration configuration, CacheService cacheService, EventBusService eventBusService) {
        this.planService = checkNotNull(planService);
        this.easytemplateProcessor = checkNotNull(easytemplateProcessor);
        this.groovyScriptService = checkNotNull(groovyScriptService);
        this.configuration = checkNotNull(configuration);
        classesByClasseIdAndPlanId = cacheService.newCache("workflow_planClasse_by_classe_id_and_plan_id", CacheConfig.SYSTEM_OBJECTS);
        eventBusService.getDaoEventBus().register(new Object() {

            @Subscribe
            public void handleClassStructureChangedEvent(ClassStructureChangedEvent event) {
                classesByClasseIdAndPlanId.invalidateAll();
            }
        });
        planService.getEventBus().register(new Object() {
            @Subscribe
            public void handlePlanUpdatedEvent(PlanUpdatedEvent event) {
                classesByClasseIdAndPlanId.invalidate(key(event.getClassId(), event.getPlanId()));
                classesByClasseIdAndPlanId.invalidate(key(event.getClassId(), DEFAULT_PLAN_ID));
            }
        });
    }

    @ConfigListener(WorkflowConfiguration.class)
    public void handleConfigUpdateEvent() {
        classesByClasseIdAndPlanId.invalidateAll();
    }

    @Override
    public String getClasseIdByPlanId(String planId) {
        return checkNotNull(planService.getClassNameOrNull(planId), "class name not found for planId =< %s >", planId);
    }

    @Override
    public Process classeAndPlanIdToPlanClasse(Classe classe, String planId) {
        checkNotNull(classe);
        checkNotBlank(planId);
        return classesByClasseIdAndPlanId.get(key(classe.getName(), planId), () -> doClasseAndPlanIdToPlanClasse(classe, planId));
    }

    private Process doClasseAndPlanIdToPlanClasse(Classe classe, String suggestedPlanId) {
        logger.debug("build plan classe for classe = {}", classe);
        try {
            String planId = null;
            Map<String, TaskDefinition> entryTaskMap = null, tasksById = null;
            try {
                if (configuration.isEnabled()) {
                    if (equal(suggestedPlanId, DEFAULT_PLAN_ID)) {
                        planId = planService.getPlanIdOrNull(classe);
                    } else {
                        checkArgument(planService.hasPlanId(suggestedPlanId), "unable to find provider for plan id =< %s >", suggestedPlanId);
                        planId = suggestedPlanId;
                    }
                    if (planId != null) {
                        entryTaskMap = buildEntryTaskMap(planService.getEntryTasks(planId));
                        tasksById = uniqueIndex(planService.getAllTasks(planId), TaskDefinition::getId);
                    }
                }
            } catch (Exception ex) {
                if (configuration.returnNullPlanOnPlanError()) {
                    logger.warn(marker(), "unable to load plan for class = {} planId = {}", classe, planId, ex);
                    planId = null;
                    entryTaskMap = null;
                    tasksById = null;
                } else {
                    throw ex;
                }
            }
            return ProcessImpl.builder()
                    .withInner(classe)
                    .withPlanId(planId)
                    .withEntryTasks(entryTaskMap)
                    .withTasksById(tasksById)
                    .build();
        } catch (Exception ex) {
            throw new WorkflowException(ex, "error processing plan classe = %s with plan id = %s", classe, suggestedPlanId);
        }
    }

    private Map<String, TaskDefinition> buildEntryTaskMap(List<TaskDefinition> entryTasks) {
        Multimap<String, TaskDefinition> map = ArrayListMultimap.create();
        entryTasks.forEach((TaskDefinition task) -> {
            for (TaskPerformer performer : task.getPerformers()) {
                switch (performer.getType()) {
                    case ADMIN -> {
                        logger.debug("load admin performer for task = {}", task);
                        map.put(ADMIN_PERFORMER_AS_GROUP, task);
                    }
                    case ROLE -> {
                        logger.debug("load role performer for task = {} role =< {} >", task, performer.getValue());
                        map.put(performer.getValue(), task);
                    }
                    case EXPRESSION -> {
                        String expression = performer.getValue();
                        Set<String> names = getPerformersFromExpression(easytemplateProcessor, groovyScriptService, expression);
                        logger.debug("load role performers from expr =< {} > resolved as =< {} > for task = {} roles =< {} >", expression, names, task, performer.getValue());
                        names.forEach((group) -> map.put(group, task));
                    }
                    default -> {
                        logger.warn("unsupported performer type = {}", performer);
                        map.put(UNKNOWN_PERFORMER_AS_GROUP, task);
                    }
                }
            }

        });
        return map.asMap().entrySet().stream().map(e -> {
            if (e.getValue().size() > 1) {
                logger.warn(marker(), "multiple task found for performer = {} tasks = {} (will use the first one)", e.getKey(), e.getValue());
                return Pair.of(e.getKey(), e.getValue().iterator().next());
            } else {
                return Pair.of(e.getKey(), getOnlyElement(e.getValue()));
            }
        }).collect(toMap(Pair::getKey, Pair::getValue)).immutable();
    }

}
