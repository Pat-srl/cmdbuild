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
import org.cmdbuild.service.rest.v4.model.WsDefaultStoredFilterForClass;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_SEARCHFILTERS_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ROLE_ID;

@Path("roles/{roleId}/filters")
@Tag(name = "Search Filters", description = "Operations related to search filters")
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_SEARCHFILTERS_VIEW_AUTHORITY)
public class RoleClassFilterWs {

    private final org.cmdbuild.service.rest.v4.endpoint.RoleClassFilterWs roleClassFilterWs;

    public RoleClassFilterWs(org.cmdbuild.service.rest.v4.endpoint.RoleClassFilterWs roleClassFilterWs) {
        this.roleClassFilterWs = checkNotNull(roleClassFilterWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get default filters for a role",
            description = "Get default filters for a role",
            parameters = {
                    @Parameter(name = ROLE_ID, in = ParameterIn.PATH, description = "ID of the role to retrieve default filters for", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of default filters data"),
                    @ApiResponse(responseCode = "404", description = "Role not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(ROLE_ID) String roleId
    ) {
        return roleClassFilterWs.read(roleId);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Update default filters for a role",
            description = "Update default filters for a role",
            parameters = {
                    @Parameter(name = ROLE_ID, in = ParameterIn.PATH, description = "ID of the role to update default filters for", required = true)
            },
            requestBody = @RequestBody(description = "Default filters to update", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of default filters data"),
                    @ApiResponse(responseCode = "404", description = "Role not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object updateWithPost(
            @PathParam(ROLE_ID) String roleId,
            List<WsDefaultStoredFilterForClass> filters
    ) {
        return roleClassFilterWs.updateWithPost(roleId, filters);
    }
}
