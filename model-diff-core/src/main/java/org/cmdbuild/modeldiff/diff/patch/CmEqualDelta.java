/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.patch;

import org.cmdbuild.modeldiff.diff.CmModelNode;

/**
 *
 * @author afelice
 *
 * @param <T> Model node
 * @param <U> Model class
 */
public class CmEqualDelta<T extends CmModelNode<T, U>, U> extends AbstractCmDelta {

    public CmEqualDelta(Class modelNodeClass, String distinguishingName, T sourceModelNode) {
        super(CmDeltaType.CM_DT_EQUAL, modelNodeClass, distinguishingName, sourceModelNode, sourceModelNode);
    }
    
    @Override
    public T getSourceModelNode() {
        return (T) super.getSourceModelNode();
    }       
    
    @Override
    public T getTargetModelNode() {
        return (T) super.getTargetModelNode();
    }   
    
    @Override
    public String toString() {
        return "Equal{value=< %s >}".formatted(sourceModelNode.toString());
    }

}
