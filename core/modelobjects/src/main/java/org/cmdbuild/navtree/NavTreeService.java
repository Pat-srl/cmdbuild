package org.cmdbuild.navtree;

import jakarta.annotation.Nullable;
import java.util.List;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

public interface NavTreeService {

    List<NavTree> getAll();

    @Nullable
    NavTree getTreeOrNull(String type);

    @Nullable
    NavTree getUserTreeOrNull(String type);

    void removeTree(String treeType);

    NavTree create(NavTree tree);

    NavTree update(NavTree tree);

    void fixDirections(String treeId);

    List<NavTree> getUserAll();

    default NavTree getTree(String type) {
        return checkNotNull(getTreeOrNull(type), "nav tree not found for name = %s", type);
    }

    default NavTree getUserTree(String type) {
        return checkNotNull(getUserTreeOrNull(type), "nav tree not found for name = %s", type);
    }
}
