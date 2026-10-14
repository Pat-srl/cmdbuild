/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;

public interface EcqlId {

    EcqlSource getSource();

    List<String> getId();

    default String getOnlyId() {
        return getOnlyElement(getId());
    }

    default boolean hasSource(EcqlSource ecqlSource) {
        return equal(getSource(), ecqlSource);
    }
}
