package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("configuration/processes/")
@Tag( name = "Process", description = "Process")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessConfigurationWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ProcessConfigurationWs processConfigurationWs;

    public ProcessConfigurationWs(org.cmdbuild.service.rest.v4.endpoint.ProcessConfigurationWs processConfigurationWs) {
        this.processConfigurationWs = checkNotNull(processConfigurationWs);
    }

    @GET
    @Path("statuses/")
    @Operation(
            summary = "Get process statuses",
            description = "Get process statuses",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process statuses"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readStatuses() {
        return processConfigurationWs.readStatuses();
    }
}
