package org.cmdbuild.api.fluent;

public class CreateReport extends ActiveReport {

	CreateReport(final FluentApiExecutor executor, final String title, final String format) {
		super(executor, title, format);
	}

	public CreateReport with(final String name, final Object value) {
		super.set(name, value);
		return this;
	}

	public DownloadedReport download() {
		return executor().download(this);
	}

}
