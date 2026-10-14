/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.domain.tree;

import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.services.serialization.domain.tree.DomainTreeData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * Builds the ordered list of paths from a <i>root node</i> to a <i>destination
 * node</i>.
 *
 * @author afelice
 */

public class DomainTree {

    final String id;
    final String name;
    final String description;
    String rootNodeClasseName;
    String leafNodeClasseName;

    /**
     * Paths from a <i>root node</i> to a (compatible) <i>leaf node</i>.
     */
    private List<DomainPath> domainPaths = list();

    public DomainTree(DomainTreeData domainTreeData) {
        this.name = domainTreeData.name;
        this.description = domainTreeData.description;
        this.id = domainTreeData.id;
    }

    void addDomainPath(DomainPath domainPath) {
        domainPaths.add(domainPath);
    }

    boolean hasPaths() {
        return !domainPaths.isEmpty() && domainPaths.stream().filter(p -> !p.isEmpty()).findAny().isPresent();
    }

    /**
     *
     * @return not empty paths.
     */
    List<DomainPath> getPaths() {
        return domainPaths.stream().filter(p -> !p.isEmpty()).collect(toList());
    }

    int getRawPathsSize() {
        return domainPaths.size();
    }

    DomainPath getRawPath(int curPos) {
        return domainPaths.get(curPos);
    }

    List<DomainPath> getRawPaths() {
        return domainPaths;
    }

}
