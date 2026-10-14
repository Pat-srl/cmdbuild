package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.service.rest.v4.command.TimezonesWsCommand;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("timezones")
@Tag(name = "System", description = "System related operations")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class TimezonesWs {

    private final TimezonesWsCommand command;

    public TimezonesWs(TimezonesWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "List available timezones",
            description = "List available timezones",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAvailableTimezones() {
        return response(command.doReadAvailableTimezones().stream().sorted().map(z -> map("_id", z, "description", z)));
    }

}
