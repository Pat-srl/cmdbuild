package org.cmdbuild.cql.compiler;

import org.cmdbuild.cql.CQLBuilderListener.FieldInputValue;

public interface Offset extends CQLElement {

	void setOffset(long offset);

	void setOffset(FieldInputValue offset);

	Object getOffsetValue();
}
