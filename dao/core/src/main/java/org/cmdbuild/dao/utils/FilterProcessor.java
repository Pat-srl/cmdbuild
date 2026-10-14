package org.cmdbuild.dao.utils;

import com.google.common.base.Predicates;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.data.filter.FilterType.ATTRIBUTE;
import static org.cmdbuild.data.filter.FilterType.FULLTEXT;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class FilterProcessor {

    public static <T> Predicate<T> predicateFromFilter(CmdbFilter filter) {
        return predicateFromFilter(filter, (BiFunction) DefaultBeanKeyToValueFunction.INSTANCE);
    }

    public static <T> Predicate<T> predicateFromFilter(CmdbFilter filter, BiFunction<String, T, Object> keyToValueFunction) {
        if (filter.isNoop()) {
            return Predicates.alwaysTrue();
        } else {
            filter.checkHasOnlySupportedFilterTypes(ATTRIBUTE, FULLTEXT);
            List<com.google.common.base.Predicate<T>> predicates = list();
            if (filter.hasAttributeFilter()) {
                predicates.add(AttributeFilterProcessor.<T>builder().withFilter(filter.getAttributeFilter()).withKeyToValueFunction(keyToValueFunction).build()::match);
            }
            if (filter.hasFulltextFilter()) {
                predicates.add(FulltextFilterProcessor.<T>build(filter.getFulltextFilter()).withKeyToValueFunction(keyToValueFunction)::match);
            }
            return Predicates.and(predicates);
        }
    }

}
