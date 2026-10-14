/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.chat.ChatPeer;
import org.cmdbuild.chat.PeerService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class ChatPeersWsCommand {

    private final PeerService peerService;

    public ChatPeersWsCommand(PeerService peerService) {
        this.peerService = checkNotNull(peerService);
    }

    public List<ChatPeer> doGetPeers() {
        return peerService.getPeersForCurrentSession();
    }
}
