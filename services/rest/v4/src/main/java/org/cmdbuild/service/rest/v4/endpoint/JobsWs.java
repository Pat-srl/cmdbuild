package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobRunStatus;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.JobSerializer;
import org.cmdbuild.service.rest.v4.command.JobsWsCommand;
import org.cmdbuild.service.rest.v4.model.WsJobData;
import org.cmdbuild.service.rest.v4.model.WsJobRunTriggerData;
import org.cmdbuild.utils.lang.CmConvertUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.WILDCARD;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_JOBS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_JOBS_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dao.utils.CmFilterProcessingUtils.mapFilter;
import static org.cmdbuild.service.rest.common.serializationhelpers.JobSerializer.serializeDetailedJob;
import static org.cmdbuild.service.rest.common.serializationhelpers.JobSerializer.serializeDetailedJobRun;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("administration/jobs/")
@Tag(name = "Jobs", description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete a job")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_JOBS_VIEW_AUTHORITY)
@Component
public class JobsWs {

    private final JobsWsCommand command;

    public JobsWs(JobsWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all jobs",
            description = "Returns all jobs",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of jobs"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        List<JobData> jobDataList = command.doReadMany();
        return response(paged(list(jobDataList)
                .map(wsQueryOptions.isDetailed() ?
                        JobSerializer::serializeDetailedJob :
                        JobSerializer::serializeBasicJob)
                .withOnly(mapFilter(wsQueryOptions.getQuery().getFilter())),
                wsQueryOptions.getQuery()));
    }

    @GET
    @Path("{jobId}")
    @Operation(
            summary = "Get job by id or code",
            description = "Returns job by id or code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job"),
                    @ApiResponse(responseCode = "404", description = "Job not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam("jobId") @Parameter(description = "Id or code of job to query", required = true) String jobId
    ) {
        JobData job = command.doReadOne(jobId);
        return response(serializeDetailedJob(job));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create new job",
            description = "Create new job",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of job"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_JOBS_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "Job data", required = true) WsJobData data
    ) {
        JobData job = command.doCreate(data);
        return response(serializeDetailedJob(job));
    }

    @PUT
    @Path("{jobId}")
    @Operation(
            summary = "Update existing job",
            description = "Update existing job",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of job"),
                    @ApiResponse(responseCode = "404", description = "Job not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_JOBS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("jobId") @Parameter(description = "Id or code of job to update", required = true) String jobId,
            @RequestBody(description = "Job data", required = true) WsJobData data
    ) {
        JobData job = command.doUpdate(jobId, data);
        return response(serializeDetailedJob(job));
    }

    @DELETE
    @Path("{jobId}")
    @Operation(
            summary = "Delete a job",
            description = "Delete a specific job",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful deletion of job"),
                    @ApiResponse( responseCode = "404", description = "Job not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_JOBS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("jobId") @Parameter(description = "Id or code of job to delete", required = true) String jobId
    ) {
        command.doDelete(jobId);
        return success();
    }

    @POST
    @Path("{jobId}/run")
    @Operation(
            summary = "Run a job immediately",
            description = "Run a job immediately",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful run of job"),
                    @ApiResponse(responseCode = "404", description = "Job not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} ) }
    )
    @Consumes(WILDCARD)
    @RolesAllowed(ADMIN_JOBS_MODIFY_AUTHORITY)
    public Object runJobNow(
            @PathParam("jobId") @Parameter(description = "Id or code of job to run", required = true) String jobId,
            @Nullable @RequestBody(description = "Job run trigger data", required = true) WsJobRunTriggerData triggerData
    ) {
        JobRun jobRun = command.doRunJobNow(jobId, triggerData);
        return response(serializeDetailedJobRun(jobRun));
    }

    @GET
    @Path("{jobId}/runs")
    @Operation(
            summary = "Get job runs",
            description = "Returns job runs for a specific job",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job runs"),
                    @ApiResponse(responseCode = "404", description = "Job not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getJobRunsForJob(
            @PathParam("jobId") @Parameter(description = "Id or code of job to query", required = true) String jobId,
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        PagedElements<JobRun> res = command.doGetJobRunsForJob(jobId, wsQueryOptions);
        return response(res.map(wsQueryOptions.isDetailed() ? JobSerializer::serializeDetailedJobRun : JobSerializer::serializeBasicJobRun));
    }

    @GET
    @Path("{jobId}/errors")
    @Operation(
            summary = "Get job run errors",
            description = "Returns job run errors for a specific job",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job run errors"),
                    @ApiResponse(responseCode = "404", description = "Job not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getJobRunErrorsForJob(
            @PathParam("jobId") @Parameter(description = "Id or code of job to query", required = true) String jobId,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit
    ) {//TODO fix this, use standard get
        PagedElements<JobRun> res = command.doGetJobRunErrorsForJob(jobId, offset, limit);
        return response(res.map(JobSerializer::serializeBasicJobRun));
    }

    @GET
    @Path("_ANY/runs")
    @Operation(
            summary = "Get job runs",
            description = "Returns job runs",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job runs"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getJobRuns(
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        PagedElements<JobRun> res = command.doGetJobRuns(wsQueryOptions);
        return response(res.map(wsQueryOptions.isDetailed() ? JobSerializer::serializeDetailedJobRun : JobSerializer::serializeBasicJobRun));
    }

    @GET
    @Path("_ANY/errors")
    @Operation(
            summary = "Get job run errors",
            description = "Returns job run errors",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job run errors"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getJobRunErrors(
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit
    ) {//TODO fix this, use standard get
        PagedElements<JobRun> res = command.doGetJobRunErrors(offset, limit);
        return response(res.map(JobSerializer::serializeBasicJobRun));
    }

    @GET
    @Path("{jobId}/runs/{runId}")
    @Operation(
            summary = "Get job run by id",
            description = "Returns job run by id",
            parameters = {
                    @Parameter(name = "jobId", in = ParameterIn.PATH, description = "Id or code of job to query", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job run"),
                    @ApiResponse(responseCode = "404", description = "Job run not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getJobRun(
            @PathParam("runId") @Parameter(description = "Id of job run to query", required = true) Long runId
    ) {
        JobRun jobRun = command.doGetJobRun(runId);
        return response(serializeDetailedJobRun(jobRun));
    }

    @GET
    @Path("_ANY/runs/stats")
    @Operation(
            summary = "Get job run statistics",
            description = "Returns job run statistics",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job run statistics"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readJobRunStats() {
        Map<JobRunStatus, Long> jobRunStats = command.doReadJobRunStats();
        return response(map(jobRunStats).mapKeys(CmConvertUtils::serializeEnum));
    }


}
