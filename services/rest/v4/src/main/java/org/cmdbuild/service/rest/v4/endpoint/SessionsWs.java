package org.cmdbuild.service.rest.v4.endpoint;

import com.google.common.collect.Ordering;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import org.cmdbuild.auth.login.PasswordResetRequiredAuthenticationException;
import org.cmdbuild.auth.login.RequestAuthenticatorResponse;
import org.cmdbuild.auth.login.UserVisibleAuthenticationException;
import org.cmdbuild.auth.session.SessionScope;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.user.SessionType;
import org.cmdbuild.config.UiFilterConfiguration;
import org.cmdbuild.service.rest.common.serializationhelpers.SessionSerializationHelper;
import org.cmdbuild.service.rest.v4.command.SessionsWsCommand;
import org.cmdbuild.service.rest.v4.model.WsSessionData;
import org.cmdbuild.service.rest.v4.wshelpers.SessionWsCommons;
import org.cmdbuild.services.rest.v4.config.WsSessionConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.HttpHeaders.SET_COOKIE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.auth.config.AuthenticationServiceConfiguration.LoginServiceReturnId.RI_ALWAYS;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_SYSCONFIG_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.auth.user.SessionType.ST_BATCH;
import static org.cmdbuild.auth.user.SessionType.ST_INTERACTIVE;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_AUTHORIZATION_COOKIE;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import static org.cmdbuild.fault.FaultLevel.FL_ERROR;
import static org.cmdbuild.fault.FaultUtils.buildMessageListForResponse;
import static org.cmdbuild.fault.FaultUtils.exceptionToUserMessage;
import static org.cmdbuild.service.rest.common.serializationhelpers.SessionSerializationHelper.serializeGroupOfPrivileges;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.*;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.*;
import static org.cmdbuild.utils.ws3.utils.WsCookieUtils.buildDeleteCookieHeader;
import static org.cmdbuild.utils.ws3.utils.WsCookieUtils.buildSetCookieHeader;

