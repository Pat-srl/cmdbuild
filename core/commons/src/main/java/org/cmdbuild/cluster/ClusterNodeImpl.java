/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cluster;

import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.lang.CmPreconditions;

public class ClusterNodeImpl implements ClusterNode {

    private final String address;
    private final String nodeId;
    private final boolean isThisNode;
    private final Map<String, String> meta;

    public ClusterNodeImpl(String nodeId, String address, boolean isThisNode, Map<String, String> meta) {
        this.address = CmPreconditions.checkNotBlank(address);
        this.nodeId = CmPreconditions.checkNotBlank(nodeId);
        this.isThisNode = isThisNode;
        this.meta = map(meta).immutable();
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public boolean isThisNode() {
        return isThisNode;
    }

    @Override
    public Map<String, String> getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return "ClusterNodeImpl{" + "address=" + address + ", nodeId=" + nodeId + ", thisNode=" + isThisNode + '}';
    }

}
