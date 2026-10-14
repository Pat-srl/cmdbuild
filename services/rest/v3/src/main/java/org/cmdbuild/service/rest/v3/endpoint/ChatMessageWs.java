package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsChatMessageData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.CHAT_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.RECORD_ID;

@Path("sessions/current/messages")
@Tag( name = "Chat", description = "Chat")
@Produces(APPLICATION_JSON)
public class ChatMessageWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ChatMessageWs chatMessageWs;

    public ChatMessageWs(org.cmdbuild.service.rest.v4.endpoint.ChatMessageWs chatMessageWs) {
        this.chatMessageWs = checkNotNull(chatMessageWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get messages for current user",
            description = "Get messages for current user",
            requestBody = @RequestBody(description = "The query options to use", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = APPLICATION_JSON, schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = WsQueryOptions.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of messages data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getMessages(
            WsQueryOptions options
    ) {
        return chatMessageWs.getMessages(options);
    }

    @PUT
    @Path("{recordId}")
    @Operation(
            summary = "Archive message",
            description = "Archive message",
            parameters = {
                    @Parameter(name = RECORD_ID, description = "Id of message to archive", required = true)
            },
            requestBody = @RequestBody(description = "Updated data for the message", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful archive of message"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(CHAT_ACCESS_AUTHORITY)
    public Object updateMessage(
            @QueryParam(RECORD_ID) Long recordId,
            WsChatMessageData message
    ) {
        return chatMessageWs.updateMessage(recordId, message);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Send message",
            description = "Send message",
            requestBody = @RequestBody(description = "Data for the message to send", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful sending of message"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(CHAT_ACCESS_AUTHORITY)
    public Object sendMessage(
            WsChatMessageData message
    ) {
        return chatMessageWs.sendMessage(message);
    }

    @DELETE
    @Path("{recordId}")
    @Operation(
            summary = "Delete message",
            description = "Delete message",
            parameters = {
                    @Parameter(name = RECORD_ID, description = "Id of message to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of message"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(CHAT_ACCESS_AUTHORITY)
    public Object deleteMessage(
            @QueryParam(RECORD_ID) Long recordId
    ) {
        return chatMessageWs.deleteMessage(recordId);
    }
}
