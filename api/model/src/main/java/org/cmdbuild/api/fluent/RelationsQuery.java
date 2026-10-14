package org.cmdbuild.api.fluent;

public class RelationsQuery {

	private final String className;
	private final long id;

	private String domainName;

	public RelationsQuery(final String className, final long id) {
		this.className = className;
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public long getCardId() {
		return id;
	}

	public String getDomainName() {
		return domainName;
	}

	void setDomainName(final String domainName) {
		this.domainName = domainName;
	}

}