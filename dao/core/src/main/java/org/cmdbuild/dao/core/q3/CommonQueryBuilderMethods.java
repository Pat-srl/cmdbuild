/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import java.util.Collection;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.dao.core.q3.DaoService.ATTRS_ALL;

public interface CommonQueryBuilderMethods<T extends CommonQueryBuilderMethods> extends SelectMatchFilterBuilder<T>, BasicCommonQueryBuilderMethods<T> {

    T select(Collection<String> attrs);

    T whereExpr(String expr, Object... params);

    T whereExpr(String expr, Collection params);

    T where(CmdbFilter filter);

    T where(PreparedQuery query);

    JoinQueryBuilder join(EntryType entryType);

    JoinQueryBuilder join(String classId);

    JoinQueryBuilder joinDomain(String domainId);

    default T select(String... attrs) {
        return select(list(attrs));
    }

    default T selectAll() {
        return select(ATTRS_ALL);
    }

    default T where(QueryBuilder query) {
        return where(query.build());
    }

}
