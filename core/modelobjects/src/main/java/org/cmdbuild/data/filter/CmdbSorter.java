/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import static com.google.common.base.Objects.equal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface CmdbSorter {

    List<SorterElement> getElements();

    CmdbSorter mapAttributeNames(Map<String, String> mapping);

    CmdbSorter thenSortBy(CmdbSorter other);

    CmdbSorter thenSortBy(String key, SorterElementDirection direction);

    CmdbSorter mapNames(Function<String, String> mapper);

    default int count() {
        return getElements().size();
    }

    default boolean isNoop() {
        return count() == 0;
    }

    default boolean isSortBy(String key, SorterElementDirection direction) {
        return count() == 1 && equal(key, getElements().get(0).getProperty()) && equal(direction, getElements().get(0).getDirection());
    }

    default boolean hasAttr(String attr) {
        checkNotBlank(attr);
        return getElements().stream().anyMatch(e -> equal(e.getProperty(), attr));
    }
}
