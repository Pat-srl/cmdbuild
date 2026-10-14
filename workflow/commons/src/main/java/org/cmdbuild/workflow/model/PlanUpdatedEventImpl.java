/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.inner.PlanUpdatedEvent;

public class PlanUpdatedEventImpl implements PlanUpdatedEvent {

    private final String classId;
    private final String planId;

    public PlanUpdatedEventImpl(String classId, String planId) {
        this.classId = checkNotBlank(classId);
        this.planId = checkNotBlank(planId);
    }

    @Override
    public String getClassId() {
        return classId;
    }

    @Override
    public String getPlanId() {
        return planId;
    }

    @Override
    public String toString() {
        return "PlanUpdatedEvent{" + "classId=" + classId + ", planId=" + planId + '}';
    }

}
