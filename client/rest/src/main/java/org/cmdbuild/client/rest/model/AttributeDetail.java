/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import static java.lang.String.format;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

public interface AttributeDetail {

	String getType();

	String getName();

	String getDescription();

	@Nullable
	String getTargetClass();

	@Nullable
	String getTargetType();

	@Nullable
	String getLookupTypeName();

	@Nullable
	String getFilter();

	default boolean hasFilter() {
		return !isBlank(getFilter());
	}

	default String targetInfoToString() {
		if (getLookupTypeName() != null) {
			return format("lookup: '%s'", getLookupTypeName());
		} else if (getTargetType() != null) {
			return format("target: %s %s", getTargetType(), getTargetClass());
		} else {
			return "";
		}
	}
}
