/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.service.rest.v4.command.SystemPluginsWsCommand;
import org.cmdbuild.service.rest.v4.wshelpers.SystemPluginHelper;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.springframework.stereotype.Component;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ataboga
 */
@Path("administration/system/plugins")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
@Tag(name = "System Plugins", description = "System Plugins")
@Component
public class SystemPluginsWs_Administration {

    private final SystemPluginHelper pluginHelper;

    private final SystemPluginsWsCommand command;

    public SystemPluginsWs_Administration(SystemPluginHelper pluginHelper, SystemPluginsWsCommand command) {
        this.pluginHelper = checkNotNull(pluginHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all plugins",
            description = "Get all plugins",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll() {
        return response(list(command.readAll()).map(pluginHelper::serializePluginDetailed));
    }

    @GET
    @Path("{pluginCode}")
    public Object read(
            @PathParam("pluginCode") @Parameter(description = "Code of the plugin to query") String pluginCode
    ) {
        SystemPlugin plugin = command.read(pluginCode);
        return response(pluginHelper.serializePluginDetailed(plugin));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            requestBody = @RequestBody(description = "Plugin files to deploy (parts may have any name)", required = true,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "files", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
                    })),
            summary = "Deploy a plugin",
            description = "Deploy a plugin",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object deploy(List<Attachment> parts) {
        command.deploy(parts);
        return success();
    }

    @POST
    @Path("{pluginCode}/patch")
    @Operation(
            summary = "Apply patches to a plugin",
            description = "Apply patches to a plugin",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object applyPatches(
            @PathParam("pluginCode") @Parameter(description = "Code of the plugin to query") String pluginCode
    ) {
        command.applyPatches(pluginCode);
        return success();
    }
}
