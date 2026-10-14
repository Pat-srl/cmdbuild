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
import org.cmdbuild.auth.user.OperationUserStack;
import org.cmdbuild.service.rest.common.serializationhelpers.UserConfigSerializationHelper;
import org.cmdbuild.service.rest.v4.command.SessionPreferencesWsCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ID;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;

@Path("sessions/{" + ID + "}/preferences")//it is actually user preferences
@Tag(name = "Preferences", description = "Operations related to user preferences")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class SessionPreferencesWs {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserConfigSerializationHelper userConfigSerializationHelper;
    private final SessionPreferencesWsCommand command;

    public SessionPreferencesWs(UserConfigSerializationHelper userConfigSerializationHelper, SessionPreferencesWsCommand command) {
        this.command = checkNotNull(command);
        this.userConfigSerializationHelper = checkNotNull(userConfigSerializationHelper);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get user preferences",
            description = "Get user preferences",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of user preferences"),
                    @ApiResponse(responseCode = "404", description = "User preferences not found"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested user preferences"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(ID) @Parameter(description = "Id of session", required = true) String sessionId
    ) {
        logger.debug("read preferences for session = {}", sessionId);
        OperationUserStack operationUserStack = command.doGetOperationUserStack(sessionId);
        Map<String, Object> data = userConfigSerializationHelper.getUserConfig(operationUserStack);
        return response(data);
    }

    @GET
    @Path("{key}")
    @Operation(
            summary = "Get user preference value",
            description = "Get user preference value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of user preference value"),
                    @ApiResponse(responseCode = "404", description = "User preference not found"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested user preference"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(TEXT_PLAIN)
    public String getUserConfig(
            @PathParam(ID) @Parameter(description = "Id of session", required = true) String sessionId,
            @PathParam("key") @Parameter(description = "Key of preference", required = true) String key
    ) {
        OperationUserStack operationUserStack = command.doGetOperationUserStack(sessionId);
        return toStringOrEmpty(userConfigSerializationHelper.getUserConfig(operationUserStack).get(key));
    }

    @PUT
    @Path("{key}")
    @Operation(
            summary = "Update user preference value",
            description = "Update user preference value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of user preference value"),
                    @ApiResponse(responseCode = "404", description = "User preference not found"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested user preference"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(TEXT_PLAIN)
    public Object updateUserConfigValue(
            @PathParam(ID) @Parameter(description = "Id of session", required = true) String sessionId,
            @PathParam("key") @Parameter(description = "Key of preference", required = true) String key,
            @RequestBody(description = "Value of the preference (plain text)", required = true) String value
    ) {
        command.doUpdateUserConfigValue(sessionId, key, value);
        return success();
    }

    @POST
    @Path("")
    @Operation(
            summary = "Update user preferences",
            description = "Update user preferences",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of user preferences"),
                    @ApiResponse(responseCode = "404", description = "User preferences not found"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested user preferences"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object updateUserConfigValues(
            @PathParam(ID) @Parameter(description = "Id of session", required = true) String sessionId,
            @RequestBody(description = "Map of preference key to value", required = true) Map<String, String> data
    ) {
        logger.info("update user preferences with data = {}", data);
        OperationUserStack operationUserStack = command.doUpdateUserConfigValues(sessionId, data);
        return response(userConfigSerializationHelper.getUserConfig(operationUserStack));
    }

    @DELETE
    @Path("{key}")
    @Operation(
            summary = "Delete user preference value",
            description = "Delete user preference value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of user preference value"),
                    @ApiResponse(responseCode = "404", description = "User preference not found"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested user preference"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deleteSystemConfigValue(
            @PathParam(ID) @Parameter(description = "Id of session", required = true) String sessionId,
            @PathParam("key") @Parameter(description = "Key of preference", required = true) String key
    ) {
        command.doDeleteSystemConfigValue(sessionId, key);
        return success();
    }
}
