/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.patch;

import org.cmdbuild.modeldiff.diff.CmModelNode;

/**
 * Inserted data to obtain a target.
 * 
 * @author afelice
 *
 * @param <T> Model node
 * @param <U> Model class
 */
public class CmInsertDelta<T extends CmModelNode<T, U>, U> extends AbstractCmDelta {

    public CmInsertDelta(Class modelNodeClass, String distinguishingName, T targetModelNode) {
        super(CmDeltaType.CM_DT_INSERT, modelNodeClass, distinguishingName, null, targetModelNode);
    }

    @Override
    public T getSourceModelNode() {
        throw new UnsupportedOperationException("can't get source node in a insert delta operation");
    }

    @Override
    public T getTargetModelNode() {
        return (T) super.getTargetModelNode();
    }       
    
    @Override
    public String toString() {
        return "Insert{value=< %s >}".formatted(targetModelNode.toString());
    }

}
