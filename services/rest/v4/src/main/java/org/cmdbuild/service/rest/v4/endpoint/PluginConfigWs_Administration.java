package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import java.util.List;
import java.util.Objects;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_SYSCONFIG_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_SYSCONFIG_VIEW_AUTHORITY;
import org.cmdbuild.plugin.config.PluginConfigService;
import org.cmdbuild.plugin.config.dao.PluginConfigData;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PLUGIN;
import org.cmdbuild.service.rest.v4.command.PluginConfigWsCommand;
import org.cmdbuild.service.rest.v4.model.WsPluginData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("administration/plugin/{" + PLUGIN + "}/config")
@Tag(name = "Plugin configuration", description = "Operations related to plugin configuration")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_SYSCONFIG_VIEW_AUTHORITY)
public class PluginConfigWs_Administration {

    private final PluginConfigService pluginConfigService;
    private final PluginConfigWsCommand command;

    public PluginConfigWs_Administration(PluginConfigService pluginConfigService, PluginConfigWsCommand command) {
        this.pluginConfigService = checkNotNull(pluginConfigService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get plugin configurations",
            description = "Get plugin configurations",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of plugin configuration data"),
                @ApiResponse(responseCode = "404", description = "Plugin configuration not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object getMany(
            @PathParam(PLUGIN) @Parameter(description = "Name of the plugin to query", required = true, example = "cmdbuild-plugin-cmis") String pluginName
    ) {
        if (Objects.equals(pluginName, "_ALL")) {
            return response(pluginConfigService.getAll());
        }
        return response(pluginConfigService.getAllByPlugin(pluginName));
    }

    @GET
    @Path("{key}")
    @Operation(
            summary = "Get plugin configuration value",
            description = "Get plugin configuration value",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of plugin configuration value")},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object getOne(
            @PathParam(PLUGIN) @Parameter(description = "Name of the plugin to query", required = true, example = "cmdbuild-plugin-cmis") String pluginName,
            @PathParam("key") @Parameter(description = "Key") String key
    ) {
        WsPluginData wsPluginData = applyOrNull(pluginConfigService.getConfigByPluginAndKeyOrNull(pluginName, key), WsPluginData::new);
        return response(wsPluginData);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a plugin configuration",
            description = "Create a plugin configuration",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful creation of plugin configuration data")},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object create(
            @PathParam(PLUGIN) @Parameter(description = "Name of the plugin to query", required = true, example = "cmdbuild-plugin-cmis") String pluginName,
            @RequestBody(description = "Plugin data", required = true) WsPluginData pluginData
    ) {
        pluginData.setPlugin(pluginName);
        WsPluginData wsPluginData = new WsPluginData(pluginConfigService.createConfig(pluginData.toPluginConfigData()));
        return response(wsPluginData);
    }

    @PUT
    @Path("{key}")
    @Operation(
            summary = "Update a plugin configuration",
            description = "Update a plugin configuration",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful update of plugin configuration data")},
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(PLUGIN) @Parameter(description = "Name of the plugin to query", required = true, example = "cmdbuild-plugin-cmis") String pluginName,
            @PathParam("key") @Parameter(description = "Key", required = true, example = "key") String key,
            @RequestBody(description = "Plugin data", required = true) WsPluginData pluginData
    ) {
        pluginData.setPlugin(pluginName).setKey(key);
        WsPluginData wsPluginData = new WsPluginData(pluginConfigService.updateConfig(pluginData.toPluginConfigData()));
        return response(wsPluginData);
    }

    @PUT
    @Path("_MANY")
    @Operation(
            summary = "Update multiple plugin configuration values",
            description = "Update multiple plugin configuration values",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful update of multiple plugin configuration values"),
                @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                @ApiResponse(responseCode = "404", description = "Plugin configuration not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object multiCreateOrUpdate(
            @PathParam(PLUGIN) @Parameter(description = "Name of the plugin to query", required = true, example = "cmdbuild-plugin-cmis") String pluginName,
            @RequestBody(description = "List of plugin configuration entries to create or update", required = true, content = @Content(schema = @Schema(implementation = WsPluginData[].class))) List<WsPluginData> plugins
    ) {
        List<PluginConfigData> pluginsData = list(plugins).map(p -> p.setPlugin(pluginName)).map(p -> buildConfigData(pluginName, p));
        return response(list(pluginConfigService.createOrUpdateConfigs(pluginsData)).map(WsPluginData::new));
    }

    @DELETE
    @Path("{key}")
    @Operation(
            summary = "Delete plugin configuration",
            description = "Delete plugin configuration",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful delete plugin configuration"),
                @ApiResponse(responseCode = "404", description = "Plugin configuration not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(PLUGIN) @Parameter(description = "Name of the plugin to query", required = true, example = "cmdbuild-plugin-cmis") String pluginName,
            @PathParam("key") @Parameter(description = "Key", required = true, example = "key") String key
    ) {
        pluginConfigService.deleteConfig(pluginName, key);
        return success();
    }

    /**
     * Builds config data, preserving existing access if needed
     */
    private PluginConfigData buildConfigData(String pluginName, WsPluginData pluginData) {
        PluginConfigData configData = pluginData.toPluginConfigData();
        if (pluginData.getAccess() == null) {
            PluginConfigData existingConfig = pluginConfigService.getConfigByPluginAndKeyOrNull(pluginName, pluginData.getKey());
            if (existingConfig != null) {
                configData = PluginConfigData.copyOf(configData)
                        .withAccess(existingConfig.getAccess())
                        .build();
            }
        }
        return configData;
    }
}
