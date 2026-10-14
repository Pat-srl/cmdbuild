/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.data.filter.RelationFilterCardInfo;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class RelationFilterCardInfoImpl implements RelationFilterCardInfo {

	private final String className;
	private final Long id;

	public RelationFilterCardInfoImpl(String className, Long id) {
		this.className = checkNotBlank(className);
		this.id = checkNotNull(id);
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public Long getId() {
		return id;
	}

}
