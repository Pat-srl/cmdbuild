package org.cmdbuild.navtree;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.data.filter.SorterElementDirection.ASC;
import org.cmdbuild.security.authorization.AccessControlService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElementOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import org.springframework.stereotype.Component;

@Component
public class NavTreeServiceImpl implements NavTreeService {

    private final DaoService dao;
    private final AccessControlService accessControlService;
    private final CmCache<Optional<NavTree>> treeByType;
    private final Holder<List<NavTree>> allTrees;

    public NavTreeServiceImpl(DaoService dao, AccessControlService accessControlService, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.accessControlService = checkNotNull(accessControlService);
        this.treeByType = cacheService.newCache("nav_tree_by_type", CacheConfig.SYSTEM_OBJECTS);
        this.allTrees = cacheService.newHolder("nav_tree_all", CacheConfig.SYSTEM_OBJECTS);
    }

    private void invalidateCache() {
        treeByType.invalidateAll();
        allTrees.invalidate();
    }

    @Override
    public List<NavTree> getAll() {
        return allTrees.get(() -> dao.selectAll().from(NavTree.class).orderBy(ATTR_CODE, ASC).asList());
    }

    @Override
    @Nullable
    public NavTree getTreeOrNull(String type) {
        return treeByType.get(type, () -> Optional.ofNullable(doGetDomainTree(type))).orElse(null);
    }

    @Override
    @Nullable
    public NavTree getUserTreeOrNull(String type) {
        return getUserAll().stream().filter(equal(NavTree::getName, type)).collect(onlyElementOrNull());
    }

    @Override
    public NavTree create(NavTree tree) {
        dao.createOnly(tree);
        invalidateCache();
        return getTree(tree.getName());
    }

    @Override
    public NavTree update(NavTree tree) {
        dao.updateOnly(NavTreeImpl.copyOf(tree).withId(getTree(tree.getName()).getId()).build());
        invalidateCache();
        return getTree(tree.getName());
    }

    @Override
    public void fixDirections(String treeId) {
        NavTree tree = getTreeOrNull(treeId);
        checkNotNull(tree, "cannot find the specified tree");
        dao.callFunction("_cm3_navtree_element_directions_invert", map("navcode", treeId));
        invalidateCache();
    }

    @Override
    public List<NavTree> getUserAll() {
        return getAll().stream().filter(accessControlService::canRead).collect(toList());
    }

    @Override
    public void removeTree(String treeType) {
        dao.delete(getTree(treeType));
        invalidateCache();
    }

    @Nullable
    private NavTree doGetDomainTree(String type) {
        return getAll().stream().filter(equal(NavTree::getName, type)).collect(onlyElementOrNull());
    }
}
