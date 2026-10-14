package org.cmdbuild.cql.compiler.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cmdbuild.cql.CQLBuilderListener.FieldOperator;
import org.cmdbuild.cql.CQLBuilderListener.FieldValueType;
import org.cmdbuild.cql.compiler.where.Field;
import org.cmdbuild.cql.compiler.where.fieldid.FieldId;

public class FieldImpl extends WhereElementImpl implements Field {

	FieldId id;
	FieldOperator operator;
	List<FieldValue> values = new ArrayList<>();

	@Override
	public FieldId getId() {
		return id;
	}

	@Override
	public FieldOperator getOperator() {
		return operator;
	}

	@Override
	public Collection<FieldValue> getValues() {
		return values;
	}

	@Override
	public void addNextValue(Object value) {
		values.get(values.size() - 1).setValue(value);
	}

	@Override
	public void addNextValueContainer(FieldValueType type) {
		values.add(new FieldValue(type));
	}

	@Override
	public void setId(FieldId id) {
		this.id = id;
	}

	@Override
	public void setOperator(FieldOperator operator) {
		this.operator = operator;
	}

}
