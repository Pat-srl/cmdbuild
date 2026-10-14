/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import static com.google.common.base.Objects.equal;

public interface QueryBuilderConfiguration {

    SqlQueryReferenceProcessingStrategy getReferenceProcessingStrategy();

    default boolean hasReferenceProcessingStrategy(SqlQueryReferenceProcessingStrategy strategy) {
        return equal(getReferenceProcessingStrategy(), strategy);
    }

    enum SqlQueryReferenceProcessingStrategy {
        RPS_DEFAULT, RPS_IGNORETENANT
    }
}
