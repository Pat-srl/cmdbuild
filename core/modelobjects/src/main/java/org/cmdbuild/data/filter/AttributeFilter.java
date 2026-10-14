/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import static com.google.common.base.Objects.equal;
import com.google.common.collect.Iterables;
import java.util.List;

public interface AttributeFilter {

    AttributeFilterMode getMode();

    List<AttributeFilter> getElements();

    AttributeFilterCondition getCondition();

    CmdbFilter toCmdbFilters();

    default boolean isSimple() {
        return AttributeFilterMode.SIMPLE.equals(getMode());
    }

    default AttributeFilter getOnlyElement() {
        return Iterables.getOnlyElement(getElements());
    }

    default boolean hasOnlyElement() {
        return getElements().size() == 1;
    }

    default boolean isTrue() {
        return isSimple() && equal(getCondition().getOperator(), AttributeFilterConditionOperator.TRUE);
    }

    enum AttributeFilterMode {
        SIMPLE, AND, OR, NOT
    }

}
