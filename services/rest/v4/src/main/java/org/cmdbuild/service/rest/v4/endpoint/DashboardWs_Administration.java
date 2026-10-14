/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.dashboard.DashboardService;
import org.cmdbuild.service.rest.common.serializationhelpers.DashboardSerializationHelper;
import org.cmdbuild.service.rest.v4.command.DashboardWsCommand;
import org.cmdbuild.service.rest.v4.model.WsDashboardData;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Path("administration/dashboards/")
@Tags({
        @Tag(name = "Dashboards", description = "APIs to manage dashboards."),
        @Tag(name = "Administration")
})
@Produces(APPLICATION_JSON)
@Component
public class DashboardWs_Administration {

    private final DashboardService dashboardService;
    private final DashboardSerializationHelper helper;
    private final DashboardWsCommand command;

    public DashboardWs_Administration(ObjectTranslationService objectTranslationService, DashboardService dashboardService, DashboardWsCommand command) {
        this.dashboardService = checkNotNull(dashboardService);
        this.helper = new DashboardSerializationHelper(objectTranslationService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all dashboards",
            description = "Get all dashboards",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of dashboards"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public Object getAll(
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "If true includes full details in the response") Boolean detailed,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0")  Integer offset
    ) {
        List<DashboardData> listDashboardData = command.doGetAll(dashboardService::getAll);
        return response(helper.applySerializationToListDashboardData(listDashboardData, detailed, limit, offset));
    }

    @GET
    @Path("{id}/")
    @Operation(
            summary = "Get a specific dashboard by ID or code",
            description = "Obtain details of a specific dashboard",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of dashboard data"),
                    @ApiResponse(responseCode = "404", description = "The dashboard was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public Object readOne(
            @PathParam(ID) @Parameter(name = ID, description = "Id or code of the dashboard", schema = @Schema(type = "string")) String idOrCode
    ) {
        DashboardData dashboardData = command.doReadOne(idOrCode, dashboardService::getByIdOrCode);
        return response(helper.serializeDetailedDashboardData(dashboardData));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new dashboard",
            description = "Create a new dashboard",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of dashboard"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "Data for the new dashboard", required = true) WsDashboardData data
    ) {
        DashboardData dashboard = command.doCreate(data);
        return response(helper.serializeDetailedDashboardData(dashboard));
    }

    @PUT
    @Path("{id}/")
    @Operation(
            summary = "Update an existing dashboard",
            description = "Update an existing dashboard",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of dashboard"),
                    @ApiResponse(responseCode = "404", description = "The dashboard was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(ID) @Parameter(name = ID, description = "Id of the dashboard", schema = @Schema(type = "long")) Long id,
            @RequestBody(description = "Updated dashboard data", required = true) WsDashboardData data
    ) {
        DashboardData dashboard = command.doUpdate(id, data);
        return response(helper.serializeDetailedDashboardData(dashboard));
    }

    @DELETE
    @Path("{id}/")
    @Operation(
            summary = "Delete a dashboard",
            description = "Delete a dashboard",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of dashboard"),
                    @ApiResponse(responseCode = "404", description = "The dashboard was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("id") @Parameter(name = ID, description = "Id of the dashboard", schema = @Schema(type = "long")) Long id
    ) {
        command.doDelete(id);
        return success();
    }
}
