/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3.beans;

import static com.google.common.base.Objects.equal;
import java.util.function.Function;
import static org.cmdbuild.dao.postgres.q3.beans.WhereTarget.WT_DEFAULT;
import static org.cmdbuild.dao.postgres.q3.beans.WhereTarget.WT_ROWNUMBER;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;

public class WhereElement {

    private final static String WHERE_ELEMENT_FALSE_EXPR = "FALSE", WHERE_ELEMENT_TRUE_EXPR = "TRUE";

    private final String expr;
    private final WhereTarget target;
    private final boolean requireWithExpr;

    private WhereElement(WhereElementBuilder builder) {
        this.expr = trimAndCheckNotBlank(builder.expr);
        this.target = firstNotNull(builder.target, WT_DEFAULT);
        this.requireWithExpr = firstNotNull(builder.requireWithExpr, false);
    }

    public String getExpr() {
        return expr;
    }

    public WhereTarget getTarget() {
        return target;
    }

    public boolean forRowNumber() {
        return equal(getTarget(), WT_ROWNUMBER);
    }

    public boolean requireWithExpr() {
        return requireWithExpr;
    }

    public WhereElement mapExpr(Function<String, String> exprFun) {
        return copyOf(this).withExpr(exprFun.apply(expr)).build();
    }

    public boolean isFalse() {
        return equal(expr, WHERE_ELEMENT_FALSE_EXPR);
    }

    public boolean isTrue() {
        return equal(expr, WHERE_ELEMENT_TRUE_EXPR);
    }

    public static WhereElement build(String expr) {
        return builder().withExpr(expr).build();
    }

    public static WhereElement whereFalse() {
        return build(WHERE_ELEMENT_FALSE_EXPR);
    }

    public static WhereElement whereTrue() {
        return build(WHERE_ELEMENT_TRUE_EXPR);
    }

    public static WhereElementBuilder builder() {
        return new WhereElementBuilder();
    }

    public static WhereElementBuilder copyOf(WhereElement source) {
        return builder()
                .requireWithExpr(source.requireWithExpr())
                .withExpr(source.getExpr())
                .withTarget(source.getTarget());
    }

    public static class WhereElementBuilder implements Builder<WhereElement, WhereElementBuilder> {

        private String expr;
        private WhereTarget target;
        private Boolean requireWithExpr;

        public WhereElementBuilder withExpr(String expr) {
            this.expr = expr;
            return this;
        }

        public WhereElementBuilder withTarget(WhereTarget target) {
            this.target = target;
            return this;
        }

        public WhereElementBuilder requireWithExpr(Boolean requireWithExpr) {
            this.requireWithExpr = requireWithExpr;
            return this;
        }

        @Override
        public WhereElement build() {
            return new WhereElement(this);
        }

    }


}
