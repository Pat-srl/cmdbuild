package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.WILDCARD;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;

@Path("system_services/")
@Tag( name = "Minions", description = "Operations related to minions")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
public class MinionsWs {

    private final org.cmdbuild.service.rest.v4.endpoint.MinionsWs minionsWs;

    public MinionsWs(org.cmdbuild.service.rest.v4.endpoint.MinionsWs minionsWs) {
        this.minionsWs = checkNotNull(minionsWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all minions",
            description = "Get all minions",
            parameters = {@Parameter(name = "hidden", in = ParameterIn.QUERY, description = "Whether to include hidden minions in the response", required = false)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of minion data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAll(
            @QueryParam("hidden") @DefaultValue("default") String hidden
    ) {
        return minionsWs.getAll(hidden);
    }

    @GET
    @Path("{serviceId}")
    @Operation(
            summary = "Get a minion",
            description = "Get a minion",
            parameters = {@Parameter(name = "serviceId", description = "Id of the minion to query")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of minion data"),
                    @ApiResponse(responseCode = "404", description = "Minion not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getOne(
            @PathParam("serviceId") String serviceId
    ) {
        return minionsWs.getOne(serviceId);
    }

    @POST
    @Path("{serviceId}/start")
    @Operation(
            summary = "Start a minion",
            description = "Start a minion",
            parameters = {@Parameter(name = "serviceId", description = "Id of the minion to start")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful start of minion"),
                    @ApiResponse(responseCode = "404", description = "Minion not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})})
    @Consumes(WILDCARD)
    public Object start(
            @PathParam("serviceId") String serviceId
    ) {
        return minionsWs.start(serviceId);
    }

    @POST
    @Path("{serviceId}/stop")
    @Operation(
            summary = "Stop a minion",
            description = "Stop a minion",
            parameters = {@Parameter(name = "serviceId", description = "Id of the minion to stop")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful stop of minion"),
                    @ApiResponse(responseCode = "404", description = "Minion not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object stop(
            @PathParam("serviceId") String serviceId
    ) {
        return minionsWs.stop(serviceId);
    }
}
