package org.cmdbuild.service.rest.v3.endpoint;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.WILDCARD;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;

@Path("boot/")
@Tag(
        name = "Boot",
        description = "Services for initial system boot and configuration"
)
@Produces(APPLICATION_JSON)
public class BootWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final org.cmdbuild.service.rest.v4.endpoint.BootWs bootWs;

    public BootWs(org.cmdbuild.service.rest.v4.endpoint.BootWs bootWs) {
        this.bootWs = bootWs;
    }

    @GET
    @Path("status")
    @Operation(
            summary = "Get the current system status",
            description = "Returns the current system status",
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object status() {
        return bootWs.status();
    }

    @POST
    @Path("database/check")
    @Operation(
            summary = "Check database configuration",
            description = "Check database configuration using provided configuration",
            requestBody = @RequestBody(content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Map.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Patches applied successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object checkDatabaseConfig(
            Map<String, String> dbConfig
    ) {
        return bootWs.checkDatabaseConfig(dbConfig);
    }

    @POST
    @Path("database/configure")
    @Operation(
            summary = "Configure database",
            description = "Configure database using provided configuration",
            requestBody = @RequestBody(content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Map.class))),
            parameters = {@Parameter(name = "file", description = "File to upload", required = false)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Patches applied successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object reconfigureDatabase(
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            Map<String, String> dbConfig
    ) {
        return bootWs.reconfigureDatabase(dataHandler, dbConfig);
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
        return bootWs.getPendingPatches();
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
        return bootWs.applyPendingPatches();
    }

}
