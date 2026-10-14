/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.q3.stats;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class StatsQueryResponseImpl implements StatsQueryResponse {

    private final List<AggregateResult> aggregateResults;

    private StatsQueryResponseImpl(StatsQueryResponseImplBuilder builder) {
        this.aggregateResults = checkNotNull(builder.aggregateResults);
    }

    @Override
    public List<AggregateResult> getAggregateResults() {
        return aggregateResults;
    }

    public static StatsQueryResponseImplBuilder builder() {
        return new StatsQueryResponseImplBuilder();
    }

    public static StatsQueryResponseImplBuilder copyOf(StatsQueryResponse source) {
        return new StatsQueryResponseImplBuilder()
                .withAggregateResults(source.getAggregateResults());
    }

    private static class AggregateResultImpl<T> implements AggregateResult<T> {

        private final T result;
        private final AggregateQuery query;

        public AggregateResultImpl(AggregateQuery query, T result) {
            this.result = checkNotNull(result);
            this.query = checkNotNull(query);
        }

        @Override
        public T getResult() {
            return result;
        }

        @Override
        public AggregateQuery getQuery() {
            return query;
        }

    }

    public static class StatsQueryResponseImplBuilder implements Builder<StatsQueryResponseImpl, StatsQueryResponseImplBuilder> {

        private final List<AggregateResult> aggregateResults = list();

        public StatsQueryResponseImplBuilder withAggregateResults(List<AggregateResult> aggregateResults) {
            this.aggregateResults.clear();
            this.aggregateResults.addAll(aggregateResults);
            return this;
        }

        public StatsQueryResponseImplBuilder withAggregateResult(AggregateQuery query, Object result) {
            aggregateResults.add(new AggregateResultImpl(query, result));
            return this;
        }

        @Override
        public StatsQueryResponseImpl build() {
            return new StatsQueryResponseImpl(this);
        }

    }
}
