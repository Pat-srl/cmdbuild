/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.cmdbuild.asyncjob.AsyncRequestJob;
import org.cmdbuild.service.rest.v4.command.AsyncOperationWsCommand;
import org.springframework.stereotype.Component;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_JOBS_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Path("administration/async/")
@Tags({
        @Tag(
                name = "Async operations",
                description = "The following documentation aims to illustrate the usage of the REST APIs by CMDBuild in order to retrieve the status of asynchronous operations."
        ),
        @Tag( name = "Administration" )
})
@Produces(APPLICATION_JSON)
@Component
public class AsyncOperationWs_Administration {

    private final AsyncOperationWsCommand command;

    public AsyncOperationWs_Administration(AsyncOperationWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("jobs/{jobId}")
    @Operation(
            summary = "Get the status of an asynchronous operation",
            description = "Get the status of an asynchronous operation.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of the status of the asynchronous operation"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource"),
                    @ApiResponse(responseCode = "404", description = "The asynchronous operation was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_JOBS_VIEW_AUTHORITY) // TODO this permission is not a perfect fit
    public Object getAsyncJobStatus(
            @PathParam("jobId") @Parameter(description = "Id of the asynchronous operation") Long jobId
    ) {
        AsyncRequestJob job = command.doGetAsyncJobStatus(jobId);
        return response(map(
                "_id", job.getId(),
                "status", job.isCompleted() ? "completed" : "running",
                "_completed", job.isCompleted()
        ));
    }

    @GET
    @Path("jobs/{jobId}/response")
    @Operation(
            summary = "Get the result of an asynchronous operation",
            description = "Get the result of an asynchronous operation",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of the result of the asynchronous operation"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource"),
                    @ApiResponse(responseCode = "404", description = "The asynchronous operation was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")

            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_JOBS_VIEW_AUTHORITY) // TODO this permission is not a perfect fit
    public Object getAsyncJobResult(
            @PathParam("jobId") @Parameter(description = "Id of the asynchronous operation") Long jobId
    ) {
        return command.doGetAsyncJobResult(jobId);
    }
}
