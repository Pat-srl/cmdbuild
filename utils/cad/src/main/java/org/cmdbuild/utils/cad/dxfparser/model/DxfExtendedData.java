/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;

public interface DxfExtendedData {

    Map<String, List<String>> getXdata();

    default boolean isEmpty() {
        return getXdata().isEmpty() || getXdata().values().stream().allMatch(List::isEmpty);
    }

    default boolean isNotEmpty() {
        return !isEmpty();
    }

    @Nullable
    default String getSingleValueOrNull(String application) {
        List<String> list = getXdata().get(application);
        return isNullOrEmpty(list) ? null : getOnlyElement(list);
    }
}
