/*
 * CMDBuild has been developed and is managed by Pat srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataSource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.service.rest.v4.wshelpers.SystemPluginHelper;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.systemplugin.SystemPluginService;
import org.cmdbuild.systemplugin.UploadSystemPluginService;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ataboga
 */
@Path("system/plugins")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
@Tag(name = "System", description = "System related operations")
public class SystemPluginsWs {

    private final SystemPluginService pluginService;
    private final UploadSystemPluginService uploadPluginService;
    private final SystemPluginHelper pluginHelper;

    public SystemPluginsWs(SystemPluginService pluginService, UploadSystemPluginService uploadPluginService, SystemPluginHelper pluginHelper) {
        this.pluginService = checkNotNull(pluginService);
        this.uploadPluginService = checkNotNull(uploadPluginService);
        this.pluginHelper = checkNotNull(pluginHelper);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all system plugins",
            description = "Get all system plugins",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of system plugins"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll() {
        return response(list(pluginService.getSystemPlugins()).map(pluginHelper::serializePluginDetailed));
    }

    @GET
    @Path("{pluginCode}")
    @Operation(
            summary = "Get system plugin details",
            description = "Get system plugin details",
            parameters = {
                    @Parameter(name = "pluginCode", in = ParameterIn.PATH, description = "Code of the plugin to retrieve details for", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of system plugin details"),
                    @ApiResponse(responseCode = "404", description = "System plugin not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("pluginCode") String pluginCode
    ) {
        SystemPlugin plugin = pluginService.getSystemPlugin(pluginCode);
        return response(pluginHelper.serializePluginDetailed(plugin));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Deploy system plugins",
            description = "Deploy system plugins. The request should be a multipart request containing one or more plugin files to deploy. Each plugin file should be included as a separate part in the multipart request, and should have a content type of application/zip. The filename of each part will be used as the plugin code for the deployed plugin. If a plugin with the same code already exists, it will be overwritten by the new plugin. After deploying the plugins, the system will automatically apply any necessary patches to ensure that the plugins are up to date and compatible with the current system version.",
            requestBody = @RequestBody(
                    description = "Multipart request containing one or more plugin files to deploy. Each plugin file should be included as a separate part in the multipart request, and should have a content type of application/zip. The filename of each part will be used as the plugin code for the deployed plugin.",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "object"))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deployment of system plugins"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid multipart data provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deploy(
            List<Attachment> parts
    ) {
        List<DataSource> dataFiles = list(parts).map(a -> newDataSource(() -> a.getDataHandler().getInputStream(), a.getContentType().toString(), a.getContentDisposition().getFilename()));
        uploadPluginService.deploySystemPlugins(dataFiles);
        return success();
    }

    @POST
    @Path("{pluginCode}/patch")
    @Operation(
            summary = "Apply patches for a system plugin",
            description = "Apply patches for a system plugin. This operation will apply any necessary patches to ensure that the specified plugin is up to date and compatible with the current system version. The plugin to apply patches for is identified by the plugin code provided in the path parameter. If the specified plugin does not exist, a 404 Not Found response will be returned. If the patches are applied successfully, a 200 OK response will be returned. If there is an error applying the patches, a 500 Internal Server Error response will be returned.",
            parameters = {
                    @Parameter(name = "pluginCode", in = ParameterIn.PATH, description = "Code of the plugin to apply patches for", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful application of patches for the specified system plugin"),
                    @ApiResponse(responseCode = "404", description = "System plugin not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error while applying patches")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object applyPatches(
            @PathParam("pluginCode") String pluginCode
    ) {
        pluginHelper.applyPatches(pluginService.getSystemPlugin(pluginCode));
        return success();
    }

}
