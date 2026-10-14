/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;

public class CmFilterProcessingUtils {

    public static Predicate mapFilter(CmdbFilter filter) {
        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (filter.hasAttributeFilter()) {
            AttributeFilterProcessor helper = AttributeFilterProcessor.builder().withFilter(filter.getAttributeFilter()).withKeyToValueFunction(AttributeFilterProcessor.MapKeyToValueFunction.INSTANCE).build();
            return helper::match;
        } else {
            return Predicates.alwaysTrue();
        }
    }

}
