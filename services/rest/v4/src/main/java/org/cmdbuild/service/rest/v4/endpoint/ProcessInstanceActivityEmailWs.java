package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.email.Email;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.ProcessInstanceActivityEmailWsCommand;
import org.cmdbuild.service.rest.v4.serializationhelpers.EmailSerializationHelper;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;

@Path("processes/{processId}/instances/{instanceId}/activities/{activityId}/emails")
@Tag(name = "Process instance activity email", description = "The following documentation aims to illustrate the usage of the REST APIs provided by cmdbuild in order to retrieve, create, update or delete an email of a process instance activity")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class ProcessInstanceActivityEmailWs {

    private final EmailSerializationHelper emailSerializationHelper;
    private final ProcessInstanceActivityEmailWsCommand command;

    public ProcessInstanceActivityEmailWs(EmailSerializationHelper emailSerializationHelper, ProcessInstanceActivityEmailWsCommand command) {
        this.emailSerializationHelper = checkNotNull(emailSerializationHelper);
        this.command = checkNotNull(command);
    }

    @POST
    @Path("sync")
    @Operation(
            summary = "Update emails with card data",
            description = "Update emails with card data",
            parameters = {
                    @Parameter(name = PROCESS_ID, in = ParameterIn.PATH, description = "Id of the process")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "404", description = "Email not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object updateEmailWithCardData(
            @PathParam("instanceId") @Parameter(description = "Id of the process instance") Long flowId,
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        //TODO check user permissions
        //TODO auto update email data from template, with current flow data (and trigger email widget hooks)
        Collection<Email> emails = command.doUpdateEmailWithCardData(flowId, wsQueryOptions);
        return response(emails.stream().map(emailSerializationHelper::serializeBasicEmail).collect(toList()));
    }

}
