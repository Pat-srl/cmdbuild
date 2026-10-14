package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("processes/{" + PROCESS_ID + "}/instances/{" + PROCESS_INSTANCE_ID + "}/activities/")
@Tag(name = "Process instance activities", description = "Operations related to process instance activities")
@Produces(APPLICATION_JSON)
public class ProcessInstanceActivityWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ProcessInstanceActivityWs processInstanceActivityWs;

    public ProcessInstanceActivityWs(org.cmdbuild.service.rest.v4.endpoint.ProcessInstanceActivityWs processInstanceActivityWs) {
        this.processInstanceActivityWs = checkNotNull(processInstanceActivityWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get activities for a process instance",
            description = "Get activities for a process instance",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Name of the process to query"),
                    @Parameter(name = PROCESS_INSTANCE_ID, description = "Id of the process instance to query"),
                    @Parameter(name = DETAILED, description = "Include or not full details in the response")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of activities data"),
                    @ApiResponse(responseCode = "404", description = "Process instance not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @PathParam(PROCESS_ID) String classId,
            @PathParam(PROCESS_INSTANCE_ID) Long cardId,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed
    ) {
        return processInstanceActivityWs.readMany(classId, cardId, detailed);
    }

    @GET
    @Path("{" + PROCESS_ACTIVITY_ID + "}/")
    @Operation(
            summary = "Get an activity for a process instance",
            description = "Get an activity for a process instance",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Name of the process to query"),
                    @Parameter(name = PROCESS_INSTANCE_ID, description = "Id of the process instance to query"),
                    @Parameter(name = PROCESS_ACTIVITY_ID, description = "Id of the activity to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of activity data"),
                    @ApiResponse(responseCode = "404", description = "Process instance or activity not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam(PROCESS_ID) String classId,
            @PathParam(PROCESS_INSTANCE_ID) Long cardId,
            @PathParam(PROCESS_ACTIVITY_ID) String taskId
    ) {
        return processInstanceActivityWs.readOne(classId, cardId, taskId);
    }

}
