/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import org.cmdbuild.dao.postgres.q3.beans.PreparedQueryExt;

public interface QueryBuilderInnerService {

    PreparedQueryExt buildPreparedQuery(QueryBuilderParams source);

    default String buildSqlQuery(QueryBuilderParams source) {
        return buildPreparedQuery(source).getQuery();
    }

}
