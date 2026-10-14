/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import java.util.Map;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.SorterElementDirection;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.data.filter.beans.SorterElementImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;

public interface QueryBuilder extends CommonQueryBuilderMethods<QueryBuilder> {

    static final WhereOperator EQ = WhereOperator.EQ;
    static final WhereOperator NOTEQ = WhereOperator.NOTEQ;
    static final WhereOperator EQ_CASE_INSENSITIVE = WhereOperator.EQ_CASE_INSENSITIVE;

    RowNumberQueryBuilder selectRowNumber();

    QueryBuilder groupBy(String attr);

    QueryBuilder groupByExpr(String name, String expr);

    QueryBuilder selectCount();

    QueryBuilder from(String classe);

    QueryBuilder fromDomain(String domain);

    QueryBuilder fromFunction(String function);

    QueryBuilder from(Classe classe);

    QueryBuilder from(Class model);

    QueryBuilder from(Domain domain);

    QueryBuilder from(StoredFunction function);

    QueryBuilder includeHistory();

    QueryBuilder orderBy(CmdbSorter sort);

    QueryBuilder offset(@Nullable Long offset);

    QueryBuilder limit(@Nullable Long limit);

    QueryBuilder delete();

    QueryBuilder update(Map<String, Object> values);

    PreparedQuery build();

    default QueryBuilder orderBy(String property, SorterElementDirection direction) {
        return orderBy(new CmdbSorterImpl(new SorterElementImpl(property, direction)));
    }

    default QueryBuilder orderBy(String property1, SorterElementDirection direction1, String property2, SorterElementDirection direction2) {
        return orderBy(new CmdbSorterImpl(new SorterElementImpl(property1, direction1), new SorterElementImpl(property2, direction2)));
    }

    default QueryBuilder orderBy(String property) {
        return orderBy(property, SorterElementDirection.ASC);
    }

    default QueryBuilder orderBy(String property1, String property2) {
        return orderBy(property1, SorterElementDirection.ASC, property2, SorterElementDirection.ASC);
    }

    default QueryBuilder offset(@Nullable Integer offset) {
        return offset(toLongOrNull(offset));
    }

    default QueryBuilder limit(@Nullable Integer limit) {
        return limit(toLongOrNull(limit));
    }

    default QueryBuilder paginate(@Nullable Integer offset, @Nullable Integer limit) {
        return this.offset(convert(offset, Long.class)).limit(convert(limit, Long.class));
    }

    default QueryBuilder paginate(@Nullable Long offset, @Nullable Long limit) {
        return this.offset(offset).limit(limit);
    }

    default QueryBuilder withOptions(DaoQueryOptions queryOptions) {
        return this
                .where(queryOptions.getFilter())
                .orderBy(queryOptions.getSorter())
                .paginate(queryOptions.getOffset(), queryOptions.getLimit());
    }

    default QueryBuilder accept(Consumer<QueryBuilder> visitor) {
        visitor.accept(this);
        return this;
    }

    @Override
    default long getCount() {
        return build().getCount();
    }

}
