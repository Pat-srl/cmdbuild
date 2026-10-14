/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3.beans;

import java.util.List;
import org.cmdbuild.dao.core.q3.PreparedQuery;

public interface InnerPreparedQuery extends PreparedQuery {

    List<WhereElement> getFilters();

    @Override
    public default boolean hasFalseFilter() {
        return getFilters().stream().anyMatch(WhereElement::isFalse);
    }

}
