package org.cmdbuild.dao.postgres.q3;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Set;
import org.cmdbuild.dao.core.q3.QueryBuilderOptions;

public interface QueryBuilderOptionsCommons {

    Set<QueryBuilderOptions> getBuilderOptions();

    default boolean enableRefLookupJoin() {
        return !hasOption(QueryBuilderOptions.QO_NO_REFLOOKUP_JOIN);
    }

    default boolean enableAliasMapping() {
        return !hasOption(QueryBuilderOptions.QO_NO_ALIAS_MAPPING);
    }

    default boolean hasOption(QueryBuilderOptions option) {
        return getBuilderOptions().contains(checkNotNull(option));
    }

}
