package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.service.rest.v4.endpoint.CustomPageWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.CustomPageWs_Management;
import org.cmdbuild.service.rest.v4.model.WsCustomPageData;
import org.cmdbuild.ui.TargetDevice;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;


@Path("custompages/")
@Produces(APPLICATION_JSON)
@Tag(name = "Custom Pages", description = "Operations related to custom pages")
public class CustomPageWs {

    private final CustomPageWs_Administration customPageWs_adm;
    private final CustomPageWs_Management customPageWs_mng;

    public CustomPageWs(CustomPageWs_Administration customPageWs_adm, CustomPageWs_Management customPageWs_mng) {
        this.customPageWs_adm = checkNotNull(customPageWs_adm);
        this.customPageWs_mng = checkNotNull(customPageWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all custom pages",
            description = "Get all custom pages. If the user has admin view permissions, all custom pages will be returned. Otherwise, only custom pages for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'.",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object list(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode
    ) {
        if (isAdminViewMode(viewMode)) {
            return customPageWs_adm.list();
        }
        return customPageWs_mng.list();
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Get custom page by id",
            description = "Get custom page by id. If the user has admin view permissions, the custom page will be returned with all details. Otherwise, only details for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id of the custom page to retrieve"),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions") },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Custom page with the specified id does not exist or the user does not have permissions to view it")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object get(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("id") Long id
    ) {
        if (isAdminViewMode(viewMode)) {
            return customPageWs_adm.get(id);
        }
        return customPageWs_mng.get(id);
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete custom page by id",
            description = "Delete custom page by id. Deletes the custom page with the specified id. If the user has admin modify permissions, the custom page will be deleted for all target devices. Otherwise, the custom page will only be deleted for target devices for which the user has management permissions. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id of the custom page to delete"),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Custom page with the specified id does not exist or the user does not have permissions to delete it")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("id") Long id
    ) {
        return customPageWs_adm.delete(id);
    }

    @DELETE
    @Path("{id}/{targetDevice}")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete custom page for target device",
            description = "Delete custom page for target device. Deletes the custom page with the specified id for the specified target device. If the user has admin modify permissions, the custom page will be deleted for the specified target device. Otherwise, the custom page will only be deleted if the user has management permissions for the specified target device. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id of the custom page to delete"),
                    @Parameter(name = "targetDevice", in = ParameterIn.PATH, description = "Target device for which to delete the custom page", schema = @Schema(allowableValues = {"desktop", "mobile", "tablet"})),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Custom page with the specified id and target device does not exist or the user does not have permissions to delete it")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object deleteForTargetDevice(
            @PathParam("id") Long id,
            @PathParam("targetDevice") TargetDevice targetDevice
    ) {
        return customPageWs_adm.deleteForTargetDevice(id, targetDevice);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new custom page",
            description = "Create a new custom page with the provided data. Creates a new custom page with the provided data. If the user has admin modify permissions, the custom page will be created for all target devices included in the request. Otherwise, the custom page will only be created for target devices for which the user has management permissions. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'.",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "merge", in = ParameterIn.QUERY, description = "Whether to merge the provided data with existing data for custom pages. If true, the provided data will be merged with existing data. If false, the provided data will overwrite existing data. Default is false.")
            },
            requestBody = @RequestBody(description = "Data for the new custom page. The '-data' parameter should include the files to upload for the custom page. The 'data' parameter should include the data for the custom page to create. The 'merge' query parameter indicates whether to merge the provided data with existing data for custom pages or to overwrite existing data.", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @Multipart("-data") List<DataHandler> files,
            @Multipart(value = "data", required = false) WsCustomPageData data,
            @QueryParam("merge") @DefaultValue(FALSE) Boolean merge
    ) {
        return customPageWs_adm.create(files, data, merge);
    }

    @PUT
    @Path("{id}")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update an existing custom page",
            description = "Update an existing custom page with the provided data. Updates the custom page with the specified id with the provided data. If the user has admin modify permissions, the custom page will be updated for all target devices. Otherwise, the custom page will only be updated for target devices for which the user has management permissions. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id of the custom page to update"),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions") },
            requestBody = @RequestBody(description = "Updated data for the custom page. The '-data' parameter should include the files to upload for the custom page. The 'data' parameter should include the data for the custom page to update.", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "Custom page with the specified id does not exist or the user does not have permissions to update it"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("id") Long id,
            @Multipart("-data") List<DataHandler> files,
            @Multipart(value = "data|DEFAULT", required = false) WsCustomPageData data
    ) {
        return customPageWs_adm.update(id, files, data);
    }

    @GET
    @Path("{id}/{targetDevice}/{file}|{id}/{targetDevice}")
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    @Operation(
            summary = "Download custom page for target device",
            description = "Download custom page for target device. Downloads the custom page with the specified id for the specified target device. If the user has admin view permissions, the custom page will be downloaded for the specified target device. Otherwise, the custom page will only be downloaded if the user has management permissions for the specified target device. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'.",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id of the custom page to download"),
                    @Parameter(name = "targetDevice", in = ParameterIn.PATH, description = "Target device for which to download the custom page", schema = @Schema(allowableValues = {"desktop", "mobile", "tablet"})),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Custom page with the specified id and target device does not exist or the user does not have permissions to view it"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler download(
            @PathParam("id") Long id,
            @PathParam("targetDevice") TargetDevice targetDevice
    ) {
        return customPageWs_adm.download(id, targetDevice);
    }
}
