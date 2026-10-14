package org.cmdbuild.api.fluent;

import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.annotation.Nullable;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Set;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public interface QueryClass extends Card {

    QueryClass withCode(String value);

    QueryClass withDescription(String value);

    QueryClass with(String name, Object value);

    QueryClass withAttribute(String name, Object value);

    QueryClass limitAttributes(String... names);

    QueryClass withFilter(String cmdbFilter);

    Set<String> getRequestedAttributes();

    CmdbFilter getFilter();

    List<Card> fetch();

    default List<Card> getCards() {
        return fetch();
    }

    default Card getCard() {
        return getOnlyElement(getCards());
    }

    @Nullable
    default Card getCardOrNull() {
        return getOnlyElement(getCards(), null);
    }

    default QueryClass withFilter(Object cmdbFilter) {
        return withFilter(cmdbFilter instanceof String strFilter ? strFilter : toJson(firstNotNull(cmdbFilter, emptyMap())));
    }

    default QueryClass where(Object cmdbFilter) {
        return withFilter(cmdbFilter);
    }

}
