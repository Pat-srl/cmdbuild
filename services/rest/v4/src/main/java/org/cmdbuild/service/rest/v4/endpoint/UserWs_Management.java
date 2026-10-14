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
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.command.UserWsCommand;
import org.cmdbuild.service.rest.v4.model.WsPasswordRecoveryData;
import org.cmdbuild.service.rest.v4.model.WsUserPswData;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;

/**
 * @author ldare
 */
@Path("users/")
@Tag(name = "Users", description = "Users management")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class UserWs_Management {

    private final UserWsCommand command;

    public UserWs_Management(UserWsCommand command) {
        this.command = checkNotNull(command);
    }

    @PUT
    @Path("current/password")
    @Operation(
            summary = "Change password for current user",
            description = "Change password for the currently logged-in user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object changePasswordForCurrentUser(
            @RequestBody(description = "Password data", required = true) WsUserPswData data
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
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object changePassword(
            @PathParam("username") @Parameter(description = "Username of the user to change password", required = true) String username,
            @RequestBody(description = "Password data", required = true) WsUserPswData data
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
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object requirePasswordRecovery(
            @PathParam("username") @Parameter(description = "Username of the user to recover password", required = true) String username,
            @RequestBody(description = "Password recovery data", required = true) WsPasswordRecoveryData data
    ) {
        command.doRequirePasswordRecovery(username, data);
        return success();
    }
}
