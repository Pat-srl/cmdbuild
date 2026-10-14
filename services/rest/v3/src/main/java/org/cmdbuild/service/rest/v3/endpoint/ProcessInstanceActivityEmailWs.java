package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("processes/{processId}/instances/{instanceId}/activities/{activityId}/emails")
@Tag(name = "Process instance activity email", description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete an email of a process instance activity")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessInstanceActivityEmailWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ProcessInstanceActivityEmailWs processInstanceActivityEmailWs;

    public ProcessInstanceActivityEmailWs(org.cmdbuild.service.rest.v4.endpoint.ProcessInstanceActivityEmailWs processInstanceActivityEmailWs) {
        this.processInstanceActivityEmailWs = checkNotNull(processInstanceActivityEmailWs);
    }

    @POST
    @Path("sync")
    @Operation(
            summary = "Update emails with card data",
            description = "Update emails with card data",
            parameters = {
                    @Parameter(name = "instanceId", in = ParameterIn.PATH, description = "The process instance id to update the email for", required = true)
            },
            requestBody = @RequestBody(description = "The data to update the email with", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object updateEmailWithCardData(
            @PathParam("instanceId") Long flowId,
            WsQueryOptions wsQueryOptions
    ) {
        return processInstanceActivityEmailWs.updateEmailWithCardData(flowId, wsQueryOptions);
    }

}
