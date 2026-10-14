/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import java.util.List;
import java.util.Map;
import org.cmdbuild.utils.lang.Builder;

public class SimpleFlowData implements FlowData {

	private final FlowStatus status;
	private final FlowInfo info;
	private final Map<String, ?> values;
	private final List<TaskInfo> tasksToAdd, tasks;

	private SimpleFlowData(FlowStatus status, FlowInfo info, Map<String, ?> values, List<TaskInfo> tasksToAdd, List<TaskInfo> tasks) {
		this.status = status;
		this.info = info;
		this.values = values;
		this.tasksToAdd = tasksToAdd;
		this.tasks = tasks;
	}

	@Override
	public FlowStatus getStatus() {
		return status;
	}

	@Override
	public FlowInfo getFlowInfo() {
		return info;
	}

	@Override
	public Map<String, ?> values() {
		return values;
	}

	@Override
	public List<TaskInfo> getTasksToAdd() {
		return tasksToAdd;
	}

	@Override
	public List<TaskInfo> getTasksToSet() {
		return tasks;
	}

	@Override
	public String toString() {
		return "SimpleWalkData{" + "status=" + status + ", info=" + info + ", values=" + values + ", tasksToAdd=" + tasksToAdd + ", tasks=" + tasks + '}';
	}

	public static SimpleFlowDataBuilder builder() {
		return new SimpleFlowDataBuilder();
	}

	public static SimpleFlowData noData() {
		return NO_DATA_INSTANCE;
	}

	private static final SimpleFlowData NO_DATA_INSTANCE = builder().build();

	public static class SimpleFlowDataBuilder implements Builder<SimpleFlowData, SimpleFlowDataBuilder> {

		private FlowStatus status;
		private FlowInfo info;
		private Map<String, ?> values;
		private List<TaskInfo> tasksToAdd, tasks;

		public SimpleFlowDataBuilder withStatus(FlowStatus status) {
			this.status = status;
			return this;
		}

		public SimpleFlowDataBuilder withInfo(FlowInfo info) {
			this.info = info;
			return this;
		}

		public SimpleFlowDataBuilder withValues(Map<String, ?> values) {
			this.values = values;
			return this;
		}

		public SimpleFlowDataBuilder withTasksToAdd(List<TaskInfo> tasksToAdd) {
			this.tasksToAdd = tasksToAdd;
			return this;
		}

		public SimpleFlowDataBuilder withTasks(List<TaskInfo> tasks) {
			this.tasks = tasks;
			return this;
		}

		@Override
		public SimpleFlowData build() {
			return new SimpleFlowData(status, info, values, tasksToAdd, tasks);
		}

	}

}
