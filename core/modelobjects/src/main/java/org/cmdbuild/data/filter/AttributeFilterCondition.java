/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;

public interface AttributeFilterCondition {

    AttributeFilterConditionOperator getOperator();

    String getKey();

    List<String> getValues();

    public String getClassName();

    boolean hasClassName();

    default boolean hasSingleValue() {
        return getValues().size() == 1;
    }

    default String getSingleValue() {
        return getOnlyElement(getValues());
    }

}
