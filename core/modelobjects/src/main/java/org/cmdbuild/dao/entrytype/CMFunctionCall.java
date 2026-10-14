package org.cmdbuild.dao.entrytype;

import java.util.List;

import org.cmdbuild.dao.function.StoredFunction;

public interface CMFunctionCall extends EntryType {

	StoredFunction getFunction();

	List<Object> getParams();

}
