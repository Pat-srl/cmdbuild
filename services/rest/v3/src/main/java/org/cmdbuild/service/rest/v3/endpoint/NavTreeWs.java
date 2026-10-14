package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_NAVTREES_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import org.cmdbuild.service.rest.v4.endpoint.NavTreeWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.NavTreeWs_Management;
import org.cmdbuild.service.rest.v4.model.WsTreeData;

@Path("domainTrees")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Tag(name = "Navigation Tree", description = "Navigation tree management")
public class NavTreeWs {

    private final NavTreeWs_Administration navTreeWs_adm;
    private final NavTreeWs_Management navTreeWs_mng;

    public NavTreeWs(NavTreeWs_Administration navTreeWs_adm, NavTreeWs_Management navTreeWs_mng) {
        this.navTreeWs_adm = checkNotNull(navTreeWs_adm);
        this.navTreeWs_mng = checkNotNull(navTreeWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all navigation trees",
            description = "Get all navigation trees. If the user has admin view permissions, all navigation trees will be returned. Otherwise, only navigation trees for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'. Additionally, the results can be filtered by name using the 'filter' query parameter, and paginated using the 'limit' and 'start' query parameters.",
            parameters = {
                @Parameter(name = VIEW_MODE_HEADER_PARAM, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of results to return"),
                @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results")
            },
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view navigation trees"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset
    ) {
        if (isAdminViewMode(viewMode)) {
            return navTreeWs_adm.readAll(null, filterStr, limit, offset, false);
        }
        return navTreeWs_mng.readAll(null, filterStr, limit, offset, false);
    }

    @GET
    @Path("{treeId}/")
    @Operation(
            summary = "Get a navigation tree",
            description = "Get a navigation tree by id. The treeMode query parameter can be used to specify whether to return the tree in a flat format (where all nodes are returned in a single list) or in a hierarchical format (where child nodes are nested within their parent nodes). If the user has admin view permissions, the navigation tree will be returned regardless of its permissions. Otherwise, the navigation tree will only be returned if the user has management permissions for it.",
            parameters = {
                @Parameter(name = "treeId", description = "Id of the navigation tree to query"),
                @Parameter(name = "treeMode", in = ParameterIn.QUERY, description = "Format to return the tree in. Can be 'flat' or 'hierarchical'", schema = @Schema(allowableValues = {"flat", "hierarchical"}))
            },
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the navigation tree"),
                @ApiResponse(responseCode = "404", description = "Navigation tree not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("treeId") String id,
            @QueryParam("treeMode") @DefaultValue("flat") String treeMode
    ) {
        return navTreeWs_mng.read(id, treeMode);
    }

    @POST
    @Path("")
    @RolesAllowed(ADMIN_NAVTREES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a navigation tree",
            description = "Create a navigation tree. The request body should contain the data for the new navigation tree. The user must have admin permissions to create a navigation tree.",
            requestBody = @RequestBody(description = "Data for the new navigation tree", required = true, content = @Content(schema = @Schema(implementation = WsTreeData.class))),
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "400", description = "Bad request - the request body is invalid"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create a navigation tree"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            WsTreeData data
    ) {
        return navTreeWs_adm.create(data);
    }

    @PUT
    @Path("{treeId}")
    @RolesAllowed(ADMIN_NAVTREES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update a navigation tree",
            description = "Update a navigation tree. The request body should contain the updated data for the navigation tree. The user must have admin permissions to update a navigation tree.",
            parameters = {
                @Parameter(name = "treeId", description = "Id of the navigation tree to update")},
            requestBody = @RequestBody(description = "Updated data for the navigation tree", required = true, content = @Content(schema = @Schema(implementation = WsTreeData.class))),
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "400", description = "Bad request - the request body is invalid"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update the navigation tree"),
                @ApiResponse(responseCode = "404", description = "Navigation tree not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("treeId") String id,
            WsTreeData data
    ) {
        return navTreeWs_adm.update(id, data);
    }

    @DELETE
    @Path("{treeId}")
    @RolesAllowed(ADMIN_NAVTREES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete a navigation tree",
            description = "Delete a navigation tree by id. The user must have admin permissions to delete a navigation tree.",
            parameters = {
                @Parameter(name = "treeId", description = "Id of the navigation tree to delete")},
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete the navigation tree"),
                @ApiResponse(responseCode = "404", description = "Navigation tree not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("treeId") String id
    ) {
        return navTreeWs_adm.delete(id);
    }

    @POST
    @Path("{treeId}/fixDirections")
    @RolesAllowed(ADMIN_NAVTREES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Fix navigation tree directions",
            description = "Fix navigation tree directions. This operation will check the directions of all nodes in the navigation tree and correct any inconsistencies. The user must have admin permissions to perform this operation.",
            parameters = {
                @Parameter(name = "treeId", description = "Id of the navigation tree to fix")},
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to fix navigation tree directions"),
                @ApiResponse(responseCode = "404", description = "Navigation tree not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object fixNavtreeDirections(@PathParam("treeId") String id) {
        return navTreeWs_adm.fixNavtreeDirections(id);
    }
}
