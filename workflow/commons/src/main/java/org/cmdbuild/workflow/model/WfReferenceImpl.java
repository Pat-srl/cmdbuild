/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.inner.WfReference;

public class WfReferenceImpl implements WfReference {

	private final long id;
	private final String className;

	public WfReferenceImpl(Long id, String className) {
		this.id = id;
		this.className = checkNotBlank(className);
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String toString() {
		return "SimpleWfReference{" + "id=" + id + ", className=" + className + '}';
	}

}
