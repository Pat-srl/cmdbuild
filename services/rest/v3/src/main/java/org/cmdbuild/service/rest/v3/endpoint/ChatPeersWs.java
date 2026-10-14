package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("sessions/current/peers")
@Tag(name = "Chat", description = "Chat")
@Produces(APPLICATION_JSON)
public class ChatPeersWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ChatPeersWs chatPeersWs;

    public ChatPeersWs(org.cmdbuild.service.rest.v4.endpoint.ChatPeersWs chatPeersWs) {
        this.chatPeersWs = checkNotNull(chatPeersWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get peers for current user",
            description = "Get peers for current user",
            requestBody = @RequestBody(description = "The query options to use", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = APPLICATION_JSON, schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = WsQueryOptions.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of peer data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    public Object getPeers(
            WsQueryOptions options
    ) {
        return chatPeersWs.getPeers(options);
    }
}
