package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;

@Path("utils/")
@Tag(name = "Utilities", description = "Utilities")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
public class UtilsWs {

    private final org.cmdbuild.service.rest.v4.endpoint.UtilsWs utilsWs;

    public UtilsWs(org.cmdbuild.service.rest.v4.endpoint.UtilsWs utilsWs) {
        this.utilsWs = utilsWs;
    }
    @POST
    @Path("crypto/encrypt")
    @Operation(
            summary = "Encrypt value",
            description = "Encrypt value",
            requestBody = @RequestBody(description = "Value to encrypt", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(APPLICATION_JSON)
    public Object encryptValue(
            @RequestBody Map<String, String> payload
    ) {
        return utilsWs.encryptValue(payload);
    }

}
