package org.cmdbuild.dao.entrytype;

import com.google.common.collect.ForwardingObject;

public abstract class ForwardingEntryTypeVisitor extends ForwardingObject implements CMEntryTypeVisitor {

	/**
	 * Usable by subclasses only.
	 */
	protected ForwardingEntryTypeVisitor() {
	}

	@Override
	protected abstract CMEntryTypeVisitor delegate();

	@Override
	public void visit(final Classe type) {
		delegate().visit(type);
	}

	@Override
	public void visit(final Domain type) {
		delegate().visit(type);
	}

	@Override
	public void visit(final CMFunctionCall type) {
		delegate().visit(type);
	}

}
