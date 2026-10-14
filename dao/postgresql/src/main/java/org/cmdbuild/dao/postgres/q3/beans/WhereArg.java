/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3.beans;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static java.util.Collections.emptyList;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.core.q3.CompositeWhereOperator;
import org.cmdbuild.dao.core.q3.WhereOperator;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.JOIN_ID_DEFAULT;
import static org.cmdbuild.dao.postgres.q3.beans.WhereArgType.WA_COMPOSITE;
import static org.cmdbuild.dao.postgres.q3.beans.WhereArgType.WA_EXPR;
import static org.cmdbuild.dao.postgres.q3.beans.WhereArgType.WA_OPERATOR;
import static org.cmdbuild.dao.postgres.q3.beans.WhereTarget.WT_DEFAULT;
import static org.cmdbuild.dao.postgres.q3.beans.WhereTarget.WT_ROWNUMBER;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;

public class WhereArg {

    private final WhereArgType type;
    private final List<Object> params;
    private final String expr;
    private final WhereOperator operator;
    private final CompositeWhereOperator compositeOperator;
    private final List<WhereArg> inners;
    private final WhereTarget target;
    private final boolean requireWithExpr, enableSmartAliasProcessing, enableExprMarkerProcessing;
    private final String joinTo, joinFrom;

    private WhereArg(WhereArgBuilder builder) {
        this.type = checkNotNull(builder.type);
        this.target = firstNotNull(builder.target, WT_DEFAULT);
        this.params = list(builder.params).immutable();
        switch (type) {
            case WA_COMPOSITE -> {
                this.operator = null;
                this.expr = null;
                this.compositeOperator = checkNotNull(builder.compositeOperator);
                this.inners = ImmutableList.copyOf(builder.inners);
                this.requireWithExpr = firstNotNull(builder.requireWithExpr, inners.stream().anyMatch(WhereArg::requireWithExpr));
                this.enableSmartAliasProcessing = firstNotNull(builder.enableSmartAliasProcessing, inners.stream().anyMatch(WhereArg::enableSmartAliasProcessing));
                this.enableExprMarkerProcessing = firstNotNull(builder.enableExprMarkerProcessing, inners.stream().anyMatch(WhereArg::enableExprMarkerProcessing));
            }
            case WA_EXPR -> {
                this.compositeOperator = null;
                this.operator = null;
                this.inners = emptyList();
                this.expr = trimAndCheckNotBlank(builder.expr);
                boolean mayRequireWithExpr = expr.contains("unnest");//TODO improve this
                this.requireWithExpr = firstNotNull(builder.requireWithExpr, mayRequireWithExpr);
                this.enableSmartAliasProcessing = firstNotNull(builder.enableSmartAliasProcessing, false);
                this.enableExprMarkerProcessing = firstNotNull(builder.enableExprMarkerProcessing, false);
            }
            case WA_OPERATOR -> {
                this.compositeOperator = null;
                this.inners = emptyList();
                this.expr = trimAndCheckNotBlank(builder.expr);
                this.operator = checkNotNull(builder.operator);
                this.requireWithExpr = false;
                this.enableSmartAliasProcessing = false;
                this.enableExprMarkerProcessing = false;
            }
            default ->
                throw unsupported("unsupported where type = %s", type);
        }
        this.joinTo = switch (target) {
            case WT_JOINON ->
                checkNotBlank(builder.joinTo);
            default ->
                firstNotBlank(builder.joinTo, JOIN_ID_DEFAULT);
        };
        this.joinFrom = firstNotBlank(builder.joinFrom, JOIN_ID_DEFAULT);
    }

    @Nullable
    public CompositeWhereOperator getCompositeOperator() {
        return compositeOperator;
    }

    public CompositeWhereOperator getCompositeOperatorNotNull() {
        return checkNotNull(getCompositeOperator());
    }

    public boolean enableSmartAliasProcessing() {
        return enableSmartAliasProcessing;
    }

    public boolean enableExprMarkerProcessing() {
        return enableExprMarkerProcessing;
    }

    public boolean hasJoinFrom() {
        return !equal(getJoinFrom(), JOIN_ID_DEFAULT);
    }

    public boolean hasJoinTo() {
        return !equal(getJoinTo(), JOIN_ID_DEFAULT);
    }

    public List<Object> getParams() {
        return params;
    }

    @Nullable
    public String getExpr() {
        return expr;
    }

