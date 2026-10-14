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
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;

@Path("languages")
@Tag( name = "Languages", description = "Operations related to languages")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class LanguagesWs {

    private final org.cmdbuild.service.rest.v4.endpoint.LanguagesWs languagesWs;

    public LanguagesWs(org.cmdbuild.service.rest.v4.endpoint.LanguagesWs languagesWs) {
        this.languagesWs = checkNotNull(languagesWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all languages",
            description = "Get all languages",
            parameters = {
                    @Parameter(name = "active", in = ParameterIn.QUERY, description = "Whether to return only active languages", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of language data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object readLanguages(
            @QueryParam("active") @DefaultValue(FALSE) Boolean activeOnly
    ) {
        return languagesWs.readLanguages(activeOnly);
    }

}
