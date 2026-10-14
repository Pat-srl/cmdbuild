/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.core;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.RiverTask;
import org.cmdbuild.workflow.river.engine.RiverFlow;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.river.engine.RiverFlowStatus;

public class RiverFlowImpl implements RiverFlow {

    private final String flowId;
    private final List<RiverTask> taskList;
    private final RiverFlowStatus status;
    private final RiverPlan plan;
    private final Map<String, Object> data;

    private RiverFlowImpl(SimpleRiverFlowBuilder builder) {
        this.taskList = ImmutableList.copyOf(checkNotNull(builder.taskList, "flow task list cannot be null"));
        this.status = checkNotNull(builder.status, "flow status cannot be null");
        this.plan = checkNotNull(builder.plan, "flow plan cannot be null");
        this.flowId = checkNotBlank(builder.flowId, "flow id cannot be null");
        this.data = map(checkNotNull(builder.data, "flow data cannot be null")).immutable();
    }

    @Override
    public String getId() {
        return flowId;
    }

    @Override
    public List<RiverTask> getTasks() {
        return taskList;
    }

    @Override
    public RiverFlowStatus getStatus() {
        return status;
    }

    @Override
    public RiverPlan getPlan() {
        return plan;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "RiverFlowImpl{" + "flowId=" + flowId + ", taskList=" + taskList + ", status=" + status + ", plan=" + plan + '}';
    }

    public static SimpleRiverFlowBuilder builder() {
        return new SimpleRiverFlowBuilder();
    }

    public static SimpleRiverFlowBuilder copyOf(RiverFlow walk) {
        return new SimpleRiverFlowBuilder()
                .withFlowId(walk.getId())
                .withPlan(walk.getPlan())
                .withTasks(walk.getTasks())
                .withData(walk.getData())
                .withFlowStatus(walk.getStatus());
    }

    public static class SimpleRiverFlowBuilder implements Builder<RiverFlowImpl, SimpleRiverFlowBuilder> {

        private Iterable<RiverTask> taskList = emptyList();
        private RiverFlowStatus status = RiverFlowStatus.READY;
        private RiverPlan plan;
        private String flowId;
        private Map<String, Object> data = emptyMap();

        public SimpleRiverFlowBuilder withTasks(Iterable<RiverTask> taskList) {
            this.taskList = taskList;
            return this;
        }

        public SimpleRiverFlowBuilder withFlowStatus(RiverFlowStatus status) {
            this.status = status;
            return this;
        }

        public SimpleRiverFlowBuilder withPlan(RiverPlan plan) {
            this.plan = plan;
            return this;
        }

        public SimpleRiverFlowBuilder withFlowId(String walkId) {
            this.flowId = walkId;
            return this;
        }

        public SimpleRiverFlowBuilder withData(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        @Override
        public RiverFlowImpl build() {
            return new RiverFlowImpl(this);
        }

    }

}
