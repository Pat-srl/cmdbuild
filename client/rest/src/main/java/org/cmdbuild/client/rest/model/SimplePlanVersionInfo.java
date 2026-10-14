/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cmdbuild.client.rest.api.WokflowApi.PlanVersionInfo;

import org.cmdbuild.client.rest.model.SimplePlanVersionInfo.SimplePlanVersionInfoBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@JsonDeserialize(builder = SimplePlanVersionInfoBuilder.class)
public class SimplePlanVersionInfo implements PlanVersionInfo {

	private final String id, version, planId, provider;
	private final boolean isDefault;

	private SimplePlanVersionInfo(SimplePlanVersionInfoBuilder builder) {
		this.id = checkNotBlank(builder.id);
		this.version = checkNotBlank(builder.version);
		this.planId = checkNotBlank(builder.planId);
		this.provider = checkNotBlank(builder.provider);
		this.isDefault = builder.isDefault;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getPlanId() {
		return planId;
	}

	@Override
	public String getProvider() {
		return provider;
	}

	@Override
	public boolean isDefault() {
		return isDefault;
	}

	@Override
	public String toString() {
		return "SimplePlanVersionInfo{" + "id=" + id + ", version=" + version + ", planId=" + planId + ", provider=" + provider + '}';
	}

	public static SimplePlanVersionInfoBuilder builder() {
		return new SimplePlanVersionInfoBuilder();
	}

	public static SimplePlanVersionInfoBuilder copyOf(PlanVersionInfo source) {
		return new SimplePlanVersionInfoBuilder()
				.withId(source.getId())
				.withVersion(source.getVersion())
				.withPlanId(source.getPlanId())
				.withDefault(source.isDefault())
				.withProvider(source.getProvider());
	}

	public static class SimplePlanVersionInfoBuilder implements Builder<SimplePlanVersionInfo, SimplePlanVersionInfoBuilder> {

		private String id;
		private String version;
		private String planId, provider;
		private Boolean isDefault;

		@JsonProperty("_id")
		public SimplePlanVersionInfoBuilder withId(String id) {
			this.id = id;
			return this;
		}

		public SimplePlanVersionInfoBuilder withVersion(String version) {
			this.version = version;
			return this;
		}

		public SimplePlanVersionInfoBuilder withDefault(Boolean isDefault) {
			this.isDefault = isDefault;
			return this;
		}

		public SimplePlanVersionInfoBuilder withPlanId(String planId) {
			this.planId = planId;
			return this;
		}

		public SimplePlanVersionInfoBuilder withProvider(String provider) {
			this.provider = provider;
			return this;
		}

		@Override
		public SimplePlanVersionInfo build() {
			return new SimplePlanVersionInfo(this);
		}

	}
}
