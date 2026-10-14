/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.data;

import java.util.Map;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;

public interface QueryOptions {

	int getLimit();

	int getOffset();

	CmdbFilter getFilter();

	CmdbSorter getSorters();

	Iterable<String> getAttributes();

	Map<String, Object> getParameters();
}
