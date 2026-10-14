package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.endpoint.DashboardWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.DashboardWs_Management;
import org.cmdbuild.service.rest.v4.model.WsDashboardData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;


@Path("dashboards/")
@Produces(APPLICATION_JSON)
@Tag(name = "Dashboards", description = "Operations related to dashboards")
public class DashboardWs {

    private final DashboardWs_Administration dashboardWs_adm;
    private final DashboardWs_Management dashboardWs_mng;

    public DashboardWs(DashboardWs_Administration dashboardWs_adm, DashboardWs_Management dashboardWs_mng) {
        this.dashboardWs_adm = checkNotNull(dashboardWs_adm);
        this.dashboardWs_mng = checkNotNull(dashboardWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all dashboards",
            description = "Get all dashboards. If the user has admin view permissions, all dashboards will be returned. Otherwise, only dashboards for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information about dashboards, such as the configuration of widgets contained in the dashboard"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of results to return"),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view dashboards"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset
    ) {
        if (isAdminViewMode(viewMode)) {
            return dashboardWs_adm.getAll(detailed, limit, offset);
        }
        return dashboardWs_mng.getAll(detailed, limit, offset);
    }

    @GET
    @Path("{id}/")
    @Operation(
            summary = "Get dashboard by id or code",
            description = "Get dashboard by id or code. If the user has admin view permissions, the dashboard will be returned if it exists, regardless of the user's management permissions for that dashboard. Otherwise, the dashboard will only be returned if the user has management permissions for it. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id or code of the dashboard to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the dashboard or the dashboard does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("id") String idOrCode
    ) {
        if (isAdminViewMode(viewMode)) {
            return dashboardWs_adm.readOne(idOrCode);
        }
        return dashboardWs_mng.readOne(idOrCode);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new dashboard",
            description = "Create a new dashboard. The dashboard code must be unique and can only contain letters, numbers, underscores and hyphens",
            requestBody = @RequestBody(description = "Data for the new dashboard", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the dashboard code is not unique or contains invalid characters, or the request body is invalid"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create dashboards"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            WsDashboardData data
    ) {
        return dashboardWs_adm.create(data);
    }

    @PUT
    @Path("{id}/")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update a dashboard",
            description = "Update a dashboard. The dashboard code must be unique and can only contain letters, numbers, underscores and hyphens",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id or code of the dashboard to update")
            },
            requestBody = @RequestBody(description = "Data for updating the dashboard", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the dashboard code is not unique or contains invalid characters, or the request body is invalid"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update dashboards or the dashboard does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("id") Long id,
            WsDashboardData data
    ) {
        return dashboardWs_adm.update(id, data);
    }

    @DELETE
    @Path("{id}/")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete a dashboard",
            description = "Delete a dashboard",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id or code of the dashboard to delete")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete dashboards or the dashboard does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("id") Long id
    ) {
        return dashboardWs_adm.delete(id);
    }
}
