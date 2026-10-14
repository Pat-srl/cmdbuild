/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view.join;

import static java.lang.String.format;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public interface JoinAttribute {

    String getExprAlias();

    String getExprAttr();

    String getName();

    @Nullable
    String getDescription();

    @Nullable
    String getGroup();

    boolean getShowInGrid();

    boolean getShowInReducedGrid();

    default String getExpr() {
        return format("%s.%s", getExprAlias(), getExprAttr());
    }

    default boolean hasGroup() {
        return isNotBlank(getGroup());
    }

    default boolean hasDescription() {
        return isNotBlank(getDescription());
    }
}
