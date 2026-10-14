package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsJobData;
import org.cmdbuild.service.rest.v4.model.WsJobRunTriggerData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.WILDCARD;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_JOBS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_JOBS_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;

@Path("jobs/")
@Tag(name = "Jobs", description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete a job")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_JOBS_VIEW_AUTHORITY)
public class JobsWs {

    private final org.cmdbuild.service.rest.v4.endpoint.JobsWs jobsWs;

    public JobsWs(org.cmdbuild.service.rest.v4.endpoint.JobsWs jobsWs) {
        this.jobsWs = checkNotNull(jobsWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all jobs",
            description = "Returns all jobs",
            requestBody = @RequestBody(description = "Query options for retrieving jobs"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of jobs"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            WsQueryOptions wsQueryOptions
    ) {
        return jobsWs.readMany(wsQueryOptions);
    }

    @GET
    @Path("{jobId}")
    @Operation(
            summary = "Get job by id or code",
            description = "Returns job by id or code",
            parameters = {@Parameter(name = "jobId", description = "Id or code of the job to query")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job"),
                    @ApiResponse(responseCode = "404", description = "Job not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam("jobId") String jobId
    ) {
        return jobsWs.readOne(jobId);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create new job",
            description = "Create new job",
            requestBody = @RequestBody(description = "Job data to create"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of job"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_JOBS_MODIFY_AUTHORITY)
    public Object create(WsJobData data) {
        return jobsWs.create(data);
    }

    @PUT
    @Path("{jobId}")
    @Operation(
            summary = "Update existing job",
            description = "Update existing job",
            requestBody = @RequestBody(description = "Job data to update"),
            parameters = {@Parameter(name = "jobId", description = "Id or code of the job to update")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of job"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Job not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_JOBS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("jobId") String jobId,
            WsJobData data
    ) {
        return jobsWs.update(jobId, data);
    }

    @DELETE
    @Path("{jobId}")
    @Operation(
            summary = "Delete a job",
            description = "Delete a specific job",
            parameters = {@Parameter(name = "jobId", description = "Id or code of the job to delete")},
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful deletion of job") ,
                    @ApiResponse( responseCode = "404", description = "Job not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_JOBS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("jobId") String jobId
    ) {
        return jobsWs.delete(jobId);
    }

    @POST
    @Path("{jobId}/run")
    @Operation(
            summary = "Run a job immediately",
            description = "Run a job immediately",
            parameters = {@Parameter(name = "jobId", description = "Id or code of the job to run")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful run of job"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} ) }
    )
    @Consumes(WILDCARD)
    @RolesAllowed(ADMIN_JOBS_MODIFY_AUTHORITY)
    public Object runJobNow(
            @PathParam("jobId") String jobId,
            @Nullable WsJobRunTriggerData triggerData
    ) {
        return jobsWs.runJobNow(jobId, triggerData);
    }

    @GET
    @Path("{jobId}/runs")
    @Operation(
            summary = "Get job runs",
            description = "Returns job runs for a specific job",
            parameters = {@Parameter(name = "jobId", description = "Id or code of the job to query")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job runs"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getJobRunsForJob(
            @PathParam("jobId") String jobId,
            WsQueryOptions wsQueryOptions
    ) {
        return jobsWs.getJobRunsForJob(jobId, wsQueryOptions);
    }

    @GET
    @Path("{jobId}/errors")
    @Operation(
            summary = "Get job run errors",
            description = "Returns job run errors for a specific job",
            parameters = {
                    @Parameter(name = "jobId", description = "Id or code of the job to query"),
                    @Parameter(name = START, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0"),
                    @Parameter(name = LIMIT, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job run errors"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getJobRunErrorsForJob(
            @PathParam("jobId") String jobId,
            @QueryParam(START) Long offset,
            @QueryParam(LIMIT) Long limit
    ) {
        return jobsWs.getJobRunErrorsForJob(jobId, offset, limit);
    }

    @GET
    @Path("_ANY/runs")
    @Operation(
            summary = "Get job runs",
            description = "Returns job runs",
            requestBody = @RequestBody(description = "Query options for retrieving job runs"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job runs"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getJobRuns(
            WsQueryOptions wsQueryOptions
    ) {
        return jobsWs.getJobRuns(wsQueryOptions);
    }

    @GET
    @Path("_ANY/errors")
    @Operation(
            summary = "Get job run errors",
            description = "Returns job run errors",
            parameters = {
                    @Parameter(name = START, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0"),
                    @Parameter(name = LIMIT, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job run errors"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getJobRunErrors(
            @QueryParam(START) Long offset,
            @QueryParam(LIMIT) Long limit
    ) {
        return jobsWs.getJobRunErrors(offset, limit);
    }

    @GET
    @Path("{jobId}/runs/{runId}")
    @Operation(
            summary = "Get job run by id",
            description = "Returns job run by id",
            parameters = {
                    @Parameter(name = "runId", description = "Id of the job run to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of job run"),
                    @ApiResponse(responseCode = "404", description = "Job run not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getJobRun(
            @PathParam("runId") Long runId
    ) {
        return jobsWs.getJobRun(runId);
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
        return jobsWs.readJobRunStats();
    }
}
