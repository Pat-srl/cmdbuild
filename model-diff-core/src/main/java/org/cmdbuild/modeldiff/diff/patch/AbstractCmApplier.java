/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.patch;

import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.modeldiff.diff.CmModelNode;

/**
 * Applies a delta patch to the CMDBuild system.
 *
 * <p>
 * <b>Note</b>: implementation of <i>appliers</i> may have notification
 * mechanisms in <i>change</i>, <i>insert</i> and/or <i>remove</i>
 * implementation.
 *
 * @author afelice
 *
 * @param <T> Model node
 * @param <U> Model class
 */
public abstract class AbstractCmApplier<T extends CmModelNode<T, U>, U> {

    public List<U> applyAll(List<AbstractCmDelta<T, U>> deltas) {
        return deltas.stream().map(this::apply).collect(toList());
    }

    public U apply(AbstractCmDelta<T, U> delta) {
        // I don't know why can't directly use oveloading of a protected innerApply() method here...
        if (delta.isEqual()) {
            return applyEqual((CmEqualDelta<T, U>) delta);
        } else if (delta.isChange()) {
            return applyChange((CmChangeDelta<T, U>) delta);
        } else if (delta.isInsert()) {
            return applyInsert((CmInsertDelta<T, U>) delta);
        } else if (delta.isRemove()) {
            return applyRemove((CmRemoveDelta<T, U>) delta);
        }

        throw new UnsupportedOperationException("unsupported CmDelta class =< %s >, deltaType".formatted(delta.getClass(), delta.deltaType));
    }

    /**
     *
     * @param delta
     * @return source model object.
     */
    protected U applyEqual(CmEqualDelta<T, U> delta) {
        // Nothing to do
        return (U) delta.getSourceModelNode().getModelObj();
    }

    /**
     * In the implementation, (eventually) add notification mechanisms.
     *
     * @param delta
     * @return updated model object.
     */
    protected abstract U applyChange(CmChangeDelta<T, U> delta);

    /**
     * In the implementation, (eventually) add notification mechanisms.
     *
     * @param delta
     * @return inserted model object.
     */
    protected abstract U applyInsert(CmInsertDelta<T, U> delta);

    /**
     * In the implementation, (eventually) add notification mechanisms.
     *
     * @param delta
     * @return removed model object.
     */
    protected abstract U applyRemove(CmRemoveDelta<T, U> delta);
}
