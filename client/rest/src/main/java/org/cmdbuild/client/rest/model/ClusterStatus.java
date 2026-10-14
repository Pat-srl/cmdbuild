/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import java.util.List;
import org.cmdbuild.cluster.ClusterNode;
import org.cmdbuild.utils.lang.CmCollectionUtils;

public interface ClusterStatus {

    boolean isRunning();

    List<ClusterNode> getNodes();

    default ClusterNode getThisNode() {
        return getNodes().stream().filter(ClusterNode::isThisNode).collect(CmCollectionUtils.onlyElement());
    }

}
