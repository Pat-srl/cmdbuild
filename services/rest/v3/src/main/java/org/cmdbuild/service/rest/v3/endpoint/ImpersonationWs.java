package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.IMPERSONATE_ALL_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.USERNAME;

@Path("sessions/current/impersonate/")
@Tag(name = "Impersonation", description = "Impersonation of users")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ImpersonationWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ImpersonationWs impersonationWs;

    public ImpersonationWs(org.cmdbuild.service.rest.v4.endpoint.ImpersonationWs impersonationWs) {
        this.impersonationWs = checkNotNull(impersonationWs);
    }

    @POST
    @Path("{" + USERNAME + "}/")
    @Operation(
            summary = "Impersonate a user",
            description = "Impersonate a user",
            parameters = {
                    @Parameter(name = USERNAME, description = "Username of the user to impersonate", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful impersonation"),
                    @ApiResponse(responseCode = "400", description = "Invalid username"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to impersonate the specified user"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(IMPERSONATE_ALL_AUTHORITY)
    public Object impersonate(
            @PathParam(USERNAME) String username
    ) {
        return impersonationWs.impersonate(username);
    }

    @DELETE
    @Path(EMPTY)
    @Operation(
            summary = "De-impersonate the current user",
            description = "De-impersonate the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful de-impersonation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user is not currently impersonating another user"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deimpersonare() {
        return impersonationWs.deimpersonare();
    }

}