    @Nullable
    public WhereOperator getOperator() {
        return operator;
    }

    public WhereOperator getOperatorNotNull() {
        return checkNotNull(getOperator());
    }

    public WhereArgType getType() {
        return type;
    }

    public List<WhereArg> getInners() {
        return inners;
    }

    public boolean requireWithExpr() {
        return requireWithExpr;
    }

    public WhereTarget getTarget() {
        return target;
    }

    public boolean isForRowNumber() {
        return equal(getTarget(), WT_ROWNUMBER);
    }

    public String getJoinTo() {
        return joinTo;
    }

    public String getJoinFrom() {
        return joinFrom;
    }

    public static WhereArgBuilder builder() {
        return new WhereArgBuilder();
    }

    public static WhereArgBuilder builder(String expr, WhereOperator operator, Object... params) {
        return new WhereArgBuilder().withType(WA_OPERATOR).withExpr(expr).withOperator(operator).withParams(params);
    }

    public static WhereArg build(String expr, WhereOperator operator, Object... params) {
        return builder(expr, operator, params).build();
    }

    public static WhereArg build(String expr) {
        return builder().withType(WA_EXPR).withExpr(expr).build();
    }

    public static WhereArg build(CompositeWhereOperator operator, List<WhereArg> inners) {
        return builder().withType(WA_COMPOSITE).withCompositeOperator(operator).withInners(inners).build();
    }

    public static WhereArgBuilder copyOf(WhereArg source) {
        return new WhereArgBuilder()
                .withExpr(source.getExpr())
                .withTarget(source.getTarget())
                .withType(source.getType())
                .withParams(source.getParams())
                .withOperator(source.getOperator())
                .withCompositeOperator(source.getCompositeOperator())
                .withInners(source.getInners())
                .requireWithExpr(source.requireWithExpr())
                .enableSmartAliasProcessing(source.enableSmartAliasProcessing())
                .enableExprMarkerProcessing(source.enableExprMarkerProcessing())
                .withJoinTo(source.getJoinTo())
                .withJoinFrom(source.getJoinFrom());
    }

    public static class WhereArgBuilder implements Builder<WhereArg, WhereArgBuilder> {

        private String expr;
        private WhereTarget target;
        private WhereArgType type;
        private final List<Object> params = list();
        private WhereOperator operator;
        private CompositeWhereOperator compositeOperator;
        private List<WhereArg> inners;

        private Boolean requireWithExpr, enableSmartAliasProcessing, enableExprMarkerProcessing;
        private String joinTo, joinFrom;

        public WhereArgBuilder withExpr(String expr) {
            this.expr = expr;
            return this;
        }

        public WhereArgBuilder withTarget(WhereTarget target) {
            this.target = target;
            return this;
        }

        public WhereArgBuilder requireWithExpr(Boolean requireWithExpr) {
            this.requireWithExpr = requireWithExpr;
            return this;
        }

        public WhereArgBuilder withType(WhereArgType type) {
            this.type = type;
            return this;
        }

        public WhereArgBuilder withParams(List<Object> params) {
            this.params.clear();
            this.params.addAll(params);
            return this;
        }

        public WhereArgBuilder withParams(Object... params) {
            return this.withParams(list(params));
        }

        public WhereArgBuilder withOperator(WhereOperator operator) {
            this.operator = operator;
            return this;
        }

        public WhereArgBuilder withCompositeOperator(CompositeWhereOperator compositeOperator) {
            this.compositeOperator = compositeOperator;
            return this;
        }

        public WhereArgBuilder withInners(List<WhereArg> inners) {
            this.inners = inners;
            return this;
        }

        public WhereArgBuilder enableSmartAliasProcessing(Boolean enableSmartAliasProcessing) {
            this.enableSmartAliasProcessing = enableSmartAliasProcessing;
            return this;
        }

        public WhereArgBuilder enableExprMarkerProcessing(Boolean enableExprMarkerProcessing) {
            this.enableExprMarkerProcessing = enableExprMarkerProcessing;
            return this;
        }

        public WhereArgBuilder withJoinTo(String joinTo) {
            this.joinTo = joinTo;
            return this;
        }

        public WhereArgBuilder withJoinFrom(String joinFrom) {
            this.joinFrom = joinFrom;
            return this;
        }

        @Override
        public WhereArg build() {
            return new WhereArg(this);
        }

    }

}
