package org.cmdbuild.menu;

import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface MenuTreeNode {

    String getCode();

    MenuItemType getType();

    String getDescription();

    String getTargetDescription();

    @Nullable
    String getTarget();

    List<MenuTreeNode> getChildren();

    default Collection<MenuTreeNode> getDescendants() {
        return getChildren().stream().flatMap(c -> list(c).with(c.getDescendants()).stream()).collect(toList());
    }

    default Collection<MenuTreeNode> getDescendantsAndSelf() {
        return list(this).with(getDescendants());
    }

    default String getActualDescription() {
        return hasOwnDescription() ? getDescription() : getTargetDescription();
    }

    default boolean hasOwnDescription() {
        return isNotBlank(getDescription());
    }

}
