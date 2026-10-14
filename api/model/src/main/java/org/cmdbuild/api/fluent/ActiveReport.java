package org.cmdbuild.api.fluent;

abstract class ActiveReport extends Report {

	private final FluentApiExecutor executor;

	ActiveReport(final FluentApiExecutor executor, final String title, final String format) {
		super(title, format);
		this.executor = executor;
	}

	protected FluentApiExecutor executor() {
		return executor;
	}

}
