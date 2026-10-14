package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.chat.ChatPeer;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.ChatPeerSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ChatPeersWsCommand;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dao.utils.SorterProcessor.sorted;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Path("sessions/current/peers")
@Tag(name = "Chat", description = "Chat")
@Produces(APPLICATION_JSON)
@Component
public class ChatPeersWs {

    private final ChatPeerSerializationHelper chatPeerSerializationHelper;
    private final ChatPeersWsCommand command;

    public ChatPeersWs(ChatPeerSerializationHelper chatPeerSerializationHelper, ChatPeersWsCommand command) {
        this.chatPeerSerializationHelper = checkNotNull(chatPeerSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get peers for current user",
            description = "Get peers for current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of peer data"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource"),
                    @ApiResponse(responseCode = "403", description = "Access to the resource is forbidden"),
                    @ApiResponse(responseCode = "404", description = "Peer not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    public Object getPeers(
            @BeanParam WsQueryOptions options
    ) {
        List<ChatPeer> chatPeerList = command.doGetPeers();
        List<FluentMap<String, Object>> peers = list(chatPeerList).map(chatPeerSerializationHelper::serializePeer);
        if (!options.getQuery().getFilter().isNoop()) {
            options.getQuery().getFilter().checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            peers = AttributeFilterProcessor.builder().withFilter(options.getQuery().getFilter().getAttributeFilter()).filter(peers);
        }
        if (!options.getQuery().getSorter().isNoop()) {
            peers = sorted(peers, options.getQuery().getSorter());
        }
        return response(paged(peers, options.getOffset(), options.getLimit()));
    }


}
