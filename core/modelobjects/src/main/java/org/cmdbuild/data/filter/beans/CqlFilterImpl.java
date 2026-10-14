/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import org.cmdbuild.data.filter.*;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CqlFilterImpl implements CqlFilter {

	private final String cql;

	public CqlFilterImpl(String cql) {
		this.cql = checkNotBlank(cql);
	}

	@Override
	public String getCqlExpression() {
		return cql;
	}

	@Override
	public String toString() {
		return "CqlFilterImpl{" + "cql='" + cql + "'}";
	}

	public static CqlFilter build(String cql) {
		return new CqlFilterImpl(cql);
	}

}
