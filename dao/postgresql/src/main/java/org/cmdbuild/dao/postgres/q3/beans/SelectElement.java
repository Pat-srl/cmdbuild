/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3.beans;

import com.google.common.collect.ImmutableList;
import static java.util.Collections.emptyList;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.postgres.utils.SqlTypeName;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.JOIN_ID_DEFAULT;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class SelectElement {

    private final List<Object> params;
    private final String expr, alias, name, joinFrom;
    private final SqlTypeName sqlTypeHint;
    private final boolean groupBy;

    private SelectElement(SelectElementBuilder builder) {
        this.expr = checkNotBlank(builder.expr);
        this.alias = checkNotBlank(builder.alias);
        this.name = checkNotBlank(builder.name);
        this.params = ImmutableList.copyOf(firstNotNull(builder.params, emptyList()));
        this.sqlTypeHint = builder.sqlTypeHint;
        this.groupBy = firstNotNull(builder.groupBy, false);
        this.joinFrom = firstNotBlank(builder.joinFrom, JOIN_ID_DEFAULT);
    }

    public String getName() {
        return name;
    }

    public String getExpr() {
        return expr;
    }

    public String getAlias() {
        return alias;
    }

    public List<Object> getParams() {
        return params;
    }

    @Nullable
    public SqlTypeName getSqlTypeHint() {
        return sqlTypeHint;
    }

    public String getJoinFrom() {
        return joinFrom;
    }

    public boolean getGroupBy() {
        return groupBy;
    }

    public SelectArg toSelectArg() {
        return SelectArg.builder()
                //                .withSqlTypeHint(getSqlTypeHint())
                .withJoinFrom(getJoinFrom())
                .withGroupBy(getGroupBy())
                .withParams(getParams())
                .withExpr(getExpr())
                .withAlias(getAlias())
                .withName(getName())
                .build();//TODO check this
    }

    @Override
    public String toString() {
        return "SelectElement{" + "expr=" + expr + ", alias=" + alias + ", name=" + name + '}';
    }

    public static SelectElementBuilder builder() {
        return new SelectElementBuilder();
    }

    public static SelectElement build(String name, String expr, String alias) {
        return builder().withName(name).withExpr(expr).withAlias(alias).build();
    }

    public static SelectElementBuilder copyOf(SelectElement source) {
        return new SelectElementBuilder()
                .withSqlTypeHint(source.getSqlTypeHint())
                .withJoinFrom(source.getJoinFrom())
                .withGroupBy(source.getGroupBy())
                .withParams(source.getParams())
                .withExpr(source.getExpr())
                .withAlias(source.getAlias())
                .withName(source.getName());
    }

    public static class SelectElementBuilder implements Builder<SelectElement, SelectElementBuilder> {

        private List<Object> params;
        private String expr;
        private String alias;
        private String name, joinFrom;
        private SqlTypeName sqlTypeHint;
        private Boolean groupBy;

        public SelectElementBuilder withParams(List<Object> params) {
            this.params = params;
            return this;
        }

        public SelectElementBuilder withExpr(String expr) {
            this.expr = expr;
            return this;
        }

        public SelectElementBuilder withJoinFrom(String joinFrom) {
            this.joinFrom = joinFrom;
            return this;
        }

        public SelectElementBuilder withAlias(String alias) {
            this.alias = alias;
            return this;
        }

        public SelectElementBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SelectElementBuilder withGroupBy(Boolean groupBy) {
            this.groupBy = groupBy;
            return this;
        }

        public SelectElementBuilder withSqlTypeHint(SqlTypeName sqlTypeHint) {
            this.sqlTypeHint = sqlTypeHint;
            return this;
        }

        @Override
        public SelectElement build() {
            return new SelectElement(this);
        }

    }

}
