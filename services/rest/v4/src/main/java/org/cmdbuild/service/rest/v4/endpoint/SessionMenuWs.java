package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.menu.Menu;
import org.cmdbuild.service.rest.common.serializationhelpers.MenuSerializationHelper;
import org.cmdbuild.service.rest.v4.command.SessionMenuWsCommand;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SESSION_ID;

@Path("sessions/{" + SESSION_ID + "}/menu")
@Tag(name = "Session", description = "Operations related to sessions")
@Produces(APPLICATION_JSON)
@Component
public class SessionMenuWs {

    private final MenuSerializationHelper menuSerializationHelper;
    private final SessionMenuWsCommand command;

    public SessionMenuWs(MenuSerializationHelper menuSerializationHelper, SessionMenuWsCommand command) {
        this.menuSerializationHelper = checkNotNull(menuSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get menu for current session",
            description = "Get menu for current session",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of menu data"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested menu"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(SESSION_ID) @Parameter(description = "Id of session", required = true) String sessionId,
            @QueryParam("flat") @DefaultValue(FALSE) @Parameter(description = "Return flat menu") Boolean flatMenu
    ) {
        Menu menu = command.doRead(sessionId);
        return response(flatMenu ? menuSerializationHelper.serializeFlatUserMenu(menu) : menuSerializationHelper.serializeUserMenu(menu));
    }

}
