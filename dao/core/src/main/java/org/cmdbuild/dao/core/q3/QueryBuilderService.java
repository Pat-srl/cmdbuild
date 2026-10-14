/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import java.util.Map;

public interface QueryBuilderService {

    QueryBuilder query();

    default QueryBuilder selectAll() {
        return query().selectAll();
    }

    default QueryBuilder selectCount() {
        return query().selectCount();
    }

    default RowNumberQueryBuilder selectRowNumber() {
        return query().selectRowNumber();
    }

    default QueryBuilder delete() {
        return query().delete();
    }

    default QueryBuilder update(Map<String, Object> values) {
        return query().update(values);
    }

}
