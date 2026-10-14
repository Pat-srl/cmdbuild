package org.cmdbuild.workflow.model;

public class SimplePlanExtendedAttribute implements PlanExtendedAttribute {

	private final String key;
	private final String value;

	public SimplePlanExtendedAttribute(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}
}
