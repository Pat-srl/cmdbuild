/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.driver.postgres.q3.stats.AggregateOperation;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.JOIN_ID_DEFAULT;
import static org.cmdbuild.dao.postgres.q3.beans.SelectType.ST_EXPR;
import static org.cmdbuild.dao.postgres.q3.beans.SelectType.ST_FILTER;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class SelectArg {

    private final List<Object> params;
    private final String name, alias, joinFrom, expr;
    private final CmdbFilter filter;
    private final boolean enableSmartAliasProcessing, enableExprMarkerProcessing, groupBy;
    private final SelectType type;
    private final AggregateOperation aggregate;

    private SelectArg(SelectArgBuilder builder) {
        this.type = firstNotNull(builder.type, ST_EXPR);
        this.name = checkNotBlank(builder.name);
        this.alias = builder.alias;
        this.aggregate = builder.aggregate;
        switch (type) {
            case ST_EXPR -> {
                this.filter = null;
                this.expr = checkNotBlank(builder.expr);
                this.joinFrom = firstNotBlank(builder.joinFrom, JOIN_ID_DEFAULT);
                this.params = ImmutableList.copyOf(firstNotNull(builder.params, emptyList()));
                this.enableSmartAliasProcessing = firstNotNull(builder.enableSmartAliasProcessing, false);
                this.enableExprMarkerProcessing = firstNotNull(builder.enableExprMarkerProcessing, false);
            }
            case ST_FILTER -> {
                this.expr = null;
                this.params = emptyList();
                this.joinFrom = JOIN_ID_DEFAULT;
                this.enableSmartAliasProcessing = false;
                this.enableExprMarkerProcessing = false;
                this.filter = checkNotNull(builder.filter);
            }
            default ->
                throw unsupported("unsupported select type = %s", type);
        }
        this.groupBy = firstNotNull(builder.groupBy, false);
    }

    @Nullable
    public AggregateOperation getAggregate() {
        return aggregate;
    }

    public boolean hasAggregate() {
        return getAggregate() != null;
    }

    public List<Object> getParams() {
        return params;
    }

    public String getName() {
        return name;
    }

    public String getExpr() {
        return expr;
    }

    @Nullable
    public String getAlias() {
        return alias;
    }

    public boolean hasAlias() {
        return isNotBlank(getAlias());
    }

    public String getJoinFrom() {
        return joinFrom;
    }

    public boolean enableSmartAliasProcessing() {
        return enableSmartAliasProcessing;
    }

    public boolean enableExprMarkerProcessing() {
        return enableExprMarkerProcessing;
    }

    public CmdbFilter getFilter() {
        return filter;
    }

    public SelectType getType() {
        return type;
    }

    public boolean getGroupBy() {
        return groupBy;
    }

    @Override
    public String toString() {
        return "SelectArg{" + "name=" + name + ", expr=" + expr + (alias == null ? "" : format(", alias=%s", alias)) + '}';
    }

    public static SelectArgBuilder builder(String name, String expr) {
        return builder().withName(name).withExpr(expr);
    }

    public static SelectArg build(String name, String expr) {
        return builder(name, expr).build();
    }

    public static SelectArg build(String name, CmdbFilter filter) {
        return builder().withName(name).withFilter(filter).withType(ST_FILTER).build();
    }

    public static SelectArgBuilder builder() {
        return new SelectArgBuilder();
    }

    public static SelectArgBuilder copyOf(SelectArg source) {
        return new SelectArgBuilder()
                .withExpr(source.getExpr())
                .withName(source.getName())
                .withAlias(source.getAlias())
                .withParams(source.getParams())
                .enableSmartAliasProcessing(source.enableSmartAliasProcessing())
                .enableExprMarkerProcessing(source.enableExprMarkerProcessing())
                .withJoinFrom(source.getJoinFrom())
                .withType(source.getType())
                .withFilter(source.getFilter())
                .withGroupBy(source.getGroupBy());
    }

    public static class SelectArgBuilder implements Builder<SelectArg, SelectArgBuilder> {

        private SelectType type;
        private CmdbFilter filter;
        private List<Object> params;
        private String name, alias, joinFrom, expr;
        private Boolean enableSmartAliasProcessing, enableExprMarkerProcessing;
        private AggregateOperation aggregate;
        private Boolean groupBy;

        public SelectArgBuilder withAggregate(AggregateOperation aggregate) {
            this.aggregate = aggregate;
            return this;
        }

        public SelectArgBuilder withFilter(CmdbFilter filter) {
            this.filter = filter;
            return this;
        }

        public SelectArgBuilder withType(SelectType type) {
            this.type = type;
            return this;
        }

        public SelectArgBuilder withParams(List<Object> params) {
            this.params = params;
            return this;
        }

        public SelectArgBuilder withExpr(String expr) {
            this.expr = expr;
            return this;
        }

        public SelectArgBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SelectArgBuilder withAlias(String alias) {
            this.alias = alias;
            return this;
        }

        public SelectArgBuilder withJoinFrom(String joinFrom) {
            this.joinFrom = joinFrom;
            return this;
        }

        public SelectArgBuilder enableSmartAliasProcessing(Boolean enableSmartAliasProcessing) {
            this.enableSmartAliasProcessing = enableSmartAliasProcessing;
            return this;
        }

        public SelectArgBuilder enableExprMarkerProcessing(Boolean enableExprMarkerProcessing) {
            this.enableExprMarkerProcessing = enableExprMarkerProcessing;
            return this;
        }

        public SelectArgBuilder withGroupBy(Boolean groupBy) {
            this.groupBy = groupBy;
            return this;
        }

        @Override
        public SelectArg build() {
            return new SelectArg(this);
        }
    }

}
