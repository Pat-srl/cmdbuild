package org.cmdbuild.dao.entrytype.attributetype;

import com.google.common.collect.ForwardingObject;

public abstract class ForwardingAttributeTypeVisitor extends ForwardingObject implements CMAttributeTypeVisitor {

	/**
	 * Usable by subclasses only.
	 */
	protected ForwardingAttributeTypeVisitor() {
	}

	@Override
	protected abstract CMAttributeTypeVisitor delegate();

	@Override
	public void visit(final BooleanAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final CharAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final DateAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final DateTimeAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final DecimalAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final DoubleAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final RegclassAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final ForeignKeyAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final IntegerAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(LongAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final IpAddressAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final LookupAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final ReferenceAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final StringArrayAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final StringAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final TextAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(final TimeAttributeType attributeType) {
		delegate().visit(attributeType);
	}

	@Override
	public void visit(ByteArrayAttributeType attributeType) {
		delegate().visit(attributeType);
	}

}
