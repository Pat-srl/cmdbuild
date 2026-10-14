/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.domain.tree;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import static java.util.Collections.emptyList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.services.serialization.domain.tree.DomainNodeData;
import org.cmdbuild.services.serialization.domain.tree.DomainTreeData;
import org.cmdbuild.sync.ClasseSync;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds a {@link DomainTree} with a ordered list of {@link DomainPath}s,
 * cutted if a <i>superclass</i> or same {@link Classe} of given leaf is found.
 *
 * @author afelice
 */
public class DomainTreeBuilder {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String MODEL_BASE_CLASSE_NAME = "Classe";

    final ClasseSync classeSync;

    public DomainTreeBuilder(ClasseSync classeSync) {
        this.classeSync = checkNotNull(classeSync);
    }

    public DomainTree buildDomainTree(DomainTreeData domainTreeData, String leafNodeClasseName) {
        DomainTree result = new DomainTree(domainTreeData);
        findAllPathsFor(result, domainTreeData, leafNodeClasseName);

        return result;
    }

    /**
     * Raises {@link DomainTreeException} if in given
     * <code>resultDomainTree</i>:
     * <ul>
     * <li>has no nodes;
     * <li>has no root;
     * <li>has multiple roots.
     * </ul>
     *
     * @param resultDomainTree
     * @param domainTreeData
     * @param leafNodeClasseName
     */
    private void findAllPathsFor(DomainTree resultDomainTree,
            DomainTreeData domainTreeData, String leafNodeClasseName) {
        if (domainTreeData.nodes.isEmpty()) {
            String errMsg = "%s has no nodes".formatted(LoggerHelper.buildLog(resultDomainTree));
            logger.error(errMsg);
            throw new DomainTreeException(errMsg);
        }

        List<DomainNodeData> roots = domainTreeData.nodes.stream().filter(DomainNodeData::isRoot).collect(toList());
        if (roots.isEmpty()) {
            String errMsg = "%s has no root node".formatted(LoggerHelper.buildLog(resultDomainTree));
            logger.error(errMsg);
            throw new DomainTreeException(errMsg);
        }
        if (roots.size() > 1) {
            String errMsg = "%s has multiple root nodes: %s".formatted(LoggerHelper.buildLog(resultDomainTree), toClasseNames(roots));
            logger.error(errMsg);
            throw new DomainTreeException(errMsg);
        }
        DomainNodeData singleRootNode = roots.get(0);
        resultDomainTree.rootNodeClasseName = singleRootNode.targetClass;
        resultDomainTree.leafNodeClasseName = leafNodeClasseName;

        // Build paths from root to leaf nodes
        // 1. build children map (by input node id)
        Map<String, List<DomainNodeData>> childrenMapById = buildChildrenMapById(domainTreeData);
        // 2. apply DFS (deepth first search) to obtain in resultDomainTree.domainPaths full paths in the same order of input.
        dfs(resultDomainTree, singleRootNode, childrenMapById);

        // Keep only paths that end with leafNodeClasseName or some of its superclass
        cutPathsOn(resultDomainTree, leafNodeClasseName);
    }

    /**
     * Build <i>children map</i> by <i>parent</i> id.
     *
     * @param domainTreeData
     * @return
     */
    private Map<String, List<DomainNodeData>> buildChildrenMapById(DomainTreeData domainTreeData) {
        Map<String, List<DomainNodeData>> childrenMap = new HashMap<>();

        for (DomainNodeData n : domainTreeData.nodes) {
            if (n.parent != null) {
                childrenMap
                        .computeIfAbsent(n.parent, k -> new ArrayList<>())
                        .add(n);
            }
        }

        return childrenMap;
    }

    private void dfs(DomainTree resultDomainTree, DomainNodeData singleRootNode, Map<String, List<DomainNodeData>> childrenMapById) {
        childrenMapById.get(singleRootNode.id).stream()
                .forEach(childNode ->
                        dfs(resultDomainTree, childNode, childrenMapById,
                        new DomainPath(singleRootNode), set(buildNodeSignature(singleRootNode))));
    }

    /**
     * To handle differently nodes with the same {@link Domain} but defined with
     * a <code>superclasse</code> and a <code>filter</code>.
     *
     * @param aNode
     * @return
     */
    private String buildNodeSignature(DomainNodeData aNode) {
        return "%s:%s".formatted(aNode.domain, aNode.targetClass);
    }

    /**
     * Recursive code for DFS (<i>Depth First Search</i>).
     *
     * @param resultDomainTree
     * @param currentNodechildrenMapById@param childrenMap
     * @param currentPath
     * @param alreadyVisitedInPath
     */
    private void dfs(DomainTree resultDomainTree, DomainNodeData currentNode, Map<String, List<DomainNodeData>> childrenMapById,
            DomainPath currentPath, Set<String> alreadyVisitedInPath) {
        final String loopDetectionSignature = buildNodeSignature(currentNode);
        // loop detection on nodes
        if (alreadyVisitedInPath.contains(loopDetectionSignature)) {
            String errMsg = "%s has loop on node with domain =< %s >, targetClass =< %s > (node id =< %s >)".formatted(LoggerHelper.buildLog(resultDomainTree),currentNode.domain, currentNode.targetClass,  currentNode.id);
            logger.error(errMsg);
            throw new DomainTreeException(errMsg);
        }

        currentPath.add(currentNode);
        alreadyVisitedInPath.add(loopDetectionSignature);

        List<DomainNodeData> children
                = childrenMapById.getOrDefault(currentNode.id, emptyList());

        if (children.isEmpty()) {
            resultDomainTree.addDomainPath(new DomainPath(currentPath));
        } else {
            for (DomainNodeData child : children) {
                dfs(
                        resultDomainTree,
                        child,
                        childrenMapById,
                        currentPath,
                        alreadyVisitedInPath
                );
            }
        }

        // backtracking
        currentPath.removeLast();
        alreadyVisitedInPath.remove(currentNode.id);
    }

