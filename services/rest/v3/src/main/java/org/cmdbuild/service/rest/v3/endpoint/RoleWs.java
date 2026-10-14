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
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_ROLES_VIEW;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ROLES_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ROLES_VIEW_AUTHORITY;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import org.cmdbuild.service.rest.v4.endpoint.RoleWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.RoleWs_Management;
import org.cmdbuild.service.rest.v4.model.WsRoleUsers;

@Path("roles/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Tag(name = "Roles", description = "Operations related to roles")
public class RoleWs {

    private final RoleWs_Administration roleWs_adm;
    private final RoleWs_Management roleWs_mng;
    private final OperationUserSupplier operationUser;

    public RoleWs(RoleWs_Administration roleWs_adm, RoleWs_Management roleWs_mng, OperationUserSupplier operationUser) {
        this.roleWs_adm = checkNotNull(roleWs_adm);
        this.roleWs_mng = checkNotNull(roleWs_mng);
        this.operationUser = checkNotNull(operationUser);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all roles",
            description = "Get all roles. If the user has admin view permissions, all roles will be returned. Otherwise, only roles for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information about roles, such as the users assigned to the role"),
                @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of results to return"),
                @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results")
            },
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view roles"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) boolean detailed
    ) {
        if (operationUser.hasPrivileges(p -> p.hasPrivileges(RP_ADMIN_ROLES_VIEW))) {
            return roleWs_adm.readMany(limit, offset, detailed);
        }
        return roleWs_mng.readMany(limit, offset);
    }

    @GET
    @Path("{roleId}/")
    @Operation(
            summary = "Get a role by ID",
            description = "Get a role by ID. The role will be returned if the user has either admin view permissions or management permissions for the role.",
            parameters = {
                @Parameter(name = ROLE_ID, in = ParameterIn.PATH, description = "ID of the role to retrieve", required = true)
            },
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of role data"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the role or the role does not exist"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam(ROLE_ID) String roleId
    ) {
        if (operationUser.hasPrivileges(p -> p.hasPrivileges(RP_ADMIN_ROLES_VIEW))) {
            return roleWs_adm.readOne(roleId);
        }
        return roleWs_mng.readOne(roleId);
    }

    @GET
    @Path("{roleId}/users")
    @RolesAllowed(ADMIN_ROLES_VIEW_AUTHORITY)
    @Operation(
            summary = "Get users assigned to a role",
            description = "Get users assigned to a role. By default, only users assigned to the role will be returned, but if the 'assigned' query parameter is set to false, users that are not assigned to the role will be returned instead. The results can be filtered by username or full name using the 'filter' query parameter, and can be sorted by username or full name using the 'sort' query parameter. Pagination of results can be achieved using the 'limit' and 'start' query parameters.",
            parameters = {
                @Parameter(name = ROLE_ID, in = ParameterIn.PATH, description = "ID of the role to retrieve users for", required = true),
                @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter string to filter users by username or full name. The filter will be applied to both username and full name, and users that match either will be returned"),
                @Parameter(name = SORT, in = ParameterIn.QUERY, description = "Field to sort results by. Can be 'username' or 'fullName'"),
                @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of results to return"),
                @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results"),
                @Parameter(name = "assigned", in = ParameterIn.QUERY, description = "Whether to return only users assigned to the role or not assigned to the role. If not set, both assigned and not assigned users will be returned")
            },
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of users assigned to the role"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the role or the role does not exist"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readRoleUsers(
            @PathParam(ROLE_ID) String roleId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam("assigned") Boolean assigned
    ) {
        return roleWs_adm.readRoleUsers(roleId, filterStr, sort, limit, offset, assigned);
    }

    @GET
    @Path("admin/dependencies")
    @RolesAllowed(ADMIN_ROLES_VIEW_AUTHORITY)
    @Operation(
            summary = "Get dependencies for roles",
            description = "Get dependencies for roles. This endpoint returns information about dependencies that would prevent deletion of roles, such as the number of users assigned to each role. This endpoint is intended to be used in the administration interface when viewing the list of roles, to show information about dependencies for each role without having to make a separate request for each role. The response is a map where the keys are role IDs and the values are objects containing information about dependencies for that role, such as the number of users assigned to the role.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of role dependencies data"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view roles"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getAdminDependencies() {
        return roleWs_adm.getAdminDependencies();
    }

    @Deprecated//TODO move to POST
    @PUT
    @Path("{roleId}/users")
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update users assigned to a role",
            description = "Update users assigned to a role. This endpoint replaces the users assigned to the role with the users specified in the request body. The request body should contain a JSON object with a single property 'users', which is an array of user IDs to assign to the role. For example, { \"users\": [\"user1\", \"user2\", \"user3\"] } would assign the users with IDs 'user1', 'user2', and 'user3' to the role, and unassign any other users that were previously assigned to the role but are not included in the request body. This endpoint is intended to be used in the administration interface when editing a role, to update the users assigned to the role. It is marked as deprecated because using PUT for this operation is not ideal, as it can be confused with updating the role itself. It would be better to use POST for this operation, or to have a separate endpoint such as /roles/{roleId}/users/update to make it clear that this endpoint is for updating the users assigned to the role and not for updating the role itself.",
            parameters = {
                @Parameter(name = ROLE_ID, in = ParameterIn.PATH, description = "ID of the role to update users for", required = true)
            },
            requestBody = @RequestBody(description = "Data to update the users assigned to the role with", required = true, content = @Content(schema = @Schema(implementation = WsRoleUsers.class))),
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "400", description = "Invalid request body"),
                @ApiResponse(responseCode = "401", description = "User not authenticated"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to modify the role or the role does not exist"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object updateUsersPut(
            @PathParam(ROLE_ID) String roleId,
            WsRoleUsers users
    ) {
        return updateUsersPost(roleId, users);
    }

    @POST
    @Path("{roleId}/users")
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update users assigned to a role",
            description = "Update users assigned to a role. This endpoint replaces the users assigned to the role with the users specified in the request body. The request body should contain a JSON object with a single property 'users', which is an array of user IDs to assign to the role. For example, { \"users\": [\"user1\", \"user2\", \"user3\"] } would assign the users with IDs 'user1', 'user2', and 'user3' to the role, and unassign any other users that were previously assigned to the role but are not included in the request body. This endpoint is intended to be used in the administration interface when editing a role, to update the users assigned to the role. It is marked as a POST endpoint because using PUT for this operation is not ideal, as it can be confused with updating the role itself. Using POST or having a separate endpoint such as /roles/{roleId}/users/update would be better to make it clear that this endpoint is for updating the users assigned to the role and not for updating the role itself.",
            parameters = {
                @Parameter(name = ROLE_ID, in = ParameterIn.PATH, description = "ID of the role to update users for", required = true)
            },
            requestBody = @RequestBody(description = "Data to update the users assigned to the role with", required = true, content = @Content(schema = @Schema(implementation = WsRoleUsers.class))),
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "400", description = "Invalid request body"),
                @ApiResponse(responseCode = "401", description = "User not authenticated"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to modify the role or the role does not exist"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object updateUsersPost(
            @PathParam(ROLE_ID) String roleId,
            WsRoleUsers users
    ) {
        return roleWs_adm.updateUsers(roleId, users);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new role",
            description = "Create a new role. The request body should contain a JSON object with the data for the new role. The exact structure of the request body will depend on the implementation of the role creation logic, but it should include at least a name for the new role. This endpoint is intended to be used in the administration interface when creating a new role. It is marked as a POST endpoint because it creates a new resource (a role) on the server. The response will typically include the data for the newly created role, including its ID, which can be used for further operations on the role such as updating or deleting it.",
            requestBody = @RequestBody(description = "Data for the new role", required = true, content = @Content(schema = @Schema(implementation = String.class))),
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "400", description = "Invalid request body"),
                @ApiResponse(responseCode = "401", description = "User not authenticated"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create roles"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            String jsonData
    ) {
        return roleWs_adm.create(jsonData);
    }

    @PUT
    @Path("{roleId}/")
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update a role",
            description = "Update a role. The request body should contain a JSON object with the data to update the role with. The exact structure of the request body will depend on the implementation of the role update logic, but it can include properties such as the name of the role or other attributes that can be updated for a role. This endpoint is intended to be used in the administration interface when editing a role, to update the role's data. It is marked as a PUT endpoint because it updates an existing resource (a role) on the server. The response will typically include the data for the updated role after the update has been applied. If the role with the specified ID does not exist, a 403 Forbidden response will be returned to avoid leaking information about the existence of the role. If the user does not have permissions to modify the role, a 403 Forbidden response will also be returned. If the request body is invalid, a 400 Bad Request response will be returned. If the update is successful, a 200 OK response will be returned with the updated role data.",
            parameters = {
                @Parameter(name = ROLE_ID, in = ParameterIn.PATH, description = "ID of the role to update", required = true)
            },
            requestBody = @RequestBody(description = "Data to update the role with", required = true, content = @Content(schema = @Schema(implementation = String.class))),
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "400", description = "Invalid request body"),
                @ApiResponse(responseCode = "401", description = "User not authenticated"),
                @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to modify the role or the role does not exist"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(ROLE_ID) String roleId,
            String jsonData
    ) {
        return roleWs_adm.update(roleId, jsonData);
    }
}
