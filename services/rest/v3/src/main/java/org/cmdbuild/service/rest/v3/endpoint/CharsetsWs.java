package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("system/charsets")
@Tag(name = "System", description = "System related operations")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CharsetsWs {

    private final org.cmdbuild.service.rest.v4.endpoint.CharsetsWs charsetsWs;

    public CharsetsWs(org.cmdbuild.service.rest.v4.endpoint.CharsetsWs charsetsWs) {
        this.charsetsWs = checkNotNull(charsetsWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "List available charsets",
            description = "List available charsets",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of charset data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAvailableCharsets() {
        return charsetsWs.readAvailableCharsets();
    }

}
