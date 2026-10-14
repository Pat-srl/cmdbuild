/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.q3.stats;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class DaoStatsQueryOptionsImpl implements DaoStatsQueryOptions {

    private final static DaoStatsQueryOptions EMPTY_OPTIONS = DaoStatsQueryOptionsImpl.builder().build();

    private final List<AggregateQuery> aggregateQueries;

    private DaoStatsQueryOptionsImpl(DaoStatsQueryOptionsImplBuilder builder) {
        this.aggregateQueries = ImmutableList.copyOf(builder.aggregateQueries);
    }

    @Override
    public List<AggregateQuery> getAggregateQueries() {
        return aggregateQueries;
    }

    public static DaoStatsQueryOptionsImplBuilder builder() {
        return new DaoStatsQueryOptionsImplBuilder();
    }

    public static DaoStatsQueryOptionsImplBuilder copyOf(DaoStatsQueryOptions source) {
        return new DaoStatsQueryOptionsImplBuilder()
                .withAggregateQueries(source.getAggregateQueries());
    }

    public static DaoStatsQueryOptions emptyOptions() {
        return EMPTY_OPTIONS;
    }

    private static class AggregateQueryImpl implements AggregateQuery {

        final String attribute;
        final AggregateOperation operation;

        public AggregateQueryImpl(String attribute, AggregateOperation operation) {
            this.attribute = checkNotBlank(attribute);
            this.operation = checkNotNull(operation);
        }

        @Override
        public String getAttribute() {
            return attribute;
        }

        @Override
        public AggregateOperation getOperation() {
            return operation;
        }

    }

    public static class DaoStatsQueryOptionsImplBuilder implements Builder<DaoStatsQueryOptionsImpl, DaoStatsQueryOptionsImplBuilder> {

        private final List<AggregateQuery> aggregateQueries = list();

        public DaoStatsQueryOptionsImplBuilder withAggregateQueries(List<AggregateQuery> aggregateQueries) {
            this.aggregateQueries.clear();
            this.aggregateQueries.addAll(aggregateQueries);
            return this;
        }

        public DaoStatsQueryOptionsImplBuilder withAggregateQuery(String attribute, AggregateOperation operation) {
            aggregateQueries.add(new AggregateQueryImpl(attribute, operation));
            return this;
        }

        @Override
        public DaoStatsQueryOptionsImpl build() {
            return new DaoStatsQueryOptionsImpl(this);
        }

    }
}
