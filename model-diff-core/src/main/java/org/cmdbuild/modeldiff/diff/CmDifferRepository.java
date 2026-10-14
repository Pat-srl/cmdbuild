/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import java.util.Map;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Repository for differs, by class of differ, used in <i>visitor pattern</i>.
 *
 * @author afelice
 */
public class CmDifferRepository {

    private final Map<Class, CmModelNodeDiffer> visitors = map();

    public CmDeltaList diff(CmModelNode leftNode, CmModelNode rightNode) {
        CmModelNodeDiffer visitor = get(leftNode.getClass());
        return visitor.diff(leftNode, rightNode);
    }

    public CmDeltaList diffComposed(List<? extends CmModelNode> leftNodes, List<? extends CmModelNode> rightNodes) {
        if (leftNodes.isEmpty() && rightNodes.isEmpty()) {
            return new CmDeltaList();
        }

        CmModelNodeDiffer visitor = null;
        if (!leftNodes.isEmpty()) {
            visitor = get(leftNodes.get(0).getClass());
        } else if (!rightNodes.isEmpty()) {
            visitor = get(rightNodes.get(0).getClass());
        }

        return visitor.diffComposed(leftNodes, rightNodes);
    }

    public <V extends CmModelNodeDiffer> void add(V visitor) {
        visitors.put(visitor.getTarget(), visitor);
    }

    public <V extends CmModelNodeDiffer> V get(Class<?> visitedClass) {
        checkArgument(visitors.containsKey(visitedClass), "couldn't find a visitor for class %s".formatted(visitedClass));
        return (V) visitors.get(visitedClass);
    }
}
