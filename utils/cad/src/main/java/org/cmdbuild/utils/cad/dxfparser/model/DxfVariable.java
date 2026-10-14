/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.Map;

public interface DxfVariable {

    String getKey();

    Map<Integer, DxfValue> getValues();

    default boolean hasStringValue() {
        return getValues().size() == 1;
    }

    default String getStringValue() {
        return getOnlyElement(getValues().values()).getValue();
    }
}
