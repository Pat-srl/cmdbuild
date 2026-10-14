/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import org.cmdbuild.chat.ChatPeer;
import org.cmdbuild.chat.PeerService;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.stereotype.Component;

import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
@Component
public class ChatPeerSerializationHelper {

    private final PeerService peerService;

    public ChatPeerSerializationHelper(PeerService peerService) {
        this.peerService = peerService;
    }

    public CmMapUtils.FluentMap<String, Object> serializePeer(ChatPeer peer) {
        return map(
                "_id", peer.getUsername(),
                "username", peer.getUsername(),
                "description", peer.getDescription(),
                "_hasMessages", peer.hasMessages(),
                "_hasNewMessages", peer.hasNewMessages(),
                "_newMessagesCount", peer.getNewMessageCount(),
                "_lastMessageTimestamp", toIsoDateTimeLocal(peer.getLastMessageTimestamp()),
                "icon", peerService.getIconUrl(peer)
        );
    }
}
