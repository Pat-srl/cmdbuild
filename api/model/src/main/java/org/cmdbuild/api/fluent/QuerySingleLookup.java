package org.cmdbuild.api.fluent;

public class QuerySingleLookup {

	private final FluentApiExecutor executor;
	private final Integer id;

	QuerySingleLookup(final FluentApiExecutor executor, final Integer id) {
		this.executor = executor;
		this.id = id;
	}

	public Lookup fetch() {
		return executor.fetch(this);
	}

	public Integer getId() {
		return id;
	}

}
