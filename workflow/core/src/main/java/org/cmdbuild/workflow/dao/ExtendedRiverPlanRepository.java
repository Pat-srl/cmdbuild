/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.data.RiverPlanRepository;

public interface ExtendedRiverPlanRepository extends RiverPlanRepository {

    RiverPlan createPlan(RiverPlan riverPlan);

    RiverPlan updatePlan(RiverPlan riverPlan);

    @Nullable
    RiverPlan getPlanByClassIdOrNull(String name);

    List<RiverPlanVersionInfo> getPlanVersionsByClassIdOrderByCreationDesc(String classId);

    EventBus getEventBus();

    default RiverPlan getPlanByClasseId(String className) {
        return checkNotNull(getPlanByClassIdOrNull(className), "plan not found for classe = %s", className);
    }

}
