/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;

public interface JoinQueryBuilder extends CommonQueryBuilderMethods<JoinQueryBuilder> {

    JoinQueryBuilder on(String attr1, WhereOperator operator, String attr2);

    QueryBuilder then();

    default JoinQueryBuilder onEq(String attr1, String attr2) {
        return on(attr1, EQ, attr2);
    }
}
