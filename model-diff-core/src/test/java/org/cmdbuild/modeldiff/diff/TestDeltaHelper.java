/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff;

import org.cmdbuild.modeldiff.diff.CmModelNode;
import org.cmdbuild.modeldiff.diff.patch.AbstractCmDelta;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author afelice
 */
public class TestDeltaHelper {
    
    static public void checkEqual(int aPos, Class<? extends CmModelNode> expNodeClass, String expDistinguishingName, Object expModelObj, CmDeltaList actualDiff) {
        AbstractCmDelta delta = actualDiff.get(aPos);
        assertEquals(expNodeClass, delta.getModelNodeClass());
        assertEquals(expDistinguishingName, delta.getDistinguishingName());
        assertTrue(delta.isEqual());
        assertEquals(expModelObj, delta.getSourceModelNode().getModelObj());
    }

    static public void checkRemoved(int aPos, Class<? extends CmModelNode> expNodeClass, String expDistinguishingName, Object expModelObj, CmDeltaList actualDiff) {
        AbstractCmDelta delta = actualDiff.get(aPos);
        assertEquals(expNodeClass, delta.getModelNodeClass());
        assertEquals(expDistinguishingName, delta.getDistinguishingName());
        assertTrue(delta.isRemove());
        assertEquals(expModelObj, delta.getSourceModelNode().getModelObj());
    }

    static public void checkInserted(int aPos, Class<? extends CmModelNode> expNodeClass, String expDistinguishingName, Object expModelObj, CmDeltaList actualDiff) {
        AbstractCmDelta delta = actualDiff.get(aPos);
        assertEquals(expNodeClass, delta.getModelNodeClass());
        assertEquals(expDistinguishingName, delta.getDistinguishingName());
        assertTrue(delta.isInsert());
        assertEquals(expModelObj, delta.getTargetModelNode().getModelObj());
    }

    static public void checkChanged(int aPos, Class<? extends CmModelNode> expNodeClass, String expDistinguishingName, Object expSourceModelObj, Object expTargetModelObj, CmDeltaList actualDiff) {
        AbstractCmDelta delta = actualDiff.get(aPos);
        assertEquals(expNodeClass, delta.getModelNodeClass());
        assertEquals(expDistinguishingName, delta.getDistinguishingName());
        assertTrue(delta.isChange());
        assertEquals(expSourceModelObj, delta.getSourceModelNode().getModelObj());
        assertEquals(expTargetModelObj, delta.getTargetModelNode().getModelObj());
    }    
}
