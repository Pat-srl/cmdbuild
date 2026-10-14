/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.annotation.Nullable;

public interface PlanDataRepository {

    @Nullable
    PlanData getPlanDataByIdOrNull(String planId);

    PlanData create(PlanData data);

    PlanData update(PlanData data);

    @Nullable
    PlanData getPlanDataForProcessClasseOrNull(String classeId);

    List<RiverPlanVersionInfo> getPlanVersionsByClassIdOrderByCreationDesc(String classId);

    default PlanData getPlanDataForProcessClass(String classeId) {
        return checkNotNull(getPlanDataForProcessClasseOrNull(classeId), "plan data not found for classe = %s", classeId);
    }

    default PlanData getPlanDataById(String planId) {
        return checkNotNull(getPlanDataByIdOrNull(planId), "card not found for planId = %s", planId);
    }
}
