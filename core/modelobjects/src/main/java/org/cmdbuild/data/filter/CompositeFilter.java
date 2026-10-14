/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import java.util.function.Function;

public interface CompositeFilter {

    CompositeFilterMode getMode();

    List<CmdbFilter> getElements();

    CompositeFilter mapElements(Function<CmdbFilter, CmdbFilter> mapper);

    default CmdbFilter getElement() {
        return getOnlyElement(getElements());
    }

    enum CompositeFilterMode {
        CFM_AND, CFM_OR, CFM_NOT
    }
}
