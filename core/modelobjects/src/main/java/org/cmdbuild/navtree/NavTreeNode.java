/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.navtree;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.utils.json.JsonBean;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@JsonBean(NavTreeNodeImpl.class)
public interface NavTreeNode {

    String getId();

    @Nullable
    String getParentId();

    String getTargetClassName();

    String getTargetClassDescription();

    @Nullable
    String getDomainName();

    RelationDirection getDirection();

    boolean getShowOnlyOne();

    List<NavTreeNode> getChildNodes();

    @Nullable
    String getTargetFilter();

    boolean getEnableRecursion();

    NavTreeNodeSubclassViewMode getSubclassViewMode();

    boolean getSubclassViewShowIntermediateNodes();

    List<String> getSubclassFilter();

    Map<String, String> getSubclassDescriptions();

    public String getBorderSource();

    public String getBorderColor();

    public String getIconSource();

    public String getIconFont();

    public String getIconColorMode();

    public String getIconColor();

    public String getSourceLookup();

    public String getSourceLookupAttribute();

    default boolean hasChildNodes() {
        return !getChildNodes().isEmpty();
    }

    default List<NavTreeNode> getThisNodeAndAllDescendants() {
        return list(this).with(list(getChildNodes()).flatMap(NavTreeNode::getThisNodeAndAllDescendants));
    }

    default boolean hasParent() {
        return isNotBlank(getParentId());
    }

    default boolean hasFilter() {
        return isNotBlank(getTargetFilter());
    }

    default boolean getDirect() {
        return getDirection().isDirect();
    }
}
