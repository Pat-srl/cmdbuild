/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class FlowActivityImpl implements FlowActivity {

    private final String instanceId, definitionId, performerGroup, taskDescription;

    public FlowActivityImpl(String instanceId, String definitionId, String performerGroup, String taskDescription) {
        this.instanceId = checkNotBlank(instanceId);
        this.definitionId = checkNotBlank(definitionId);
        this.performerGroup = checkNotBlank(performerGroup);
        this.taskDescription = taskDescription;
    }

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public String getDefinitionId() {
        return definitionId;
    }

    @Override
    public String getPerformerGroup() {
        return performerGroup;
    }

    @Override
    public String getDescription() {
        return taskDescription;
    }

    @Override
    public String toString() {
        return "FlowActivityImpl{" + "instanceId=" + instanceId + ", definitionId=" + definitionId + ", performerGroup=" + performerGroup + '}';
    }

}
