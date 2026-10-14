package org.cmdbuild.dao.entrytype;

public class NullEntryTypeVisitor implements CMEntryTypeVisitor {

	private static final NullEntryTypeVisitor INSTANCE = new NullEntryTypeVisitor();

	public static NullEntryTypeVisitor getInstance() {
		return INSTANCE;
	}

	private NullEntryTypeVisitor() {
		// use factory method
	}

	@Override
	public void visit(final Classe type) {
		// nothing to do
	}

	@Override
	public void visit(final Domain type) {
		// nothing to do
	}

	@Override
	public void visit(final CMFunctionCall type) {
		// nothing to do
	}

}
