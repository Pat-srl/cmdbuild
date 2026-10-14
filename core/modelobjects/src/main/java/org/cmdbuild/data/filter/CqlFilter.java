/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import org.cmdbuild.data.filter.beans.CmdbFilterImpl;

public interface CqlFilter {

	String getCqlExpression();

	default CmdbFilter toCmdbFilter() {
		return CmdbFilterImpl.builder().withCqlFilter(this).build();
	}

}
