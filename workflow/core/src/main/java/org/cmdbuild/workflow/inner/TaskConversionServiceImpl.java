/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import static com.google.common.collect.Maps.transformValues;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.auth.role.RolePrivilege.RP_DATA_ALL_WRITE;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.workflow.WorkflowTypeConverter;
import org.cmdbuild.workflow.dao.RiverFlowConversionService;
import static org.cmdbuild.workflow.inner.FlowConversionMode.CM_FULL;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.FlowActivity;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.TaskImpl;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import org.cmdbuild.workflow.river.engine.RiverTask;
import static org.cmdbuild.workflow.utils.ClosedFlowUtils.buildTaskForClosedFlowAddWidgets;
import static org.cmdbuild.workflow.utils.ClosedFlowUtils.isDummyTaskIdForClosedFlow;
import org.cmdbuild.workflow.utils.WfWidgetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskConversionServiceImpl implements TaskConversionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WidgetService widgetService;
    private final OperationUserSupplier userSupplier;
    private final RiverFlowConversionService conversionService;
    private final TaskDefinitionConversionService definitionConversionService;
    private final WorkflowTypeConverter typeConverter;

    public TaskConversionServiceImpl(WidgetService widgetService, OperationUserSupplier userSupplier, RiverFlowConversionService conversionService, TaskDefinitionConversionService definitionConversionService, WorkflowTypeConverter typeConverter) {
        this.widgetService = checkNotNull(widgetService);
        this.userSupplier = checkNotNull(userSupplier);
        this.conversionService = checkNotNull(conversionService);
        this.definitionConversionService = checkNotNull(definitionConversionService);
        this.typeConverter = checkNotNull(typeConverter);
    }

    @Override
    public Task toUserTask(Flow flow, RiverTask task, String userTaskId, String taskPerformer) {
        RiverFlow riverFlow = conversionService.cardToRiverFlow(flow);//TODO: cache converted river flow in flow instance
        return toUserTask(flow, riverFlow, task, userTaskId, taskPerformer, CM_FULL);
    }

    @Override
    public List<Task> getTaskList(Flow flow, FlowConversionMode mode) {
        RiverFlow riverFlow = conversionService.cardToRiverFlow(flow, mode);
        return flow.getFlowActivities().stream().map((a) -> toUserTask(flow, riverFlow, riverFlow.getPlan().getTask(a.getDefinitionId()), a.getInstanceId(), a.getPerformerGroup(), mode)).collect(toList());
    }

    @Override
    public Task getTask(Flow card, String userTaskId, FlowConversionMode mode) {
        try {
            RiverFlow riverFlow = conversionService.cardToRiverFlow(card, mode);
            if (isDummyTaskIdForClosedFlow(userTaskId)) {
                return buildTaskForClosedFlowAddWidgets(card, widgetService);
            } else {
                FlowActivity flowActivity = card.getFlowActivityByInstanceId(userTaskId);
                RiverTask riverTask = riverFlow.getTaskById(flowActivity.getDefinitionId());
                return toUserTask(card, riverFlow, riverTask, userTaskId, flowActivity.getPerformerGroup(), mode);
            }
        } catch (Exception ex) {
            throw new WorkflowException(ex, "error building task for card = %s user task id = %s", card, userTaskId);
        }
    }

    private Task toUserTask(Flow flow, RiverFlow riverFlow, RiverTask task, String userTaskId, String taskPerformer, FlowConversionMode mode) {
        try {
            logger.debug("convert river task = {} from flow = {} to user task", task, flow);

            TaskDefinition taskDefinition = definitionConversionService.toTaskDefinition(task);
            OperationUser operationUser = userSupplier.getUser();

            Task res = TaskImpl.builder()
                    .withTaskDefinition(taskDefinition)
                    .withTaskId(userTaskId)
                    .withFlowId(flow.getFlowId())
                    .withTaskPerformer(taskPerformer)
                    .accept(b -> {
                        switch (mode) {
                            case CM_FULL ->
                                b.withTaskWidgetSupplier(new TaskWidgetSupplier(flow.getClassName(), riverFlow::getData, taskDefinition));
                            case CM_LEAN ->
                                b.withTaskWidgetSupplier(Collections::emptyList);
                            default ->
                                throw new IllegalArgumentException("unsupported conversion mode = " + mode);
                        }
                        if (taskDefinition.hasTaskDescriptionAttrName()) {
                            Object value = riverFlow.getData().get(taskDefinition.getTaskDescriptionAttrName());
                            value = typeConverter.inflateFlowValueToCardValue(value);
                            b.withDescriptionValue(value);
                        }
                        if (taskDefinition.hasActivitySubsetId()) {
                            Object value = taskDefinition.getTaskActivitySubsetId();
                            b.withActivitySubsetId(value);
                        }
                    })
                    .isWritable(task.isUser() && riverFlow.isRunning() && (operationUser.hasPrivileges(RP_DATA_ALL_WRITE) || operationUser.hasActiveGroupName(taskPerformer)))
                    .withCard(flow)
                    .build();

            logger.debug("converted river task = {} from flow = {} to user task = {}", task, flow, res);

            return res;
        } catch (Exception ex) {
            throw new WorkflowException(ex, "error converting task = %s to user task", task);
        }
    }

    private class TaskWidgetSupplier implements Supplier<List<Widget>> {

        private final Supplier<Map<String, Object>> flowDataSupplier;
        private final TaskDefinition taskDefinition;
        private final String processId;
        private List<Widget> widgets;

        public TaskWidgetSupplier(String processId, Supplier<Map<String, Object>> flowDataSupplier, TaskDefinition taskDefinition) {
            this.flowDataSupplier = checkNotNull(flowDataSupplier);
            this.taskDefinition = checkNotNull(taskDefinition);
            this.processId = checkNotBlank(processId);
        }

        @Override
        public List<Widget> get() {
            if (widgets == null) {

                Map<String, Object> dataForWidgets = flowDataSupplier.get();

                dataForWidgets = map(transformValues(dataForWidgets, WfWidgetUtils::convertValueForWidget));

                widgets = widgetService.widgetDataToWidget(processId, taskDefinition.getId(), taskDefinition.getWidgets(), dataForWidgets);
            }
            return widgets;
        }

    }

}
