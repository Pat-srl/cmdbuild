package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.service.rest.common.serializationhelpers.HistorySerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ProcessWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ProcessInstancesHistoryWsCommand;
import org.cmdbuild.workflow.model.Flow;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("processes/{" + PROCESS_ID + "}/instances/{" + PROCESS_INSTANCE_ID + "}/history")
@Tag(name = "Process instance history", description = "Operations related to process instance history")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class ProcessInstancesHistoryWs {

    private final ProcessWsSerializationHelper processWsSerializationHelper;
    private final HistorySerializationHelper historySerializationHelper;
    private final ProcessInstancesHistoryWsCommand command;

    public ProcessInstancesHistoryWs(ProcessWsSerializationHelper processWsSerializationHelper, HistorySerializationHelper historySerializationHelper, ProcessInstancesHistoryWsCommand command) {
        this.processWsSerializationHelper = checkNotNull(processWsSerializationHelper);
        this.historySerializationHelper = checkNotNull(historySerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get history for a process instance",
            description = "Get history for a process instance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of history data"),
                    @ApiResponse(responseCode = "404", description = "Process instance not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getHistory(
            @PathParam(PROCESS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(PROCESS_INSTANCE_ID) @Parameter(description = "Id of the process instance to query") Long cardId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response", schema = @Schema(type = "boolean")) Boolean detailed,
            @QueryParam(FILTER) @Parameter(description = "How to filter results", schema = @Schema(ref = "DefaultHistoryFilterExample")) String filterStr,
            @QueryParam("types") @Parameter(description = "Comma separated list of history types to return", schema = @Schema(type = "string")) @DefaultValue("cards") String types
    ) {
        PagedElements<DatabaseRecord> history = command.doGetHistory(classId, cardId, limit, offset, filterStr, types);
        return response(history.stream().map(p -> {
            if (p instanceof Flow) {
                return detailed ? processWsSerializationHelper.serializeDetailedHistory((Flow) p) : processWsSerializationHelper.serializeBasicHistory((Flow) p);
            } else {
                return historySerializationHelper.serializeBasicHistory(p);
            }
        }), history.totalSize());
    }

    @GET
    @Path("{recordId}/")
    @Operation(
            summary = "Get history record for a process instance",
            description = "Get history record for a process instance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of history record data"),
                    @ApiResponse(responseCode = "404", description = "History record not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getHistoryRecord(
            @PathParam(PROCESS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(PROCESS_INSTANCE_ID) @Parameter(description = "Id of the process instance to query") Long id,
            @PathParam("recordId") @Parameter(description = "Id of the history record to query") Long recordId
    ) {
        Flow record = command.doGetHistoryRecord(classId, id, recordId);
        return response(processWsSerializationHelper.serializeDetailedHistory(record));
    }

}
