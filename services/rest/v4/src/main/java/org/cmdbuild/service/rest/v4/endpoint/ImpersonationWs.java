package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.command.ImpersonationWsCommand;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.IMPERSONATE_ALL_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.USERNAME;

@Path("sessions/current/impersonate/")
@Tag(name = "Impersonation", description = "Impersonation of users")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class ImpersonationWs {

    private final ImpersonationWsCommand command;

    public ImpersonationWs(ImpersonationWsCommand command) {
        this.command = checkNotNull(command);
    }

    @POST
    @Path("{" + USERNAME + "}/")
    @Operation(
            summary = "Impersonate a user",
            description = "Impersonate a user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful impersonation"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(IMPERSONATE_ALL_AUTHORITY)
    public Object impersonate(
            @PathParam(USERNAME) @Parameter(description = "Username of the user to impersonate") String username
    ) {
        command.doImpersonate(username);
        return success();
    }

    @DELETE
    @Path(EMPTY)
    @Operation(
            summary = "De-impersonate the current user",
            description = "De-impersonate the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful de-impersonation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deimpersonare() {
        command.doDeimpersonate();
        return success();
    }

}