    /**
     * Cut all paths till first <i>superclass</i> or same {@link Classe} as
     * given leaf.
     *
     * @param resultDomainTree
     * @param leafNodeClasseName
     */
    private void cutPathsOn(DomainTree resultDomainTree, String leafNodeClasseName) {
        logger.debug("%s - cutting [%s] paths, given leaf class =< %s >:%s".formatted(LoggerHelper.buildLog(resultDomainTree), resultDomainTree.getPaths().size(), leafNodeClasseName, buildLog(resultDomainTree.getRawPaths())));

        logger.debug("%s - fetching superclassees for leaf classe =< %s >".formatted(LoggerHelper.buildLog(resultDomainTree), leafNodeClasseName));
        Set<String> superclasses = fetchSuperclasses(leafNodeClasseName, resultDomainTree);
        logger.debug("%s - superclassees for leaf classe =< %s >: %s".formatted(LoggerHelper.buildLog(resultDomainTree), leafNodeClasseName, Joiner.on(",").join(superclasses)));

        int numOfPaths = resultDomainTree.getRawPathsSize();
        for (int curPathPos = 0; curPathPos < numOfPaths; curPathPos++) {
            DomainPath curPath = resultDomainTree.getRawPath(curPathPos);

            List<DomainNodeData> curCutPath = list();
            for (DomainNodeData curNodeData : curPath.pathData) {
                String curNodeClasse = curNodeData.targetClass;
                curCutPath.add(curNodeData);
                if (superclasses.contains(curNodeClasse)) {
                    // A superclass of leaf: point of cut reached
                    curPath.pathData = curCutPath;
                    break; // exit from for
                }
            }
            // Filter to apply to a Classe (e.g. Floor) Cards (because in relation with
            // child (e.g. Room) Cards, is given in input in the node where targetClass is
            // the firt (e.g. in domain "BuildingFloor"). DomainPathFinder
            // needs that filter on targetClass (Floor) when processing the child
            // node, so:
            // - store initial filter as asLeafFilter;
            // - move the initial filter from each node to the child nodes.
            // Move filter from parent node to child
            String curCqlFilter = "";
            String foundCqlFilter;
            for (DomainNodeData curDomainNodeData : curCutPath) {
                foundCqlFilter = curDomainNodeData.cqlFilter;
                curDomainNodeData.asLeafFilter = foundCqlFilter; // Used when this node is the leaf in the path to search for
                curDomainNodeData.cqlFilter = curCqlFilter;

                // Prepare for child node
                curCqlFilter = foundCqlFilter;
            }

            // Remove paths not ending with a superclass of input leaf
            if (!curCutPath.isEmpty()
                    && !superclasses.contains(curCutPath.get(curCutPath.size() - 1).targetClass)) {
                // Path not ending with a superclass of input leaf: skip it
                logger.debug("%s - removing path because superclass/class not recognized for leaf classe =< %s >: %s".formatted(LoggerHelper.buildLog(resultDomainTree), leafNodeClasseName, toClasseNames(curPath.pathData)));
                curPath.pathData = list();
            }
        }

        logger.debug("%s - remaining (cut) [%s] paths, given leaf class =< %s >:%s".formatted(LoggerHelper.buildLog(resultDomainTree), resultDomainTree.getPaths().size(), leafNodeClasseName, buildLog(resultDomainTree.getPaths())));
    }

    /**
     * Fetch all ancestors {@link Classe}, stopping when <code>Classe</code> is
     * reached.
     *
     * @param aClasseName
     * @return
     */
    private Set<String> fetchSuperclasses(String aClasseName, DomainTree resultDomainTree) {
        resultDomainTree.leafNodeClasseName = aClasseName;

        Set<String> result = set();
        result.add(aClasseName); // add itself
        fetchSuperclasses(result, aClasseName, resultDomainTree);

        return result;
    }

    /**
     * Recursive code to search superclasses.
     * @param result
     * @param aClasseName
     */
    private void fetchSuperclasses(Set<String> result, String aClasseName, DomainTree resultDomainTree) {
        if (!MODEL_BASE_CLASSE_NAME.equals(aClasseName)) { // base of classes reached
            Classe aClasse = classeSync.read(aClasseName, true); // not cached in DaoService, but called once because searching superclasses of a single leaf classe

            if (aClasse == null) {
                String errMsg = "%s couldn't find classe =< %s > searching superclasses for leaf classe =< %s >".formatted(LoggerHelper.buildLog(resultDomainTree), aClasseName, resultDomainTree.leafNodeClasseName);
                logger.error(errMsg);
                throw new DomainTreeException(errMsg);
            }

            List<String> ancestors = list(aClasse.getAncestors());
            ancestors.remove(MODEL_BASE_CLASSE_NAME);

            result.addAll(ancestors);
            ancestors.forEach(ancestorName -> {
                fetchSuperclasses(result, ancestorName, resultDomainTree);
            });
        }
    }

    private static String toClasseNames(List<DomainNodeData> roots) {
        return Joiner.on(",").join(roots.stream().map(DomainNodeData::getTargetClasseName).collect(toList()));
    }

    private String buildLog(List<DomainPath> domainPaths) {
        StringBuilder strBuf = new StringBuilder();
        domainPaths.forEach(path -> {
            strBuf.append("%s\t%s".formatted(System.lineSeparator(), LoggerHelper.buildLog(path)));
        });

        return strBuf.toString();
    }

}