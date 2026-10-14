/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.navtree;

import jakarta.annotation.Nullable;
import org.cmdbuild.navtree.NavTreeInfo;
import org.cmdbuild.navtree.NavTreeNode;
import org.cmdbuild.navtree.NavTreeType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;

public interface NavTree extends NavTreeInfo {

    @Nullable
    Long getId();

    NavTreeNode getData();

    boolean getActive();

    NavTreeType getType();

    NavTreeMetadata getMetadata();

    default NavTreeNode getNodeById(String nodeId) {
        return getData().getThisNodeAndAllDescendants().stream().filter(equal(NavTreeNode::getId, nodeId)).collect(onlyElement("node not found for id = %s", nodeId));
    }
}
