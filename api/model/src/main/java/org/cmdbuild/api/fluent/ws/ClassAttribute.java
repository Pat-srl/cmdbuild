package org.cmdbuild.api.fluent.ws;

public final class ClassAttribute extends EntryTypeAttributeImpl {

	protected ClassAttribute(final String className, final String attributeName) {
		super(className, attributeName);
	}

	@Override
	public void accept(final AttrTypeVisitor visitor) {
		visitor.visit(this);
	}

	public String getClassName() {
		return entryTypeName;
	}

	public static ClassAttribute classAttribute(final String className, final String attributeName) {
		return new ClassAttribute(className, attributeName);
	}

}
