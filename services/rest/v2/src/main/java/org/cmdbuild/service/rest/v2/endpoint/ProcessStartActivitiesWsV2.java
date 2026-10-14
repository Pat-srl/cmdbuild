package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.service.rest.v2.serializationhelpers.WsSerializationUtilsv2;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.utils.WorkflowUtils;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.model.TaskAttribute;
import org.cmdbuild.workflow.dao.ExtendedRiverPlanRepository;

@Path("processes/{processId}/start_activities/")
@Produces(APPLICATION_JSON)
public class ProcessStartActivitiesWsV2 {

    private final WorkflowService workflowService;
    private final OperationUserSupplier userSupplier;
    private final WsSerializationUtilsv2 serializationUtils;
    private final ExtendedRiverPlanRepository planRepository;
    private final WidgetService widgetService;

    public ProcessStartActivitiesWsV2(
            OperationUserSupplier userSupplier, 
            WorkflowService workflowService, 
            WsSerializationUtilsv2 serializationUtils, 
            WidgetService widgetService,
            ExtendedRiverPlanRepository planRepository) {
        this.workflowService = checkNotNull(workflowService);
        this.userSupplier = checkNotNull(userSupplier);
        this.serializationUtils = checkNotNull(serializationUtils);
        this.widgetService = checkNotNull(widgetService);
        this.planRepository = checkNotNull(planRepository);
    }

    @GET
    @Path("{processActivityId}")
    public Object readOne(@PathParam("processId") String processId, @PathParam("processActivityId") String activityId) {
        Process planClasse = workflowService.getProcess(processId);
        TaskDefinition task = WorkflowUtils.getEntryTaskForCurrentUser(planClasse, userSupplier.getUser());
        return map("data", serializeActivity(task, processId), "meta", map());

    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("processId") String processId) {
        Process planClasse = workflowService.getProcess(processId);
        TaskDefinition task = WorkflowUtils.getEntryTaskForCurrentUser(planClasse, userSupplier.getUser());
        return map("data", list(serializeTaskService(task)), "meta", map("total", 1));
    }

    private FluentMap<String, Object> serializeTaskService(TaskDefinition task) {
        return map("writable", true,
                "description", task.getDescription(),
                "_id", task.getId());
    }

    private FluentMap<Object, Object> serializeActivity(TaskDefinition task, String processId) {
        AtomicInteger i = new AtomicInteger(0);
        return map("writable", true,
                "description", task.getDescription(),
                "instructions", task.getInstructions(),
                "attributes", task.getVariables().stream().map((x) -> serializeVariable(x).with("index", i.getAndIncrement())).collect(toList()),
                "_id", task.getId()).with("widgets", task.getWidgets().stream()
                        .map((w) -> widgetService.widgetDataToWidget(w, planRepository.getPlanByClasseId(processId).getDefaultValues()))//TODO move this somewhere else, not in ws layer
                        .map((p) -> serializationUtils.serializeWidget(p)).collect(toList()));
    }

    private FluentMap<String, Object> serializeVariable(TaskAttribute variable) {
        return map(
                "writable", variable.isWritable(),
                "mandatory", variable.isMandatory(),
                "_id", variable.getName()
        );
    }
}
