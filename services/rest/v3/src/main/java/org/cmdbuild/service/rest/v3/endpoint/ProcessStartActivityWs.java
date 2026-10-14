package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;

@Path("processes/{" + PROCESS_ID + "}/start_activities/")
@Tag(name = "Process start activity", description = "Operations related to process start activity")
@Produces(APPLICATION_JSON)
public class ProcessStartActivityWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ProcessStartActivityWs processStartActivityWs;

    public ProcessStartActivityWs(org.cmdbuild.service.rest.v4.endpoint.ProcessStartActivityWs processStartActivityWs) {
        this.processStartActivityWs = checkNotNull(processStartActivityWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get start activity for a process",
            description = "Get start activity for a process",
            parameters = {
                    @Parameter(name = PROCESS_ID, in = ParameterIn.PATH , description = "Name of the process to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of start activity data"),
                    @ApiResponse(responseCode = "404", description = "Process not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(PROCESS_ID) String processId
    ) {
        return processStartActivityWs.read(processId);
    }

}
