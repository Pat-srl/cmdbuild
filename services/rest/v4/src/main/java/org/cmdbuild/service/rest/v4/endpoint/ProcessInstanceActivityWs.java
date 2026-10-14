package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.serializationhelpers.ProcessWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ProcessInstanceActivityWsCommand;
import org.cmdbuild.workflow.model.Task;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("processes/{" + PROCESS_ID + "}/instances/{" + PROCESS_INSTANCE_ID + "}/activities/")
@Tag(name = "Process instance activities", description = "Operations related to process instance activities")
@Produces(APPLICATION_JSON)
@Component
public class ProcessInstanceActivityWs {

    private final ProcessWsSerializationHelper processWsSerializationHelper;
    private final ProcessInstanceActivityWsCommand command;

    public ProcessInstanceActivityWs(ProcessWsSerializationHelper processWsSerializationHelper, ProcessInstanceActivityWsCommand command) {
        this.processWsSerializationHelper = checkNotNull(processWsSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get activities for a process instance",
            description = "Get activities for a process instance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of activities data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @PathParam(PROCESS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(PROCESS_INSTANCE_ID) @Parameter(description = "Id of the process instance to query") Long cardId,
            @QueryParam(DETAILED) @Parameter(description = "Include or not full details in the response") @DefaultValue(FALSE) Boolean detailed
    ) {//TODO pagination
        List<Task> tasks = command.doReadMany(classId, cardId);
        return response(tasks.stream().map(detailed ? t -> processWsSerializationHelper.serializeDetailedTask(t) : t -> processWsSerializationHelper.serializeBasicTask(t)).collect(toList()));
    }

    @GET
    @Path("{" + PROCESS_ACTIVITY_ID + "}/")
    @Operation(
            summary = "Get an activity for a process instance",
            description = "Get an activity for a process instance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of activity data"),
                    @ApiResponse(responseCode = "404", description = "Activity not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam(PROCESS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(PROCESS_INSTANCE_ID) @Parameter(description = "Id of the process instance to query") Long cardId,
            @PathParam(PROCESS_ACTIVITY_ID) @Parameter(description = "Id of the activity to query") String taskId
    ) {
        Task task = command.doReadOne(classId, cardId, taskId);
        return response(processWsSerializationHelper.serializeDetailedTask(task));
    }

}
