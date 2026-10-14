/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import org.cmdbuild.data.filter.CmdbFilter;

public interface SelectMatchFilterBuilder<T> {

    T selectMatchFilter(String name, CmdbFilter filter);

}
