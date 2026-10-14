package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("processes/{" + PROCESS_ID + "}/instances/{" + PROCESS_INSTANCE_ID + "}/history")
@Tag(name = "Process instance history", description = "Operations related to process instance history")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstancesHistoryWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ProcessInstancesHistoryWs processInstancesHistoryWs;

    public ProcessInstancesHistoryWs(org.cmdbuild.service.rest.v4.endpoint.ProcessInstancesHistoryWs processInstancesHistoryWs) {
        this.processInstancesHistoryWs = checkNotNull(processInstancesHistoryWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get history for a process instance",
            description = "Get history for a process instance",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process"),
                    @Parameter(name = PROCESS_INSTANCE_ID, description = "Id of the process instance"),
                    @Parameter(name = LIMIT, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0"),
                    @Parameter(name = DETAILED, description = "Include or not full details in the response", schema = @Schema(type = "boolean", defaultValue = FALSE)),
                    @Parameter(name = FILTER, description = "Filter to apply to the query"),
                    @Parameter(name = "types", description = "Types of history records to retrieve", schema = @Schema(type = "string", defaultValue = "cards"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of history data"),
                    @ApiResponse(responseCode = "404", description = "Process instance or history not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getHistory(
            @PathParam(PROCESS_ID) String classId,
            @PathParam(PROCESS_INSTANCE_ID) Long cardId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response") Boolean detailed,
            @QueryParam(FILTER) String filterStr,
            @QueryParam("types") @DefaultValue("cards") String types
    ) {
        return processInstancesHistoryWs.getHistory(classId, cardId, limit, offset, detailed, filterStr, types);
    }

    @GET
    @Path("{recordId}/")
    @Operation(
            summary = "Get history record for a process instance",
            description = "Get history record for a process instance",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process"),
                    @Parameter(name = PROCESS_INSTANCE_ID, description = "Id of the process instance"),
                    @Parameter(name = RECORD_ID, description = "Id of the history record to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of history record data"),
                    @ApiResponse(responseCode = "404", description = "Process instance or history record not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getHistoryRecord(
            @PathParam(PROCESS_ID) String classId,
            @PathParam(PROCESS_INSTANCE_ID) Long id,
            @PathParam(RECORD_ID) Long recordId
    ) {
        return processInstancesHistoryWs.getHistoryRecord(classId, id, recordId);
    }

}
