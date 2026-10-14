package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.cmdbuild.service.rest.v4.endpoint.AsyncOperationWs_Administration;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("async/")
@Tag(name = "Async operations", description = "Operations related to async operations")
@Produces(APPLICATION_JSON)
public class AsyncOperationWs {

    private final AsyncOperationWs_Administration asyncOperationWs_adm;

    public AsyncOperationWs(AsyncOperationWs_Administration asyncOperationWs_adm) {
        this.asyncOperationWs_adm = asyncOperationWs_adm;
    }

    @GET
    @Path("jobs/{jobId}")
    @Operation(
            summary = "Get status of an async job",
            description = "Obtain the status of an async job. The response includes the current status of the job and " +
                    "additional details such as the start time, end time, and any error messages if the job failed.",
            parameters = {
                    @Parameter(name = "jobId", in = ParameterIn.PATH, description = "Identifier of the async job to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Async job not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAsyncJobStatus(
            @PathParam("jobId") Long jobId
    ) {
        return asyncOperationWs_adm.getAsyncJobStatus(jobId);
    }

    @GET
    @Path("jobs/{jobId}/response")
    @Operation(
            summary = "Get result of an async job",
            description = "Obtain the result of an async job. This endpoint should be called after the async job has " +
                    "completed (i.e., its status is 'completed'). The response includes the actual result of the async " +
                    "job, which can be any JSON-serializable object depending on the specific operation performed by " +
                    "the async job. If the job failed, the response includes details about the failure (e.g., error messages).",
            parameters = {
                    @Parameter(name = "jobId", in = ParameterIn.PATH, description = "Identifier of the async job to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Async job not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAsyncJobResult(
            @PathParam("jobId") Long jobId
    ) {
        return asyncOperationWs_adm.getAsyncJobResult(jobId);
    }

}
