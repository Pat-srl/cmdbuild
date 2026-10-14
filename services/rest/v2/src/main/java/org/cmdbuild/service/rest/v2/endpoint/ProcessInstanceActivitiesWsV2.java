package org.cmdbuild.service.rest.v2.endpoint;

import java.util.List;
import static java.util.stream.Collectors.toList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ACTIVITY_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_INSTANCE_ID;
import org.cmdbuild.service.rest.v2.serializationhelpers.FlowConverterServicev2;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Task;

@Path("processes/{processId}/instances/{processInstanceId}/activities/")
@Produces(APPLICATION_JSON)
public class ProcessInstanceActivitiesWsV2 {

    WorkflowService workflowService;
    private final FlowConverterServicev2 converterService;

    public ProcessInstanceActivitiesWsV2(WorkflowService workflowService, FlowConverterServicev2 converterService) {
        this.workflowService = workflowService;
        this.converterService = converterService;
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam(PROCESS_ID) String classId, @PathParam(PROCESS_INSTANCE_ID) Long cardId) {
        List<Task> tasks = workflowService.getTaskListForCurrentUserByClassIdAndCardId(classId, cardId);
        return map("data", tasks.stream().map(FlowConverterServicev2::taskToTaskResponseWithBasicDetail).collect(toList()), "meta", map("total", tasks.size()));
    }

    @GET
    @Path("{processActivityId}/")
    public Object readOne(@PathParam(PROCESS_ID) String classId, @PathParam(PROCESS_INSTANCE_ID) Long cardId, @PathParam(PROCESS_ACTIVITY_ID) String taskId) {
        Flow card = workflowService.getFlowCard(classId, cardId);
        Task task = workflowService.getUserTask(card, taskId);
        return map("data", converterService.taskToTaskResponseWithFullDetail(card, task), "meta", map("total", null));
    }

}
