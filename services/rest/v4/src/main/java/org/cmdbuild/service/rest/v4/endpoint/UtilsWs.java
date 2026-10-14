package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.service.rest.v4.command.UtilsWsCommand;
import org.springframework.stereotype.Component;

import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;


@Path("utils/")
@Tag(name = "Utilities", description = "Utilities")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
@Component
public class UtilsWs {

    private final UtilsWsCommand command;

    public UtilsWs(UtilsWsCommand command) {
        this.command = checkNotNull(command);
    }

    @POST
    @Path("crypto/encrypt")
    @Operation(
            summary = "Encrypt value",
            description = "Encrypt value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(APPLICATION_JSON)
    public Object encryptValue(
            @RequestBody(description = "Map containing the value to encrypt, under key \"value\"", required = true) Map<String, String> payload
    ) {
        return response(map("encrypted", command.doEncryptValue(payload)));
    }

}
