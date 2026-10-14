package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.v4.command.ProcessConfigurationWsCommand;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import org.springframework.stereotype.Component;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.v4.command.ProcessConfigurationWsCommand.PROCESS_STATUS_CODE_MAP;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("configuration/processes/")
@Tag( name = "Process", description = "Process")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class ProcessConfigurationWs {

    private final ProcessConfigurationWsCommand command;

    public ProcessConfigurationWs(ProcessConfigurationWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("statuses/")
    @Operation(
            summary = "Get process statuses",
            description = "Get process statuses",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process statuses"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readStatuses() {
        FluentList<LookupValue> lookupValueList = command.doReadStatuses();
        return response(lookupValueList.map(l -> map("id", l.getId(), "value", PROCESS_STATUS_CODE_MAP.get(l.getCode()), "description", l.getDescription())));
    }
}
