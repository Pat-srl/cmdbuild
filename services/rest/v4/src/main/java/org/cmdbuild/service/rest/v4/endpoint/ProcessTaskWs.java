package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import org.cmdbuild.service.rest.common.serializationhelpers.ProcessWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ProcessTaskWsCommand;
import org.cmdbuild.workflow.model.Task;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.v4.utils.PositionOfUtils.handlePositionOfAndGetMeta;

@Path("processes/{" + PROCESS_ID + "}/instance_activities/")
@Tag(name = "Processes", description = "Processes")
@Produces(APPLICATION_JSON)
@Component
public class ProcessTaskWs {

    private final ProcessWsSerializationHelper processWsSerializationHelper;
    private final ProcessTaskWsCommand command;

    public ProcessTaskWs(ProcessWsSerializationHelper processWsSerializationHelper, ProcessTaskWsCommand command) {
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
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAllActivities(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String processId,
            @QueryParam(POSITION_OF) @Parameter(description = "Position of the card") Long positionOfCard,
            @QueryParam(POSITION_OF_GOTOPAGE) @Parameter(description = "Go to page") @DefaultValue(TRUE) Boolean goToPage,
            @QueryParam(FILTER) @Parameter(description = "Filter") String filter,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(SORT) @Parameter(description = "How to order results", schema = @Schema(ref = "DefaultSortExample")) String sort
    ) {
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder()
                .withOffset(offset)
                .withLimit(limit)
                .withSorter(CmSorterUtils.parseSorter(sort))
                .withFilter(CmFilterUtils.parseFilter(filter))
                .withPositionOf(positionOfCard, goToPage)
                .build();

        PagedElements<Task> tasklist = command.doGetAllActivities(processId, queryOptions);

        return response(tasklist.map((task) -> processWsSerializationHelper.serializeFlow(task.getProcessInstance()).with(
                "_activity_id", task.getId(),
                "_activity_writable", task.isWritable(),
                "_activity_performer", task.getPerformerName(),
                "_activity_description", task.getDefinition().getDescription(),
                "_activity_description_addition", processWsSerializationHelper.serializeTaskDescriptionValue(task.getDescriptionValue()),
                "_activity_subset_id", task.getActivitySubsetId())
        ).elements(), tasklist.totalSize(), handlePositionOfAndGetMeta(queryOptions, tasklist));
    }
}
