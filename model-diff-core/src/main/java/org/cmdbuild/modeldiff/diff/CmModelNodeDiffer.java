/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff;

import java.util.List;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;

/**
 * Visitor for model <b>nodes </b> (abstraction on CMDBuild model to aggregate ù
 * related information).
 *
 * @author afelice
 * @param <T> Model node
 * @param <U> Model class
 */
public interface CmModelNodeDiffer<T extends CmModelNode<T, U>, U> {

    <T extends CmModelNode<T, U>, U> CmDeltaList diff(T left, T right);

    /**
     *
     * @param <Z> Model node
     * @param left
     * @param right
     * @return
     */
    <Z extends CmModelNode> CmDeltaList diffComposed(List<Z> left, List<Z> right);

    Class<T> getTarget();
}
