/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cluster;

import java.util.List;

public interface ClusterRpcService {

    String invokeMethodOnNode(String nodeId, String payload);

    List<ClusterRpcMethodResult> invokeMethodOnAllNodes(String payload);

}
