package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import org.cmdbuild.service.rest.v4.model.WsSessionData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("sessions/")
@Tag(name = "Sessions", description = "Sessions")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class SessionsWs {

    private final org.cmdbuild.service.rest.v4.endpoint.SessionsWs sessionsWs;

    public SessionsWs(org.cmdbuild.service.rest.v4.endpoint.SessionsWs sessionsWs) {
        this.sessionsWs = checkNotNull(sessionsWs);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a session",
            description = "Create a session",
            requestBody = @RequestBody(description = "Session data", required = true),
            parameters = {
                    @Parameter(name = EXT, in = ParameterIn.QUERY, description = "Whether to include extended data in the response", required = false),
                    @Parameter(name = "scope", in = ParameterIn.QUERY, description = "Comma-separated list of scopes to include in the session", required = false),
                    @Parameter(name = "returnId", in = ParameterIn.QUERY, description = "Whether to return only the session ID in the response", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "409", description = "Conflict"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    public Object create(
            @Context HttpServletRequest request,
            WsSessionData sessionData,
            @QueryParam(EXT) @Nullable Boolean includeExtendedData,
            @QueryParam("scope") String scopeStr,
            @QueryParam("returnId") Boolean returnId
    ) {
        return sessionsWs.create(request, sessionData, includeExtendedData, scopeStr, returnId);
    }

    @GET
    @Path("{" + ID + "}/")
    @Operation(
            summary = "Read session",
            description = "Read session",
            parameters = {
                    @Parameter(name = ID, in = ParameterIn.PATH, description = "Session ID", required = true),
                    @Parameter(name = EXT, in = ParameterIn.QUERY, description = "Whether to include extended data in the response", required = false),
                    @Parameter(name = "if_exists", in = ParameterIn.QUERY, description = "Whether to return a 404 if the session does not exist", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam(ID) String sessionId,
            @QueryParam(EXT) Boolean includeExtendedData,
            @QueryParam("if_exists") @DefaultValue(FALSE) Boolean checkIfExists
    ) {
        return sessionsWs.readOne(sessionId, includeExtendedData, checkIfExists, false);
    }

    @GET
    @Path("{sessionId}/privileges")
    @Operation(
            summary = "Read privileges for session",
            description = "Read privileges for session",
            parameters = {
                    @Parameter(name = SESSION_ID, in = ParameterIn.PATH, description = "Session ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    public Object readPrivileges(
            @PathParam(SESSION_ID) String sessionId
    ) {
        return sessionsWs.readPrivileges(sessionId);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all sessions",
            description = "Get all sessions",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object readAll() {
        return sessionsWs.readAll();
    }

    @PUT
    @Path("{" + ID + "}/")
    @Operation(
            summary = "Update session",
            description = "Update session",
            parameters = {
                    @Parameter(name = ID, in = ParameterIn.PATH, description = "Session ID", required = true),
                    @Parameter(name = EXT, in = ParameterIn.QUERY, description = "Whether to include extended data in the response", required = false)
            },
            requestBody = @RequestBody(description = "Session data", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(ID) String sessionId,
            WsSessionData sessionData,
            @QueryParam(EXT) Boolean includeExtendedData
    ) {
        return sessionsWs.update(sessionId, sessionData, includeExtendedData);
    }

    @DELETE
    @Path("{" + ID + "}/")
    @Operation(
            summary = "Delete session",
            description = "Delete session",
            parameters = {
                    @Parameter(name = ID, in = ParameterIn.PATH, description = "Session ID", required = true)
            },
            requestBody = @RequestBody(description = "Session data", required = false),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    public Object delete(
            @Context HttpServletRequest request,
            @PathParam(ID) String sessionId
    ) {
        return sessionsWs.delete(request, sessionId);
    }

    @DELETE
    @Path("all")
    @Operation(
            summary = "Delete all sessions",
            description = "Delete all sessions",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object deleteAll() {
        return sessionsWs.deleteAll();
    }

    @POST
    @Path("current/keepalive")
    @Operation(
            summary = "Keepalive current session",
            description = "Keepalive current session",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    public Object keepalive() {
        return sessionsWs.keepalive();
    }
}
