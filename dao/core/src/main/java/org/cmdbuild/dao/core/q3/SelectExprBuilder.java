/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import static java.lang.String.format;

public interface SelectExprBuilder<T> {

    T selectExpr(String name, String expr);

    default T selectExpr(String name, String expr, Object... args) {
        return selectExpr(name, format(expr, args));
    }
}
