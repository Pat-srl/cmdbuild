/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.SorterElement;
import org.cmdbuild.data.filter.SorterElementDirection;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CmdbSorterImpl implements CmdbSorter {

    private final static CmdbSorter NOOP = new CmdbSorterImpl();

    private final List<SorterElement> elements;

    public CmdbSorterImpl(SorterElement... elements) {
        this(list(elements));
    }

    public CmdbSorterImpl(List<SorterElement> elements) {
        this.elements = ImmutableList.copyOf(checkNotNull(elements));
    }

    @Override
    public List<SorterElement> getElements() {
        return elements;
    }

    @Override
    public CmdbSorter mapAttributeNames(Map<String, String> mapping) {
        return new CmdbSorterImpl(elements.stream().map((e) -> new SorterElementImpl(firstNonNull(mapping.get(e.getProperty()), e.getProperty()), e.getDirection())).collect(toImmutableList()));
    }

    @Override
    public CmdbSorter thenSortBy(CmdbSorter other) {
        return new CmdbSorterImpl(list(elements).with(other.getElements()));
    }

    @Override
    public CmdbSorter thenSortBy(String key, SorterElementDirection direction) {
        return thenSortBy(sorter(key, direction));
    }

    @Override
    public CmdbSorter mapNames(Function<String, String> mapper) {
        return new CmdbSorterImpl(list(elements).map(e -> new SorterElementImpl(mapper.apply(e.getProperty()), e.getDirection())));
    }

    public static CmdbSorter noopSorter() {
        return NOOP;
    }

    public static CmdbSorter sorter(String key, SorterElementDirection direction) {
        return builder().sortBy(key, direction).build();
    }

    public static CmdbSorterImplBuilder builder() {
        return new CmdbSorterImplBuilder();
    }

    @Override
    public String toString() {
        return "CmdbSorter{" + elements + '}';
    }

    public static class CmdbSorterImplBuilder implements Builder<CmdbSorter, CmdbSorterImplBuilder> {

        private final List<SorterElement> elements = list();

        public CmdbSorterImplBuilder sortBy(String property, SorterElementDirection direction) {
            elements.add(new SorterElementImpl(property, direction));
            return this;
        }

        @Override
        public CmdbSorter build() {
            return new CmdbSorterImpl(elements);
        }

    }
}
