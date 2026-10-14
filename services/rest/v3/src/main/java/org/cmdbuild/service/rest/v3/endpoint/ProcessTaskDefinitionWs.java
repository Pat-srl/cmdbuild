package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.endpoint.ProcessTaskDefinitionWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.ProcessTaskDefinitionWs_Management;
import org.cmdbuild.service.rest.v4.model.WsTaskDefinitionData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_PROCESSES_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("processes/{processId}/activities/")
@Tag( name = "Process task definition", description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete a task definition of a chosen Process")
@Produces(APPLICATION_JSON)
public class ProcessTaskDefinitionWs {

    private final ProcessTaskDefinitionWs_Administration processTaskDefinitionWs_adm;
    private final ProcessTaskDefinitionWs_Management processTaskDefinitionWs_mng;

    public ProcessTaskDefinitionWs(ProcessTaskDefinitionWs_Administration processTaskDefinitionWs_adm, ProcessTaskDefinitionWs_Management processTaskDefinitionWs_mng) {
        this.processTaskDefinitionWs_adm = checkNotNull(processTaskDefinitionWs_adm);
        this.processTaskDefinitionWs_mng = checkNotNull(processTaskDefinitionWs_mng);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all activities of a process",
            description = "Returns all activities of a process",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process to retrieve activities from"),
                    @Parameter(name = START, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0"),
                    @Parameter(name = LIMIT, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process activities"),
                    @ApiResponse(responseCode = "404", description = "Process not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAllActivities(
            @PathParam(PROCESS_ID) String processId,
            @QueryParam(START) Long offset,
            @QueryParam(LIMIT) Long limit
    ) {
        return processTaskDefinitionWs_mng.getAllActivities(processId, offset, limit);
    }

    @GET
    @Path("{taskId}")
    @Operation(
            summary = "Get a specific activity of a process",
            description = "Returns a specific activity of a process",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process to retrieve activities from"),
                    @Parameter(name = "taskId", description = "Id of the activity to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of a process activity"),
                    @ApiResponse(responseCode = "404", description = "Process or activity not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getOne(
            @PathParam(PROCESS_ID) String processId,
            @PathParam("taskId") String taskId
    ) {
        return processTaskDefinitionWs_mng.getOne(processId, taskId);
    }

    @PUT
    @Path("{taskId}")
    @Operation(
            summary = "Update a specific activity of a process",
            description = "Updates a specific activity of a process",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process to update activities from"),
                    @Parameter(name = "taskId", description = "Id of the activity to update")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of a process activity"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update process activities or the process/activity does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_PROCESSES_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(PROCESS_ID) String processId,
            @PathParam("taskId") String taskId,  WsTaskDefinitionData data
    ) {
        return processTaskDefinitionWs_adm.update(processId, taskId, data);
    }
}
