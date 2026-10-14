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
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("processes/{processId}/instance_activities/")
@Tag(name = "Processes", description = "Processes")
@Produces(APPLICATION_JSON)
public class ProcessTaskWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ProcessTaskWs processTaskWs;

    public ProcessTaskWs(org.cmdbuild.service.rest.v4.endpoint.ProcessTaskWs processTaskWs) {
        this.processTaskWs = checkNotNull(processTaskWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all activities of a process",
            description = "Returns all activities of a process",
            parameters = {
                    @Parameter(name = PROCESS_ID, description = "Id of the process to query"),
                    @Parameter(name = POSITION_OF, description = "Position of the card in the process"),
                    @Parameter(name = POSITION_OF_GOTOPAGE, description = "Whether to go to the page of the position"),
                    @Parameter(name = FILTER, description = "Filter to apply to the query"),
                    @Parameter(name = START, description = "Offset to start the query from"),
                    @Parameter(name = LIMIT, description = "Limit the number of items returned"),
                    @Parameter(name = SORT, description = "Sort the query results")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process activities"),
                    @ApiResponse(responseCode = "404", description = "Process not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAllActivities(
            @PathParam(PROCESS_ID) String processId,
            @QueryParam(POSITION_OF) Long positionOfCard,
            @QueryParam(POSITION_OF_GOTOPAGE) @DefaultValue(TRUE) Boolean goToPage,
            @QueryParam(FILTER) String filter,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(SORT) String sort
    ) {
        return processTaskWs.getAllActivities(processId, positionOfCard, goToPage, filter, offset, limit, sort);
    }
}
