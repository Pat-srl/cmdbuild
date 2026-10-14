/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.formstructure.FormStructureImpl;
import org.cmdbuild.formstructure.FormStructureService;
import org.cmdbuild.service.rest.common.serializationhelpers.ProcessWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ProcessTaskDefinitionWsCommand;
import org.cmdbuild.service.rest.v4.model.WsTaskDefinitionData;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_PROCESSES_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_PROCESSES_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.workflow.utils.ClosedFlowUtils.DUMMY_TASK_FOR_CLOSED_PROCESS;
import static org.cmdbuild.workflow.utils.ClosedFlowUtils.buildTaskDefinitionForClosedTask;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;

/**
 * @author ldare
 */
@Path("administration/processes/{processId}/activities/")
@Tag( name = "Process task definition", description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete a task definition of a chosen Process")
@Produces(APPLICATION_JSON)
@Component
public class ProcessTaskDefinitionWs_Administration {

    private final ProcessWsSerializationHelper processWsSerializationHelper;
    private final ProcessTaskDefinitionWsCommand command;

    public ProcessTaskDefinitionWs_Administration(ProcessWsSerializationHelper processWsSerializationHelper, ProcessTaskDefinitionWsCommand command) {
        this.processWsSerializationHelper = checkNotNull(processWsSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all activities of a process",
            description = "Returns all activities of a process",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process activities"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_PROCESSES_VIEW_AUTHORITY)
    public Object getAllActivities(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String processId,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit
    ) {
        org.cmdbuild.workflow.model.Process process = command.doGetProcess(processId);
        List<TaskDefinition> tasks = command.doGetTaskDefinitions(processId);
        return response(paged(tasks, offset, limit).map(t -> processWsSerializationHelper.serializeDetailedTaskDefinition(process, t)));
    }

    @GET
    @Path("{taskId}")
    @Operation(
            summary = "Get a specific activity of a process",
            description = "Returns a specific activity of a process",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of a process activity"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_PROCESSES_VIEW_AUTHORITY)
    public Object getOne(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String processId,
            @PathParam("taskId") @Parameter(description = "Id of the activity to query", required = true) String taskId
    ) {
        org.cmdbuild.workflow.model.Process process = command.doGetProcess(processId);
        TaskDefinition task = command.doGetTaskDefinition(processId, taskId);
        return response(processWsSerializationHelper.serializeDetailedTaskDefinition(process, task));
    }

    @PUT
    @Path("{taskId}")
    @Operation(
            summary = "Update a specific activity of a process",
            description = "Updates a specific activity of a process",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of a process activity"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_PROCESSES_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String processId,
            @PathParam("taskId") @Parameter(description = "Id of the activity to update", required = true) String taskId,
            @RequestBody(description = "Updated task definition data", required = true) WsTaskDefinitionData data
    ) {
        Process process = command.doGetProcess(processId);
        TaskDefinition task = command.doUpdate(process, processId, taskId, data);
        return response(processWsSerializationHelper.serializeDetailedTaskDefinition(process, task));
    }
}
