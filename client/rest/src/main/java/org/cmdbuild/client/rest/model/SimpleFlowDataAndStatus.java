/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import java.util.Map;

import java.util.List;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.client.rest.api.WokflowApi.FlowDataAndStatus;
import org.cmdbuild.client.rest.api.WokflowApi.TaskDetail;
import org.cmdbuild.client.rest.model.SimpleFlowDataAndStatus.SimpleFlowDataAndStatusBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.model.AdvancedFlowStatus;

public class SimpleFlowDataAndStatus implements FlowDataAndStatus {

	private final String flowId, flowCardId;
	private final Map<String, Object> attributes;
	private final AdvancedFlowStatus status;
	private final List<TaskDetail> tasklist;

	private SimpleFlowDataAndStatus(SimpleFlowDataAndStatusBuilder builder) {
		this.flowId = checkNotBlank(builder.flowId, "flow id cannot be null");
		this.flowCardId = checkNotBlank(builder.flowCardId, "card id cannot be null");
		this.attributes = checkNotNull(builder.attributes, "attributes cannot be null");
		this.status = checkNotNull(builder.status, "status cannot be null");
		this.tasklist = checkNotNull(builder.tasklist, "tasklist cannot be null");
	}

	@Override
	public String getFlowCardId() {
		return flowCardId;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public AdvancedFlowStatus getFlowStatus() {
		return status;
	}

	@Override
	public List<TaskDetail> getTaskList() {
		return tasklist;
	}

	public static SimpleFlowDataAndStatusBuilder builder() {
		return new SimpleFlowDataAndStatusBuilder();
	}

	public static SimpleFlowDataAndStatusBuilder copyOf(SimpleFlowDataAndStatus source) {
		return new SimpleFlowDataAndStatusBuilder()
				.withFlowId(source.getFlowId())
				.withFlowCardId(source.getFlowCardId())
				.withAttributes(source.getAttributes())
				.withFlowStatus(source.getFlowStatus())
				.withTasklist(source.getTaskList());
	}

	@Override
	public String getFlowId() {
		return flowId;
	}

	@Override
	public String getStatus() {
		return getFlowStatus().name();
	}

	public static class SimpleFlowDataAndStatusBuilder implements Builder<SimpleFlowDataAndStatus, SimpleFlowDataAndStatusBuilder> {

		private String flowId, flowCardId;
		private Map<String, Object> attributes;
		private AdvancedFlowStatus status;
		private List<TaskDetail> tasklist;

		public SimpleFlowDataAndStatusBuilder withFlowId(String flowId) {
			this.flowId = flowId;
			return this;
		}

		public SimpleFlowDataAndStatusBuilder withFlowCardId(String flowCardId) {
			this.flowCardId = flowCardId;
			return this;
		}

		public SimpleFlowDataAndStatusBuilder withAttributes(Map<String, Object> attributes) {
			this.attributes = attributes;
			return this;
		}

		public SimpleFlowDataAndStatusBuilder withFlowStatus(AdvancedFlowStatus status) {
			this.status = status;
			return this;
		}

		public SimpleFlowDataAndStatusBuilder withTasklist(List<TaskDetail> tasklist) {
			this.tasklist = tasklist;
			return this;
		}

		@Override
		public SimpleFlowDataAndStatus build() {
			return new SimpleFlowDataAndStatus(this);
		}

	}
}
