package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsFlowData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_INSTANCE_ID;

@Path("processes/{" + PROCESS_ID + "}/instances/")
@Tag(name = "Process instances", description = "Operations related to process instances")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstancesWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ProcessInstancesWs processInstancesWs;

    public ProcessInstancesWs(org.cmdbuild.service.rest.v4.endpoint.ProcessInstancesWs processInstancesWs) {
        this.processInstancesWs = checkNotNull(processInstancesWs);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new process instance",
            description = "Create a new process instance for a specific process",
            parameters = {@Parameter(name = PROCESS_ID, description = "Id of the process to create an instance for")},
            requestBody = @RequestBody(description = "The data to create the process instance with", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of process instance data"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(@PathParam(PROCESS_ID) String processId,
                         WsFlowData processInstance) {
        return processInstancesWs.create(processId, processInstance);
    }

    @PUT
    @Path("{" + PROCESS_INSTANCE_ID + "}")
    @Operation(
            summary = "Update a process instance",
            description = "Update a process instance",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process to update"),
                    @Parameter(name = PROCESS_INSTANCE_ID, description = "Id of the process instance to update")
            },
            requestBody = @RequestBody(description = "The data to update the process instance with", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of process instance data"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(@PathParam(PROCESS_ID) String planClassId,
                         @PathParam(PROCESS_INSTANCE_ID) Long flowCardId,
                         WsFlowData processInstance) {
        return processInstancesWs.update(planClassId, flowCardId, processInstance);
    }

    @GET
    @Path("{" + PROCESS_INSTANCE_ID + "}")
    @Operation(
            summary = "Get a process instance",
            description = "Get a process instance",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process to get an instance for"),
                    @Parameter(name = PROCESS_INSTANCE_ID, description = "Id of the process instance to get"),
                    @Parameter(name = "includeModel", description = "Whether to include the process model in the response"),
                    @Parameter(name = "include_tasklist", description = "Whether to include the tasklist in the response"),
                    @Parameter(name = "includeStats", description = "Whether to include statistical data related to the process instance in the response")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process instance data"),
                    @ApiResponse(responseCode = "404", description = "Process instance not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(PROCESS_ID) String planClasseId,
            @PathParam(PROCESS_INSTANCE_ID) Long flowCardId,
            @QueryParam("includeModel") @DefaultValue(FALSE) Boolean includeModel,
            @QueryParam("include_tasklist") @DefaultValue(FALSE) Boolean includeTasklist,
            @QueryParam("includeStats") @DefaultValue(FALSE) Boolean includeStats) {
        return processInstancesWs.read(planClasseId, flowCardId, includeModel, includeTasklist, includeStats);
    }

    @GET
    @Path("{" + PROCESS_INSTANCE_ID + "}/graph/")
    @Operation(
            summary = "Get a graph image for a process instance",
            description = "Get a graph image for a process instance",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process to get an instance for"),
                    @Parameter(name = PROCESS_INSTANCE_ID, description = "Id of the process instance to get"),
                    @Parameter(name = "simplified", description = "Whether to simplify the graph image (true for simplified, false for full)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process instance graph image"),
                    @ApiResponse(responseCode = "404", description = "Process instance not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler plotGraph(
            @PathParam(PROCESS_ID) String processId,
            @PathParam(PROCESS_INSTANCE_ID) Long cardId,
            @QueryParam("simplified") @DefaultValue(FALSE) Boolean simplified
    ) {
        return processInstancesWs.plotGraph(processId, cardId, simplified);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get process instances",
            description = "Get process instances",
            parameters = {
                    @Parameter(name = PROCESS_ID, in = ParameterIn.PATH, description = "Id of the process to get instances for"),
                    @Parameter(name = "include_tasklist", in = ParameterIn.QUERY, description = "Whether to include the tasklist in the response")
            },
            requestBody = @RequestBody(description = "The query options for filtering and sorting the process instances"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process instances data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @PathParam(PROCESS_ID) String processId,
            WsQueryOptions wsQueryOptions,
            @QueryParam("include_tasklist") @DefaultValue(FALSE) Boolean includeTasklist
    ) {
        return processInstancesWs.readMany(processId, wsQueryOptions, includeTasklist);
    }

    @DELETE
    @Path("{" + PROCESS_INSTANCE_ID + "}") //TODO add permission control; use 'user can stop' wf option'
    @Operation(
            summary = "Abort a process instance",
            description = "Abort a process instance",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process to abort an instance for"),
                    @Parameter(name = PROCESS_INSTANCE_ID, description = "Id of the process instance to abort")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successful deletion of process instance"),
                    @ApiResponse(responseCode = "404", description = "Process instance not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public void delete(
            @PathParam(PROCESS_ID) String processId,
            @PathParam(PROCESS_INSTANCE_ID) Long instanceId
    ) {
        processInstancesWs.delete(processId, instanceId);
    }

    @POST
    @Path("{" + PROCESS_INSTANCE_ID + "}/suspend")
    @Operation(
            summary = "Suspend a process instance",
            description = "Suspend a process instance",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process to suspend an instance for"),
                    @Parameter(name = PROCESS_INSTANCE_ID, description = "Id of the process instance to suspend")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful suspension of process instance"),
                    @ApiResponse(responseCode = "404", description = "Process instance not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public void suspend(
            @PathParam(PROCESS_ID) String processId,
            @PathParam(PROCESS_INSTANCE_ID) Long instanceId
    ) {
        processInstancesWs.suspend(processId, instanceId);
    }

    @POST
    @Path("{" + PROCESS_INSTANCE_ID + "}/resume")
    @Operation(
            summary = "Resume a process instance",
            description = "Resume a process instance",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process to resume an instance for"),
                    @Parameter(name = PROCESS_INSTANCE_ID, description = "Id of the process instance to resume")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful resumption of process instance"),
                    @ApiResponse(responseCode = "404", description = "Process instance not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public void resume(
            @PathParam(PROCESS_ID) String processId,
            @PathParam(PROCESS_INSTANCE_ID) Long instanceId
    ) {
        processInstancesWs.resume(processId, instanceId);
    }

    @DELETE
    @Path("")
    @Operation(
            summary = "Abort multiple process instances",
            description = "Abort multiple process instances",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process to abort instances for")
            },
            requestBody = @RequestBody(description = "The query options for filtering and sorting the process instances to abort"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of multiple process instances"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deleteMany(
            @PathParam(PROCESS_ID) String processId,
            WsQueryOptions wsQueryOptions
    ) {
        return processInstancesWs.deleteMany(processId, wsQueryOptions);
    }
}
