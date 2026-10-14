/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use edit CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.patch;

import org.cmdbuild.modeldiff.diff.CmModelNode;

/**
 * Removed data from a source.
 *
 * @author afelice
 *
 * @param <T> Model node
 * @param <U> Model class
 */
public class CmRemoveDelta<T extends CmModelNode<T, U>, U> extends AbstractCmDelta {

    public CmRemoveDelta(Class modelNodeClass, String distinguishingName, T sourceModelNode) {
        super(CmDeltaType.CM_DT_REMOVE, modelNodeClass, distinguishingName, sourceModelNode, null);
    }

    @Override
    public T getSourceModelNode() {
        return (T) super.getSourceModelNode();
    }    
    
    @Override
    public T getTargetModelNode() {
        throw new UnsupportedOperationException("can't get target node in a remove delta operation.");
    }

    @Override
    public String toString() {
        return "Remove{value=< %s >}".formatted(sourceModelNode.toString());
    }

}
