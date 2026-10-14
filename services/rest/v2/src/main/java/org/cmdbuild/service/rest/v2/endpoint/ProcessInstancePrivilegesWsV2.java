package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.WorkflowService;

@Path("processes/{processId}/instances/{processInstanceId}/privileges/")
@Produces(APPLICATION_JSON)
public class ProcessInstancePrivilegesWsV2 {

    private final WorkflowService workflowService;

    public ProcessInstancePrivilegesWsV2(WorkflowService workflowService) {
        this.workflowService = checkNotNull(workflowService);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("processId") String processId, @PathParam("processInstanceId") Long processInstanceId) {
        return map("data", map("stoppable", workflowService.getProcess(processId).getMetadata().isWfUserStoppable()));
    }

}
