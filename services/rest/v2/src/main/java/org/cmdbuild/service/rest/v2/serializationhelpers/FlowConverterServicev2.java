/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v2.serializationhelpers;

import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NEXT_EXECUTOR;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_PREV_EXECUTORS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_DEFINITION_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_TASK_INSTANCE_ID;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.workflow.dao.ExtendedRiverPlanRepository;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_FLOW_DATA;
import org.cmdbuild.lookup.LookupService;

@Component
public class FlowConverterServicev2 {

    private final AttributeTypeConversionService attributeDetailService;
    private final LookupService lookupService;
    private final WidgetService widgetService;
    private final ExtendedRiverPlanRepository planRepository;
    private final WsSerializationUtilsv2 serializationUtils;
    private final CardWsSerializationHelperV2 helper;

    public FlowConverterServicev2(AttributeTypeConversionService attributeDetailService, LookupService lookupService, WidgetService widgetService, ExtendedRiverPlanRepository planRepository, WsSerializationUtilsv2 serializationUtils, CardWsSerializationHelperV2 helper) {
        this.attributeDetailService = checkNotNull(attributeDetailService);
        this.lookupService = checkNotNull(lookupService);
        this.widgetService = checkNotNull(widgetService);
        this.planRepository = checkNotNull(planRepository);
        this.serializationUtils = checkNotNull(serializationUtils);
        this.helper = checkNotNull(helper);
    }

    public Object taskToTaskResponseWithFullDetail(Flow card, Task task) {
        return serializeTask(card.getType(), task)
                .with("_id", task.getDefinition().getId(), "writable", task.isWritable());
    }

    public Object buildTaskResponseWithFullDetail(Process planClasse, TaskDefinition taskDefinition) {
        return serializeTask(planClasse, taskDefinition)
                .with("_id", taskDefinition.getId(), "writable", true);
    }

    private FluentMap serializeTask(Process planClasse, TaskDefinition definition) {
        return serializeTaskWithoutWidgets(planClasse, definition)
                .with("widgets", definition.getWidgets().stream()
                        .map((w) -> widgetService.widgetDataToWidget(w, planRepository.getPlanByClasseId(planClasse.getName()).getDefaultValues()))//TODO move this somewhere else, not in ws layer
                        .map((p) -> serializationUtils.serializeWidget(p))
                        .collect(toList()));
    }

    private FluentMap serializeTask(Process planClasse, Task task) {
        return serializeTaskWithoutWidgets(planClasse, task.getDefinition())
                .with("widgets", task.getWidgets().stream().map((p) -> serializationUtils.serializeWidget(p)).collect(toList()));
    }

    private FluentMap serializeTaskWithoutWidgets(Process planClasse, TaskDefinition definition) {
        Map<String, Object> attributesByName = transformValues(uniqueIndex(planClasse.getCoreAttributes(), Attribute::getName), attributeDetailService::serializeAttributeType);
        return map(
                "description", definition.getDescription(),
                "instructions", definition.getInstructions())
                .with("attributes", buildAttributesResponse(attributesByName, definition));
    }

    private Object buildAttributesResponse(Map<String, Object> attributesByName, TaskDefinition definition) {
        AtomicInteger index = new AtomicInteger(0);
        return definition.getVariables().stream().map((attr) -> {
            return map(
                    "_id", attr.getName(),
                    "mandatory", attr.isMandatory(),
                    "writable", attr.isWritable(),
                    "index", index.getAndIncrement()
            )
                    .skipNullValues();
        }).collect(toList());
    }

    public static Object taskToTaskResponseWithBasicDetail(Task task) {
        return map(
                "_id", task.getId(),
                "writable", task.isWritable(),
                "Description", Optional.ofNullable(task.getDefinition()).map(TaskDefinition::getDescription).orElse("")
        );
    }

    private static final Set<String> WF_SYSTEM_ONLY_COLUMNS = ImmutableSet.of(ATTR_TASK_INSTANCE_ID, ATTR_NEXT_EXECUTOR, ATTR_PREV_EXECUTORS, ATTR_TASK_DEFINITION_ID, ATTR_FLOW_DATA);

    public FluentMap<String, Object> serializeFlow(Flow flowCard) {
        return helper.serializeCard(flowCard).with(
                "_name", flowCard.getFlowId(),
                "_status", flowCard.getFlowStatusId(),
                "_status_description", flowCard.getFlowStatusDescription()
        ).withoutKeys(WF_SYSTEM_ONLY_COLUMNS);
    }
}
