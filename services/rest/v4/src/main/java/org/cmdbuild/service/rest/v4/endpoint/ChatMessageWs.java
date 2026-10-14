package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.chat.ChatMessage;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.ChatMessageSerializer;
import org.cmdbuild.service.rest.v4.command.ChatMessageWsCommand;
import org.cmdbuild.service.rest.v4.model.WsChatMessageData;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.CHAT_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.serializationhelpers.ChatMessageSerializer.serializeDetailedMessage;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.RECORD_ID;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

@Path("sessions/current/messages")
@Tag( name = "Chat", description = "Chat")
@Produces(APPLICATION_JSON)
@Component
public class ChatMessageWs {

    private final ChatMessageWsCommand command;

    public ChatMessageWs(ChatMessageWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get messages for current user",
            description = "Get messages for current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of messages data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getMessages(
            @BeanParam WsQueryOptions options
    ) {
        PagedElements<ChatMessage> chatMessages = command.doGetMessages(options);
        return response(chatMessages.map(options.isDetailed() ? ChatMessageSerializer::serializeDetailedMessage : ChatMessageSerializer::serializeBasicMessage));
    }

    @PUT
    @Path("{recordId}")
    @Operation(
            summary = "Archive message",
            description = "Archive message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful archive of message"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(CHAT_ACCESS_AUTHORITY)
    public Object updateMessage(
            @QueryParam(RECORD_ID) @Parameter(description = "Record Id") Long recordId,
            @RequestBody(description = "Message data", required = true) WsChatMessageData message
    ) {
        command.doUpdateMessage(recordId, message);
        return success();
    }

    @POST
    @Path("")
    @Operation(
            summary = "Send message",
            description = "Send message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful sending of message"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(CHAT_ACCESS_AUTHORITY)
    public Object sendMessage(
            @RequestBody(description = "Message data", required = true) WsChatMessageData message
    ) {
        ChatMessage chatMessage = command.doSendMessage(message);
        return response(serializeDetailedMessage(chatMessage));
    }

    @DELETE
    @Path("{recordId}")
    @Operation(
            summary = "Delete message",
            description = "Delete message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of message"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(CHAT_ACCESS_AUTHORITY)
    public Object deleteMessage(
            @QueryParam(RECORD_ID) @Parameter(description = "Record Id") Long recordId
    ) {
        command.doDeleteMessage(recordId);
        return success();
    }

    @PUT
    @Path("_ALL/archive")
    @RolesAllowed(CHAT_ACCESS_AUTHORITY)
    public Object archiveAllForUser() {
        command.doArchiveAllForUser();
        return success();
    }

    @DELETE
    @Path("_ALL/delete")
    @RolesAllowed(CHAT_ACCESS_AUTHORITY)
    public Object deleteAllForUser() {
        command.doDeleteAllForUser();
        return success();
    }
}
