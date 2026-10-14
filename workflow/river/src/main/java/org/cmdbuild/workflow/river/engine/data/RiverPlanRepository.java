/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.data;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.workflow.river.engine.RiverPlan;

public interface RiverPlanRepository {

    default RiverPlan getPlanById(String planId) {
        return checkNotNull(getPlanByIdOrNull(planId), "plan not found for planId = %s", planId);
    }

    @Nullable
    RiverPlan getPlanByIdOrNull(String planId);
}
