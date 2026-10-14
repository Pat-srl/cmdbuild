/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import com.google.common.base.Predicates;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.utils.lang.CmMapUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.emptyList;
import static org.cmdbuild.dao.utils.FulltextMatcherImpl.fulltextMatcher;

public interface ModelSearch {

    List<Map<String, Object>> search(CmdbFilter filter);
}

// TODO this name has to be changed
class TypedSearchHelperNew<T, O> {

    private final Function<T, Collection<O>> itemsHelper;
    private final Function<T, CmMapUtils.FluentMap<String, Object>> mapper;
    private final Function<O, Map<String, Object>> itemMapper;
    private final Function<T, Collection<String>> entryFiltrables;
    private final Function<O, Collection<String>> itemFiltrables;
    private final Predicate<CmMapUtils.FluentMap<String, Object>> recordFilter;
    private final Predicate<Collection<String>> fulltextMatcher;

    public TypedSearchHelperNew(Function<T, Collection<String>> entryFiltrables, Function<T, CmMapUtils.FluentMap<String, Object>> mapper, @Nullable Function<T, Collection<O>> helper, @Nullable Function<O, Collection<String>> itemFiltrables, @Nullable Function<O, Map<String, Object>> itemMapper, CmdbFilter filter) {
        this.entryFiltrables = checkNotNull(entryFiltrables);
        this.mapper = checkNotNull(mapper);
        this.itemsHelper = helper;
        this.itemMapper = itemMapper;
        this.itemFiltrables = itemFiltrables;
        recordFilter = filter.hasAttributeFilter() ? AttributeFilterProcessor.builder().withKeyToValueFunction(AttributeFilterProcessor.MapKeyToValueFunction.INSTANCE).withFilter(filter.getAttributeFilter()).build()::match : Predicates.alwaysTrue();
        fulltextMatcher = filter.hasFulltextFilter() ? fulltextMatcher(filter.getFulltextFilter().getQuery())::matchesAny : Predicates.alwaysTrue();
    }

    public List<Map<String, Object>> search(Stream<T> source) {
        return source.map(FilterHelper::new).filter(FilterHelper::matches).map(FilterHelper::map).filter(recordFilter).collect(toImmutableList());
    }

    private class FilterHelper {

        private final T entry;
        private final List<O> items;
        private final boolean matches;

        public FilterHelper(T entry) {
            this.entry = checkNotNull(entry);
            items = itemsHelper == null ? emptyList() : itemsHelper.apply(entry).stream().filter(itemFiltrables.andThen(fulltextMatcher::test)::apply).collect(toImmutableList());
            matches = !items.isEmpty() || entryFiltrables.andThen(fulltextMatcher::test).apply(entry);
        }

        public boolean matches() {
            return matches;
        }

        public CmMapUtils.FluentMap<String, Object> map() {
            return mapper.apply(entry).accept(m -> {
                if (itemsHelper != null) {
                    m.put("items", items.stream().map(itemMapper).collect(toImmutableList()));
                }
            });
        }

    }

}
