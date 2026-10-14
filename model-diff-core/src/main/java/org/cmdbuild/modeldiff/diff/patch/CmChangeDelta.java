/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, CMDBuild according to the license
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
public class CmChangeDelta<T extends CmModelNode<T, U>, U> extends AbstractCmDelta {

    public CmChangeDelta(Class modelNodeClass, String distinguishingName, T sourceModelNode, T targetModelNode) {
        super(CmDeltaType.CM_DT_CHANGE, modelNodeClass, distinguishingName, sourceModelNode, targetModelNode);
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
        return "Change{from value=< %s > to value =< %s >}".formatted(sourceModelNode.toString(), targetModelNode.toString());
    }

    /**
     * Implement your
     * @return 
     */
    public boolean isSomethingChanged() {
        return true;
    }    
    
}
