package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SESSION_ID;

@Path("sessions/{sessionId}/menu")
@Tag(name = "Session", description = "Operations related to sessions")
@Produces(APPLICATION_JSON)
public class SessionMenuWs {

    private final org.cmdbuild.service.rest.v4.endpoint.SessionMenuWs sessionMenuWs;

    public SessionMenuWs(org.cmdbuild.service.rest.v4.endpoint.SessionMenuWs sessionMenuWs) {
        this.sessionMenuWs = checkNotNull(sessionMenuWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get menu for current session",
            description = "Get menu for current session",
            parameters = {
                    @Parameter(name = SESSION_ID, in = ParameterIn.PATH, description = "Session ID", required = true),
                    @Parameter(name = "flat", in = ParameterIn.QUERY, description = "Whether to return a flat menu structure", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of menu data"),
                    @ApiResponse(responseCode = "404", description = "Session not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(SESSION_ID) String sessionId,
            @QueryParam("flat") @DefaultValue(FALSE) Boolean flatMenu
    ) {
        return sessionMenuWs.read(sessionId, flatMenu);
    }

}
