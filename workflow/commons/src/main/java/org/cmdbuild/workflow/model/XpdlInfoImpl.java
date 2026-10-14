/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class XpdlInfoImpl implements XpdlInfo {

	private final String version, planId, provider;
	private final ZonedDateTime lastUpdate;
	private final boolean isDefault;

	private XpdlInfoImpl(XpdlInfoImplBuilder builder) {
		this.version = checkNotBlank(builder.version);
		this.planId = checkNotBlank(builder.planId);
		this.provider = checkNotBlank(builder.provider);
		this.lastUpdate = checkNotNull(builder.lastUpdate);
		this.isDefault = builder.isDefault;
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
	public ZonedDateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public boolean isDefault() {
		return isDefault;
	}

	@Override
	public String toString() {
		return "XpdlInfoImpl{" + "version=" + version + ", planId=" + planId + ", provider=" + provider + '}';
	}

	public static XpdlInfoImplBuilder builder() {
		return new XpdlInfoImplBuilder();
	}

	public static XpdlInfoImplBuilder copyOf(XpdlInfo source) {
		return new XpdlInfoImplBuilder()
				.withVersion(source.getVersion())
				.withPlanId(source.getPlanId())
				.withProvider(source.getProvider())
				.withDefault(source.isDefault())
				.withLastUpdate(source.getLastUpdate());
	}

	public static class XpdlInfoImplBuilder implements Builder<XpdlInfoImpl, XpdlInfoImplBuilder> {

		private String version;
		private String planId;
		private String provider;
		private ZonedDateTime lastUpdate;
		private Boolean isDefault;

		public XpdlInfoImplBuilder withVersion(String version) {
			this.version = version;
			return this;
		}

		public XpdlInfoImplBuilder withLastUpdate(ZonedDateTime lastUpdate) {
			this.lastUpdate = lastUpdate;
			return this;
		}

		public XpdlInfoImplBuilder withDefault(Boolean isDefault) {
			this.isDefault = isDefault;
			return this;
		}

		public XpdlInfoImplBuilder withPlanId(String planId) {
			this.planId = planId;
			return this;
		}

		public XpdlInfoImplBuilder withProvider(String provider) {
			this.provider = provider;
			return this;
		}

		@Override
		public XpdlInfoImpl build() {
			return new XpdlInfoImpl(this);
		}

	}
}
