/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.common.Constants;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.workflow.model.Process;

public interface ProcessRepository {

    Process classToPlanClasse(Classe classe);

    Process getPlanClasseByClassAndPlanId(String classId, String planId);

    Process getPlanClasseByPlanId(String planId);

    @Nullable
    Process getPlanClasseOrNull(String classId);

    List<Process> getAllPlanClassesForCurrentUser();

    default List<Process> getPlanClassesActiveAndWithXpdlOrSupreclasses() {
        return getActivePlanClasses().stream().filter((Process plan) -> {
            return plan.getName().equals(Constants.BASE_PROCESS_CLASS_NAME) || plan.isSuperclass() || !isBlank(plan.getPlanIdOrNull());
        }).collect(toList());
    }

    default Process getProcessClassByName(String name) {
        return checkNotNull(getPlanClasseOrNull(name), "plan not found for class =< %s >", name);
    }

    default List<Process> getActivePlanClasses() {
        return getAllPlanClassesForCurrentUser().stream().filter((Process input) -> input.isActive()).collect(toList());
    }

    default Iterable<Process> getPlanClasses(boolean requireActiveAndXpdlOrSuperclass) {
        if (requireActiveAndXpdlOrSuperclass) {
            return getPlanClassesActiveAndWithXpdlOrSupreclasses();
        } else {
            return getAllPlanClassesForCurrentUser();
        }
    }

    default Process getPlanByClassId(String className) {
        return checkNotNull(getPlanClasseOrNull(className), "process class not found for name =< %s >", className);
    }
}
