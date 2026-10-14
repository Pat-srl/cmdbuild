/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.serializationhelpers.ProcessWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ProcessTaskDefinitionWsCommand;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 * @author ldare
 */
@Path("processes/{" + PROCESS_ID + "}/activities/")
@Tag( name = "Process task definition", description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete a task definition of a chosen Process")
@Produces(APPLICATION_JSON)
@Component
public class ProcessTaskDefinitionWs_Management {

    private final ProcessWsSerializationHelper processWsSerializationHelper;
    private final ProcessTaskDefinitionWsCommand command;

    public ProcessTaskDefinitionWs_Management(ProcessWsSerializationHelper processWsSerializationHelper, ProcessTaskDefinitionWsCommand command) {
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
                    @ApiResponse(responseCode = "404", description = "Process not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAllActivities(
            @PathParam(PROCESS_ID) @Parameter(description = "Id of the process to query") String processId,
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
                    @ApiResponse(responseCode = "404", description = "Process or activity not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getOne(
            @PathParam(PROCESS_ID) @Parameter(description = "Id of the process to query") String processId,
            @PathParam("taskId") @Parameter(description = "Id of the activity to query") String taskId
    ) {
        Process process = command.doGetProcess(processId);
        TaskDefinition task = command.doGetTaskDefinition(processId, taskId);
        return response(processWsSerializationHelper.serializeDetailedTaskDefinition(process, task));
    }
}
