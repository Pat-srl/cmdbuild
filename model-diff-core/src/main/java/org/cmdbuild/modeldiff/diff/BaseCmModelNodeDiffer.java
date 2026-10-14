/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff;

import java.util.List;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;

/**
 * Visitor design pattern
 *
 * @author afelice
 */
public interface BaseCmModelNodeDiffer {

    <T extends CmModelNode<T, U>, U> CmDeltaList diff(T curLeft, T curRight);

    <T extends CmModelNode> CmDeltaList diffComposed(List<T> left, List<T> right);

    <T extends CmModelNode<T, U>, U> CmDeltaList equal(String distinguishingName, T modelNode);

    <T extends CmModelNode<T, U>, U> CmDeltaList inserted(String distinguishingName, T modelNode);

    <T extends CmModelNode<T, U>, U> CmDeltaList removed(String distinguishingName, T modelNode);

    <T extends CmModelNode<T, U>, U> CmDeltaList changed(String distinguishingName, T leftModelNode, T rightModelNode);
}
