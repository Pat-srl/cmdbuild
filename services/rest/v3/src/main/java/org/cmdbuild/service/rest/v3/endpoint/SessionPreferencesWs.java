package org.cmdbuild.service.rest.v3.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ID;

@Path("sessions/{" + ID + "}/preferences")//it is actually user preferences
@Tag(name = "Preferences", description = "Operations related to user preferences")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class SessionPreferencesWs {

    private final org.cmdbuild.service.rest.v4.endpoint.SessionPreferencesWs sessionPreferencesWs;

    public SessionPreferencesWs(org.cmdbuild.service.rest.v4.endpoint.SessionPreferencesWs sessionPreferencesWs) {
        this.sessionPreferencesWs = checkNotNull(sessionPreferencesWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get user preferences",
            description = "Get user preferences",
            parameters = {
                    @Parameter(name = ID, in = ParameterIn.PATH, description = "Session ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of user preferences"),
                    @ApiResponse(responseCode = "404", description = "Session not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(ID) String sessionId
    ) {
        return sessionPreferencesWs.read(sessionId);
    }

    @GET
    @Path("{key}")
    @Operation(
            summary = "Get user preference value",
            description = "Get user preference value",
            parameters = {
                    @Parameter(name = ID, in = ParameterIn.PATH, description = "Session ID", required = true),
                    @Parameter(name = "key", in = ParameterIn.PATH, description = "Preference key", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of user preference value"),
                    @ApiResponse(responseCode = "404", description = "Preference not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(TEXT_PLAIN)
    public String getUserConfig(
            @PathParam(ID) String sessionId,
            @PathParam("key") String key
    ) {
        return sessionPreferencesWs.getUserConfig(sessionId, key);
    }

    @PUT
    @Path("{key}")
    @Operation(
            summary = "Update user preference value",
            description = "Update user preference value",
            parameters = {
                    @Parameter(name = ID, in = ParameterIn.PATH, description = "Session ID", required = true),
                    @Parameter(name = "key", in = ParameterIn.PATH, description = "Preference key", required = true),
                    @Parameter(name = "value", in = ParameterIn.QUERY, description = "Preference value", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of user preference value"),
                    @ApiResponse(responseCode = "404", description = "Preference not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(TEXT_PLAIN)
    public Object updateUserConfigValue(
            @PathParam(ID) String sessionId,
            @PathParam("key") String key,
            String value
    ) {
        return sessionPreferencesWs.updateUserConfigValue(sessionId, key, value);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Update user preferences",
            description = "Update user preferences",
            parameters = {
                    @Parameter(name = ID, in = ParameterIn.PATH, description = "Session ID", required = true),
            },
            requestBody = @RequestBody(description = "Map of preference keys and values to update", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of user preferences"),
                    @ApiResponse(responseCode = "404", description = "Session not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object updateUserConfigValues(
            @PathParam(ID) String sessionId,
            Map<String, String> data
    ) {
        return sessionPreferencesWs.updateUserConfigValues(sessionId, data);
    }

    @DELETE
    @Path("{key}")
    @Operation(
            summary = "Delete user preference value",
            description = "Delete user preference value",
            parameters = {
                    @Parameter(name = ID, in = ParameterIn.PATH, description = "Session ID", required = true),
                    @Parameter(name = "key", in = ParameterIn.PATH, description = "Preference key", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of user preference value"),
                    @ApiResponse(responseCode = "404", description = "Preference not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deleteSystemConfigValue(
            @PathParam(ID) String sessionId,
            @PathParam("key") String key
    ) {
        return sessionPreferencesWs.deleteSystemConfigValue(sessionId, key);
    }
}
