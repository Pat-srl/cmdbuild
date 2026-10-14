package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.endpoint.UserWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.UserWs_Management;
import org.cmdbuild.service.rest.v4.model.WsPasswordRecoveryData;
import org.cmdbuild.service.rest.v4.model.WsUserData;
import org.cmdbuild.service.rest.v4.model.WsUserPswData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_USERS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_USERS_VIEW_AUTHORITY;

@Path("users/")
@Tag(name = "Users", description = "Users management")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class UserWs {

    private final UserWs_Administration userWs_adm;
    private final UserWs_Management userWs_mng;


    public UserWs(UserWs_Administration userWs_adm, UserWs_Management userWs_mng) {
        this.userWs_adm = checkNotNull(userWs_adm);
        this.userWs_mng = checkNotNull(userWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all users",
            description = "Get all users with pagination and filtering options",
            requestBody = @RequestBody(description = "Query options for filtering and pagination"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users"),
                    @ApiResponse(responseCode = "400", description = "Invalid query options"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_USERS_VIEW_AUTHORITY)
    public Object readMany(
            WsQueryOptions query
    ) {
        return userWs_adm.readMany(query);
    }

    @GET
    @Path("{userId}/")
    @Operation(
            summary = "Get user by ID",
            description = "Get detailed information about a user by their ID",
            parameters = {
                    @Parameter(name = "userId", description = "ID of the user to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "User details"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_USERS_VIEW_AUTHORITY)
    public Object readOne(
            @PathParam("userId") Long id
    ) {
        return userWs_adm.readOne(id);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new user",
            description = "Create a new user with the provided details",
            requestBody = @RequestBody(description = "User data to create", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Created user details"),
                    @ApiResponse(responseCode = "400", description = "Invalid user data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_USERS_MODIFY_AUTHORITY)
    public Object create(
            WsUserData data
    ) {
        return userWs_adm.create(data);
    }

    @PUT
    @Path("{userId}/")
    @Operation(
            summary = "Update an existing user",
            description = "Update an existing user with the provided details",
            parameters = {@Parameter(name = "userId", description = "ID of the user to update", required = true)},
            requestBody = @RequestBody(description = "User data to update", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated user details"),
                    @ApiResponse(responseCode = "400", description = "Invalid user data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_USERS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("userId") Long id,
            WsUserData data
    ) {
        return userWs_adm.update(id, data);
    }

    @PUT
    @Path("current/password")
    @Operation(
            summary = "Change password for current user",
            description = "Change password for the currently logged-in user",
            requestBody = @RequestBody(description = "New password data", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid password data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object changePasswordForCurrentUser(
            WsUserPswData data
    ) {
        return userWs_mng.changePasswordForCurrentUser(data);
    }

    @PUT
    @Path("{username}/password")
    @Operation(
            summary = "Change password for specified user",
            description = "Change password for the specified user by username",
            parameters = {@Parameter(name = "username", description = "Username of the user to change password", required = true)},
            requestBody = @RequestBody(description = "New password data", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid password data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object changePassword(
            @PathParam("username") String username,
            WsUserPswData data
    ) {
        return userWs_mng.changePassword(username, data);
    }

    @POST
    @Path("{username}/password/recovery")
    @Operation(
            summary = "Require password recovery for specified user",
            description = "Initiate password recovery process for the specified user by username",
            parameters = {@Parameter(name = "username", description = "Username of the user to initiate password recovery", required = true)},
            requestBody = @RequestBody(description = "Recovery data", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password recovery initiated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid recovery data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object requirePasswordRecovery(
            @PathParam("username") String username,
            WsPasswordRecoveryData data
    ) {
        return userWs_mng.requirePasswordRecovery(username, data);
    }
}
