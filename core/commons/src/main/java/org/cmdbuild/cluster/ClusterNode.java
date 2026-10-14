/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cluster;

import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface ClusterNode {

    final String NODE_ID_ATTR = "org.cmdbuild.cluster.NODE_ID",
            NODE_WORKGROUPS_ATTR = "org.cmdbuild.cluster.NODE_WORKGROUP";

    boolean isThisNode();

    String getNodeId();

    String getAddress();

    Map<String, String> getMeta();

    default List<String> getWorkgroups() {
        return toListOfStrings(getMeta().get(NODE_WORKGROUPS_ATTR));
    }

    default boolean isOtherNode() {
        return !isThisNode();
    }

    default boolean hasWorkgroup(String workgroup) {
        return getWorkgroups().contains(checkNotBlank(workgroup));
    }
}
