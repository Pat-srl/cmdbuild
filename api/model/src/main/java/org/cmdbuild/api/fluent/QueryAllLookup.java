package org.cmdbuild.api.fluent;

public class QueryAllLookup {

	private final FluentApiExecutor executor;
	private final String type;

	QueryAllLookup(final FluentApiExecutor executor, final String type) {
		this.executor = executor;
		this.type = type;
	}

	public Iterable<Lookup> fetch() {
		return executor.fetch(this);
	}

	public QuerySingleLookup elementWithId(final Integer id) {
		return new QuerySingleLookup(executor, id);
	}

	public String getType() {
		return type;
	}

}
