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

@Path("timezones")
@Tag(name = "System", description = "System related operations")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class TimezonesWs {

    private final org.cmdbuild.service.rest.v4.endpoint.TimezonesWs timezonesWs;

    public TimezonesWs(org.cmdbuild.service.rest.v4.endpoint.TimezonesWs timezonesWs) {
        this.timezonesWs = checkNotNull(timezonesWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "List available timezones",
            description = "List available timezones",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK") ,
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAvailableTimezones() {
        return timezonesWs.readAvailableTimezones();
    }

}
