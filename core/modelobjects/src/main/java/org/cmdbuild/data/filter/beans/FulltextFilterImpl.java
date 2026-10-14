/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import org.cmdbuild.data.filter.FulltextFilter;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class FulltextFilterImpl implements FulltextFilter {

    private final String query;

    public FulltextFilterImpl(String query) {
        this.query = checkNotBlank(query);
    }

    @Override
    public String getQuery() {
        return query;
    }
}
