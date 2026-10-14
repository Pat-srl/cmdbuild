/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.ws.rs.*;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.command.SystemPluginsWsCommand;
import org.cmdbuild.service.rest.v4.wshelpers.SystemPluginHelper;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.springframework.stereotype.Component;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ataboga
 */
@Path("system/plugins")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class SystemPluginsWs_Management {

    private final SystemPluginHelper pluginHelper;

    private final SystemPluginsWsCommand command;

    public SystemPluginsWs_Management(SystemPluginHelper pluginHelper, SystemPluginsWsCommand command) {
        this.pluginHelper = checkNotNull(pluginHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all system plugins",
            description = "Get all system plugins",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll() {
        return response(list(command.readAll()).map(pluginHelper::serializePlugin));
    }

    @GET
    @Path("{pluginCode}")
    @Operation(
            summary = "Get system plugin by code",
            description = "Get system plugin by code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("pluginCode") @Parameter(description = "Plugin code", required = true) String pluginCode
    ) {
        SystemPlugin plugin = command.read(pluginCode);
        return response(pluginHelper.serializePlugin(plugin));
    }
}
