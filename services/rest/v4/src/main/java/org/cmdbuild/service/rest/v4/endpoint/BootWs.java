package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.dao.config.inner.Patch;
import org.cmdbuild.minions.SystemStatus;
import org.cmdbuild.service.rest.common.utils.WsSerializationUtils;
import org.cmdbuild.service.rest.v4.command.BootWsCommand;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.WILDCARD;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.minions.SystemStatusUtils.serializeSystemStatus;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("boot/")
@Tag(
        name = "Boot",
        description = "Services for initial system boot and configuration"
)
@Produces(APPLICATION_JSON)
@Component
public class BootWs {


    private final BootWsCommand command;

    public BootWs(BootWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("status")
    @Operation(
            summary = "Get the current system status",
            description = "Returns the current system status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "System status retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object status() {
        CmMapUtils.FluentMap<Object, Object> status = command.doStatus();
        return success().with(status);
    }

    @POST
    @Path("database/check")
    @Operation(
            summary = "Check database configuration",
            description = "Check database configuration using provided configuration",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Patches applied successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object checkDatabaseConfig(
            @RequestBody(description = "Database connection configuration, as map of property name to string value", required = true) Map<String, String> dbConfig
    ) {
        command.doCheckDatabaseConfig(dbConfig);
        return success();
    }

    @POST
    @Path("database/configure")
    @Operation(
            requestBody = @RequestBody(description = "Optional database dump file to restore (part 'file') and the database configuration (JSON map of property name to string value)", required = false,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary")),
                    @SchemaProperty(name = "data", schema = @Schema(type = "object", description = "Database configuration as map of property name to string value", requiredMode = Schema.RequiredMode.REQUIRED))
                    })),
            summary = "Configure database",
            description = "Configure database using provided configuration",
            responses =    {
                    @ApiResponse(responseCode = "200", description = "Patches applied successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object reconfigureDatabase(
            @Multipart(value = FILE, required = false) @Parameter(schema = @Schema(implementation = DataHandler.class)) DataHandler dataHandler,
            @RequestBody(description = "Database connection configuration, as map of property name to string value", required = true) Map<String, String> dbConfig
    ) {
        SystemStatus status = command.doReconfigureDatabase(dataHandler, dbConfig);
        return success().with("status", serializeSystemStatus(status));
    }

    @GET
    @Path("patches")
    @Operation(
            summary = "Get pending patches",
            description = "Returns a list of pending patches to be applied",
            responses = {
                    @ApiResponse( responseCode = "200", description = "List of pending patches", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Patch.class))),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getPendingPatches() {
        List<Patch> patches = command.doGetPendingPatches();
        return response(patches.stream().map(WsSerializationUtils::serializePatchInfo).collect(toList()));
    }

    @POST
    @Path("patches/apply")
    @Operation(
            summary = "Apply pending patches",
            description = "Applies all pending patches",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Patches applied successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    public Object applyPendingPatches() {
        command.doApplyPendingPatches();
        return success();
    }

}
