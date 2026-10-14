/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class SimpleTaskInfo implements TaskInfo {

	private final String flowId, taskDefId, taskId, taskName, taskDescription;
	private final List<String> participants;

	private SimpleTaskInfo(SimpleTaskInfoBuilder builder) {
		flowId = checkNotBlank(builder.flowId);
		taskDefId = checkNotBlank(builder.taskDefId);
		taskId = checkNotBlank(builder.taskId);
		taskName = builder.taskName;
		taskDescription = builder.taskDescription;
		participants = ImmutableList.copyOf(checkNotNull(builder.participants));
	}

	@Override
	public String getFlowId() {
		return flowId;
	}

	@Override
	public String getTaskDefinitionId() {
		return taskDefId;
	}

	@Override
	public String getTaskId() {
		return taskId;
	}

	@Override
	public String getTaskName() {
		return taskName;
	}

	@Override
	public String getTaskDescription() {
		return taskDescription;
	}

	@Override
	public Collection<String> getParticipantList() {
		return participants;
	}

	@Override
	public String toString() {
		return "SimpleTaskInfo{" + "flowId=" + flowId + ", taskDefId=" + taskDefId + ", taskId=" + taskId + ", taskName=" + taskName + '}';
	}

	public static SimpleTaskInfoBuilder builder() {
		return new SimpleTaskInfoBuilder();
	}

	public static SimpleTaskInfoBuilder copyOf(TaskInfo taskInfo) {
		return builder()
				.withFlowId(taskInfo.getFlowId())
				.withParticipants(taskInfo.getParticipantList())
				.withTaskName(taskInfo.getTaskName())
				.withTaskId(taskInfo.getTaskId())
				.withTaskDescription(taskInfo.getTaskDescription())
				.withTaskDefId(taskInfo.getTaskDefinitionId());
	}

	public static class SimpleTaskInfoBuilder implements Builder<SimpleTaskInfo, SimpleTaskInfoBuilder> {

		private String flowId, taskDefId, taskId, taskName, taskDescription;
		private Collection<String> participants;

		public SimpleTaskInfoBuilder withFlowId(String flowId) {
			this.flowId = flowId;
			return this;
		}

		public SimpleTaskInfoBuilder withTaskDefId(String taskDefId) {
			this.taskDefId = taskDefId;
			return this;
		}

		public SimpleTaskInfoBuilder withTaskId(String taskId) {
			this.taskId = taskId;
			return this;
		}

		public SimpleTaskInfoBuilder withTaskName(String taskName) {
			this.taskName = taskName;
			return this;
		}

		public SimpleTaskInfoBuilder withTaskDescription(String taskDescription) {
			this.taskDescription = taskDescription;
			return this;
		}

		public SimpleTaskInfoBuilder withParticipants(Collection<String> participants) {
			this.participants = participants;
			return this;
		}

		@Override
		public SimpleTaskInfo build() {
			return new SimpleTaskInfo(this);
		}

	}

}
