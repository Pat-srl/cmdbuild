/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cmdbuild.modeldiff.diff.patch.CmDeltaList;

/**
 * Base of nodes hierarchy.
 *
 * <p>
 * The <i>Visitable</i>.
 *
 * @author afelice
 * @param <T> Model node
 * @param <U> Model class
 */
public interface CmModelNode<T extends CmModelNode<T, U>, U> {

    String getDistinguishingName();

    void overwriteDistinguishingName(String fakeDistinguishingName);

    CmDeltaList calculateDiff(CmDifferRepository differRepository, T rightModelNode);

    @JsonIgnore
    U getModelObj();

}
