package org.cmdbuild.service.rest.v4.endpoint;

import com.google.common.collect.Ordering;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.minions.Minion;
import org.cmdbuild.minions.MinionService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.WILDCARD;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import jakarta.ws.rs.*;
import org.cmdbuild.minions.Minion;
import org.cmdbuild.service.rest.v4.command.MinionsWsCommand;
import org.cmdbuild.services.serialization.MinionSerializer;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.WILDCARD;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.services.serialization.MinionSerializer.serializeServiceStatus;

@Path("system_services/")
@Tag( name = "Minions", description = "Operations related to minions")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
@Component
public class MinionsWs {

    private final MinionsWsCommand command;

    public MinionsWs(MinionsWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all minions",
            description = "Get all minions",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of minion data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAll(
            @QueryParam("hidden") @DefaultValue("default") @Parameter(description = "Filter hidden minions", schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"default", "true", "false"})) String hidden
    ) {
        List<Minion> minionList = command.doGetAll(hidden);
        return response(minionList.stream().map(MinionSerializer::serializeServiceStatus));
    }

    @GET
    @Path("{serviceId}")
    @Operation(
            summary = "Get a minion",
            description = "Get a minion",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of minion data"),
                    @ApiResponse(responseCode = "404", description = "Minion not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getOne(
            @PathParam("serviceId") @Parameter(description = "Id of the minion to query") String serviceId
    ) {
        Minion minion = command.doGetOne(serviceId);
        return response(serializeServiceStatus(minion));
    }

    @POST
    @Path("{serviceId}/start")
    @Operation(
            summary = "Start a minion",
            description = "Start a minion",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful start of minion"),
                    @ApiResponse(responseCode = "404", description = "Minion not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})})
    @Consumes(WILDCARD)
    public Object start(
            @PathParam("serviceId") @Parameter(description = "Id of the minion to start") String serviceId
    ) {
        Minion minion = command.doStart(serviceId);
        return response(serializeServiceStatus(minion));
    }

    @POST
    @Path("{serviceId}/stop")
    @Operation(
            summary = "Stop a minion",
            description = "Stop a minion",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful stop of minion"),
                    @ApiResponse(responseCode = "404", description = "Minion not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object stop(
            @PathParam("serviceId") @Parameter(description = "Id of the minion to stop") String serviceId
    ) {
        Minion minion = command.doStop(serviceId);
        return response(serializeServiceStatus(minion));
    }
}
