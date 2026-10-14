/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.dao.driver.postgres.q3.stats;

public interface AggregateResult<T> extends AggregateQuery {

    T getResult();

    AggregateQuery getQuery();

    @Override
    public default String getAttribute() {
        return getQuery().getAttribute();
    }

    @Override
    public default AggregateOperation getOperation() {
        return getQuery().getOperation();
    }

}
