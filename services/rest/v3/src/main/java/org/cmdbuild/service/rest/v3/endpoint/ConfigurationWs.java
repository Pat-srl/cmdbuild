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

@Path("configuration/")
@Tag( name = "Configuration", description = "Configuration")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ConfigurationWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ConfigurationWs configurationWs;

    public ConfigurationWs(org.cmdbuild.service.rest.v4.endpoint.ConfigurationWs configurationWs) {
        this.configurationWs = checkNotNull(configurationWs);
    }

    @GET
    @Path("public")
    @Operation(
            summary = "Get public configuration",
            description = "Get public configuration",
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of public configuration data")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getPublicConfig() {
        return configurationWs.getPublicConfig();
    }

    @GET
    @Path("system")
    @Operation(
            summary = "Get system configuration",
            description = "Get system configuration",
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of system configuration data")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getSystemConfig() {
        return configurationWs.getSystemConfig();
    }
}
