/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

public interface RowNumberQueryBuilder {

    RowNumberQueryBuilder where(String attr, WhereOperator operator, Object... params);

    QueryBuilder then();

}
