/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view.join;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class JoinAttributeImpl implements JoinAttribute {

    private final String exprAlias, exprAttr, name, description, group;
    private final boolean showInGrid, showInReducedGrid;

    public JoinAttributeImpl(Map<String, ?> config) {
        String expr = toStringNotBlank(config.get("expr"));
        Matcher matcher = Pattern.compile("([^.]+)[.]([^.]+)").matcher(expr);
        checkArgument(matcher.matches(), "invalid expr format, for expr =< %s >", expr);
        exprAlias = checkNotBlank(matcher.group(1));
        exprAttr = checkNotBlank(matcher.group(2));
        name = toStringNotBlank(config.get("name"));
        description = toStringOrNull(config.get("description"));
        group = toStringOrNull(config.get("group"));
        showInGrid = toBooleanOrDefault(config.get("showInGrid"), false);
        showInReducedGrid = toBooleanOrDefault(config.get("showInReducedGrid"), false);
    }

    private JoinAttributeImpl(JoinAttributeImplBuilder builder) {
        this(builder.toMap());
    }

    @JsonAnyGetter
    public Map<String, String> toMap() {
        return copyOf(this).toMap();
    }

    @Override
    public String getExprAlias() {
        return exprAlias;
    }

    @Override
    public String getExprAttr() {
        return exprAttr;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public boolean getShowInGrid() {
        return showInGrid;
    }

    @Override
    public boolean getShowInReducedGrid() {
        return showInReducedGrid;
    }

    @Override
    public String toString() {
        return "JoinAttribute{" + "expr=" + getExpr() + ", name=" + name + '}';
    }

    public static JoinAttributeImplBuilder builder() {
        return new JoinAttributeImplBuilder();
    }

    public static JoinAttributeImplBuilder copyOf(JoinAttribute source) {
        return new JoinAttributeImplBuilder()
                .withExpr(source.getExpr())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withGroup(source.getGroup())
                .withShowInGrid(source.getShowInGrid())
                .withShowInReducedGrid(source.getShowInReducedGrid());
    }

    public static class JoinAttributeImplBuilder implements Builder<JoinAttributeImpl, JoinAttributeImplBuilder> {

        private String expr;
        private String name;
        private String description;
        private String group;
        private Boolean showInGrid;
        private Boolean showInReducedGrid;

        public Map<String, String> toMap() {
            return map("expr", expr,
                    "name", name,
                    "description", description,
                    "group", group,
                    "showInGrid", toStringOrNull(showInGrid),
                    "showInReducedGrid", toStringOrNull(showInReducedGrid));
        }

        public JoinAttributeImplBuilder withExpr(String expr) {
            this.expr = expr;
            return this;
        }

        public JoinAttributeImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public JoinAttributeImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public JoinAttributeImplBuilder withGroup(String group) {
            this.group = group;
            return this;
        }

        public JoinAttributeImplBuilder withShowInGrid(Boolean showInGrid) {
            this.showInGrid = showInGrid;
            return this;
        }

        public JoinAttributeImplBuilder withShowInReducedGrid(Boolean showInReducedGrid) {
            this.showInReducedGrid = showInReducedGrid;
            return this;
        }

        @Override
        public JoinAttributeImpl build() {
            return new JoinAttributeImpl(this);
        }

    }
}
