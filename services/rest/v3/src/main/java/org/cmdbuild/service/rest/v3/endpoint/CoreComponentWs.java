package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.corecomponents.CoreComponentType;
import org.cmdbuild.service.rest.v4.endpoint.CoreComponentWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.CoreComponentWs_Management;
import org.cmdbuild.service.rest.v4.model.WsCoreComponentData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CORECOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CORECOMPONENTS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

@Path("components/core/{type}/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Tag(name = "Core Component", description = "Operations related to core components")
public class CoreComponentWs {

    private final CoreComponentWs_Administration coreComponentWs_adm;
    private final CoreComponentWs_Management coreComponentWs_mng;

    public CoreComponentWs(CoreComponentWs_Administration coreComponentWs_adm, CoreComponentWs_Management coreComponentWs_mng) {
        this.coreComponentWs_adm = checkNotNull(coreComponentWs_adm);
        this.coreComponentWs_mng = checkNotNull(coreComponentWs_mng);
    }

    @GET
    @Path(EMPTY)
    @RolesAllowed(ADMIN_CORECOMPONENTS_VIEW_AUTHORITY)
    @Operation(
            summary = "List core components of a specific type",
            description = "List core components of a specific type. If the user has admin view permissions, all core components of the specified type will be returned. Otherwise, only core components for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'. If the 'detailed' query parameter is set to true, detailed information about core components will be included in the response. Otherwise, only basic information will be included.",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "Type of core component to list", required = true, schema = @Schema(allowableValues = {"datatypes", "measurementunits", "classifications"})),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information about core components in the response")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view core components of the specified type")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object listByType(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("type") CoreComponentType type,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed
    ) {
        if (isAdminViewMode(viewMode)) {
            return coreComponentWs_adm.listByType(type, detailed);
        }
        throw runtime("Operation blocked. You don't have the permissions");
    }

    @GET
    @Path("{code}")
    @Operation(
            summary = "Get a core component by code",
            description = "Get a core component by code. If the user has admin view permissions, the core component will be returned with all details. Otherwise, only details for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "code", in = ParameterIn.PATH, description = "Code of the core component to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the core component with the specified code")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object get(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("code") String code
    ) {
        if (isAdminViewMode(viewMode)) {
            return coreComponentWs_adm.get(code);
        }
        return coreComponentWs_mng.get(code);
    }

    @DELETE
    @Path("{code}")
    @RolesAllowed(ADMIN_CORECOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete a core component by code",
            description = "Delete a core component by code. Only users with admin modify permissions can perform this operation",
            parameters = {
                    @Parameter(name = "code", in = ParameterIn.PATH, description = "Code of the core component to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete the core component with the specified code"),
                    @ApiResponse(responseCode = "404", description = "Core component with the specified code not found")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(@PathParam("code") String code) {
        return coreComponentWs_adm.delete(code);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_CORECOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new core component",
            description = "Create a new core component with the provided data. Only users with admin modify permissions can perform this operation",
            requestBody = @RequestBody(description = "Data for the new core component", required = true),
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "Type of core component to create", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create a core component of the specified type"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam("type") CoreComponentType type,
            WsCoreComponentData data
    ) {
        return coreComponentWs_adm.create(type, data);
    }

    @PUT
    @Path("{code}")
    @RolesAllowed(ADMIN_CORECOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update an existing core component",
            description = "Update an existing core component with the provided data. Only users with admin modify permissions can perform this operation",
            parameters = {
                    @Parameter(name = "code", in = ParameterIn.PATH, description = "Code of the core component to update", required = true),
            },
            requestBody = @RequestBody(description = "Updated data for the core component", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update a core component of the specified type"),
                    @ApiResponse(responseCode = "404", description = "Core component with the specified code not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("code") String code,
            WsCoreComponentData data
    ) {
        return coreComponentWs_adm.update(code, data);
    }
}
