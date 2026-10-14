/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.dao.postgres.q3.beans.MatchFilter;
import org.cmdbuild.dao.postgres.q3.beans.QueryMode;
import org.cmdbuild.dao.postgres.q3.beans.SelectArg;
import java.util.List;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.postgres.q3.beans.QueryMode.QM_COUNT;
import org.cmdbuild.dao.postgres.q3.beans.WhereArg;
import org.cmdbuild.data.filter.CmdbFilter;

public interface SuperclassQueryBuilderService {

    PreparedQuery buildQuery(SuperclassQuery query);

    interface SuperclassQuery extends QueryBuilderOptionsCommons {

        QueryMode getQueryMode();

        Classe getSuperclass();

        DaoQueryOptions getOptions();

        List<SuperclassSubclassQuery> getSubclassQueries();

        List<SelectArg> getCustomSelectArgs();

        List<WhereArg> getWhereArgs();

        boolean processAllSubclasses();

        default boolean isCount() {
            return equal(getQueryMode(), QM_COUNT);
        }
    }

    interface SuperclassSubclassQuery {

        Classe getSubclass();

        CmdbFilter getFilter();

        List<MatchFilter> getMatchFilters();

        List<WhereArg> getWhereArgs();

        default boolean hasFiltersOrWhereArgs() {
            return !getFilter().isNoop() || !getMatchFilters().isEmpty() || !getWhereArgs().isEmpty();
        }

    }

}
