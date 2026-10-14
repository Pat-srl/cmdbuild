/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.TaskAttributeImpl;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.TaskDefinitionImpl;
import org.cmdbuild.workflow.model.TaskImpl;
import org.cmdbuild.workflow.model.TaskImpl.TaskImplBuilder;

public class ClosedFlowUtils {

    public static final String DUMMY_TASK_FOR_CLOSED_PROCESS = "DUMMY_TASK_FOR_CLOSED_PROCESS";

    public static Task buildTaskForClosedFlow(Flow flow) {
        return buildTaskImplBuilderForClosedTask(flow).build();
    }

    public static Task buildTaskForClosedFlowAddWidgets(Flow flow, WidgetService widgetService) {
        TaskImplBuilder taskImplBuilder = buildTaskImplBuilderForClosedTask(flow);
        Classe classe = flow.getType().asClasse();
        return taskImplBuilder
                .withTaskWidgetSupplier(() -> list(widgetService.getActiveWidgetsForClass(classe))
                .map(w -> widgetService.widgetDataToWidget(classe.getName(), null, w, map(classe.getActiveServiceAttributes(), Attribute::getName, a -> flow.get(a.getName())))))
                .build();
    }

    public static TaskDefinition buildTaskDefinitionForClosedTask() {
        return TaskDefinitionImpl.builder()
                .withId(DUMMY_TASK_FOR_CLOSED_PROCESS)
                .withDescription("Closed process")
                .build();//TODO check this
    }

    public static boolean isDummyTaskIdForClosedFlow(String userTaskId) {
        return userTaskId.startsWith("closed_flow_");
    }

    private static TaskImplBuilder buildTaskImplBuilderForClosedTask(Flow flow) {
        checkArgument(flow.isCompleted(), "cannot build completed flow task for flow = %s: this flow is not completed", flow);
        TaskDefinition def = TaskDefinitionImpl.builder()
                .withId(DUMMY_TASK_FOR_CLOSED_PROCESS)
                .withDescription("Closed process")
                .withVariables(flow.getType().getActiveServiceAttributes().stream().map(a -> TaskAttributeImpl.builder().withName(a.getName()).withWritable(false).build()).collect(toList()))
                .build();//TODO check this
        return TaskImpl.builder() //TODO check this
                .withTaskDefinition(def)
                .withCard(flow)
                .withFlowId(flow.getFlowId())
                .withTaskId(format("closed_flow_%s", flow.getFlowId()))
                .isWritable(false)
                .withTaskPerformer("nobody");
    }
}
