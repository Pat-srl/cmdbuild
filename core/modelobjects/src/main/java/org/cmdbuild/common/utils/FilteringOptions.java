/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.common.utils;

import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;

/**
 * Wrap interface for DaoQueryOptions, which requires dao-commons dependency
 *
 * <b>Note</b>: if you need <code>expandFulltextFilter()</code> method, you have
 * to use {@link org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions}
 *
 * @author afelice
 */
public interface FilteringOptions extends PagingInfo {

    Set<String> getAttrs();

    CmdbFilter getFilter();

    boolean getGoToPage();

    boolean getOnlyGridAttrs();

    long getPositionOf();

    CmdbSorter getSorter();

    boolean hasPositionOf();

    FilteringOptions mapAttrNames(Map<String, String> map);

    FilteringOptions withOffset(@Nullable Long offset);

    FilteringOptions withoutPaging();

    default boolean hasAttrs() {
        return !getAttrs().isEmpty();
    }

    default boolean isPaged() {
        return PagedElements.isPaged(getOffset(), getLimit());
    }

    default boolean isPagedAndHasFullPage(int cardCount) {
        return isPaged() && (!hasLimit() || getLimit() == cardCount);
    }

    default boolean hasOffset() {
        return PagedElements.hasOffset(getOffset());
    }

    default boolean hasLimit() {
        return PagedElements.hasLimit(getLimit());
    }

}
