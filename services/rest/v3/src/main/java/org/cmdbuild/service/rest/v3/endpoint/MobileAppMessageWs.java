package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsMobileAppMessageData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.RECORD_ID;

@Path("sessions/current/mobile/messages")
@Tag( name = "Mobile App Messages", description = "Mobile App Messages")
@Produces(APPLICATION_JSON)
public class MobileAppMessageWs {

    private final org.cmdbuild.service.rest.v4.endpoint.MobileAppMessageWs mobileAppMessageWs;

    public MobileAppMessageWs(org.cmdbuild.service.rest.v4.endpoint.MobileAppMessageWs mobileAppMessageWs) {
        this.mobileAppMessageWs = checkNotNull(mobileAppMessageWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get messages for current user",
            description = "Get messages for current user",
            requestBody = @RequestBody(description = "Query options for filtering and pagination of messages", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of mobile app message data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getMessages(WsQueryOptions options) {
        return mobileAppMessageWs.getMessages(options);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Send message to mobile app",
            description = "Send message to mobile app",
            requestBody = @RequestBody(description = "Message data to send", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object sendMessage(
            WsMobileAppMessageData message
    ) {
        return mobileAppMessageWs.sendMessage(message);
    }

    @PUT
    @Path("{recordId}")
    @Operation(
            summary = "Archive message",
            description = "Archive message",
            parameters = {@Parameter(name = RECORD_ID, description = "Id of the message to archive", required = true)},
            requestBody = @RequestBody(description = "Message data to archive", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful archive of mobile app message"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object updateMessage(
            @QueryParam(RECORD_ID) Long recordId,
            WsMobileAppMessageData message
    ) {
        return mobileAppMessageWs.updateMessage(recordId, message);
    }

    @DELETE
    @Path("{recordId}")
    @Operation(
            summary = "Delete message",
            description = "Delete message",
            parameters = {@Parameter(name = RECORD_ID, description = "Id of the message to delete", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of mobile app message"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deleteMessage(
            @QueryParam(RECORD_ID) Long recordId
    ) {
        return mobileAppMessageWs.deleteMessage(recordId);
    }
}
