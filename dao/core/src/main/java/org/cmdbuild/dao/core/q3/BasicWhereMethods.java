/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import java.util.function.Consumer;

public interface BasicWhereMethods<T extends BasicWhereMethods> {

    T where(String attr, WhereOperator operator, Object... params);

    T where(CompositeWhereOperator operator, Consumer<CompositeWhereHelper> consumer);

    default T where(WhereOperator operator, String attr, Object... params) {
        return where(attr, operator, params);
    }
}