@Path("sessions/")
@Tag(name = "Sessions", description = "Sessions")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class SessionsWs extends SessionWsCommons {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final UiFilterConfiguration uiFilterConfiguration;
    private final SessionSerializationHelper sessionSerializationHelper;
    private final SessionsWsCommand command;

    public SessionsWs(UiFilterConfiguration config, SessionService sessionService, AuthenticationConfiguration authenticationConfiguration, SessionSerializationHelper sessionSerializationHelper, SessionsWsCommand command) {
        super(sessionService);
        this.uiFilterConfiguration = checkNotNull(config);
        this.authenticationConfiguration = checkNotNull(authenticationConfiguration);
        this.sessionSerializationHelper = checkNotNull(sessionSerializationHelper);
        this.command = checkNotNull(command);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a session",
            description = "Create a session",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "409", description = "Conflict"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    public Object create(
            @Context HttpServletRequest request,
            @RequestBody(description = "Session data", required = true) WsSessionData sessionData,
            @QueryParam(EXT)  @Parameter(description = "Include extended data (default = false)") @Nullable Boolean includeExtendedData,
            @QueryParam("scope")  @Parameter(description = "Session scope (default = UI)") String scopeStr,
            @QueryParam("returnId")  @Parameter(description = "Return session id (default = false)") Boolean returnId
    ) {
        checkNotBlank(sessionData.getUsername(), "'username' param cannot be null");
        checkNotNull(sessionData.getPassword(), "'password' param cannot be null");

        SessionScope scope = checkNotNull(convert(firstNotBlankOrNull(scopeStr, sessionData.scope), SessionScope.class), "must set 'scope' param (valid values = %s)", list(SessionScope.values()).stream().map(SessionScope::name).map(String::toLowerCase).collect(joining(",")));
        boolean serviceUsersAllowed, attachSessionCookie;
        SessionType sessionType;
        switch (scope) {
            case SERVICE -> {
                serviceUsersAllowed = true;
                attachSessionCookie = false;
                sessionType = ST_BATCH;
            }
            case UI -> {
                serviceUsersAllowed = false;
                attachSessionCookie = true;
                sessionType = ST_INTERACTIVE;
            }
            default -> throw unsupported("unsupported session scope = %s", scope);
        }
        try {
            String sessionId = command.doCreateSessionId(sessionData, sessionType, serviceUsersAllowed);
            Session session = command.doReadOne(sessionId);

            returnId = firstNotNull(returnId, equal(authenticationConfiguration.getLoginServiceReturnIdMode(), RI_ALWAYS));

            Object responsePayload = response(sessionSerializationHelper.serializeSession(session, includeExtendedData, returnId));
            if (attachSessionCookie) {
                return Response.ok(responsePayload).header(SET_COOKIE, buildSetCookieHeader(CMDBUILD_AUTHORIZATION_COOKIE, sessionId, uiFilterConfiguration.getCookieMaxAgeSeconds(), firstNotBlank(request.getContextPath(), "/"), uiFilterConfiguration.enableCookieSecure(request.isSecure()), true, uiFilterConfiguration.getCookieSameSiteMode())).build();
            } else {
                return responsePayload;
            }
        } catch (UserVisibleAuthenticationException ex) {
            return Response.status(UNAUTHORIZED).entity(failure().with("messages", buildMessageListForResponse(FL_ERROR, true, exceptionToUserMessage(ex)))).build();
        } catch (PasswordResetRequiredAuthenticationException ex) {
            return Response.status(UNAUTHORIZED).entity(failure().with("passwordResetRequired", true)).build();
        }
    }

    @POST
    @Path("configure")
    @Operation(
            summary = "Configure session",
            description = "Configure session",
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
    @RolesAllowed(ADMIN_SYSCONFIG_MODIFY_AUTHORITY)
    public Object configureSession(
            @RequestBody(description = "Session configuration data", required = true) WsSessionConfig sessionConfig
    ) {
        command.doConfigureSession(sessionConfig);
        return success();
    }

    @GET
    @Path("{" + ID + "}/")
    @Operation(
            summary = "Read session",
            description = "Read session",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam(ID) @Parameter(description = "Session id", required = true) String sessionId,
            @QueryParam(EXT) @Parameter(description = "Include extended data (default = false)") Boolean includeExtendedData,
            @QueryParam("if_exists") @Parameter(description = "Return 404 if session does not exist (default = false)") @DefaultValue(FALSE) Boolean checkIfExists,
            @QueryParam("operatorinfo") @DefaultValue(FALSE) @Parameter(description = "Request Current User infos") Boolean includeOperator
    ) {
        sessionId = sessionIdOrCurrent(sessionId);
        boolean exists = isNotBlank(sessionId) && sessionService.exists(sessionId);

        if (!exists && Boolean.TRUE.equals(checkIfExists)) {
            return response(map("exists", false));
        }

        checkArgument(exists, "session not found for id = %s", sessionId);
        Session session = command.doReadOne(sessionId);
        String finalSessionId = sessionId;
        return response(sessionSerializationHelper.serializeSession(session, includeExtendedData, false).accept(m -> {
            if (Boolean.TRUE.equals(checkIfExists)) {
                m.put("exists", true);
            }
            try {
                if (Boolean.TRUE.equals(includeOperator)) {
                    m.with(command.doReadOperatorData());
                }
            } catch (Exception ex) {
                logger.warn("failed to read operator data for sessionId = {}", finalSessionId, ex);
            }
        }));
    }

    @GET
    @Path("{sessionId}/privileges")
    @Operation(
            summary = "Read privileges for session",
            description = "Read privileges for session",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    public Object readPrivileges(
            @PathParam("sessionId") @Parameter(description = "Session id", required = true) String sessionId
    ) {
        Set<Entry<String, UserPrivilegesForObject>> userPrivileges = command.doReadPrivileges(sessionId);
        return response(userPrivileges.stream().sorted(Ordering.natural().onResultOf(Entry::getKey)).map(p -> map(
                "subject", p.getKey())
                .with(serializeGroupOfPrivileges(p.getValue().getMinPrivilegesForAllRecords()))
                .accept(m -> {
                    if (p.getValue().hasPrivilegesWithFilter()) {
                        m.put("hasPrivilegesWithFilter", true,
                                "privilegesWithFilter", p.getValue().getPrivilegeGroupsWithFilter().stream().map(pf -> map("filter", serializeFilter(pf.getFilter())).with(serializeGroupOfPrivileges(pf))).toList());
                    }
                })).toList());
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all sessions",
            description = "Get all sessions",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object readAll() {
        List<Session> sessions = command.doReadAll();
        return response(sessions.stream().map(sessionSerializationHelper::serializeSession).toList());
    }

    @PUT
    @Path("{" + ID + "}/")
    @Operation(
            summary = "Update session",
            description = "Update session",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(ID) @Parameter(description = "Session id", required = true) String sessionId,
            @RequestBody(description = "Session data", required = true) WsSessionData sessionData,
            @QueryParam(EXT) @Parameter(description = "Include extended data (default = false)") Boolean includeExtendedData
    ) {
        Session session = command.doUpdate(sessionId, sessionData);
        return response(sessionSerializationHelper.serializeSession(session, includeExtendedData, false));
    }

    @DELETE
    @Path("{" + ID + "}/")
    @Operation(
            summary = "Delete session",
            description = "Delete session",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    public Object delete(
            @Context HttpServletRequest request,
            @PathParam(ID) @Parameter(description = "Session id", required = true) String sessionId
    ) {
        RequestAuthenticatorResponse<Void> response = command.doDelete(request, sessionId);
        return Response.ok(success().accept(m -> {
            if (response.hasRedirectUrl()) {
                m.put("redirect", response.getRedirectUrl());
            }
        })).header(SET_COOKIE, buildDeleteCookieHeader(CMDBUILD_AUTHORIZATION_COOKIE, firstNotBlank(request.getContextPath(), "/"), uiFilterConfiguration.enableCookieSecure(request.isSecure()), true, uiFilterConfiguration.getCookieSameSiteMode())).build();
    }

    @DELETE
    @Path("all")
    @Operation(
            summary = "Delete all sessions",
            description = "Delete all sessions",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "429", description = "Too Many Requests"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object deleteAll() {
        command.doDeleteAll();
        return success();
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
                    @ApiResponse(responseCode = "429", description = "Too Many Requests"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {@SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {})}
    )
    public Object keepalive() {
        return response(command.doKeepAlive());
    }
}
