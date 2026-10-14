/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
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
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.UserWsCommand;
import org.cmdbuild.service.rest.v4.model.WsPasswordRecoveryData;
import org.cmdbuild.service.rest.v4.model.WsUserData;
import org.cmdbuild.service.rest.v4.model.WsUserPswData;
import org.cmdbuild.service.rest.v4.serializationhelpers.UserSerializationHelper;
import org.springframework.stereotype.Component;

import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_USERS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_USERS_VIEW_AUTHORITY;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.v4.utils.PositionOfUtils.handlePositionOfAndGetMeta;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Path("administration/users/")
@Tag(name = "Users", description = "Users management")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class UserWs_Administration {

    private final static Map<String, String> USER_TABLE_ATTR_NAME_MAPPING = Map.of(
            "username", "Username",
            "description", ATTR_DESCRIPTION,
            "email", "Email",
            "active", "Active"
    );

    private final UserWsCommand command;
    private final UserSerializationHelper userSerializationHelper;

    public UserWs_Administration(UserWsCommand command, UserSerializationHelper userSerializationHelper) {
        this.command = checkNotNull(command);
        this.userSerializationHelper = checkNotNull(userSerializationHelper);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all users",
            description = "Get all users with pagination and filtering options",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_USERS_VIEW_AUTHORITY)
    public Object readMany(
            @BeanParam WsQueryOptions query
    ) {
        DaoQueryOptions queryOptions = query.getQuery().mapAttrNames(USER_TABLE_ATTR_NAME_MAPPING);
        PagedElements<UserData> users = command.doReadMany(queryOptions);
        return response(users.map(query.isDetailed() ? userSerializationHelper::serializeFastDetailedUser : userSerializationHelper::serializeUser), handlePositionOfAndGetMeta(queryOptions, users));
    }

    @GET
    @Path("{userId}/")
    @Operation(
            summary = "Get user by ID",
            description = "Get detailed information about a user by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User details"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_USERS_VIEW_AUTHORITY)
    public Object readOne(
            @PathParam("userId") @Parameter(description = "Id of the user") Long id
    ) {
        UserData user = command.doReadOne(id);
        return response(userSerializationHelper.serializeDetailedUser(user));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new user",
            description = "Create a new user with the provided details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Created user details"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "409", description = "User already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_USERS_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "User data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsUserData.class))) WsUserData data
    ) {
        UserData user = command.doCreate(data);
        return response(userSerializationHelper.serializeDetailedUser(user));
    }

    @PUT
    @Path("{userId}/")
    @Operation(
            summary = "Update an existing user",
            description = "Update an existing user with the provided details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated user details"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_USERS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("userId") @Parameter(description = "Id of the user") Long id,
            @RequestBody(description = "User data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsUserData.class))) WsUserData data
    ) {
        UserData user = command.doUpdate(id, data);
        return response(userSerializationHelper.serializeDetailedUser(user));
    }

    @PUT
    @Path("current/password")
    @Operation(
            summary = "Change password for current user",
            description = "Change password for the currently logged-in user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_USERS_MODIFY_AUTHORITY)
    public Object changePasswordForCurrentUser(
            @RequestBody(description = "Password data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsUserPswData.class))) WsUserPswData data
    ) {
        command.doChangePasswordForCurrentUser(data);
        return success();
    }

    @PUT
    @Path("{username}/password")
    @Operation(
            summary = "Change password for specified user",
            description = "Change password for the specified user by username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_USERS_MODIFY_AUTHORITY)
    public Object changePassword(
            @PathParam("username") @Parameter(description = "Username of the user") String username,
            @RequestBody(description = "Password data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsUserPswData.class))) WsUserPswData data
    ) {
        command.doChangePassword(username, data);
        return success();
    }

    @POST
    @Path("{username}/password/recovery")
    @Operation(
            summary = "Require password recovery for specified user",
            description = "Initiate password recovery process for the specified user by username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password recovery initiated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_USERS_VIEW_AUTHORITY)
    public Object requirePasswordRecovery(
            @PathParam("username") @Parameter(description = "Username of the user") String username,
            @RequestBody(description = "Password recovery data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsPasswordRecoveryData.class))) WsPasswordRecoveryData data
    ) {
        command.doRequirePasswordRecovery(username, data);
        return success();
    }

}
