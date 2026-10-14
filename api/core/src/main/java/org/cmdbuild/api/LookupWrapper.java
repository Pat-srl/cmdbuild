package org.cmdbuild.api;

import org.cmdbuild.api.fluent.Lookup;

public class LookupWrapper implements Lookup {

	private final org.cmdbuild.lookup.LookupValue inner;

	public LookupWrapper(final org.cmdbuild.lookup.LookupValue inner) {
		this.inner = inner;
	}

	@Override
	public String getType() {
		return inner.getType().getName();
	}

	@Override
	public String getCode() {
		return inner.getCode();
	}

	@Override
	public String getDescription() {
		return inner.getDescription();
	}

	@Override
	public Long getId() {
		return  inner.getId();
	}

}
