/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cluster;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import jakarta.inject.Provider;
import org.cmdbuild.auth.session.SessionService;
import org.springframework.stereotype.Component;

@Component
public class ClusterRpcServiceImpl implements ClusterRpcService {

    private final ClusterService clusterService;
    private final SessionService sessionService;
    private final Provider<RpcHelper> rpcHelper;

    public ClusterRpcServiceImpl(ClusterService clusterService, SessionService sessionService, Provider<RpcHelper> rpcHelper) {
        this.clusterService = checkNotNull(clusterService);
        this.sessionService = checkNotNull(sessionService);
        this.rpcHelper = checkNotNull(rpcHelper);
    }

    @Override
    public String invokeMethodOnNode(String nodeId, String payload) {
        if (clusterService.isThisNode(nodeId)) {
            return rpcHelper.get().invokeRpcMethod(payload);
        } else {
            return clusterService.invokeRpcMethod(nodeId, sessionService.getCurrentSessionIdOrNull(), payload);
        }
    }

    @Override
    public List<ClusterRpcMethodResult> invokeMethodOnAllNodes(String payload) {
        return clusterService.getClusterNodes().stream().map(n -> new ClusterRpcMethodResultImpl(invokeMethodOnNode(n.getNodeId(), payload), n)).collect(toImmutableList());
    }

    private static class ClusterRpcMethodResultImpl implements ClusterRpcMethodResult {

        final String output;
        final ClusterNode node;

        public ClusterRpcMethodResultImpl(String output, ClusterNode node) {
            this.output = output;
            this.node = node;
        }

        @Override
        public String getOutput() {
            return output;
        }

        @Override
        public ClusterNode getNode() {
            return node;
        }

    }
}
