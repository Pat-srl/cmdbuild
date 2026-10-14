/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import java.util.List;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.core.q3.QueryBuilderOptions.QO_NO_ALIAS_MAPPING;
import static org.cmdbuild.dao.core.q3.QueryBuilderOptions.QO_NO_REFLOOKUP_JOIN;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.CmdbFilter;

public interface SuperclassQueryService {

    SuperclassQueryBuilderHelper queryFromSuperclass(Classe classe);

    SuperclassQueryBuilderHelper queryFromSuperclass(String classId);

    interface SuperclassQueryBuilderHelper extends BasicCommonQueryBuilderMethods<SuperclassQueryBuilderHelper> {

        SuperclassQueryBuilderHelper enableSmartSubclassFilterProcessing();

        SuperclassQueryBuilderHelper enableAllSubclassesProcessing();

        SuperclassQueryBuilderHelper withOptions(DaoQueryOptions options);

        SuperclassQueryBuilderHelper withBuilderOptions(QueryBuilderOptions... options);

        SuperclassSubclassQueryBuilderHelper withSubclass(String classId);

        SuperclassSubclassQueryBuilderHelper withSubclass(Classe classe);

        long count();

        @Nullable
        Long getRowNumber();

        PreparedQuery build();

        default SuperclassQueryBuilderHelper accept(Consumer<SuperclassQueryBuilderHelper> consumer) {
            consumer.accept(this);
            return this;
        }

        @Override
        default List<ResultRow> run() {
            return build().run();
        }

        default SuperclassQueryBuilderHelper withNoAliasMapping() {
            return withBuilderOptions(QO_NO_ALIAS_MAPPING);
        }

        default SuperclassQueryBuilderHelper withNoRefLookupJoin() {
            return withBuilderOptions(QO_NO_REFLOOKUP_JOIN);
        }

    }

    interface SuperclassSubclassQueryBuilderHelper extends BasicWhereMethods<SuperclassSubclassQueryBuilderHelper>, SelectMatchFilterBuilder<SuperclassSubclassQueryBuilderHelper> {

        SuperclassSubclassQueryBuilderHelper where(CmdbFilter filter);

        SuperclassQueryBuilderHelper then();

        default SuperclassSubclassQueryBuilderHelper accept(Consumer<SuperclassSubclassQueryBuilderHelper> consumer) {
            consumer.accept(this);
            return this;
        }

    }
}
