package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.cmdbuild.service.rest.common.serializationhelpers.ProcessWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ProcessStartActivityWsCommand;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;

@Path("processes/{" + PROCESS_ID + "}/start_activities/")
@Tag(name = "Process start activity", description = "Operations related to process start activity")
@Produces(APPLICATION_JSON)
@Component
public class ProcessStartActivityWs {

    private final ProcessWsSerializationHelper processWsSerializationHelper;
    private final ProcessStartActivityWsCommand command;

    public ProcessStartActivityWs(ProcessWsSerializationHelper processWsSerializationHelper, ProcessStartActivityWsCommand command) {
        this.processWsSerializationHelper = checkNotNull(processWsSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get start activity for a process",
            description = "Get start activity for a process",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of start activity data"),
                    @ApiResponse(responseCode = "404", description = "Process not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(PROCESS_ID) @Parameter(description = "Id of process", required = true) String processId
    ) {
        Process planClasse = command.doReadProcess(processId);
        TaskDefinition task = command.doReadTaskDefinition(processId);
        return response(processWsSerializationHelper.serializeDetailedTaskDefinition(planClasse, task));
    }

}
