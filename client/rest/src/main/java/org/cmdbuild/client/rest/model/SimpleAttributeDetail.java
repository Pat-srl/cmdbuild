/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map;
import org.cmdbuild.client.rest.model.SimpleAttributeDetail.SimpleAttributeDetailBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

@JsonDeserialize(builder = SimpleAttributeDetailBuilder.class)
public class SimpleAttributeDetail implements AttributeDetail {

	private final String type;
	private final String name;
	private final String description;
	private final String targetClass;
	private final String targetType;
	private final String lookupTypeName;
	private final String filter;

	private SimpleAttributeDetail(SimpleAttributeDetailBuilder builder) {
		this.type = checkNotBlank(builder.type);
		this.name = checkNotBlank(builder.name);
		this.description = nullToEmpty(builder.description);
		this.targetClass = (builder.targetClass);
		this.targetType = (builder.targetType);
		this.lookupTypeName = (builder.lookupTypeName);
		this.filter = (builder.filter);
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getTargetClass() {
		return targetClass;
	}

	@Override
	public String getTargetType() {
		return targetType;
	}

	@Override
	public String getLookupTypeName() {
		return lookupTypeName;
	}

	@Override
	public String getFilter() {
		return filter;
	}

	public static SimpleAttributeDetailBuilder builder() {
		return new SimpleAttributeDetailBuilder();
	}

	public static SimpleAttributeDetailBuilder copyOf(SimpleAttributeDetail source) {
		return new SimpleAttributeDetailBuilder()
				.withType(source.getType())
				.withName(source.getName())
				.withDescription(source.getDescription())
				.withTargetClass(source.getTargetClass())
				.withTargetType(source.getTargetType())
				.withFilterString(source.getFilter())
				.withLookupTypeName(source.getLookupTypeName());
	}

	public static class SimpleAttributeDetailBuilder implements Builder<SimpleAttributeDetail, SimpleAttributeDetailBuilder> {

		private String type;
		private String name;
		private String description;
		private String targetClass;
		private String targetType;
		private String lookupTypeName, filter;

		public SimpleAttributeDetailBuilder withType(String type) {
			this.type = type;
			return this;
		}

		public SimpleAttributeDetailBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public SimpleAttributeDetailBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public SimpleAttributeDetailBuilder withTargetClass(String targetClass) {
			this.targetClass = targetClass;
			return this;
		}

		public SimpleAttributeDetailBuilder withTargetType(String targetType) {
			this.targetType = targetType;
			return this;
		}

		@JsonProperty("lookupType")
		public SimpleAttributeDetailBuilder withLookupTypeName(String lookupTypeName) {
			this.lookupTypeName = lookupTypeName;
			return this;
		}

		@Override
		public SimpleAttributeDetail build() {
			return new SimpleAttributeDetail(this);
		}

		@JsonIgnore
//		@JsonProperty("filter.text")
		public SimpleAttributeDetailBuilder withFilterString(String filter) {
			this.filter = filter;
			return this;
		}

		public SimpleAttributeDetailBuilder withFilter(Map filter) {
			this.filter = filter == null ? null : emptyToNull(toStringOrNull(filter.get("text")));
			return this;
		}

	}
}
