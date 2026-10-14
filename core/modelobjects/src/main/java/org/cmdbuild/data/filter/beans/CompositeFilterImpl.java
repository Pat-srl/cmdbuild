/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.Collection;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CompositeFilter;
import static org.cmdbuild.data.filter.CompositeFilter.CompositeFilterMode.CFM_AND;
import static org.cmdbuild.data.filter.CompositeFilter.CompositeFilterMode.CFM_NOT;
import static org.cmdbuild.data.filter.CompositeFilter.CompositeFilterMode.CFM_OR;
import static org.cmdbuild.data.filter.beans.CmdbFilterImpl.noopFilter;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CompositeFilterImpl implements CompositeFilter {

    private final CompositeFilterMode mode;
    private final List<CmdbFilter> elements;

    public CompositeFilterImpl(CompositeFilterMode mode, Collection<CmdbFilter> elements) {
        this.mode = checkNotNull(mode);
        this.elements = ImmutableList.copyOf(checkNotEmpty(elements));
        if (equal(mode, CFM_NOT)) {
            checkArgument(elements.size() == 1);
        }
    }

    public static CmdbFilter and(CmdbFilter... filters) {
        return and(list(filters));
    }

    public static CmdbFilter and(Collection<CmdbFilter> filters) {
        filters = list(filters).stream().filter(CmdbFilter::hasFilter).collect(toList());
        if (filters.isEmpty()) {
            return noopFilter();
        } else if (filters.size() == 1) {
            return getOnlyElement(filters);
        } else {
            return CmdbFilterImpl.builder().withCompositeFilter(new CompositeFilterImpl(CFM_AND, filters)).build();
        }
    }

    public static CmdbFilter or(CmdbFilter... filters) {
        return or(list(filters));
    }

    public static CmdbFilter or(Collection<CmdbFilter> filters) {
        if (filters.stream().anyMatch(CmdbFilter::isNoop)) {
            return noopFilter();
        } else if (filters.size() == 1) {
            return getOnlyElement(filters);
        } else {
            return CmdbFilterImpl.builder().withCompositeFilter(new CompositeFilterImpl(CFM_OR, filters)).build();
        }
    }

    public static CmdbFilter not(CmdbFilter filter) {
        return CmdbFilterImpl.builder().withCompositeFilter(new CompositeFilterImpl(CFM_NOT, singletonList(filter))).build();
    }

    @Override
    public CompositeFilterMode getMode() {
        return mode;
    }

    @Override
    public List<CmdbFilter> getElements() {
        return elements;
    }

    @Override
    public CompositeFilter mapElements(Function<CmdbFilter, CmdbFilter> mapper) {
        return new CompositeFilterImpl(mode, elements.stream().map(mapper).collect(toImmutableList()));
    }

    public CmdbFilter toCmdbFilters() {
        return CmdbFilterImpl.builder().withCompositeFilter(this).build();
    }
}
