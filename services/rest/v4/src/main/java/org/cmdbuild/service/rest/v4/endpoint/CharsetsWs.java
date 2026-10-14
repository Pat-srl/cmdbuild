package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.service.rest.v4.command.CharsetsWsCommand;
import org.springframework.stereotype.Component;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("system/charsets")
@Tag(name = "System", description = "System related operations")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class CharsetsWs {

    private final CharsetsWsCommand command;

    public CharsetsWs(CharsetsWsCommand command) {
        this.command = checkNotNull(command);
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
        return response(command.doReadAvailableCharsets().stream().map(e -> map(
                "_id", e.getKey(),
                "description", format("%s (%s)", e.getValue().displayName(), e.getValue().aliases().stream().collect(joining(" ")))
        )));
    }

}
