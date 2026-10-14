/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import org.cmdbuild.utils.lang.Builder;
import org.cmdbuild.client.rest.api.WokflowApi;

public class FlowDataImpl implements WokflowApi.FlowData {

	private final String instanceId;
	private final Map<String, Object> attributes;
	private final String status;

	private FlowDataImpl(FlowDataImplBuilder builder) {
		this.instanceId = builder.instanceId;
		this.attributes = checkNotNull(builder.attributes);
		this.status = checkNotNull(builder.status);
	}

	@Override
	public String getFlowId() {
		return instanceId;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getStatus() {
		return status;
	}

	public static FlowDataImplBuilder builder() {
		return new FlowDataImplBuilder();
	}

	public static FlowDataImplBuilder copyOf(FlowDataImpl source) {
		return new FlowDataImplBuilder()
				.withInstanceId(source.getFlowId())
				.withAttributes(source.getAttributes())
				.withStatus(source.getStatus());
	}

	public static class FlowDataImplBuilder implements Builder<FlowDataImpl, FlowDataImplBuilder> {

		private String instanceId;
		private Map<String, Object> attributes = emptyMap();
		private String status = "undefined";

		public FlowDataImplBuilder withInstanceId(String instanceId) {
			this.instanceId = instanceId;
			return this;
		}

		public FlowDataImplBuilder withAttributes(Map<String, Object> attributes) {
			this.attributes = attributes;
			return this;
		}

		public FlowDataImplBuilder withStatus(String status) {
			this.status = status;
			return this;
		}

		@Override
		public FlowDataImpl build() {
			return new FlowDataImpl(this);
		}

	}
}
