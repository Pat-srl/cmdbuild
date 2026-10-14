/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3.beans;

import org.cmdbuild.data.filter.CmdbFilter;

public interface MatchFilter {

    String getName();

    CmdbFilter getFilter();

}
