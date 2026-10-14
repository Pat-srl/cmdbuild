/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class SimpleClasseInfo implements ClasseInfo {

	private final long oid;
	private final String name;

	public SimpleClasseInfo(long oid, String name) {
		this.oid = oid;
		this.name = checkNotBlank(name);
	}

	@Override
	public long getOid() {
		return oid;
	}

	@Override
	public String getName() {
		return name;
	}

}
