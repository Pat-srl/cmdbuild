package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.*;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_SYSCONFIG_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_SYSCONFIG_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;

@Path("system/config")
@Tag(name = "System configuration", description = "Operations related to system configuration")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_SYSCONFIG_VIEW_AUTHORITY)
public class SystemConfigWs {

    private final org.cmdbuild.service.rest.v4.endpoint.SystemConfigWs systemConfigWs;

    public SystemConfigWs(org.cmdbuild.service.rest.v4.endpoint.SystemConfigWs systemConfigWs) {
        this.systemConfigWs = checkNotNull(systemConfigWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get system configuration",
            description = "Get system configuration",
            parameters = {
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information in the response", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of system configuration"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getSystemConfigAll(
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed
    ) {
        return systemConfigWs.getSystemConfigAll(detailed);
    }

    @GET
    @Path("/{key}")
    @Operation(
            summary = "Get system configuration value",
            description = "Get system configuration value",
            parameters = {
                    @Parameter(name = "key", in = ParameterIn.PATH, description = "Key of the system configuration value to retrieve", required = true),
                    @Parameter(name = "include_default", in = ParameterIn.QUERY, description = "Whether to include the default value in the response if the key is not set", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of system configuration value"),
                    @ApiResponse(responseCode = "404", description = "System configuration value not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getSystemConfigValue(
            @PathParam("key") String key,
            @QueryParam("include_default") @DefaultValue(TRUE) Boolean includeDefault
    ) {
        return systemConfigWs.getSystemConfigValue(key, includeDefault);
    }

    @PUT
    @Path("/{key}")
    @Operation(
            summary = "Update system configuration value",
            description = "Update system configuration value",
            parameters = {
                    @Parameter(name = "key", in = ParameterIn.PATH, description = "Key of the system configuration value to update", required = true),
                    @Parameter(name = "value", in = ParameterIn.QUERY, description = "New value of the system configuration value", required = false),
                    @Parameter(name = "encrypt", in = ParameterIn.QUERY, description = "Whether the value should be encrypted before saving", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of system configuration value"),
                    @ApiResponse(responseCode = "404", description = "System configuration value not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(TEXT_PLAIN)
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object updateSystemConfigValue(
            @PathParam("key") String key,
            @Nullable String value,
            @QueryParam("encrypt") Boolean encrypt
    ) {
        return systemConfigWs.updateSystemConfigValue(key, value, encrypt);
    }

    @PUT
    @Path("/_MANY")
    @Operation(
            summary = "Update multiple system configuration values",
            description = "Update multiple system configuration values",
            requestBody = @RequestBody(description = "Map of key-value pairs to update", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of multiple system configuration values"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object updateSystemConfigValues(
            Map<String, String> data
    ) {
        return systemConfigWs.updateSystemConfigValues(data);
    }

    @DELETE
    @Path("/{key}")
    @Operation(
            summary = "Delete system configuration value",
            description = "Delete system configuration value",
            parameters = {@Parameter(name = "key", in = ParameterIn.PATH, description = "Key of the system configuration value to delete", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of system configuration value"),
                    @ApiResponse(responseCode = "404", description = "System configuration value not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object deleteSystemConfigValue(
            @PathParam("key") String key
    ) {
        return systemConfigWs.deleteSystemConfigValue(key);
    }

    @POST
    @Path("/reload")
    @Operation(
            summary = "Reload system configuration",
            description = "Reload system configuration",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful reload of system configuration"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(WILDCARD)
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object reloadConfig() {
        return systemConfigWs.reloadConfig();
    }
}
