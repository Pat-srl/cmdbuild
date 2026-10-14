/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.google.common.collect.MapDifference;

/**
 * Mimic a synthesized {@link ValueDifference}, will contain Json rehydrated
 * content.
 *
 * @author afelice
 * @param <V>
 */
public class CmValueDifference<V> implements MapDifference.ValueDifference<V> {

    /**
     * Mimic a synthesized {@link ValueDifference} where difference * was
     * programmatically determined as equal (link a <code>NullNode</code> and
     * it's equivalent <ode>"null"</code>).
     */
    public final static CmValueDifference DUMMY_DIFFERENCE = new CmValueDifference(null, null){};
    
    private final V left;
    private final V right;

    public CmValueDifference(V left, V right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public V leftValue() {
        return left;
    }

    @Override
    public V rightValue() {
        return right;
    }
}
